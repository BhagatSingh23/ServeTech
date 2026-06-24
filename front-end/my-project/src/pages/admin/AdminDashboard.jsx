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
      <div className="space-y-8 animate-[fadeIn_0.4s_ease-in]">
        
        {/* Welcome Section */}
        <div className="bg-gradient-to-r from-slate-800 to-slate-800/40 rounded-2xl p-6 md:p-8 shadow-2xl border border-slate-700/50 backdrop-blur-md relative overflow-hidden">
          <div className="absolute top-0 right-0 w-64 h-64 bg-amber-500/10 rounded-full blur-3xl -translate-y-1/2 translate-x-1/3"></div>
          <div className="relative z-10 flex flex-col md:flex-row justify-between items-start md:items-center gap-6">
            <div>
              <h1 className="text-3xl font-bold text-white mb-2">
                Welcome back, <span className="text-transparent bg-clip-text bg-gradient-to-r from-amber-400 to-amber-200">Admin</span>
              </h1>
              <p className="text-slate-400">Here is what's happening across the ServeTech platform today.</p>
            </div>
            <div className="flex gap-4">
               <button
                onClick={() => navigate('/admin/users')}
                className="px-6 py-3 bg-slate-700/50 hover:bg-slate-700 border border-slate-600 text-white rounded-xl transition-all shadow-lg hover:-translate-y-1"
              >
                Manage Users
              </button>
            </div>
          </div>
        </div>

        {/* Stats Grid */}
        <div>
          <h2 className="text-xl font-bold text-white mb-4 flex items-center gap-2">
            <span className="text-2xl">📊</span> Platform Overview
          </h2>
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-5">
            <StatsCard icon="👥" label="Total Users" value={stats.totalUsers || 0} color="blue" />
            <StatsCard icon="🔧" label="Workers" value={stats.totalWorkers || 0} color="amber" />
            <StatsCard icon="💼" label="Clients" value={stats.totalClients || 0} color="green" />
            <StatsCard icon="📑" label="Assignments" value={stats.totalAssignments || 0} color="blue" />
            <StatsCard icon="📝" label="Work Requests" value={stats.totalWorkRequests || 0} color="amber" />
            <StatsCard icon="💰" label="Revenue" value={formatCurrency(stats.totalRevenue || 0)} color="green" />
          </div>
        </div>

        {/* Quick Actions */}
        <div>
          <h2 className="text-xl font-bold text-white mb-4 flex items-center gap-2">
            <span className="text-2xl">⚡️</span> Quick Actions
          </h2>
          <div className="grid grid-cols-1 sm:grid-cols-3 gap-5">
            <button
              onClick={() => navigate('/admin/users')}
              className="bg-slate-800/80 backdrop-blur-sm rounded-2xl shadow-xl border border-slate-700/80 p-6 hover:border-blue-500/50 hover:bg-slate-800 transition-all duration-300 cursor-pointer text-left group"
            >
              <div className="flex items-start gap-4">
                <div className="h-14 w-14 rounded-2xl bg-blue-500/10 flex items-center justify-center text-blue-400 text-2xl group-hover:scale-110 transition-transform shadow-inner border border-blue-500/20">
                  👥
                </div>
                <div className="flex-1 mt-1">
                  <p className="text-white font-bold text-lg mb-1 group-hover:text-blue-400 transition-colors">Manage Users</p>
                  <p className="text-sm text-slate-400">View, edit, and moderate all platform users</p>
                </div>
              </div>
            </button>
            <button
              onClick={() => navigate('/admin/complaints')}
              className="bg-slate-800/80 backdrop-blur-sm rounded-2xl shadow-xl border border-slate-700/80 p-6 hover:border-red-500/50 hover:bg-slate-800 transition-all duration-300 cursor-pointer text-left group"
            >
              <div className="flex items-start gap-4">
                <div className="h-14 w-14 rounded-2xl bg-red-500/10 flex items-center justify-center text-red-400 text-2xl group-hover:scale-110 transition-transform shadow-inner border border-red-500/20">
                  📢
                </div>
                <div className="flex-1 mt-1">
                  <p className="text-white font-bold text-lg mb-1 group-hover:text-red-400 transition-colors">Complaints</p>
                  <p className="text-sm text-slate-400">Handle user disputes and active complaints</p>
                </div>
              </div>
            </button>
            <div className="bg-slate-800/80 backdrop-blur-sm rounded-2xl shadow-xl border border-slate-700/80 p-6">
              <div className="flex items-start gap-4">
                <div className="h-14 w-14 rounded-2xl bg-amber-500/10 flex items-center justify-center text-amber-400 text-2xl shadow-inner border border-amber-500/20">
                  ✅
                </div>
                <div className="flex-1 mt-1">
                  <p className="text-white font-bold text-lg mb-1">Verifications</p>
                  <p className="text-sm text-slate-400"><span className="text-amber-400 font-bold">{pendingVerifications.length}</span> workers currently awaiting approval</p>
                </div>
              </div>
            </div>
          </div>
        </div>

        {/* Pending Verifications Table */}
        <section>
          <h2 className="text-xl font-bold text-white mb-4 flex items-center gap-2">
            <span className="text-2xl">📋</span> Action Required: Verifications
          </h2>
          {pendingVerifications.length === 0 ? (
            <div className="bg-slate-800/50 backdrop-blur-sm rounded-2xl p-10 border border-slate-700 shadow-xl text-center">
              <div className="h-20 w-20 mx-auto rounded-full bg-green-500/10 flex items-center justify-center mb-4">
                 <span className="text-4xl">🎉</span>
              </div>
              <h3 className="text-xl font-bold text-white mb-2">All Caught Up!</h3>
              <p className="text-slate-400">There are no pending worker verifications at the moment.</p>
            </div>
          ) : (
            <div className="bg-slate-800/80 backdrop-blur-sm rounded-2xl shadow-xl border border-slate-700 overflow-hidden">
              <div className="overflow-x-auto">
                <table className="w-full">
                  <thead>
                    <tr className="bg-slate-900/50 border-b border-slate-700">
                      <th className="text-left px-6 py-4 text-slate-400 text-xs tracking-wider uppercase font-bold">Worker Profile</th>
                      <th className="text-left px-6 py-4 text-slate-400 text-xs tracking-wider uppercase font-bold">Contact</th>
                      <th className="text-left px-6 py-4 text-slate-400 text-xs tracking-wider uppercase font-bold">Skills Offered</th>
                      <th className="text-left px-6 py-4 text-slate-400 text-xs tracking-wider uppercase font-bold">Registration Date</th>
                      <th className="text-left px-6 py-4 text-slate-400 text-xs tracking-wider uppercase font-bold">Actions</th>
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-slate-700/50">
                    {pendingVerifications.map((worker) => {
                      const workerId = worker.userId || worker.id;
                      return (
                        <tr key={workerId} className="hover:bg-slate-700/30 transition-colors">
                          <td className="px-6 py-5">
                            <div className="flex items-center gap-4">
                              <div className="h-10 w-10 rounded-xl bg-gradient-to-tr from-amber-500 to-amber-300 flex items-center justify-center text-slate-900 text-lg font-bold shadow-lg">
                                {(worker.name || worker.firstName || 'W').charAt(0).toUpperCase()}
                              </div>
                              <div>
                                <p className="text-white font-semibold">
                                  {worker.name || `${worker.firstName || ''} ${worker.lastName || ''}`.trim()}
                                </p>
                                <p className="text-xs text-slate-400 mt-1">ID: #{workerId}</p>
                              </div>
                            </div>
                          </td>
                          <td className="px-6 py-5">
                            <div className="flex items-center gap-2 text-sm text-slate-300">
                              <span className="text-slate-500">📞</span> {worker.phone || '—'}
                            </div>
                          </td>
                          <td className="px-6 py-5">
                            <div className="flex flex-wrap gap-2">
                              {(worker.skills || []).slice(0, 3).map((skill) => (
                                <span key={skill} className="bg-slate-900/80 border border-slate-600 text-slate-300 text-xs px-2.5 py-1 rounded-lg font-medium">
                                  {skill}
                                </span>
                              ))}
                              {(worker.skills || []).length > 3 && (
                                <span className="bg-slate-900/80 border border-slate-600 text-slate-400 text-xs px-2.5 py-1 rounded-lg font-medium">
                                  +{worker.skills.length - 3}
                                </span>
                              )}
                            </div>
                          </td>
                          <td className="px-6 py-5 text-sm text-slate-400 font-medium">
                            {formatDate(worker.createdAt || worker.joinedAt)}
                          </td>
                          <td className="px-6 py-5">
                            <div className="flex gap-3">
                              <button
                                onClick={() => handleVerify(workerId)}
                                disabled={actionLoading[workerId]}
                                className="px-4 py-2 bg-green-500/10 text-green-400 border border-green-500/20 hover:bg-green-500/20 hover:border-green-500/40 rounded-lg text-sm font-semibold transition-all disabled:opacity-50"
                              >
                                {actionLoading[workerId] === 'verify' ? 'Verifying...' : 'Approve'}
                              </button>
                              <button
                                onClick={() => handleReject(workerId)}
                                disabled={actionLoading[workerId]}
                                className="px-4 py-2 bg-red-500/10 text-red-400 border border-red-500/20 hover:bg-red-500/20 hover:border-red-500/40 rounded-lg text-sm font-semibold transition-all disabled:opacity-50"
                              >
                                {actionLoading[workerId] === 'reject' ? 'Rejecting...' : 'Reject'}
                              </button>
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

      </div>
    </DashboardLayout>
  );
};

export default AdminDashboard;
