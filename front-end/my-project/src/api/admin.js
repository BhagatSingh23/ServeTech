import api from './axios';

export const getAdminDashboard = () => api.get('/admin/dashboard');
export const getAllUsers = (params) => api.get('/admin/users', { params });
export const getUserById = (id) => api.get(`/admin/users/${id}`);
export const updateUserStatus = (id, status) => api.patch(`/admin/users/${id}/status`, { accountStatus: status });
export const verifyWorker = (id) => api.patch(`/admin/workers/${id}/verify`);
export const rejectVerification = (id) => api.patch(`/admin/workers/${id}/reject-verification`);
export const getAllComplaints = (status) => api.get('/admin/complaints', { params: { status } });
export const assignComplaint = (id) => api.patch(`/admin/complaints/${id}/assign`);
export const resolveComplaint = (id, data) => api.patch(`/admin/complaints/${id}/resolve`, data);
export const getAllWorkRequests = (status) => api.get('/admin/work-requests', { params: { status } });
export const getAllAssignments = (status) => api.get('/admin/assignments', { params: { status } });
