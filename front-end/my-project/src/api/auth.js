import api from './axios';

export const login = (credentials) => api.post('/auth/login', credentials);
export const loginVerify = (data) => api.post('/auth/login-verify', data);
export const googleLogin = (data) => api.post('/auth/google', data);
export const register = (userData) => api.post('/auth/signup', userData);
export const sendOtp = (data) => api.post('/auth/send-otp', data);
export const verifyOtp = (data) => api.post('/auth/verify-otp', data);
export const resetPassword = (data) => api.post('/auth/reset-password', data);
export const getLocationByPincode = (pincode) => api.get(`/auth/pincode/${pincode}`);
