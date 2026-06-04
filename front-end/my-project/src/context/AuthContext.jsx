import { createContext, useContext, useState } from 'react';
import * as authApi from '../api/auth';

const AuthContext = createContext(null);

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};

/**
 * Normalize user data from the backend so that all components
 * can consistently access `user.name` and `user.role` (singular).
 *
 * Backend UserResponse fields:
 *   firstName, lastName, roles: ["ROLE_WORKER"] | ["ROLE_CLIENT"] | ["ROLE_ADMIN"]
 *
 * After normalization:
 *   name  → "FirstName LastName"
 *   role  → "WORKER" | "CLIENT" | "ADMIN"  (primary role without ROLE_ prefix)
 *   roles → ["ROLE_WORKER"] (original array kept for ProtectedRoute checks)
 */
const normalizeUser = (raw) => {
  if (!raw) return null;

  // Derive display name
  const name = raw.name
    || [raw.firstName, raw.lastName].filter(Boolean).join(' ')
    || raw.username
    || 'User';

  // Derive primary role (strip ROLE_ prefix for UI display / nav lookup)
  let role = raw.role; // if already set (e.g. re-normalizing)
  if (!role) {
    const rolesArr = Array.isArray(raw.roles) ? raw.roles : [...(raw.roles || [])];
    // Priority: ADMIN > CLIENT > WORKER
    if (rolesArr.includes('ROLE_ADMIN')) role = 'ADMIN';
    else if (rolesArr.includes('ROLE_CLIENT')) role = 'CLIENT';
    else if (rolesArr.includes('ROLE_WORKER')) role = 'WORKER';
    else role = rolesArr[0]?.replace('ROLE_', '') || 'WORKER';
  }

  // Ensure roles is always a plain array (backend may send a Set serialized as array)
  const roles = Array.isArray(raw.roles) ? raw.roles : [...(raw.roles || [])];

  return { ...raw, name, role, roles };
};

// Read auth state from localStorage synchronously so it's available
// on the very first render — prevents "Access Denied" flash on reload.
const getInitialAuth = () => {
  try {
    const storedToken = localStorage.getItem('token');
    const storedUser = localStorage.getItem('user');
    if (storedToken && storedUser) {
      return { token: storedToken, user: normalizeUser(JSON.parse(storedUser)) };
    }
  } catch {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
  }
  return { token: null, user: null };
};

export const AuthProvider = ({ children }) => {
  const initial = getInitialAuth();
  const [user, setUser] = useState(initial.user);
  const [token, setToken] = useState(initial.token);
  const [loading] = useState(false); // no async loading needed

  const isAuthenticated = !!token && !!user;

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
      const userData = normalizeUser(authResponse.user);

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
        const normalized = normalizeUser(authResponse.user);
        localStorage.setItem('token', authResponse.token);
        localStorage.setItem('user', JSON.stringify(normalized));
        setToken(authResponse.token);
        setUser(normalized);
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
    const updatedUser = normalizeUser({ ...user, ...updatedData });
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

