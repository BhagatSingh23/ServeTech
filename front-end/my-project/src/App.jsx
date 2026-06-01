import { Routes, Route, Navigate } from 'react-router-dom';
import { useAuth } from './context/AuthContext';
import ProtectedRoute from './components/common/ProtectedRoute';

// Auth pages
import Login from './pages/auth/Login';
import Register from './pages/auth/Register';

// Worker pages
import WorkerDashboard from './pages/worker/WorkerDashboard';
import BrowseJobs from './pages/worker/BrowseJobs';
import MyApplications from './pages/worker/MyApplications';
import WorkerProfile from './pages/worker/WorkerProfile';

// Client pages
import ClientDashboard from './pages/client/ClientDashboard';
import PostJob from './pages/client/PostJob';
import MyJobs from './pages/client/MyJobs';
import ViewApplicants from './pages/client/ViewApplicants';
import RecordPayment from './pages/client/RecordPayment';

// Admin pages
import AdminDashboard from './pages/admin/AdminDashboard';
import UserManagement from './pages/admin/UserManagement';
import Complaints from './pages/admin/Complaints';

function App() {
  const { isAuthenticated, user } = useAuth();

  const getDefaultRoute = () => {
    if (!isAuthenticated || !user) return '/login';
    const roles = user.roles || [];
    if (roles.includes('ROLE_ADMIN')) return '/admin/dashboard';
    if (roles.includes('ROLE_CLIENT')) return '/client/dashboard';
    if (roles.includes('ROLE_WORKER')) return '/worker/dashboard';
    return '/login';
  };

  return (
    <div className="min-h-screen bg-slate-900">
      <Routes>
        <Route path="/" element={<Navigate to={getDefaultRoute()} replace />} />
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />

        {/* Worker Routes */}
        <Route path="/worker/dashboard" element={<ProtectedRoute allowedRoles={['ROLE_WORKER']}><WorkerDashboard /></ProtectedRoute>} />
        <Route path="/worker/browse-jobs" element={<ProtectedRoute allowedRoles={['ROLE_WORKER']}><BrowseJobs /></ProtectedRoute>} />
        <Route path="/worker/applications" element={<ProtectedRoute allowedRoles={['ROLE_WORKER']}><MyApplications /></ProtectedRoute>} />
        <Route path="/worker/profile" element={<ProtectedRoute allowedRoles={['ROLE_WORKER']}><WorkerProfile /></ProtectedRoute>} />

        {/* Client Routes */}
        <Route path="/client/dashboard" element={<ProtectedRoute allowedRoles={['ROLE_CLIENT']}><ClientDashboard /></ProtectedRoute>} />
        <Route path="/client/post-job" element={<ProtectedRoute allowedRoles={['ROLE_CLIENT']}><PostJob /></ProtectedRoute>} />
        <Route path="/client/my-jobs" element={<ProtectedRoute allowedRoles={['ROLE_CLIENT']}><MyJobs /></ProtectedRoute>} />
        <Route path="/client/jobs/:id/applicants" element={<ProtectedRoute allowedRoles={['ROLE_CLIENT']}><ViewApplicants /></ProtectedRoute>} />
        <Route path="/client/payments" element={<ProtectedRoute allowedRoles={['ROLE_CLIENT']}><RecordPayment /></ProtectedRoute>} />

        {/* Admin Routes */}
        <Route path="/admin/dashboard" element={<ProtectedRoute allowedRoles={['ROLE_ADMIN']}><AdminDashboard /></ProtectedRoute>} />
        <Route path="/admin/users" element={<ProtectedRoute allowedRoles={['ROLE_ADMIN']}><UserManagement /></ProtectedRoute>} />
        <Route path="/admin/complaints" element={<ProtectedRoute allowedRoles={['ROLE_ADMIN']}><Complaints /></ProtectedRoute>} />

        {/* Catch all */}
        <Route path="*" element={<Navigate to={getDefaultRoute()} replace />} />
      </Routes>
    </div>
  );
}

export default App;
