import { useState, useEffect } from 'react';
import DashboardLayout from '../../components/common/DashboardLayout';
import LoadingSpinner from '../../components/common/LoadingSpinner';
import EmptyState from '../../components/common/EmptyState';
import Badge from '../../components/common/Badge';
import Modal from '../../components/common/Modal';
import Button from '../../components/common/Button';
import { useToast } from '../../components/common/Toast';
import { getWorkerApplications } from '../../api/worker';
import api from '../../api/axios';
import { formatCurrency } from '../../utils/formatCurrency';
import { formatDate, timeAgo } from '../../utils/formatDate';

const TABS = [
  { key: '', label: 'All' },
  { key: 'PENDING', label: 'Pending' },
  { key: 'ACCEPTED', label: 'Accepted' },
  { key: 'REJECTED', label: 'Rejected' },
  { key: 'WITHDRAWN', label: 'Withdrawn' },
];

const MyApplications = () => {
  const [applications, setApplications] = useState([]);
  const [loading, setLoading] = useState(true);
  const [activeTab, setActiveTab] = useState('');
  const [detailModal, setDetailModal] = useState({ open: false, application: null });
  const [withdrawModal, setWithdrawModal] = useState({ open: false, application: null });
  const [withdrawing, setWithdrawing] = useState(false);
  const toast = useToast();

  useEffect(() => {
    fetchApplications();
  }, [activeTab]);

  const fetchApplications = async () => {
    setLoading(true);
    try {
      const response = await getWorkerApplications(activeTab || undefined);
      setApplications(response.data.data || []);
    } catch {
      toast.error('Failed to load applications');
    } finally {
      setLoading(false);
    }
  };

  const handleWithdraw = async () => {
    const app = withdrawModal.application;
    if (!app) return;

    setWithdrawing(true);
    try {
      await api.patch(`/applications/${app.id}/withdraw`);
      toast.success('Application withdrawn successfully');
      setWithdrawModal({ open: false, application: null });
      fetchApplications();
    } catch (error) {
      toast.error(error.response?.data?.message || 'Failed to withdraw application');
    } finally {
      setWithdrawing(false);
    }
  };

  return (
    <DashboardLayout pageTitle="My Applications">
      {/* Tab Bar */}
      <div className="bg-slate-800/50 rounded-lg p-1 inline-flex gap-1 mb-6 flex-wrap">
        {TABS.map((tab) => (
          <button
            key={tab.key}
            onClick={() => setActiveTab(tab.key)}
            className={`px-4 py-2 rounded-md text-sm font-medium transition-all duration-200 cursor-pointer ${
              activeTab === tab.key
                ? 'bg-amber-500 text-black'
                : 'text-slate-400 hover:text-white hover:bg-slate-700'
            }`}
          >
            {tab.label}
          </button>
        ))}
      </div>

      {/* Applications List */}
      {loading ? (
        <LoadingSpinner text="Loading applications..." />
      ) : applications.length === 0 ? (
        <EmptyState
          icon="📋"
          title="No applications found"
          description={activeTab ? `No ${activeTab.toLowerCase()} applications.` : 'You haven\'t applied to any jobs yet.'}
        />
      ) : (
        <div className="space-y-4">
          {applications.map((app) => (
            <div
              key={app.applicationId || app.id}
              className="bg-slate-800 rounded-xl shadow-lg border border-slate-700 p-5 hover:border-slate-600 transition-all duration-200"
            >
              <div className="flex flex-col sm:flex-row sm:items-start justify-between gap-3">
                <div
                  className="flex-1 min-w-0 cursor-pointer"
                  onClick={() => setDetailModal({ open: true, application: app })}
                >
                  <h3 className="text-white font-semibold truncate hover:text-amber-400 transition-colors">
                    {app.jobTitle || app.workRequestTitle || 'Job Application'}
                  </h3>
                  <p className="text-sm text-slate-400 mt-1">
                    Client: {app.clientName || '—'}
                  </p>
                </div>
                <Badge status={app.status} type="application" />
              </div>

              <div className="grid grid-cols-2 sm:grid-cols-3 gap-3 mt-4">
                <div>
                  <p className="text-xs text-slate-500">Proposed Wage</p>
                  <p className="text-sm text-amber-400 font-semibold">
                    {formatCurrency(app.proposedWagePerDay)}/day
                  </p>
                </div>
                <div>
                  <p className="text-xs text-slate-500">Applied</p>
                  <p className="text-sm text-white">{formatDate(app.appliedAt || app.createdAt)}</p>
                </div>
                {app.jobWage || app.offeredWage ? (
                  <div>
                    <p className="text-xs text-slate-500">Offered Wage</p>
                    <p className="text-sm text-white">{formatCurrency(app.jobWage || app.offeredWage)}/day</p>
                  </div>
                ) : null}
              </div>

              {app.coverLetter && (
                <p className="text-sm text-slate-400 mt-3 line-clamp-2 italic">&ldquo;{app.coverLetter}&rdquo;</p>
              )}

              <div className="flex items-center justify-between mt-4">
                <span className="text-xs text-slate-500">{timeAgo(app.appliedAt || app.createdAt)}</span>
                <div className="flex gap-2">
                  <Button
                    variant="ghost"
                    size="sm"
                    onClick={() => setDetailModal({ open: true, application: app })}
                  >
                    View Details
                  </Button>
                  {app.status === 'PENDING' && (
                    <Button
                      variant="danger"
                      size="sm"
                      onClick={() => setWithdrawModal({ open: true, application: app })}
                    >
                      Withdraw
                    </Button>
                  )}
                </div>
              </div>
            </div>
          ))}
        </div>
      )}

      {/* Detail Modal */}
      <Modal
        isOpen={detailModal.open}
        onClose={() => setDetailModal({ open: false, application: null })}
        title="Application Details"
        size="lg"
      >
        {detailModal.application && (
          <ApplicationDetail application={detailModal.application} />
        )}
      </Modal>

      {/* Withdraw Confirmation Modal */}
      <Modal
        isOpen={withdrawModal.open}
        onClose={() => setWithdrawModal({ open: false, application: null })}
        title="Withdraw Application"
      >
        <div className="space-y-4">
          <p className="text-slate-300">
            Are you sure you want to withdraw your application for{' '}
            <span className="text-white font-semibold">
              {withdrawModal.application?.jobTitle || withdrawModal.application?.workRequestTitle}
            </span>
            ?
          </p>
          <p className="text-sm text-slate-400">This action cannot be undone.</p>
          <div className="flex justify-end gap-3 pt-2">
            <Button
              variant="secondary"
              onClick={() => setWithdrawModal({ open: false, application: null })}
            >
              Cancel
            </Button>
            <Button
              variant="danger"
              loading={withdrawing}
              onClick={handleWithdraw}
            >
              Withdraw Application
            </Button>
          </div>
        </div>
      </Modal>
    </DashboardLayout>
  );
};

