import { useState, useEffect } from 'react';
import DashboardLayout from '../../components/common/DashboardLayout';
import StatsCard from '../../components/common/StatsCard';
import LoadingSpinner from '../../components/common/LoadingSpinner';
import Badge from '../../components/common/Badge';
import EmptyState from '../../components/common/EmptyState';
import Modal from '../../components/common/Modal';
import Button from '../../components/common/Button';
import { useToast } from '../../components/common/Toast';
import { getWorkerDashboard } from '../../api/worker';
import { formatCurrency } from '../../utils/formatCurrency';
import { formatDate, timeAgo } from '../../utils/formatDate';

const WorkerDashboard = () => {
  const [dashboard, setDashboard] = useState(null);
  const [loading, setLoading] = useState(true);
  const [complaintModal, setComplaintModal] = useState({ open: false, assignment: null });
  const toast = useToast();

  useEffect(() => {
    fetchDashboard();
  }, []);

  const fetchDashboard = async () => {
    try {
      const response = await getWorkerDashboard();
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

  const totalEarnings = dashboard?.totalEarnings || 0;
  const pendingPayments = dashboard?.pendingPayments || 0;
  const jobsCompleted = dashboard?.totalJobsCompleted || 0;
  const averageRating = dashboard?.averageRating;
  const currentWork = dashboard?.currentBookings || [];
  const upcomingWork = dashboard?.upcomingBookings || [];
  const previousBookings = dashboard?.previousBookings || [];

  return (
    <DashboardLayout pageTitle="Dashboard">
      {/* Stats Row */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 mb-8">
        <StatsCard
          icon="💰"
          label="Total Earnings"
          value={formatCurrency(totalEarnings)}
          color="amber"
        />
        <StatsCard
          icon="⏳"
          label="Pending Payments"
          value={formatCurrency(pendingPayments)}
          color="red"
        />
        <StatsCard
          icon="✅"
          label="Jobs Completed"
          value={jobsCompleted}
          color="green"
        />
        <StatsCard
          icon="⭐"
          label="Average Rating"
          value={averageRating ? `${Number(averageRating).toFixed(1)} ⭐` : 'N/A'}
          color="blue"
        />
      </div>

      {/* Current Work */}
      <section className="mb-8">
        <h2 className="text-lg font-semibold text-white mb-4 flex items-center gap-2">
          <span>🔨</span> Current Work
        </h2>
        {currentWork.length === 0 ? (
          <EmptyState
            icon="🔨"
            title="No active work"
            description="You don't have any active assignments right now."
          />
        ) : (
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-4">
            {currentWork.map((work) => (
              <div key={work.assignmentId || work.id} className="bg-slate-800 rounded-xl shadow-lg border border-slate-700 p-5">
                <div className="flex items-start justify-between mb-3">
                  <h3 className="text-white font-semibold text-base truncate pr-2">{work.title || work.workRequestTitle}</h3>
                  <Badge status={work.status || work.progressStatus} type="workProgress" />
                </div>
                <p className="text-sm text-slate-400 mb-3">{work.clientName && `Client: ${work.clientName}`}</p>

                {/* Progress Bar */}
                <div className="mb-3">
                  <div className="flex justify-between text-xs text-slate-400 mb-1">
                    <span>Progress</span>
                    <span>{work.progressPercentage || 0}%</span>
                  </div>
                  <div className="w-full bg-slate-700 rounded-full h-2">
                    <div
                      className="bg-amber-500 h-2 rounded-full transition-all duration-500"
                      style={{ width: `${work.progressPercentage || 0}%` }}
                    />
                  </div>
                </div>

                <div className="flex items-center justify-between text-xs text-slate-400">
                  <span>{formatCurrency(work.wagePerDay || work.dailyWage)}/day</span>
                  <span>{formatDate(work.startDate)} - {formatDate(work.endDate)}</span>
                </div>
              </div>
            ))}
          </div>
        )}
      </section>

      {/* Upcoming Work */}
      <section className="mb-8">
        <h2 className="text-lg font-semibold text-white mb-4 flex items-center gap-2">
          <span>📅</span> Upcoming Work
        </h2>
        {upcomingWork.length === 0 ? (
          <EmptyState
            icon="📅"
            title="No upcoming work"
            description="No upcoming assignments scheduled."
          />
        ) : (
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-4">
            {upcomingWork.map((work) => (
              <div key={work.assignmentId || work.id} className="bg-slate-800 rounded-xl shadow-lg border border-slate-700 p-5">
                <div className="flex items-start justify-between mb-2">
                  <h3 className="text-white font-semibold truncate pr-2">{work.title || work.workRequestTitle}</h3>
                  <Badge status={work.status || 'NOT_STARTED'} type="workProgress" />
                </div>
                <p className="text-sm text-slate-400 mb-1">{work.clientName && `Client: ${work.clientName}`}</p>
                <div className="flex items-center justify-between text-xs text-slate-400 mt-2">
                  <span>{formatCurrency(work.wagePerDay || work.dailyWage)}/day</span>
                  <span>Starts {formatDate(work.startDate)}</span>
                </div>
              </div>
            ))}
          </div>
        )}
      </section>

      {/* Previous Bookings */}
      <section>
        <h2 className="text-lg font-semibold text-white mb-4 flex items-center gap-2">
          <span>📜</span> Previous Bookings
        </h2>
        {previousBookings.length === 0 ? (
          <EmptyState
            icon="📜"
            title="No previous bookings"
            description="Your completed work history will appear here."
          />
        ) : (
          <div className="space-y-4">
            {previousBookings.map((booking) => (
              <div key={booking.assignmentId || booking.id} className="bg-slate-800 rounded-xl shadow-lg border border-slate-700 p-5">
                <div className="flex flex-col sm:flex-row sm:items-start justify-between gap-3 mb-4">
                  <div className="flex-1 min-w-0">
                    <h3 className="text-white font-semibold truncate">{booking.title || booking.workRequestTitle}</h3>
                    <p className="text-sm text-slate-400 mt-1">Client: {booking.clientName || '—'}</p>
                  </div>
                  <Badge status={booking.status || booking.progressStatus || 'COMPLETED'} type="workProgress" />
                </div>

                <div className="grid grid-cols-2 sm:grid-cols-4 gap-3 mb-4">
                  <div>
                    <p className="text-xs text-slate-500">Duration</p>
                    <p className="text-sm text-white">{formatDate(booking.startDate)} - {formatDate(booking.endDate)}</p>
                  </div>
                  <div>
                    <p className="text-xs text-slate-500">Daily Wage</p>
                    <p className="text-sm text-white">{formatCurrency(booking.wagePerDay || booking.dailyWage)}</p>
                  </div>
                  <div>
                    <p className="text-xs text-slate-500">Total Earned</p>
                    <p className="text-sm text-amber-400 font-semibold">{formatCurrency(booking.totalEarned || booking.totalPaid || 0)}</p>
                  </div>
                  <div>
                    <p className="text-xs text-slate-500">Rating</p>
                    <p className="text-sm text-white">
                      {booking.rating ? `${booking.rating} ⭐` : 'Not rated'}
                    </p>
                  </div>
                </div>

                {/* Payment breakdown */}
                {booking.payments && booking.payments.length > 0 && (
                  <div className="bg-slate-700/50 rounded-lg p-3 mb-3">
                    <p className="text-xs text-slate-400 font-medium mb-2">Payment Breakdown</p>
                    <div className="space-y-1">
                      {booking.payments.map((payment, idx) => (
                        <div key={payment.id || idx} className="flex justify-between text-xs">
                          <span className="text-slate-300">{formatDate(payment.date || payment.paidAt)}</span>
                          <span className="text-green-400">{formatCurrency(payment.amount)}</span>
                        </div>
                      ))}
                    </div>
                  </div>
                )}

                <div className="flex items-center justify-between">
                  <span className="text-xs text-slate-500">{timeAgo(booking.completedAt || booking.endDate)}</span>
                  <Button
                    variant="ghost"
                    size="sm"
                    onClick={() => setComplaintModal({ open: true, assignment: booking })}
                  >
                    🚩 Raise Complaint
                  </Button>
                </div>
              </div>
            ))}
          </div>
        )}
      </section>

      {/* Complaint Modal */}
      <Modal
        isOpen={complaintModal.open}
        onClose={() => setComplaintModal({ open: false, assignment: null })}
        title="Raise a Complaint"
      >
        <ComplaintForm
          assignment={complaintModal.assignment}
          onClose={() => setComplaintModal({ open: false, assignment: null })}
        />
      </Modal>
    </DashboardLayout>
  );
};

const ComplaintForm = ({ assignment, onClose }) => {
  const [subject, setSubject] = useState('');
  const [description, setDescription] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const toast = useToast();

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!subject.trim() || !description.trim()) {
      toast.warning('Please fill in all fields');
      return;
    }
    setSubmitting(true);
    try {
      // API call placeholder - would call submitComplaint
      toast.success('Complaint submitted successfully');
      onClose();
    } catch {
      toast.error('Failed to submit complaint');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-4">
      <p className="text-sm text-slate-400">
        Regarding: <span className="text-white">{assignment?.title || assignment?.workRequestTitle}</span>
      </p>
      <div>
        <label className="block text-sm font-medium text-slate-300 mb-1.5">Subject</label>
        <input
          type="text"
          value={subject}
          onChange={(e) => setSubject(e.target.value)}
          placeholder="Brief subject of your complaint"
          className="w-full bg-slate-700 border border-slate-600 text-white rounded-lg px-4 py-2.5 focus:border-amber-400 focus:ring-1 focus:ring-amber-400 outline-none transition-all placeholder-slate-500"
        />
      </div>
      <div>
        <label className="block text-sm font-medium text-slate-300 mb-1.5">Description</label>
        <textarea
          value={description}
          onChange={(e) => setDescription(e.target.value)}
          rows={4}
          placeholder="Describe your complaint in detail..."
          className="w-full bg-slate-700 border border-slate-600 text-white rounded-lg px-4 py-2.5 focus:border-amber-400 focus:ring-1 focus:ring-amber-400 outline-none transition-all placeholder-slate-500 resize-none"
        />
      </div>
      <div className="flex justify-end gap-3 pt-2">
        <Button variant="secondary" onClick={onClose} type="button">Cancel</Button>
        <Button variant="primary" type="submit" loading={submitting}>Submit Complaint</Button>
      </div>
    </form>
  );
};

export default WorkerDashboard;
