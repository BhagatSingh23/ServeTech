import { useState, useEffect } from 'react';
import DashboardLayout from '../../components/common/DashboardLayout';
import LoadingSpinner from '../../components/common/LoadingSpinner';
import EmptyState from '../../components/common/EmptyState';
import Badge from '../../components/common/Badge';
import Modal from '../../components/common/Modal';
import Button from '../../components/common/Button';
import { useToast } from '../../components/common/Toast';
import { getAllUsers, updateUserStatus } from '../../api/admin';
import { formatDate } from '../../utils/formatDate';

const STATUS_FILTERS = [
  { value: '', label: 'All Status' },
  { value: 'ACTIVE', label: 'Active' },
  { value: 'SUSPENDED', label: 'Suspended' },
  { value: 'DELETED', label: 'Deleted' },
];

const ROLE_BADGES = {
  WORKER: 'bg-blue-500/20 text-blue-400 border border-blue-500/30',
  CLIENT: 'bg-green-500/20 text-green-400 border border-green-500/30',
  ADMIN: 'bg-red-500/20 text-red-400 border border-red-500/30',
};

const UserManagement = () => {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');
  const [roleFilter, setRoleFilter] = useState('');
  const [statusFilter, setStatusFilter] = useState('');
  const [page, setPage] = useState(0);
  const [hasMore, setHasMore] = useState(false);
  const [loadingMore, setLoadingMore] = useState(false);

  const [statusModal, setStatusModal] = useState({ open: false, user: null, newStatus: '' });
  const [actionLoading, setActionLoading] = useState(false);

  const toast = useToast();
const ROLE_FILTERS = [
    { value: '', label: 'All Roles' },
    { value: 'WORKER', label: 'Worker' },
    { value: 'CLIENT', label: 'Client' },
    { value: 'ADMIN', label: 'Admin' },
  ];

  useEffect(() => {
    setPage(0);
    fetchUsers(true);
  }, [roleFilter, statusFilter]);

  const fetchUsers = async (reset = false) => {
    if (reset) setLoading(true);
    else setLoadingMore(true);

    try {
      const currentPage = reset ? 0 : page;
      const response = await getAllUsers({
        search: search.trim() || undefined,
        role: roleFilter || undefined,
        status: statusFilter || undefined,
        page: currentPage,
        size: 20,
      });

      const data = response.data.data;
      const content = data.content || data || [];
      const filteredContent = !statusFilter 
        ? content.filter(u => (u.accountStatus || u.status) !== 'DELETED') 
        : content;

      if (reset) {
        setUsers(filteredContent);
      } else {
        setUsers((prev) => [...prev, ...filteredContent]);
      }

      setHasMore(data.totalPages ? currentPage + 1 < data.totalPages : content.length === 20);
      if (!reset) setPage(currentPage + 1);
    } catch {
      toast.error('Failed to load users');
    } finally {
      setLoading(false);
      setLoadingMore(false);
    }
  };

  const handleSearch = () => {
    setPage(0);
    fetchUsers(true);
  };

  const handleKeyDown = (e) => {
    if (e.key === 'Enter') handleSearch();
  };

  const handleStatusChange = async () => {
    const { user, newStatus } = statusModal;
    if (!user) return;

    setActionLoading(true);
    try {
      await updateUserStatus(user.userId || user.id, newStatus);
      toast.success(`User ${newStatus.toLowerCase()} successfully`);
      setStatusModal({ open: false, user: null, newStatus: '' });

      if (newStatus === 'DELETED' || newStatus === 'REJECTED') {
        setUsers((prev) => prev.filter((u) => (u.userId || u.id) !== (user.userId || user.id)));
      } else {
        fetchUsers(true);
      }
    } catch (error) {
      toast.error(error.response?.data?.message || 'Failed to update user status');
    } finally {
      setActionLoading(false);
    }
  };

  const loadMore = () => {
    setPage((prev) => prev + 1);
    fetchUsers(false);
  };

  return (
    <DashboardLayout pageTitle="User Management">
      {/* Filters */}
      <div className="bg-slate-800/80 backdrop-blur-xl rounded-xl shadow-lg border border-slate-700/80 p-5 mb-6 transition-all hover:border-slate-600/80">
        <div className="flex flex-col sm:flex-row gap-4">
          <div className="flex-1">
            <div className="relative">
              <svg className="absolute left-3 top-1/2 -translate-y-1/2 h-5 w-5 text-slate-400" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                <path strokeLinecap="round" strokeLinejoin="round" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
              </svg>
              <input
                type="text"
                value={search}
                onChange={(e) => setSearch(e.target.value)}
                onKeyDown={handleKeyDown}
                placeholder="Search by name or phone..."
                className="w-full bg-slate-900/50 border border-slate-600/50 text-white rounded-lg pl-10 pr-4 py-2.5 focus:border-amber-400/80 focus:ring-1 focus:ring-amber-400/80 outline-none transition-all placeholder-slate-500"
              />
            </div>
          </div>

          <select
            value={roleFilter}
            onChange={(e) => setRoleFilter(e.target.value)}
            className="bg-slate-900/50 border border-slate-600/50 text-white rounded-lg px-4 py-2.5 focus:border-amber-400/80 focus:ring-1 focus:ring-amber-400/80 outline-none transition-all"
          >
            {ROLE_FILTERS.map((opt) => (
              <option key={opt.value} value={opt.value}>{opt.label}</option>
            ))}
          </select>

          <select
            value={statusFilter}
            onChange={(e) => setStatusFilter(e.target.value)}
            className="bg-slate-900/50 border border-slate-600/50 text-white rounded-lg px-4 py-2.5 focus:border-amber-400/80 focus:ring-1 focus:ring-amber-400/80 outline-none transition-all"
          >
            {STATUS_FILTERS.map((opt) => (
              <option key={opt.value} value={opt.value}>{opt.label}</option>
            ))}
          </select>

          <Button 
            variant="primary" 
            onClick={handleSearch}
            className="bg-gradient-to-r from-amber-500 to-amber-400 text-black hover:from-amber-400 hover:to-amber-300 transition-all hover:scale-105 shadow-lg hover:shadow-amber-500/25 border-none"
          >
            Search
          </Button>
        </div>
      </div>

      {/* Users Table */}
      {loading ? (
        <LoadingSpinner text="Loading users..." />
      ) : users.length === 0 ? (
        <EmptyState
          icon="👥"
          title="No users found"
          description="Try adjusting your search or filters."
        />
      ) : (
        <>
          <div className="bg-slate-800/80 backdrop-blur-xl rounded-xl shadow-lg border border-slate-700/80 overflow-hidden">
            <div className="overflow-x-auto">
              <table className="w-full">
                <thead>
                  <tr className="border-b border-slate-700/80 bg-slate-900/20">
                    <th className="text-left px-5 py-4 text-slate-400 text-xs uppercase font-semibold tracking-wider">Name</th>
                    <th className="text-left px-5 py-4 text-slate-400 text-xs uppercase font-semibold tracking-wider">Phone</th>
                    <th className="text-left px-5 py-4 text-slate-400 text-xs uppercase font-semibold tracking-wider">Role</th>
                    <th className="text-left px-5 py-4 text-slate-400 text-xs uppercase font-semibold tracking-wider">Status</th>
                    <th className="text-left px-5 py-4 text-slate-400 text-xs uppercase font-semibold tracking-wider">Date</th>
                    <th className="text-left px-5 py-4 text-slate-400 text-xs uppercase font-semibold tracking-wider">Actions</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-700/50">
                  {users.map((user) => {
                    const userId = user.userId || user.id;
                    return (
                      <tr key={userId} className="hover:bg-slate-700/30 transition-colors group">
                        <td className="px-5 py-4">
                          <div className="flex items-center gap-3">
                            <div className="h-9 w-9 rounded-full bg-gradient-to-br from-slate-700 to-slate-800 border border-slate-600 flex items-center justify-center text-amber-400 text-sm font-bold shadow-inner group-hover:border-amber-500/50 transition-colors">
                              {(user.name || user.firstName || 'U').charAt(0)}
                            </div>
                            <p className="text-sm text-white font-medium group-hover:text-amber-400 transition-colors">
                              {user.name || `${user.firstName || ''} ${user.lastName || ''}`.trim() || '—'}
                            </p>
                          </div>
                        </td>
                        <td className="px-5 py-4 text-sm text-slate-300">{user.phone || '—'}</td>
                        <td className="px-5 py-4">
                          <span className={`inline-flex items-center px-2.5 py-1 rounded-full text-xs font-medium ${ROLE_BADGES[user.role] || 'bg-slate-500/20 text-slate-400'}`}>
                            {user.role === 'WORKER' ? ('Worker') : user.role === 'CLIENT' ? ('Client') : user.role === 'ADMIN' ? ('Admin') : user.role}
                          </span>
                        </td>
                        <td className="px-5 py-4">
                          <Badge status={user.accountStatus || user.status || 'ACTIVE'} type="account" />
                        </td>
                        <td className="px-5 py-4 text-sm text-slate-400">
                          {formatDate(user.createdAt || user.joinedAt)}
                        </td>
                        <td className="px-5 py-4">
                          <div className="flex gap-2">
                            {(user.accountStatus || user.status) !== 'ACTIVE' && (
                              <Button
                                variant="primary"
                                size="sm"
                                onClick={() => setStatusModal({ open: true, user, newStatus: 'ACTIVE' })}
                                className="bg-gradient-to-r from-green-500 to-emerald-400 text-black border-none hover:scale-105 transition-transform"
                              >
                                Activate
                              </Button>
                            )}
                            {(user.accountStatus || user.status) !== 'SUSPENDED' && (
                              <Button
                                variant="secondary"
                                size="sm"
                                onClick={() => setStatusModal({ open: true, user, newStatus: 'SUSPENDED' })}
                                className="bg-slate-700/50 hover:bg-slate-600 text-white border-slate-600 hover:border-slate-500 transition-all"
                              >
                                Suspend
                              </Button>
                            )}
                            {(user.accountStatus || user.status) !== 'DELETED' && (
                              <Button
                                variant="danger"
                                size="sm"
                                onClick={() => setStatusModal({ open: true, user, newStatus: 'DELETED' })}
                                className="bg-red-500/20 hover:bg-red-500/40 text-red-400 border border-red-500/30 transition-all"
                              >
                                Delete
                              </Button>
                            )}
                          </div>
                        </td>
                      </tr>
                    );
                  })}
                </tbody>
              </table>
            </div>
          </div>

          {hasMore && (
            <div className="flex justify-center mt-8">
              <Button 
                variant="secondary" 
                loading={loadingMore} 
                onClick={loadMore}
                className="bg-slate-800/80 backdrop-blur-sm border-slate-700 hover:bg-slate-700 transition-all rounded-full px-8"
              >
                Load More
              </Button>
            </div>
          )}
        </>
      )}

      {/* Status Change Modal */}
      <Modal
        isOpen={statusModal.open}
        onClose={() => setStatusModal({ open: false, user: null, newStatus: '' })}
        title={`${statusModal.newStatus === 'ACTIVE' ? 'Activate' : statusModal.newStatus === 'SUSPENDED' ? 'Suspend' : 'Delete'} User`}
      >
        <div className="space-y-5 p-2">
          <p className="text-slate-300 text-lg">
            Are you sure you want to{' '}
            <span className={
              statusModal.newStatus === 'ACTIVE' ? 'text-green-400 font-semibold' :
              statusModal.newStatus === 'SUSPENDED' ? 'text-amber-400 font-semibold' : 'text-red-400 font-semibold'
            }>
              {statusModal.newStatus?.toLowerCase()}
            </span>{' '}
            the user{' '}
            <span className="text-white font-bold">
              {statusModal.user?.name || `${statusModal.user?.firstName || ''} ${statusModal.user?.lastName || ''}`.trim()}
            </span>
            ?
          </p>
          {statusModal.newStatus === 'DELETED' && (
            <div className="bg-red-500/10 border border-red-500/20 rounded-lg p-3">
              <p className="text-sm text-red-400 flex items-center gap-2">
                <span className="text-lg">⚠️</span> This action may not be reversible.
              </p>
            </div>
          )}
          <div className="flex justify-end gap-3 pt-4 border-t border-slate-700/50">
            <Button variant="ghost" onClick={() => setStatusModal({ open: false, user: null, newStatus: '' })}>
              Cancel
            </Button>
            <Button
              variant={statusModal.newStatus === 'ACTIVE' ? 'primary' : 'danger'}
              loading={actionLoading}
              onClick={handleStatusChange}
              className={statusModal.newStatus === 'ACTIVE' ? "bg-gradient-to-r from-green-500 to-emerald-400 text-black border-none" : ""}
            >
              Confirm
            </Button>
          </div>
        </div>
      </Modal>
    </DashboardLayout>
  );
};

export default UserManagement;
