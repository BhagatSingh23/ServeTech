import { useState } from 'react';
import { NavLink, useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';

const Sidebar = ({ isOpen, onClose }) => {
  const { user, logout } = useAuth();
const navigate = useNavigate();

  const userRoles = user?.roles || [];
  let role = 'WORKER';
  if (userRoles.includes('ROLE_ADMIN')) role = 'ADMIN';
  else if (userRoles.includes('ROLE_CLIENT')) role = 'CLIENT';

  const getNavLinks = (r) => {
    switch (r) {
      case 'ADMIN':
        return [
          { path: '/admin/dashboard', label: 'Dashboard', icon: '📊' },
          { path: '/admin/users', label: 'Users', icon: '👥' },
          { path: '/admin/complaints', label: 'Complaints', icon: '📢' },
        ];
      case 'CLIENT':
        return [
          { path: '/client/dashboard', label: 'Dashboard', icon: '📊' },
          { path: '/client/post-job', label: 'Post Job', icon: '📝' },
          { path: '/client/my-jobs', label: 'My Jobs', icon: '💼' },
          { path: '/client/payments', label: 'Payments', icon: '💰' },
          { path: '/client/complaints', label: 'Complaints', icon: '📢' },
        ];
      case 'WORKER':
      default:
        return [
          { path: '/worker/dashboard', label: 'Dashboard', icon: '📊' },
          { path: '/worker/browse-jobs', label: 'Browse Jobs', icon: '🔍' },
          { path: '/worker/applications', label: 'My Applications', icon: '📋' },
          { path: '/worker/complaints', label: 'Complaints', icon: '📢' },
          { path: '/worker/profile', label: 'Profile', icon: '👤' },
        ];
    }
  };

  const getRoleLabel = (r) => {
    switch (r) {
      case 'ADMIN': return 'Admin';
      case 'CLIENT': return 'Client';
      case 'WORKER': return 'Worker';
      default: return r;
    }
  };

  const links = getNavLinks(role);

  const handleLogout = () => {
    logout();
  };

  return (
    <>
      {/* Mobile overlay */}
      {isOpen && (
        <div
          className="fixed inset-0 bg-slate-950/80 backdrop-blur-sm z-40 lg:hidden transition-opacity"
          onClick={onClose}
        />
      )}

      {/* Sidebar */}
      <aside
        className={`
          fixed top-0 left-0 h-full w-72 bg-slate-900/95 backdrop-blur-2xl border-r border-slate-800/80 z-50
          flex flex-col transition-all duration-300 ease-out shadow-2xl
          lg:translate-x-0 lg:static lg:z-auto
          ${isOpen ? 'translate-x-0' : '-translate-x-full'}
        `}
      >
        {/* Brand */}
        <div className="flex items-center justify-between px-6 py-6 border-b border-slate-800/80 relative overflow-hidden">
          <div className="absolute -top-10 -right-10 w-32 h-32 bg-amber-500/10 rounded-full blur-2xl"></div>
          <div
            className="flex items-center gap-3 cursor-pointer z-10 hover:scale-105 transition-transform"
            onClick={() => navigate('/')}
          >
            <div className="h-10 w-10 rounded-xl bg-gradient-to-tr from-amber-500 to-amber-300 flex items-center justify-center shadow-lg shadow-amber-500/20 overflow-hidden">
              <img src="/logo.png" alt="ServeTech Logo" className="h-full w-full object-cover" />
            </div>
            <span className="text-xl font-bold text-white tracking-tight">
              Serve<span className="text-transparent bg-clip-text bg-gradient-to-r from-amber-400 to-amber-200">Tech</span>
            </span>
          </div>
          {/* Mobile close */}
          <button
            onClick={onClose}
            className="lg:hidden text-slate-400 hover:text-white bg-slate-800/50 hover:bg-slate-700 p-2 rounded-lg transition-all cursor-pointer z-10"
          >
            <svg className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
              <path strokeLinecap="round" strokeLinejoin="round" d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>
        </div>

        {/* User info */}
        <div className="px-6 py-5 border-b border-slate-800/80 bg-slate-800/20">
          <div className="flex items-center gap-4">
            <div className="relative">
              <div className="h-12 w-12 rounded-2xl bg-gradient-to-br from-slate-700 to-slate-800 flex items-center justify-center text-amber-400 font-bold text-lg shadow-inner border border-slate-600">
                {user?.name?.charAt(0)?.toUpperCase() || 'U'}
              </div>
              <div className="absolute -bottom-1 -right-1 h-4 w-4 bg-green-500 border-2 border-slate-900 rounded-full"></div>
            </div>
            <div className="min-w-0 flex-1">
              <p className="text-base font-bold text-white truncate">{user?.name || 'User'}</p>
              <p className="text-xs font-medium text-amber-400/80 mt-0.5 tracking-wide uppercase">{getRoleLabel(role)}</p>
            </div>
          </div>
        </div>

        {/* Navigation */}
        <nav className="flex-1 px-4 py-6 space-y-1.5 overflow-y-auto custom-scrollbar">
          {links.map((link) => (
            <NavLink
              key={link.path}
              to={link.path}
              onClick={onClose}
              className={({ isActive }) =>
                `flex items-center gap-3.5 px-4 py-3 rounded-xl text-sm font-medium transition-all duration-200 group ${
                  isActive
                    ? 'bg-gradient-to-r from-amber-500/20 to-amber-500/5 text-amber-400 border border-amber-500/20 shadow-sm'
                    : 'text-slate-400 hover:text-white hover:bg-slate-800/50 border border-transparent'
                }`
              }
            >
              <span className={`text-xl transition-transform duration-200 group-hover:scale-110`}>{link.icon}</span>
              <span className="tracking-wide">{link.label}</span>
            </NavLink>
          ))}
        </nav>

        {/* Logout */}
        <div className="p-4 border-t border-slate-800/80 bg-slate-900/50">
          <button
            onClick={handleLogout}
            className="flex items-center justify-center gap-2 w-full px-4 py-3 rounded-xl text-sm font-bold text-red-400 hover:text-white hover:bg-red-500 hover:shadow-lg hover:shadow-red-500/20 border border-red-500/20 hover:border-transparent transition-all duration-200 cursor-pointer group"
          >
            <svg className="h-5 w-5 group-hover:translate-x-1 transition-transform" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
              <path strokeLinecap="round" strokeLinejoin="round" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" />
            </svg>
            <span>Logout</span>
          </button>
        </div>
      </aside>
    </>
  );
};

export default Sidebar;
