import api from './axios';

export const submitRating = (data) => api.post('/ratings', data);
export const getMyRatings = () => api.get('/ratings/my-ratings');
export const getGivenRatings = () => api.get('/ratings/given-by-me');
export const getUserRatings = (userId) => api.get(`/ratings/user/${userId}`);
export const getAssignmentRatings = (id) => api.get(`/ratings/assignment/${id}`);
