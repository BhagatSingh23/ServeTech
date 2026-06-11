import api from './axios';

export const fileComplaint = (data) => api.post('/complaints', data);
export const getMyComplaints = () => api.get('/complaints');
