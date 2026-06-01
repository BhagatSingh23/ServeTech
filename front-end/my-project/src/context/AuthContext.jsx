import { createContext, useContext, useState, useEffect } from 'react';
import * as authApi from '../api/auth';

const AuthContext = createContext(null);

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [token, setToken] = useState(null);
  const [loading, setLoading] = useState(true);

  const isAuthenticated = !!token && !!user;

  // Auto-load user from localStorage on mount
  useEffect(() => {
    try {
      const storedToken = localStorage.getItem('token');
      const storedUser = localStorage.getItem('user');

      if (storedToken && storedUser) {
        setToken(storedToken);
        setUser(JSON.parse(storedUser));
      }
    } catch {
      localStorage.removeItem('token');
      localStorage.removeItem('user');
    } finally {
      setLoading(false);
    }
  }, []);

  // Login Step 1: Validate credentials, send OTP
  const loginStep1 = async (credentials) => {
    try {
      const response = await authApi.login(credentials);
      const apiData = response.data; // { success, message }
      return { success: apiData.success, message: apiData.message };
    } catch (error) {
      const message = error.response?.data?.message || 'Login failed. Please try again.';
      return { success: false, message };
    }
  };

  // Login Step 2: Verify OTP, get token
  const loginStep2 = async (phoneNumber, otp) => {
    try {
      const response = await authApi.loginVerify({ phoneNumber, otp });
      const apiData = response.data; // { success, message, data: { token, user } }
      const authResponse = apiData.data; // AuthResponse { token, tokenType, user }
      const jwt = authResponse.token;
      const userData = authResponse.user;

      localStorage.setItem('token', jwt);
      localStorage.setItem('user', JSON.stringify(userData));

      setToken(jwt);
      setUser(userData);

      return { success: true, user: userData };
    } catch (error) {
      const message = error.response?.data?.message || 'OTP verification failed.';
      return { success: false, message };
    }
  };

  const register = async (userData) => {
    try {
      const response = await authApi.register(userData);
      const apiData = response.data; // { success, message, data: { token, user } }
      const authResponse = apiData.data; // AuthResponse { token, tokenType, user }

      if (authResponse && authResponse.token && authResponse.user) {
        localStorage.setItem('token', authResponse.token);
        localStorage.setItem('user', JSON.stringify(authResponse.user));
        setToken(authResponse.token);
        setUser(authResponse.user);
      }

      return { success: true, data: apiData };
    } catch (error) {
      const message = error.response?.data?.message || 'Registration failed. Please try again.';
      return { success: false, message };
    }
  };

  const logout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    setToken(null);
    setUser(null);
    window.location.href = '/login';
  };

  const updateUser = (updatedData) => {
    const updatedUser = { ...user, ...updatedData };
    setUser(updatedUser);
    localStorage.setItem('user', JSON.stringify(updatedUser));
  };

  const value = {
    user,
    token,
    isAuthenticated,
    loading,
    loginStep1,
    loginStep2,
    logout,
    register,
    updateUser,
  };

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );
};

export default AuthContext;
