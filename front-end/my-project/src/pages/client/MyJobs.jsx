import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import DashboardLayout from '../../components/common/DashboardLayout';
import LoadingSpinner from '../../components/common/LoadingSpinner';
import EmptyState from '../../components/common/EmptyState';
import Badge from '../../components/common/Badge';
import Modal from '../../components/common/Modal';
import Button from '../../components/common/Button';
import { useToast } from '../../components/common/Toast';
import { getMyWorkRequests, deleteWorkRequest, closeWorkRequest } from '../../api/client';
import { formatCurrency } from '../../utils/formatCurrency';
import { formatDate, timeAgo } from '../../utils/formatDate';

const TABS = [
  { key: '', label: 'All' },
  { key: 'OPEN', label: 'Open' },
  { key: 'IN_PROGRESS', label: 'In Progress' },
  { key: 'COMPLETED', label: 'Completed' },
  { key: 'CLOSED', label: 'Closed' },
];

const MyJobs = () => {
const [jobs, setJobs] = useState([]);
  const [loading, setLoading] = useState(true);
  const [activeTab, setActiveTab] = useState('');
  const [deleteModal, setDeleteModal] = useState({ open: false, job: null });
  const [closeModal, setCloseModal] = useState({ open: false, job: null });
  const [actionLoading, setActionLoading] = useState(false);

  const toast = useToast();
  const navigate = useNavigate();

  useEffect(() => {
    fetchJobs();
  }, [activeTab]);

  const fetchJobs = async () => {
    setLoading(true);
    try {
      const response = await getMyWorkRequests(activeTab || undefined);
      setJobs(response.data.data || []);
    } catch (error) {
      if (error.response?.status === 403) {
        toast.error('Access denied. Only clients can view work requests.');
      } else {
        toast.error('Failed to load jobs');
      }
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async () => {
    const job = deleteModal.job;
    if (!job) return;
    setActionLoading(true);
    try {
      await deleteWorkRequest(job.id || job.workRequestId);
      toast.success('Job deleted successfully');
      setDeleteModal({ open: false, job: null });
      fetchJobs();
    } catch (error) {
      toast.error(error.response?.data?.message || 'Failed to delete job');
    } finally {
      setActionLoading(false);
    }
  };

  const handleClose = async () => {
    const job = closeModal.job;
    if (!job) return;
    setActionLoading(true);
    try {
      await closeWorkRequest(job.id || job.workRequestId);
      toast.success('Job closed successfully');
      setCloseModal({ open: false, job: null });
      fetchJobs();
    } catch (error) {
      toast.error(error.response?.data?.message || 'Failed to close job');
    } finally {
      setActionLoading(false);
    }
  };

  const canEdit = (status) => ['DRAFT', 'OPEN'].includes(status);
  const canDelete = (status) => ['DRAFT', 'OPEN'].includes(status);
  const canClose = (status) => ['OPEN', 'IN_PROGRESS', 'COMPLETED'].includes(status);

  return (
    <DashboardLayout pageTitle="My Jobs">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 mb-6">
        <div className="bg-slate-800/80 backdrop-blur-md rounded-lg p-1.5 inline-flex gap-1 flex-wrap border border-slate-700/80 shadow-lg">
          {TABS.map((tab) => (
            <button
              key={tab.key}
              onClick={() => setActiveTab(tab.key)}
              className={`px-4 py-2 rounded-md text-sm font-medium transition-all duration-300 cursor-pointer ${
                activeTab === tab.key
                  ? 'bg-gradient-to-r from-amber-500 to-amber-400 text-black shadow-md transform scale-105'
                  : 'text-slate-400 hover:text-white hover:bg-slate-700/50'
              }`}
            >
              {tab.label}
            </button>
          ))}
        </div>
        <Button variant="primary" onClick={() => navigate('/client/post-job')} className="bg-gradient-to-r from-amber-500 to-amber-400 text-black hover:from-amber-400 hover:to-amber-300 shadow-lg shadow-amber-500/20 transform hover:-translate-y-0.5 transition-all border-0">
          + Post a New Job
        </Button>
      </div>

      {loading ? (
        <LoadingSpinner text="Loading jobs..." />
      ) : jobs.length === 0 ? (
        <div className="bg-slate-800/80 backdrop-blur-xl rounded-xl border border-slate-700/80 shadow-xl p-8 transition-all hover:border-slate-600/50">
          <EmptyState
            icon="💼"
            title="No jobs posted yet"
            description={activeTab ? `No ${activeTab.toLowerCase().replace('_', ' ')} jobs.` : "You haven't posted any jobs yet."}
            actionLabel="Post a New Job"
            onAction={() => navigate('/client/post-job')}
          />
        </div>
      ) : (
        <div className="space-y-4">
          {jobs.map((job) => (
            <div
              key={job.id || job.workRequestId}
              className="bg-slate-800/80 backdrop-blur-xl rounded-xl shadow-lg border border-slate-700/80 p-5 hover:border-amber-500/50 hover:shadow-amber-500/10 transition-all duration-300"
            >
              <div className="flex flex-col sm:flex-row sm:items-start justify-between gap-3 mb-4">
                <div
                  className="flex-1 min-w-0 cursor-pointer group"
                  onClick={() => navigate(`/client/jobs/${job.id}/applicants`)}
                >
                  <h3 className="text-white font-semibold text-lg group-hover:text-amber-400 transition-colors truncate drop-shadow-md">
                    {job.title}
                  </h3>
                </div>
                <Badge status={job.status} type="workRequest" />
              </div>

              <div className="flex flex-wrap gap-1.5 mb-3">
                {(job.requiredSkills || job.skills || []).map((skill) => (
                  <span key={skill} className="bg-slate-700/50 backdrop-blur-sm text-slate-300 text-xs px-2.5 py-1 rounded-md border border-slate-600/50 shadow-sm">
                    {skill}
                  </span>
                ))}
              </div>

              <div className="grid grid-cols-2 sm:grid-cols-4 gap-3 mb-4 text-sm bg-slate-900/40 rounded-lg p-3">
                <div>
                  <p className="text-xs text-slate-500">Workers</p>
                  <p className="text-white">
                    {job.workersAssigned || 0}/{job.workersNeeded || 0}
                  </p>
                </div>
                <div>
                  <p className="text-xs text-slate-500">Wage/Day</p>
                  <p className="text-amber-400 font-semibold drop-shadow-sm">{formatCurrency(job.offeredWagePerDay)}</p>
                </div>
                <div>
                  <p className="text-xs text-slate-500">Start</p>
                  <p className="text-white">{formatDate(job.startDate)}</p>
                </div>
                <div>
                  <p className="text-xs text-slate-500">End</p>
                  <p className="text-white">{formatDate(job.endDate)}</p>
                </div>
              </div>

              <div className="flex items-center justify-between pt-3 border-t border-slate-700/80">
                <span className="text-xs text-slate-500">Posted {timeAgo(job.createdAt)}</span>
                <div className="flex gap-2">
                  <Button
                    variant="ghost"
                    size="sm"
                    onClick={() => navigate(`/client/jobs/${job.id}/applicants`)}
                    className="hover:bg-slate-700/50 text-slate-300"
                  >
                    View Applicants {job.applicationCount > 0 ? `(${job.applicationCount})` : ''}
                  </Button>
                  {canEdit(job.status) && (
                    <Button variant="secondary" size="sm" onClick={() => navigate(`/client/post-job?edit=${job.id || job.workRequestId}`)} className="hover:bg-slate-700/80 border-slate-600">
                      Edit
                    </Button>
                  )}
                  {canClose(job.status) && (
                    <Button variant="secondary" size="sm" onClick={() => setCloseModal({ open: true, job })} className="hover:bg-slate-700/80 border-slate-600">
                      Close
                    </Button>
                  )}
                  {canDelete(job.status) && (
                    <Button variant="danger" size="sm" onClick={() => setDeleteModal({ open: true, job })} className="bg-red-500/10 text-red-400 hover:bg-red-500/20 border-red-500/50">
                      Delete
                    </Button>
                  )}
                </div>
              </div>
            </div>
          ))}
        </div>
      )}

      <Modal
        isOpen={deleteModal.open}
        onClose={() => setDeleteModal({ open: false, job: null })}
        title="Delete Job"
      >
        <div className="space-y-4">
          <p className="text-slate-300">
            Are you sure you want to delete{' '}
            <span className="text-white font-semibold">{deleteModal.job?.title}</span>?
          </p>
          <p className="text-sm text-red-400">This action cannot be undone.</p>
          <div className="flex justify-end gap-3 pt-2">
            <Button variant="secondary" onClick={() => setDeleteModal({ open: false, job: null })}>
              Cancel
            </Button>
            <Button variant="danger" loading={actionLoading} onClick={handleDelete}>
              Delete Job
            </Button>
          </div>
        </div>
      </Modal>

      <Modal
        isOpen={closeModal.open}
        onClose={() => setCloseModal({ open: false, job: null })}
        title="Close Job"
      >
        <div className="space-y-4">
          <p className="text-slate-300">
            Are you sure you want to close{' '}
            <span className="text-white font-semibold">{closeModal.job?.title}</span>?
          </p>
          <p className="text-sm text-slate-400">No more applications will be accepted after closing.</p>
          <div className="flex justify-end gap-3 pt-2">
            <Button variant="secondary" onClick={() => setCloseModal({ open: false, job: null })}>
              Cancel
            </Button>
            <Button variant="primary" loading={actionLoading} onClick={handleClose}>
              Close Job
            </Button>
          </div>
        </div>
      </Modal>
    </DashboardLayout>
  );
};

export default MyJobs;
