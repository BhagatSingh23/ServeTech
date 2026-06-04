import { useState, useEffect } from 'react';
import DashboardLayout from '../../components/common/DashboardLayout';
import LoadingSpinner from '../../components/common/LoadingSpinner';
import EmptyState from '../../components/common/EmptyState';
import Button from '../../components/common/Button';
import { useToast } from '../../components/common/Toast';
import { browseJobs, getRecommendedJobs } from '../../api/jobs';
import { getWorkerApplications } from '../../api/worker';
import api from '../../api/axios';
import { formatCurrency } from '../../utils/formatCurrency';
import { formatDate, formatDateTime, timeAgo } from '../../utils/formatDate';

const SKILLS = [
  'PAINTER', 'PLUMBER', 'ELECTRICIAN', 'CARPENTER', 'MASON',
  'WELDER', 'DRIVER', 'COOK', 'GARDENER', 'CLEANER', 'HELPER', 'LABOUR', 'OTHER',
];

const BrowseJobs = () => {
  const [jobs, setJobs] = useState([]);
  const [loading, setLoading] = useState(true);
  const [appliedJobIds, setAppliedJobIds] = useState(new Set());
  const [expandedJobId, setExpandedJobId] = useState(null);
  const [applyingJobId, setApplyingJobId] = useState(null);
  const [counterWages, setCounterWages] = useState({});

  // Filters
  const [searchPincode, setSearchPincode] = useState('');
  const [selectedSkills, setSelectedSkills] = useState([]);
  const [urgentOnly, setUrgentOnly] = useState(false);

  const toast = useToast();

  useEffect(() => {
    loadInitialData();
  }, []);

  const loadInitialData = async () => {
    setLoading(true);
    try {
      const [jobsRes, appsRes] = await Promise.all([
        getRecommendedJobs(),
        getWorkerApplications(),
      ]);
      setJobs(jobsRes.data.data || []);
      const appliedIds = new Set(
        (appsRes.data.data || []).map((app) => app.workRequestId || app.jobId)
      );
      setAppliedJobIds(appliedIds);
    } catch {
      toast.error('Failed to load jobs');
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = async () => {
    setLoading(true);
    try {
      const params = {};
      if (searchPincode) params.pincode = searchPincode;
      if (selectedSkills.length > 0) params.skills = selectedSkills.join(',');
      if (urgentOnly) params.urgent = true;
      const response = await browseJobs(params);
      setJobs(response.data.data || []);
    } catch {
      toast.error('Failed to search jobs');
    } finally {
      setLoading(false);
    }
  };

  const handleApply = async (job) => {
    const jobId = job.id || job.workRequestId;
    const wage = job.isNegotiable && counterWages[jobId]
      ? Number(counterWages[jobId])
      : job.offeredWagePerDay;
    if (!wage || wage <= 0) {
      toast.warning('Please enter a valid wage');
      return;
    }
    setApplyingJobId(jobId);
    try {
      await api.post('/applications', {
        workRequestId: jobId,
        proposedWagePerDay: wage,
        coverLetter: '',
      });
      toast.success('Application submitted successfully!');
      setAppliedJobIds((prev) => new Set([...prev, jobId]));
    } catch (error) {
      toast.error(error.response?.data?.message || 'Failed to apply');
    } finally {
      setApplyingJobId(null);
    }
  };

  const toggleSkill = (skill) => {
    setSelectedSkills((prev) =>
      prev.includes(skill) ? prev.filter((s) => s !== skill) : [...prev, skill]
    );
  };

  const clearFilters = () => {
    setSearchPincode('');
    setSelectedSkills([]);
    setUrgentOnly(false);
    loadInitialData();
  };

  return (
    <DashboardLayout pageTitle="Browse Jobs">
      {/* Filters */}
      <div className="bg-slate-800 rounded-xl shadow-lg border border-slate-700 p-5 mb-6">
        <div className="flex flex-col sm:flex-row gap-4 mb-4">
          <div className="flex-1">
            <div className="relative">
              <svg className="absolute left-3 top-1/2 -translate-y-1/2 h-5 w-5 text-slate-400" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                <path strokeLinecap="round" strokeLinejoin="round" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
              </svg>
              <input
                type="text"
                value={searchPincode}
                onChange={(e) => setSearchPincode(e.target.value.replace(/\D/g, '').slice(0, 6))}
                placeholder="Filter by pincode..."
                className="w-full bg-slate-700 border border-slate-600 text-white rounded-lg pl-10 pr-4 py-2.5 focus:border-amber-400 focus:ring-1 focus:ring-amber-400 outline-none transition-all placeholder-slate-500"
              />
            </div>
          </div>
          <div className="flex items-center gap-3">
            <label className="flex items-center gap-2 cursor-pointer select-none">
              <div onClick={() => setUrgentOnly(!urgentOnly)}
                className={`relative w-11 h-6 rounded-full transition-all duration-200 cursor-pointer ${urgentOnly ? 'bg-amber-500' : 'bg-slate-600'}`}>
                <div className={`absolute top-0.5 left-0.5 h-5 w-5 bg-white rounded-full transition-transform duration-200 ${urgentOnly ? 'translate-x-5' : ''}`} />
              </div>
              <span className="text-sm text-slate-300">Urgent Only</span>
            </label>
            <Button variant="primary" size="sm" onClick={handleSearch}>Search</Button>
            <Button variant="ghost" size="sm" onClick={clearFilters}>Clear</Button>
          </div>
        </div>
        <div className="flex flex-wrap gap-2">
          {SKILLS.map((skill) => (
            <button key={skill} onClick={() => toggleSkill(skill)}
              className={`px-3 py-1.5 rounded-full text-xs font-medium transition-all duration-200 cursor-pointer ${
                selectedSkills.includes(skill) ? 'bg-amber-500 text-black' : 'bg-slate-700 text-slate-300 hover:bg-slate-600'
              }`}>{skill}</button>
          ))}
        </div>
      </div>

      {/* Jobs List */}
      {loading ? (
        <LoadingSpinner text="Loading jobs..." />
      ) : jobs.length === 0 ? (
        <EmptyState icon="🔍" title="No jobs found" description="Try adjusting your filters or check back later." actionLabel="Clear Filters" onAction={clearFilters} />
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-4">
          {jobs.map((job) => {
            const jobId = job.id || job.workRequestId;
            const isApplied = appliedJobIds.has(jobId);
            const isExpanded = expandedJobId === jobId;
            const isApplying = applyingJobId === jobId;
            const totalBudget = job.totalBudget || (job.offeredWagePerDay * (job.estimatedDurationDays || 1) * (job.workersNeeded || 1));

            return (
              <div key={jobId} className="bg-slate-800 rounded-xl shadow-lg border border-slate-700 p-5 hover:border-slate-600 transition-all duration-200 flex flex-col">
                {/* Header */}
                <div className="flex items-start justify-between mb-3">
                  <h3 className="text-white font-semibold text-base leading-tight pr-2">{job.title}</h3>
                  <div className="flex gap-1.5 flex-shrink-0">
                    {job.isUrgent && (
                      <span className="bg-red-500/20 text-red-400 text-xs font-medium px-2 py-0.5 rounded-full">Urgent</span>
                    )}
                  </div>
                </div>

                {job.description && (
                  <p className="text-sm text-slate-400 mb-3 line-clamp-2">{job.description}</p>
                )}

                {/* Skills */}
                <div className="flex flex-wrap gap-1.5 mb-3">
                  {(job.requiredSkills || job.skills || []).map((skill) => (
                    <span key={skill} className="bg-slate-700 text-slate-300 text-xs px-2 py-0.5 rounded-md">{skill}</span>
                  ))}
                </div>

                {/* Wage & Key Info */}
                <div className="bg-slate-700/40 rounded-lg p-3 mb-3 border border-slate-600/50">
                  <div className="flex items-center justify-between mb-1">
                    <span className="text-xs text-slate-400">Offered Wage</span>
                    {job.isNegotiable !== false ? (
                      <span className="bg-green-500/15 text-green-400 text-xs font-medium px-2 py-0.5 rounded-full">Negotiable</span>
                    ) : (
                      <span className="bg-slate-600/50 text-slate-400 text-xs font-medium px-2 py-0.5 rounded-full">Fixed</span>
                    )}
                  </div>
                  <p className="text-lg font-bold text-amber-400">{formatCurrency(job.offeredWagePerDay)}<span className="text-xs font-normal text-slate-400">/day</span></p>
                  <p className="text-xs text-slate-500 mt-0.5">Est. Total: {formatCurrency(totalBudget)}</p>
                </div>

                {/* Quick Info Grid */}
                <div className="grid grid-cols-2 gap-2 mb-3 text-xs text-slate-400">
                  <div className="flex items-center gap-1"><span>📅</span><span>{job.estimatedDurationDays || '—'} days</span></div>
                  <div className="flex items-center gap-1"><span>👷</span><span>{job.workersNeeded || 1} needed</span></div>
                  <div className="flex items-center gap-1"><span>📍</span><span>{job.pincode || '—'}</span></div>
                  <div className="flex items-center gap-1"><span>🕐</span><span>{timeAgo(job.createdAt)}</span></div>
                </div>

                {/* Expandable Details */}
                <button
                  onClick={() => setExpandedJobId(isExpanded ? null : jobId)}
                  className="flex items-center justify-center gap-1 text-xs text-amber-400 hover:text-amber-300 mb-3 cursor-pointer transition-colors"
                >
                  {isExpanded ? '▲ Hide Details' : '▼ View Details'}
                </button>

                {isExpanded && (
                  <div className="bg-slate-700/30 rounded-lg p-3 mb-3 border border-slate-600/50 space-y-2 text-sm animate-[fadeIn_0.2s_ease-in]">
                    <div className="grid grid-cols-2 gap-3">
                      <div>
                        <p className="text-xs text-slate-500">Client</p>
                        <p className="text-white text-sm">{job.clientName || '—'}</p>
                      </div>
                      <div>
                        <p className="text-xs text-slate-500">Pincode</p>
                        <p className="text-white text-sm">{job.pincode || '—'}</p>
                      </div>
                    </div>
                    {job.workAddress && (
                      <div>
                        <p className="text-xs text-slate-500">Work Address</p>
                        <p className="text-white text-sm">{job.workAddress}</p>
                      </div>
                    )}
                    <div className="grid grid-cols-2 gap-3">
                      <div>
                        <p className="text-xs text-slate-500">Start Date</p>
                        <p className="text-white text-sm">{formatDateTime(job.startDate) || '—'}</p>
                      </div>
                      <div>
                        <p className="text-xs text-slate-500">End Date</p>
                        <p className="text-white text-sm">{formatDateTime(job.endDate) || '—'}</p>
                      </div>
                    </div>
                    <div className="grid grid-cols-2 gap-3">
                      <div>
                        <p className="text-xs text-slate-500">Duration</p>
                        <p className="text-white text-sm">{job.estimatedDurationDays || '—'} days</p>
                      </div>
                      <div>
                        <p className="text-xs text-slate-500">Total Budget</p>
                        <p className="text-amber-400 text-sm font-semibold">{formatCurrency(totalBudget)}</p>
                      </div>
                    </div>
                  </div>
                )}

                {/* Action */}
                <div className="mt-auto">
                  {isApplied ? (
                    <div className="w-full bg-green-500/10 border border-green-500/30 text-green-400 text-sm font-medium rounded-lg px-4 py-2.5 text-center">
                      ✓ Applied
                    </div>
                  ) : (
                    <div className="space-y-2">
                      {job.isNegotiable !== false && (
                        <div>
                          <label className="block text-xs text-slate-400 mb-1">Your Proposed Wage (₹/day)</label>
                          <input
                            type="number"
                            value={counterWages[jobId] ?? ''}
                            onChange={(e) => setCounterWages((prev) => ({ ...prev, [jobId]: e.target.value }))}
                            placeholder={`${job.offeredWagePerDay || 'offered wage'}`}
                            min="1"
                            className="w-full bg-slate-700 border border-slate-600 text-white rounded-lg px-3 py-2 text-sm focus:border-amber-400 focus:ring-1 focus:ring-amber-400 outline-none transition-all placeholder-slate-500"
                          />
                        </div>
                      )}
                      <Button variant="primary" fullWidth onClick={() => handleApply(job)} loading={isApplying}>
                        Apply
                      </Button>
                    </div>
                  )}
                </div>
              </div>
            );
          })}
        </div>
      )}
    </DashboardLayout>
  );
};

export default BrowseJobs;
