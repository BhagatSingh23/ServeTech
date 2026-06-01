import { Navigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import LoadingSpinner from './LoadingSpinner';

const ProtectedRoute = ({ children, allowedRoles = [] }) => {
  const { isAuthenticated, user, loading } = useAuth();

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-950">
        <LoadingSpinner text="Checking authentication..." size="lg" />
      </div>
    );
  }

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  if (allowedRoles.length > 0) {
    const userRoles = user?.roles || [];
    const hasAccess = allowedRoles.some(r => userRoles.includes(r));
    if (!hasAccess) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-950 px-4">
        <div className="text-center">
          <div className="flex items-center justify-center h-20 w-20 rounded-2xl bg-red-500/10 border border-red-500/20 mx-auto mb-5">
            <span className="text-4xl">🚫</span>
          </div>
          <h2 className="text-2xl font-bold text-white mb-2">Access Denied</h2>
          <p className="text-gray-400 mb-6 max-w-sm">
            You don&apos;t have permission to access this page. Please contact support if you believe this is an error.
          </p>
          <button
            onClick={() => window.history.back()}
            className="inline-flex items-center gap-2 px-4 py-2.5 bg-gray-800 hover:bg-gray-700 text-white text-sm font-medium rounded-lg transition-colors cursor-pointer"
          >
            <svg className="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
              <path strokeLinecap="round" strokeLinejoin="round" d="M10 19l-7-7m0 0l7-7m-7 7h18" />
            </svg>
            Go Back
          </button>
        </div>
      </div>
    );
    }
  }

  return children;
};

export default ProtectedRoute;
