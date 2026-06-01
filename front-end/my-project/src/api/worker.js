import api from './axios';

export const getWorkerDashboard = () => api.get('/worker/dashboard');
export const getWorkerProfile = () => api.get('/worker/profile');
export const updateWorkerProfile = (data) => api.put('/worker/profile', data);
export const toggleAvailability = () => api.patch('/worker/profile/availability');
export const getWorkerApplications = (status) => api.get('/worker/applications', { params: { status } });