const ApplicationDetail = ({ application }) => {
  const app = application;
  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between">
        <h3 className="text-white font-semibold text-lg">{app.jobTitle || app.workRequestTitle}</h3>
        <Badge status={app.status} type="application" />
      </div>

      <div className="grid grid-cols-2 gap-4 bg-slate-700/50 rounded-lg p-4">
        <div>
          <p className="text-xs text-slate-400">Client</p>
          <p className="text-sm text-white">{app.clientName || '—'}</p>
        </div>
        <div>
          <p className="text-xs text-slate-400">Location</p>
          <p className="text-sm text-white">{app.location || app.pincode || '—'}</p>
        </div>
        <div>
          <p className="text-xs text-slate-400">Offered Wage</p>
          <p className="text-sm text-white">{formatCurrency(app.jobWage || app.offeredWage || 0)}/day</p>
        </div>
        <div>
          <p className="text-xs text-slate-400">Your Proposed Wage</p>
          <p className="text-sm text-amber-400 font-semibold">{formatCurrency(app.proposedWagePerDay || 0)}/day</p>
        </div>
        <div>
          <p className="text-xs text-slate-400">Duration</p>
          <p className="text-sm text-white">{app.durationDays || '—'} days</p>
        </div>
        <div>
          <p className="text-xs text-slate-400">Applied On</p>
          <p className="text-sm text-white">{formatDate(app.appliedAt || app.createdAt)}</p>
        </div>
      </div>

      {app.coverLetter && (
        <div>
          <p className="text-xs text-slate-400 mb-1">Cover Letter</p>
          <p className="text-sm text-slate-300 bg-slate-700/50 rounded-lg p-3">{app.coverLetter}</p>
        </div>
      )}

      {app.jobDescription && (
        <div>
          <p className="text-xs text-slate-400 mb-1">Job Description</p>
          <p className="text-sm text-slate-300">{app.jobDescription}</p>
        </div>
      )}

      {(app.skills || app.skillsRequired) && (
        <div>
          <p className="text-xs text-slate-400 mb-1">Skills Required</p>
          <div className="flex flex-wrap gap-1.5">
            {(app.skills || app.skillsRequired || []).map((skill) => (
              <span key={skill} className="bg-slate-700 text-slate-300 text-xs px-2 py-0.5 rounded-md">{skill}</span>
            ))}
          </div>
        </div>
      )}
    </div>
  );
};

export default MyApplications;
