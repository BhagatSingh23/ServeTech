import { useState, useEffect } from 'react';
import DashboardLayout from '../../components/common/DashboardLayout';
import LoadingSpinner from '../../components/common/LoadingSpinner';
import EmptyState from '../../components/common/EmptyState';
import Modal from '../../components/common/Modal';
import Button from '../../components/common/Button';
import Badge from '../../components/common/Badge';
import { useToast } from '../../components/common/Toast';
import { browseJobs, getRecommendedJobs, getJobDetails } from '../../api/jobs';
import { getWorkerApplications } from '../../api/worker';
import api from '../../api/axios';
import { formatCurrency } from '../../utils/formatCurrency';
import { formatDate, timeAgo } from '../../utils/formatDate';

const SKILLS = [
  'PAINTER', 'PLUMBER', 'ELECTRICIAN', 'CARPENTER', 'MASON',
  'WELDER', 'DRIVER', 'COOK', 'GARDENER', 'CLEANER', 'HELPER', 'LABOUR', 'OTHER',
];

const BrowseJobs = () => {
  const [jobs, setJobs] = useState([]);
  const [loading, setLoading] = useState(true);
  const [appliedJobIds, setAppliedJobIds] = useState(new Set());
  const [applyModal, setApplyModal] = useState({ open: false, job: null });

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

  const toggleSkill = (skill) => {
    setSelectedSkills((prev) =>
      prev.includes(skill)
        ? prev.filter((s) => s !== skill)
        : [...prev, skill]
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
      {/* Filters Section */}
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
              <div
                onClick={() => setUrgentOnly(!urgentOnly)}
                className={`relative w-11 h-6 rounded-full transition-all duration-200 cursor-pointer ${
                  urgentOnly ? 'bg-amber-500' : 'bg-slate-600'
                }`}
              >
                <div
                  className={`absolute top-0.5 left-0.5 h-5 w-5 bg-white rounded-full transition-transform duration-200 ${
                    urgentOnly ? 'translate-x-5' : ''
                  }`}
                />
              </div>
              <span className="text-sm text-slate-300">Urgent Only</span>
            </label>
            <Button variant="primary" size="sm" onClick={handleSearch}>
              Search
            </Button>
            <Button variant="ghost" size="sm" onClick={clearFilters}>
              Clear
            </Button>
          </div>
        </div>

        {/* Skill Chips */}
        <div className="flex flex-wrap gap-2">
          {SKILLS.map((skill) => (
            <button
              key={skill}
              onClick={() => toggleSkill(skill)}
              className={`px-3 py-1.5 rounded-full text-xs font-medium transition-all duration-200 cursor-pointer ${
                selectedSkills.includes(skill)
                  ? 'bg-amber-500 text-black'
                  : 'bg-slate-700 text-slate-300 hover:bg-slate-600'
              }`}
            >
              {skill}
            </button>
          ))}
        </div>
      </div>

      {/* Jobs List */}
      {loading ? (
        <LoadingSpinner text="Loading jobs..." />
      ) : jobs.length === 0 ? (
        <EmptyState
          icon="🔍"
          title="No jobs found"
          description="Try adjusting your filters or check back later for new opportunities."
          actionLabel="Clear Filters"
          onAction={clearFilters}
        />
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-4">
          {jobs.map((job) => {
            const isApplied = appliedJobIds.has(job.id || job.workRequestId);
            return (
              <div
                key={job.id || job.workRequestId}
                className="bg-slate-800 rounded-xl shadow-lg border border-slate-700 p-5 hover:border-slate-600 transition-all duration-200 flex flex-col"
              >
                <div className="flex items-start justify-between mb-3">
                  <h3 className="text-white font-semibold text-base leading-tight pr-2">{job.title}</h3>
                  {job.urgent && (
                    <span className="bg-red-500/20 text-red-400 text-xs font-medium px-2 py-0.5 rounded-full flex-shrink-0">
                      Urgent
                    </span>
                  )}
                </div>

                {job.description && (
                  <p className="text-sm text-slate-400 mb-3 line-clamp-2">{job.description}</p>
                )}

                {/* Skills Tags */}
                <div className="flex flex-wrap gap-1.5 mb-3">
                  {(job.skills || job.skillsRequired || []).map((skill) => (
                    <span
                      key={skill}
                      className="bg-slate-700 text-slate-300 text-xs px-2 py-0.5 rounded-md"
                    >
                      {skill}
                    </span>
                  ))}
                </div>

                {/* Job Details */}
                <div className="grid grid-cols-2 gap-2 mb-4 text-xs text-slate-400">
                  <div className="flex items-center gap-1">
                    <span>💰</span>
                    <span className="text-amber-400 font-semibold">{formatCurrency(job.wagePerDay || job.dailyWage)}/day</span>
                  </div>
                  <div className="flex items-center gap-1">
                    <span>📅</span>
                    <span>{job.durationDays || job.duration || '—'} days</span>
                  </div>
                  <div className="flex items-center gap-1">
                    <span>📍</span>
                    <span>{job.location || job.pincode || '—'}</span>
                  </div>
                  <div className="flex items-center gap-1">
                    <span>👷</span>
                    <span>{job.workersNeeded || job.numberOfWorkers || 1} needed</span>
                  </div>
                </div>

                <div className="text-xs text-slate-500 mb-4">
                  Posted {timeAgo(job.createdAt || job.postedAt)}
                </div>

                {/* Action */}
                <div className="mt-auto">
                  {isApplied ? (
                    <div className="w-full bg-green-500/10 border border-green-500/30 text-green-400 text-sm font-medium rounded-lg px-4 py-2.5 text-center">
                      ✓ Already Applied
                    </div>
                  ) : (
                    <Button
                      variant="primary"
                      fullWidth
                      onClick={() => setApplyModal({ open: true, job })}
                    >
                      ⚡ Quick Apply
                    </Button>
                  )}
                </div>
              </div>
            );
          })}
        </div>
      )}

      {/* Apply Modal */}
      <Modal
        isOpen={applyModal.open}
        onClose={() => setApplyModal({ open: false, job: null })}
        title="Apply for Job"
      >
        {applyModal.job && (
          <ApplyForm
            job={applyModal.job}
            onClose={() => setApplyModal({ open: false, job: null })}
            onSuccess={(jobId) => {
              setAppliedJobIds((prev) => new Set([...prev, jobId]));
              setApplyModal({ open: false, job: null });
            }}
          />
        )}
      </Modal>
    </DashboardLayout>
  );
};

const ApplyForm = ({ job, onClose, onSuccess }) => {
  const [proposedWage, setProposedWage] = useState(job.wagePerDay || job.dailyWage || '');
  const [coverLetter, setCoverLetter] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const toast = useToast();

  const handleApply = async (e) => {
    e.preventDefault();
    if (!proposedWage || Number(proposedWage) <= 0) {
      toast.warning('Please enter a valid proposed wage');
      return;
    }

    setSubmitting(true);
    try {
      await api.post(`/jobs/${job.id || job.workRequestId}/apply`, {
        proposedWage: Number(proposedWage),
        coverLetter: coverLetter.trim(),
      });
      toast.success('Application submitted successfully!');
      onSuccess(job.id || job.workRequestId);
    } catch (error) {
      toast.error(error.response?.data?.message || 'Failed to submit application');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <form onSubmit={handleApply} className="space-y-4">
      <div className="bg-slate-700/50 rounded-lg p-4 border border-slate-600">
        <h4 className="text-white font-semibold mb-1">{job.title}</h4>
        <p className="text-sm text-slate-400">
          Offered wage: <span className="text-amber-400">{formatCurrency(job.wagePerDay || job.dailyWage)}/day</span>
        </p>
      </div>

      <div>
        <label className="block text-sm font-medium text-slate-300 mb-1.5">Your Proposed Wage (₹/day)</label>
        <input
          type="number"
          value={proposedWage}
          onChange={(e) => setProposedWage(e.target.value)}
          min="1"
          className="w-full bg-slate-700 border border-slate-600 text-white rounded-lg px-4 py-2.5 focus:border-amber-400 focus:ring-1 focus:ring-amber-400 outline-none transition-all"
        />
      </div>

      <div>
        <label className="block text-sm font-medium text-slate-300 mb-1.5">Cover Letter (Optional)</label>
        <textarea
          value={coverLetter}
          onChange={(e) => setCoverLetter(e.target.value)}
          rows={3}
          placeholder="Tell the client why you're a good fit..."
          className="w-full bg-slate-700 border border-slate-600 text-white rounded-lg px-4 py-2.5 focus:border-amber-400 focus:ring-1 focus:ring-amber-400 outline-none transition-all placeholder-slate-500 resize-none"
        />
      </div>

      <div className="flex justify-end gap-3 pt-2">
        <Button variant="secondary" onClick={onClose} type="button">Cancel</Button>
        <Button variant="primary" type="submit" loading={submitting}>Submit Application</Button>
      </div>
    </form>
  );
};

export default BrowseJobs;
