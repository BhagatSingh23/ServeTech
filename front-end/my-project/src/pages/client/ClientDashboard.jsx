import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import DashboardLayout from '../../components/common/DashboardLayout';
import StatsCard from '../../components/common/StatsCard';
import LoadingSpinner from '../../components/common/LoadingSpinner';
import Badge from '../../components/common/Badge';
import EmptyState from '../../components/common/EmptyState';
import { useToast } from '../../components/common/Toast';
import { getClientDashboard } from '../../api/client';
import { formatCurrency } from '../../utils/formatCurrency';
import { formatDate } from '../../utils/formatDate';

const ClientDashboard = () => {
  const [dashboard, setDashboard] = useState(null);
  const [loading, setLoading] = useState(true);
  const toast = useToast();
  const navigate = useNavigate();

  useEffect(() => {
    fetchDashboard();
  }, []);

  const fetchDashboard = async () => {
    try {
      const response = await getClientDashboard();
      setDashboard(response.data.data);
    } catch {
      toast.error('Failed to load dashboard data');
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <DashboardLayout pageTitle="Dashboard">
        <LoadingSpinner text="Loading dashboard..." />
      </DashboardLayout>
    );
  }

  const stats = dashboard?.stats || {};
  const activeJobs = dashboard?.activeJobs || [];
  const recentWorkers = dashboard?.recentWorkers || [];

  return (
    <DashboardLayout pageTitle="Dashboard">
      {/* Stats Row */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 mb-8">
        <StatsCard
          icon="📝"
          label="Projects Posted"
          value={stats.projectsPosted || stats.totalWorkRequests || 0}
          color="blue"
        />
        <StatsCard
          icon="🔄"
          label="Active Projects"
          value={stats.activeProjects || stats.activeWorkRequests || 0}
          color="amber"
        />
        <StatsCard
          icon="👷"
          label="Workers Hired"
          value={stats.workersHired || stats.totalAssignments || 0}
          color="green"
        />
        <StatsCard
          icon="💰"
          label="Total Spent"
          value={formatCurrency(stats.totalSpent || 0)}
          color="red"
        />
      </div>

      {/* Active Jobs */}
      <section className="mb-8">
        <div className="flex items-center justify-between mb-4">
          <h2 className="text-lg font-semibold text-white flex items-center gap-2">
            <span>💼</span> Active Jobs
          </h2>
          <button
            onClick={() => navigate('/client/my-jobs')}
            className="text-sm text-amber-400 hover:text-amber-300 transition-colors cursor-pointer"
          >
            View All →
          </button>
        </div>
        {activeJobs.length === 0 ? (
          <EmptyState
            icon="💼"
            title="No active jobs"
            description="Post a new job to get started."
            actionLabel="Post a Job"
            onAction={() => navigate('/client/post-job')}
          />
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
            {activeJobs.map((job) => (
              <div
                key={job.id || job.workRequestId}
                onClick={() => navigate(`/client/jobs/${job.id || job.workRequestId}/applicants`)}
                className="bg-slate-800 rounded-xl shadow-lg border border-slate-700 p-5 hover:border-slate-600 transition-all duration-200 cursor-pointer"
              >
                <div className="flex items-start justify-between mb-3">
                  <h3 className="text-white font-semibold text-base truncate pr-2">{job.title}</h3>
                  <Badge status={job.status} type="workRequest" />
                </div>

                <div className="flex flex-wrap gap-1.5 mb-3">
                  {(job.skills || job.skillsRequired || []).slice(0, 3).map((skill) => (
                    <span key={skill} className="bg-slate-700 text-slate-300 text-xs px-2 py-0.5 rounded-md">
                      {skill}
                    </span>
                  ))}
                  {(job.skills || job.skillsRequired || []).length > 3 && (
                    <span className="text-xs text-slate-500">
                      +{(job.skills || job.skillsRequired || []).length - 3} more
                    </span>
                  )}
                </div>

                <div className="grid grid-cols-2 gap-2 text-xs text-slate-400">
                  <div>
                    <span className="text-slate-500">Workers: </span>
                    <span className="text-white">
                      {job.workersAssigned || 0}/{job.workersNeeded || job.numberOfWorkers || 0}
                    </span>
                  </div>
                  <div>
                    <span className="text-slate-500">Wage: </span>
                    <span className="text-amber-400">{formatCurrency(job.wagePerDay || job.dailyWage)}</span>
                  </div>
                  <div className="col-span-2">
                    <span className="text-slate-500">Period: </span>
                    <span className="text-white">{formatDate(job.startDate)} - {formatDate(job.endDate)}</span>
                  </div>
                </div>
              </div>
            ))}
          </div>
        )}
      </section>

      {/* Recent Workers */}
      <section>
        <h2 className="text-lg font-semibold text-white mb-4 flex items-center gap-2">
          <span>👷</span> Recent Workers
        </h2>
        {recentWorkers.length === 0 ? (
          <EmptyState
            icon="👷"
            title="No workers yet"
            description="Workers you hire will appear here."
          />
        ) : (
          <div className="bg-slate-800 rounded-xl shadow-lg border border-slate-700 overflow-hidden">
            <div className="overflow-x-auto">
              <table className="w-full">
                <thead>
                  <tr className="border-b border-slate-700">
                    <th className="text-left px-5 py-3 text-slate-400 text-xs uppercase font-medium">Worker</th>
                    <th className="text-left px-5 py-3 text-slate-400 text-xs uppercase font-medium">Job</th>
                    <th className="text-left px-5 py-3 text-slate-400 text-xs uppercase font-medium">Wage/Day</th>
                    <th className="text-left px-5 py-3 text-slate-400 text-xs uppercase font-medium">Payment</th>
                    <th className="text-left px-5 py-3 text-slate-400 text-xs uppercase font-medium">Status</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-700">
                  {recentWorkers.map((worker) => (
                    <tr key={worker.assignmentId || worker.id} className="hover:bg-slate-700/50 transition-colors">
                      <td className="px-5 py-3">
                        <div className="flex items-center gap-3">
                          <div className="h-8 w-8 rounded-full bg-slate-700 flex items-center justify-center text-amber-400 text-xs font-semibold">
                            {(worker.workerName || 'W').charAt(0)}
                          </div>
                          <div>
                            <p className="text-sm text-white font-medium">{worker.workerName || '—'}</p>
                            <p className="text-xs text-slate-500">{worker.workerPhone || ''}</p>
                          </div>
                        </div>
                      </td>
                      <td className="px-5 py-3 text-sm text-white">{worker.jobTitle || worker.workRequestTitle || '—'}</td>
                      <td className="px-5 py-3 text-sm text-amber-400">{formatCurrency(worker.wagePerDay || worker.dailyWage)}</td>
                      <td className="px-5 py-3">
                        <div>
                          <p className="text-sm text-white">{formatCurrency(worker.totalPaid || 0)}</p>
                          {worker.pendingAmount > 0 && (
                            <p className="text-xs text-red-400">Pending: {formatCurrency(worker.pendingAmount)}</p>
                          )}
                        </div>
                      </td>
                      <td className="px-5 py-3">
                        <Badge status={worker.status || worker.progressStatus} type="workProgress" />
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        )}
      </section>
    </DashboardLayout>
  );
};

export default ClientDashboard;
