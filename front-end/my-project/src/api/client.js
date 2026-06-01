import api from './axios';

export const getClientDashboard = () => api.get('/client/dashboard');
export const createWorkRequest = (data) => api.post('/client/work-requests', data);
export const getMyWorkRequests = (status) => api.get('/client/work-requests', { params: { status } });
export const getWorkRequestById = (id) => api.get(`/client/work-requests/${id}`);
export const updateWorkRequest = (id, data) => api.put(`/client/work-requests/${id}`, data);
export const deleteWorkRequest = (id) => api.delete(`/client/work-requests/${id}`);
export const closeWorkRequest = (id) => api.patch(`/client/work-requests/${id}/close`);
export const getWorkRequestApplications = (id) => api.get(`/client/work-requests/${id}/applications`);
