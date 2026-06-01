import api from './axios';

export const browseJobs = (params) => api.get('/jobs/browse', { params });
export const getRecommendedJobs = () => api.get('/jobs/recommended');
export const getJobDetails = (id) => api.get(`/jobs/${id}`);
