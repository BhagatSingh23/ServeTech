import { useState, useRef, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';

const roleLabels = {
  WORKER: 'Worker',
  CLIENT: 'Client',
  ADMIN: 'Admin',
};

const roleDashboard = {
  WORKER: '/worker/dashboard',
  CLIENT: '/client/dashboard',
  ADMIN: '/admin/dashboard',
};

const roleBadgeColors = {
  WORKER: 'bg-blue-500/20 text-blue-400',
  CLIENT: 'bg-green-500/20 text-green-400',
  ADMIN: 'bg-red-500/20 text-red-400',
};

const Navbar = () => {
  const { isAuthenticated, user, logout } = useAuth();
  const [dropdownOpen, setDropdownOpen] = useState(false);
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);
  const dropdownRef = useRef(null);
  const navigate = useNavigate();

  // Close dropdown on outside click
  useEffect(() => {
    const handleClick = (e) => {
      if (dropdownRef.current && !dropdownRef.current.contains(e.target)) {
        setDropdownOpen(false);
      }
    };
    document.addEventListener('mousedown', handleClick);
    return () => document.removeEventListener('mousedown', handleClick);
  }, []);

  const handleLogout = () => {
    setDropdownOpen(false);
    logout();
  };

  const handleDashboard = () => {
    setDropdownOpen(false);
    const path = roleDashboard[user?.role] || '/';
    navigate(path);
  };

  return (
    <nav className="fixed top-0 left-0 right-0 z-50 bg-[#010409] border-b border-gray-800/50">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between h-16">
          {/* Brand */}
          <Link to="/" className="flex items-center gap-2.5 group">
            <div className="h-8 w-8 rounded-lg bg-amber-500 flex items-center justify-center transition-transform group-hover:scale-105">
              <span className="text-black font-bold text-base">S</span>
            </div>
            <span className="text-lg font-bold text-white tracking-tight">
              Serve<span className="text-amber-400">Tech</span>
            </span>
          </Link>

          {/* Desktop nav */}
          <div className="hidden md:flex items-center gap-4">
            {!isAuthenticated ? (
              <>
                <Link
                  to="/login"
                  className="px-4 py-2 text-sm font-medium text-gray-300 hover:text-white transition-colors"
                >
                  Login
                </Link>
                <Link
                  to="/register"
                  className="px-4 py-2 text-sm font-semibold bg-amber-500 hover:bg-amber-600 text-black rounded-lg transition-colors"
                >
                  Register
                </Link>
              </>
            ) : (
              <div className="flex items-center gap-3">
                {/* Notification bell */}
                <button className="relative text-gray-400 hover:text-white transition-colors p-2 cursor-pointer">
                  <svg className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                    <path strokeLinecap="round" strokeLinejoin="round" d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9" />
                  </svg>
                  <span className="absolute top-1 right-1 h-2 w-2 rounded-full bg-amber-500" />
                </button>

                {/* User dropdown */}
                <div className="relative" ref={dropdownRef}>
                  <button
                    onClick={() => setDropdownOpen(!dropdownOpen)}
                    className="flex items-center gap-2.5 px-3 py-1.5 rounded-lg hover:bg-gray-800 transition-colors cursor-pointer"
                  >
                    <div className="h-8 w-8 rounded-full bg-gray-700 flex items-center justify-center text-amber-400 font-semibold text-sm">
                      {user?.name?.charAt(0)?.toUpperCase() || 'U'}
                    </div>
                    <div className="text-left hidden lg:block">
                      <p className="text-sm font-medium text-white leading-tight">{user?.name || 'User'}</p>
                    </div>
                    <span className={`text-[10px] font-medium px-2 py-0.5 rounded-full ${roleBadgeColors[user?.role] || 'bg-gray-500/20 text-gray-400'}`}>
                      {roleLabels[user?.role] || user?.role}
                    </span>
                    <svg className={`h-4 w-4 text-gray-400 transition-transform ${dropdownOpen ? 'rotate-180' : ''}`} fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                      <path strokeLinecap="round" strokeLinejoin="round" d="M19 9l-7 7-7-7" />
                    </svg>
                  </button>

                  {/* Dropdown menu */}
                  {dropdownOpen && (
                    <div className="absolute right-0 mt-2 w-56 rounded-xl bg-gray-800 border border-gray-700 shadow-xl py-1 animate-[fadeIn_0.15s_ease-out]">
                      <div className="px-4 py-3 border-b border-gray-700">
                        <p className="text-sm font-medium text-white">{user?.name}</p>
                        <p className="text-xs text-gray-400 truncate">{user?.email}</p>
                      </div>
                      <button
                        onClick={handleDashboard}
                        className="w-full flex items-center gap-2.5 px-4 py-2.5 text-sm text-gray-300 hover:text-white hover:bg-gray-700/50 transition-colors cursor-pointer"
                      >
                        <span>📊</span> Dashboard
                      </button>
                      <button
                        onClick={() => { setDropdownOpen(false); navigate(`/${user?.role?.toLowerCase()}/profile`); }}
                        className="w-full flex items-center gap-2.5 px-4 py-2.5 text-sm text-gray-300 hover:text-white hover:bg-gray-700/50 transition-colors cursor-pointer"
                      >
                        <span>👤</span> Profile
                      </button>
                      <div className="border-t border-gray-700 mt-1">
                        <button
                          onClick={handleLogout}
                          className="w-full flex items-center gap-2.5 px-4 py-2.5 text-sm text-red-400 hover:bg-red-500/10 transition-colors cursor-pointer"
                        >
                          <span>🚪</span> Logout
                        </button>
                      </div>
                    </div>
                  )}
                </div>
              </div>
            )}
          </div>

          {/* Mobile menu button */}
          <button
            onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
            className="md:hidden text-gray-400 hover:text-white transition-colors cursor-pointer p-2"
          >
            {mobileMenuOpen ? (
              <svg className="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                <path strokeLinecap="round" strokeLinejoin="round" d="M6 18L18 6M6 6l12 12" />
              </svg>
            ) : (
              <svg className="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                <path strokeLinecap="round" strokeLinejoin="round" d="M4 6h16M4 12h16M4 18h16" />
              </svg>
            )}
          </button>
        </div>
      </div>

      {/* Mobile menu */}
      {mobileMenuOpen && (
        <div className="md:hidden border-t border-gray-800 bg-[#010409]">
          <div className="px-4 py-4 space-y-2">
            {!isAuthenticated ? (
              <>
                <Link
                  to="/login"
                  onClick={() => setMobileMenuOpen(false)}
                  className="block px-4 py-2.5 text-sm font-medium text-gray-300 hover:text-white hover:bg-gray-800 rounded-lg transition-colors"
                >
                  Login
                </Link>
                <Link
                  to="/register"
                  onClick={() => setMobileMenuOpen(false)}
                  className="block px-4 py-2.5 text-sm font-semibold bg-amber-500 hover:bg-amber-600 text-black rounded-lg transition-colors text-center"
                >
                  Register
                </Link>
              </>
            ) : (
              <>
                <div className="flex items-center gap-3 px-4 py-3 mb-2">
                  <div className="h-10 w-10 rounded-full bg-gray-700 flex items-center justify-center text-amber-400 font-semibold">
                    {user?.name?.charAt(0)?.toUpperCase() || 'U'}
                  </div>
                  <div>
                    <p className="text-sm font-medium text-white">{user?.name}</p>
                    <p className="text-xs text-gray-500">{roleLabels[user?.role] || user?.role}</p>
                  </div>
                </div>
                <button
                  onClick={() => { setMobileMenuOpen(false); handleDashboard(); }}
                  className="w-full flex items-center gap-2.5 px-4 py-2.5 text-sm text-gray-300 hover:text-white hover:bg-gray-800 rounded-lg transition-colors cursor-pointer"
                >
                  <span>📊</span> Dashboard
                </button>
                <button
                  onClick={() => { setMobileMenuOpen(false); handleLogout(); }}
                  className="w-full flex items-center gap-2.5 px-4 py-2.5 text-sm text-red-400 hover:bg-red-500/10 rounded-lg transition-colors cursor-pointer"
                >
                  <span>🚪</span> Logout
                </button>
              </>
            )}
          </div>
        </div>
      )}

      <style>{`
        @keyframes fadeIn {
          from { opacity: 0; transform: translateY(-4px); }
          to { opacity: 1; transform: translateY(0); }
        }
      `}</style>
    </nav>
  );
};

export default Navbar;