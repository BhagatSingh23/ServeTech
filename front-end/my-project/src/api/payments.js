import api from './axios';

export const recordPayment = (data) => api.post('/payments', data);
export const getPaymentsForAssignment = (id) => api.get(`/payments/assignment/${id}`);
export const getWorkerPayments = () => api.get('/payments/worker/me');
export const getClientPayments = () => api.get('/payments/client/me');
