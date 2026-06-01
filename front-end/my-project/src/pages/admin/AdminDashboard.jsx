import { useState, useEffect } from 'react';
import DashboardLayout from '../../components/common/DashboardLayout';
import StatsCard from '../../components/common/StatsCard';
import LoadingSpinner from '../../components/common/LoadingSpinner';
import EmptyState from '../../components/common/EmptyState';
import Badge from '../../components/common/Badge';
import Button from '../../components/common/Button';
import { useToast } from '../../components/common/Toast';
import { getAdminDashboard, verifyWorker, rejectVerification } from '../../api/admin';
import { formatCurrency } from '../../utils/formatCurrency';
import { formatDate } from '../../utils/formatDate';
import { useNavigate } from 'react-router-dom';

const AdminDashboard = () => {
  const [dashboard, setDashboard] = useState(null);
  const [loading, setLoading] = useState(true);
  const [actionLoading, setActionLoading] = useState({});
  const toast = useToast();
  const navigate = useNavigate();

  useEffect(() => {
    fetchDashboard();
  }, []);

  const fetchDashboard = async () => {
    try {
      const response = await getAdminDashboard();
      setDashboard(response.data.data);
    } catch {
      toast.error('Failed to load dashboard data');
    } finally {
      setLoading(false);
    }
  };

  const handleVerify = async (workerId) => {
    setActionLoading((prev) => ({ ...prev, [workerId]: 'verify' }));
    try {
      await verifyWorker(workerId);
      toast.success('Worker verified successfully');
      fetchDashboard();
    } catch (error) {
      toast.error(error.response?.data?.message || 'Failed to verify worker');
    } finally {
      setActionLoading((prev) => ({ ...prev, [workerId]: null }));
    }
  };

  const handleReject = async (workerId) => {
    setActionLoading((prev) => ({ ...prev, [workerId]: 'reject' }));
    try {
      await rejectVerification(workerId);
      toast.success('Verification rejected');
      fetchDashboard();
    } catch (error) {
      toast.error(error.response?.data?.message || 'Failed to reject verification');
    } finally {
      setActionLoading((prev) => ({ ...prev, [workerId]: null }));
    }
  };

  if (loading) {
    return (
      <DashboardLayout pageTitle="Admin Dashboard">
        <LoadingSpinner text="Loading dashboard..." />
      </DashboardLayout>
    );
  }

  const stats = dashboard || {};
  const pendingVerifications = Array.isArray(dashboard?.pendingVerificationsList) 
    ? dashboard.pendingVerificationsList 
    : [];

  return (
    <DashboardLayout pageTitle="Admin Dashboard">
      {/* Stats Grid */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-6 gap-4 mb-8">
        <StatsCard icon="👥" label="Total Users" value={stats.totalUsers || 0} color="blue" />
        <StatsCard icon="🔧" label="Workers" value={stats.totalWorkers || 0} color="amber" />
        <StatsCard icon="💼" label="Clients" value={stats.totalClients || 0} color="green" />
        <StatsCard icon="📑" label="Assignments" value={stats.totalAssignments || 0} color="blue" />
        <StatsCard icon="📝" label="Work Requests" value={stats.totalWorkRequests || 0} color="amber" />
        <StatsCard icon="💰" label="Revenue" value={formatCurrency(stats.totalRevenue || 0)} color="green" />
      </div>

      {/* Quick Actions */}
      <div className="grid grid-cols-1 sm:grid-cols-3 gap-4 mb-8">
        <button
          onClick={() => navigate('/admin/users')}
          className="bg-slate-800 rounded-xl shadow-lg border border-slate-700 p-5 hover:border-amber-500/50 hover:bg-slate-800/80 transition-all duration-200 cursor-pointer text-left"
        >
          <div className="flex items-center gap-3">
            <div className="h-10 w-10 rounded-lg bg-blue-500/10 flex items-center justify-center text-blue-400 text-xl">
              👥
            </div>
            <div>
              <p className="text-white font-semibold">Manage Users</p>
              <p className="text-xs text-slate-400">View and manage all users</p>
            </div>
          </div>
        </button>
        <button
          onClick={() => navigate('/admin/complaints')}
          className="bg-slate-800 rounded-xl shadow-lg border border-slate-700 p-5 hover:border-amber-500/50 hover:bg-slate-800/80 transition-all duration-200 cursor-pointer text-left"
        >
          <div className="flex items-center gap-3">
            <div className="h-10 w-10 rounded-lg bg-red-500/10 flex items-center justify-center text-red-400 text-xl">
              📢
            </div>
            <div>
              <p className="text-white font-semibold">Complaints</p>
              <p className="text-xs text-slate-400">Handle user complaints</p>
            </div>
          </div>
        </button>
        <div className="bg-slate-800 rounded-xl shadow-lg border border-slate-700 p-5">
          <div className="flex items-center gap-3">
            <div className="h-10 w-10 rounded-lg bg-amber-500/10 flex items-center justify-center text-amber-400 text-xl">
              ✅
            </div>
            <div>
              <p className="text-white font-semibold">Pending Verifications</p>
              <p className="text-xs text-slate-400">{pendingVerifications.length} workers awaiting</p>
            </div>
          </div>
        </div>
      </div>

      {/* Pending Verifications */}
      <section>
        <h2 className="text-lg font-semibold text-white mb-4 flex items-center gap-2">
          <span>✅</span> Pending Worker Verifications
        </h2>
        {pendingVerifications.length === 0 ? (
          <EmptyState
            icon="✅"
            title="No pending verifications"
            description="All worker verifications have been processed."
          />
        ) : (
          <div className="bg-slate-800 rounded-xl shadow-lg border border-slate-700 overflow-hidden">
            <div className="overflow-x-auto">
              <table className="w-full">
                <thead>
                  <tr className="border-b border-slate-700">
                    <th className="text-left px-5 py-3 text-slate-400 text-xs uppercase font-medium">Worker</th>
                    <th className="text-left px-5 py-3 text-slate-400 text-xs uppercase font-medium">Phone</th>
                    <th className="text-left px-5 py-3 text-slate-400 text-xs uppercase font-medium">Skills</th>
                    <th className="text-left px-5 py-3 text-slate-400 text-xs uppercase font-medium">Joined</th>
                    <th className="text-left px-5 py-3 text-slate-400 text-xs uppercase font-medium">Actions</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-700">
                  {pendingVerifications.map((worker) => {
                    const workerId = worker.userId || worker.id;
                    return (
                      <tr key={workerId} className="hover:bg-slate-700/50 transition-colors">
                        <td className="px-5 py-3">
                          <div className="flex items-center gap-3">
                            <div className="h-8 w-8 rounded-full bg-slate-700 flex items-center justify-center text-amber-400 text-xs font-semibold">
                              {(worker.name || worker.firstName || 'W').charAt(0)}
                            </div>
                            <p className="text-sm text-white font-medium">
                              {worker.name || `${worker.firstName || ''} ${worker.lastName || ''}`.trim()}
                            </p>
                          </div>
                        </td>
                        <td className="px-5 py-3 text-sm text-slate-300">{worker.phone || '—'}</td>
                        <td className="px-5 py-3">
                          <div className="flex flex-wrap gap-1">
                            {(worker.skills || []).slice(0, 3).map((skill) => (
                              <span key={skill} className="bg-slate-700 text-slate-300 text-xs px-2 py-0.5 rounded-md">
                                {skill}
                              </span>
                            ))}
                          </div>
                        </td>
                        <td className="px-5 py-3 text-sm text-slate-400">{formatDate(worker.createdAt || worker.joinedAt)}</td>
                        <td className="px-5 py-3">
                          <div className="flex gap-2">
                            <Button
                              variant="primary"
                              size="sm"
                              loading={actionLoading[workerId] === 'verify'}
                              onClick={() => handleVerify(workerId)}
                            >
                              Verify
                            </Button>
                            <Button
                              variant="danger"
                              size="sm"
                              loading={actionLoading[workerId] === 'reject'}
                              onClick={() => handleReject(workerId)}
                            >
                              Reject
                            </Button>
                          </div>
                        </td>
                      </tr>
                    );
                  })}
                </tbody>
              </table>
            </div>
          </div>
        )}
      </section>
    </DashboardLayout>
  );
};

export default AdminDashboard;
