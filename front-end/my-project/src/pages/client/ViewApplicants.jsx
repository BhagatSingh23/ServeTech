import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import DashboardLayout from '../../components/common/DashboardLayout';
import LoadingSpinner from '../../components/common/LoadingSpinner';
import EmptyState from '../../components/common/EmptyState';
import Badge from '../../components/common/Badge';
import Modal from '../../components/common/Modal';
import Button from '../../components/common/Button';
import { useToast } from '../../components/common/Toast';
import { getWorkRequestById, getWorkRequestApplications } from '../../api/client';
import api from '../../api/axios';
import { formatCurrency } from '../../utils/formatCurrency';
import { formatDate, timeAgo } from '../../utils/formatDate';

const ViewApplicants = () => {
const { id } = useParams();
  const navigate = useNavigate();
  const toast = useToast();

  const [job, setJob] = useState(null);
  const [applications, setApplications] = useState([]);
  const [loading, setLoading] = useState(true);
  const [confirmModal, setConfirmModal] = useState({ open: false, application: null, action: '' });
  const [actionLoading, setActionLoading] = useState(false);

  useEffect(() => {
    fetchData();
  }, [id]);

  const fetchData = async () => {
    setLoading(true);
    try {
      const [jobRes, appsRes] = await Promise.all([
        getWorkRequestById(id),
        getWorkRequestApplications(id),
      ]);
      setJob(jobRes.data.data);
      setApplications(appsRes.data.data || []);
    } catch {
      toast.error('Failed to load job details');
    } finally {
      setLoading(false);
    }
  };

  const handleAction = async () => {
    const { application, action } = confirmModal;
    if (!application) return;

    setActionLoading(true);
    try {
      const appId = application.applicationId || application.id;
      if (action === 'accept') {
        await api.patch(`/applications/${appId}/accept`);
        toast.success(`Application from ${application.workerName || 'worker'} accepted!`);
      } else {
        await api.patch(`/applications/${appId}/reject`);
        toast.success(`Application from ${application.workerName || 'worker'} rejected.`);
      }
      setConfirmModal({ open: false, application: null, action: '' });
      fetchData();
    } catch (error) {
      toast.error(error.response?.data?.message || `Failed to ${action} application`);
    } finally {
      setActionLoading(false);
    }
  };

  const renderStars = (rating) => {
    if (!rating) return <span className="text-slate-500 text-sm">No rating</span>;
    const stars = [];
    const fullStars = Math.floor(rating);
    for (let i = 0; i < 5; i++) {
      stars.push(
        <span key={i} className={i < fullStars ? 'text-amber-400 drop-shadow-sm' : 'text-slate-600'}>
          ★
        </span>
      );
    }
    return (
      <span className="flex items-center gap-0.5 text-sm">
        {stars}
        <span className="text-xs text-slate-400 ml-1">({Number(rating).toFixed(1)})</span>
      </span>
    );
  };

  if (loading) {
    return (
      <DashboardLayout pageTitle="View Applicants">
        <LoadingSpinner text="Loading applicants..." />
      </DashboardLayout>
    );
  }

  return (
    <DashboardLayout pageTitle="View Applicants">
      <button
        onClick={() => navigate('/client/my-jobs')}
        className="flex items-center gap-2 text-sm text-slate-400 hover:text-amber-400 mb-6 transition-colors cursor-pointer group"
      >
        <svg className="h-4 w-4 transform group-hover:-translate-x-1 transition-transform" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
          <path strokeLinecap="round" strokeLinejoin="round" d="M10 19l-7-7m0 0l7-7m-7 7h18" />
        </svg>
        Back to My Jobs
      </button>

      {job && (
        <div className="bg-slate-800/80 backdrop-blur-xl rounded-xl shadow-lg border border-slate-700/80 p-6 mb-8 transition-all duration-300 hover:shadow-amber-500/10 hover:border-slate-600/50">
          <div className="flex flex-col sm:flex-row sm:items-start justify-between gap-3 mb-6">
            <div>
              <h2 className="text-xl font-bold text-white drop-shadow-md">{job.title}</h2>
              <p className="text-sm text-slate-400 mt-1 line-clamp-2">{job.description}</p>
            </div>
            <Badge status={job.status} type="workRequest" />
          </div>

          <div className="grid grid-cols-2 sm:grid-cols-4 gap-4 text-sm bg-slate-900/40 rounded-lg p-4">
            <div>
              <p className="text-xs text-slate-500">Wage/Day</p>
              <p className="text-amber-400 font-semibold drop-shadow-sm">{formatCurrency(job.offeredWagePerDay)}</p>
            </div>
            <div>
              <p className="text-xs text-slate-500">Workers Needed</p>
              <p className="text-white">{job.workersNeeded || 0}</p>
            </div>
            <div>
              <p className="text-xs text-slate-500">Period</p>
              <p className="text-white">{formatDate(job.startDate)} - {formatDate(job.endDate)}</p>
            </div>
            <div>
              <p className="text-xs text-slate-500">Applications</p>
              <p className="text-white">{applications.length}</p>
            </div>
          </div>
        </div>
      )}

      <h3 className="text-lg font-semibold text-white mb-4">
        Applicants ({applications.length})
      </h3>

      {applications.length === 0 ? (
        <div className="bg-slate-800/80 backdrop-blur-xl rounded-xl border border-slate-700/80 shadow-xl p-8">
          <EmptyState
            icon="👷"
            title="No applicants yet"
            description="Workers will appear here once they apply to your job."
          />
        </div>
      ) : (
        <div className="space-y-4">
          {applications.map((app) => (
            <div
              key={app.applicationId || app.id}
              className="bg-slate-800/80 backdrop-blur-xl rounded-xl shadow-lg border border-slate-700/80 p-5 hover:border-amber-500/50 hover:shadow-amber-500/10 transition-all duration-300"
            >
              <div className="flex flex-col sm:flex-row gap-4">
                <div className="flex items-start gap-4 flex-1 min-w-0">
                  <div className="h-14 w-14 rounded-full bg-slate-700/80 border border-slate-600/50 flex items-center justify-center text-amber-400 font-bold text-xl flex-shrink-0 shadow-inner">
                    {(app.workerName || 'W').charAt(0).toUpperCase()}
                  </div>
                  <div className="flex-1 min-w-0">
                    <div className="flex items-center gap-2 flex-wrap">
                      <h4 className="text-white font-semibold text-lg">{app.workerName || 'Worker'}</h4>
                      <Badge status={app.status} type="application" />
                    </div>

                    <div className="flex items-center gap-3 mt-1.5 flex-wrap">
                      {renderStars(app.workerRating || app.rating)}
                      {(app.workerExperience || app.experienceYears) && (
                        <span className="text-xs text-slate-400 bg-slate-700/30 px-2 py-0.5 rounded-md">
                          {app.workerExperience || app.experienceYears} yrs exp
                        </span>
                      )}
                    </div>

                    <div className="mt-3 flex items-center gap-4 text-sm bg-slate-900/30 p-2 rounded-lg inline-block">
                      <div>
                        <span className="text-slate-400">Proposed: </span>
                        <span className="text-amber-400 font-semibold drop-shadow-sm">
                          {formatCurrency(app.proposedWagePerDay)}/day
                        </span>
                      </div>
                    </div>

                    {app.coverLetter && (
                      <div className="mt-3 bg-slate-700/20 border-l-2 border-amber-500/50 pl-3 py-1 text-sm text-slate-300 italic">
                        &ldquo;{app.coverLetter}&rdquo;
                      </div>
                    )}

                    {(app.workerSkills || app.skills) && (
                      <div className="flex flex-wrap gap-1.5 mt-3">
                        {(app.workerSkills || app.skills || []).map((skill) => (
                          <span key={skill} className="bg-slate-700/50 backdrop-blur-sm text-slate-300 text-xs px-2.5 py-1 rounded-md border border-slate-600/50 shadow-sm">
                            {skill}
                          </span>
                        ))}
                      </div>
                    )}

                    <p className="text-xs text-slate-500 mt-3">
                      Applied {timeAgo(app.appliedAt || app.createdAt)}
                    </p>
                  </div>
                </div>

                {app.status === 'PENDING' && (
                  <div className="flex sm:flex-col gap-2 sm:items-end justify-center">
                    <Button
                      variant="primary"
                      size="sm"
                      onClick={() => setConfirmModal({ open: true, application: app, action: 'accept' })}
                      className="bg-green-500/10 text-green-400 hover:bg-green-500/20 border-green-500/50 w-full sm:w-auto transition-colors"
                    >
                      ✓ Accept
                    </Button>
                    <Button
                      variant="danger"
                      size="sm"
                      onClick={() => setConfirmModal({ open: true, application: app, action: 'reject' })}
                      className="bg-red-500/10 text-red-400 hover:bg-red-500/20 border-red-500/50 w-full sm:w-auto transition-colors"
                    >
                      ✕ Reject
                    </Button>
                  </div>
                )}
              </div>
            </div>
          ))}
        </div>
      )}

      <Modal
        isOpen={confirmModal.open}
        onClose={() => setConfirmModal({ open: false, application: null, action: '' })}
        title={confirmModal.action === 'accept' ? 'Accept Application' : 'Reject Application'}
      >
        <div className="space-y-4">
          <p className="text-slate-300">
            Are you sure you want to{' '}
            <span className={confirmModal.action === 'accept' ? 'text-green-400 font-semibold' : 'text-red-400 font-semibold'}>
              {confirmModal.action}
            </span>{' '}
            the application from{' '}
            <span className="text-white font-semibold">
              {confirmModal.application?.workerName || 'this worker'}
            </span>
            ?
          </p>
          {confirmModal.action === 'accept' && (
            <p className="text-sm text-slate-400">
              The worker will be assigned to this job at their proposed wage of{' '}
              <span className="text-amber-400 font-medium">
                {formatCurrency(confirmModal.application?.proposedWagePerDay)}/day
              </span>.
            </p>
          )}
          <div className="flex justify-end gap-3 pt-2">
            <Button
              variant="secondary"
              onClick={() => setConfirmModal({ open: false, application: null, action: '' })}
              className="hover:bg-slate-700"
            >
              Cancel
            </Button>
            <Button
              variant={confirmModal.action === 'accept' ? 'primary' : 'danger'}
              loading={actionLoading}
              onClick={handleAction}
              className={confirmModal.action === 'accept' ? 'bg-green-500 hover:bg-green-600 text-white border-0' : 'bg-red-500 hover:bg-red-600 text-white border-0'}
            >
              {confirmModal.action === 'accept' ? 'Accept' : 'Reject'}
            </Button>
          </div>
        </div>
      </Modal>
    </DashboardLayout>
  );
};

export default ViewApplicants;
