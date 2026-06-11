import { useState } from 'react';
import { NavLink, useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';

const navConfig = {
  WORKER: [
    { path: '/worker/dashboard', label: 'Dashboard', icon: '📊' },
    { path: '/worker/browse-jobs', label: 'Browse Jobs', icon: '🔍' },
    { path: '/worker/applications', label: 'My Applications', icon: '📋' },
    { path: '/worker/complaints', label: 'Complaints', icon: '📢' },
    { path: '/worker/profile', label: 'Profile', icon: '👤' },
  ],
  CLIENT: [
    { path: '/client/dashboard', label: 'Dashboard', icon: '📊' },
    { path: '/client/post-job', label: 'Post Job', icon: '📝' },
    { path: '/client/my-jobs', label: 'My Jobs', icon: '💼' },
    { path: '/client/payments', label: 'Payments', icon: '💰' },
    { path: '/client/complaints', label: 'Complaints', icon: '📢' },
  ],
  ADMIN: [
    { path: '/admin/dashboard', label: 'Dashboard', icon: '📊' },
    { path: '/admin/users', label: 'Users', icon: '👥' },
    { path: '/admin/complaints', label: 'Complaints', icon: '📢' },
  ],
};

const roleLabels = {
  WORKER: 'Worker',
  CLIENT: 'Client',
  ADMIN: 'Admin',
};

const Sidebar = ({ isOpen, onClose }) => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const userRoles = user?.roles || [];
  let role = 'WORKER';
  if (userRoles.includes('ROLE_ADMIN')) role = 'ADMIN';
  else if (userRoles.includes('ROLE_CLIENT')) role = 'CLIENT';
  const links = navConfig[role] || navConfig.WORKER;

  const handleLogout = () => {
    logout();
  };

  return (
    <>
      {/* Mobile overlay */}
      {isOpen && (
        <div
          className="fixed inset-0 bg-black/50 z-40 lg:hidden"
          onClick={onClose}
        />
      )}

      {/* Sidebar */}
      <aside
        className={`
          fixed top-0 left-0 h-full w-64 bg-slate-900/95 backdrop-blur-xl border-r border-slate-800/80 z-50
          flex flex-col transition-transform duration-300 ease-in-out shadow-2xl
          lg:translate-x-0 lg:static lg:z-auto
          ${isOpen ? 'translate-x-0' : '-translate-x-full'}
        `}
      >
        {/* Brand */}
        <div className="flex items-center justify-between px-6 py-5 border-b border-slate-800/80">
          <div
            className="flex items-center gap-2.5 cursor-pointer"
            onClick={() => navigate('/')}
          >
            <div className="h-9 w-9 rounded-lg bg-amber-500 flex items-center justify-center">
              <span className="text-black font-bold text-lg">S</span>
            </div>
            <span className="text-lg font-bold text-white tracking-tight">
              Serve<span className="text-amber-400">Tech</span>
            </span>
          </div>
          {/* Mobile close */}
          <button
            onClick={onClose}
            className="lg:hidden text-gray-400 hover:text-white transition-colors cursor-pointer"
          >
            <svg className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
              <path strokeLinecap="round" strokeLinejoin="round" d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>
        </div>

        {/* User info */}
        <div className="px-5 py-4 border-b border-gray-800">
          <div className="flex items-center gap-3">
            <div className="h-10 w-10 rounded-full bg-gray-700 flex items-center justify-center text-amber-400 font-semibold text-sm">
              {user?.name?.charAt(0)?.toUpperCase() || 'U'}
            </div>
            <div className="min-w-0">
              <p className="text-sm font-medium text-white truncate">{user?.name || 'User'}</p>
              <p className="text-xs text-gray-500">{roleLabels[role] || role}</p>
            </div>
          </div>
        </div>

        {/* Navigation */}
        <nav className="flex-1 px-3 py-4 space-y-1 overflow-y-auto">
          {links.map((link) => (
            <NavLink
              key={link.path}
              to={link.path}
              onClick={onClose}
              className={({ isActive }) =>
                `flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm font-medium transition-all duration-150 group ${
                  isActive
                    ? 'bg-amber-500/10 text-amber-400 border border-amber-500/20'
                    : 'text-gray-400 hover:text-white hover:bg-gray-800 border border-transparent'
                }`
              }
            >
              <span className="text-lg flex-shrink-0">{link.icon}</span>
              <span>{link.label}</span>
            </NavLink>
          ))}
        </nav>

        {/* Logout */}
        <div className="px-3 py-4 border-t border-gray-800">
          <button
            onClick={handleLogout}
            className="flex items-center gap-3 w-full px-3 py-2.5 rounded-lg text-sm font-medium text-gray-400 hover:text-red-400 hover:bg-red-500/10 transition-all duration-150 cursor-pointer"
          >
            <svg className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
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
