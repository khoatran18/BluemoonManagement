import axiosNoAuth from './axiosNoAuth';
import axiosClient from './axiosClient';
import { apiCall } from './apiCall';

const AUTH_BASE = '/auth_service';

export const login = async ({ username, password }) => {
  return apiCall(
    () => axiosNoAuth.post(`${AUTH_BASE}/login`, { username, password }),
    { label: 'login' }
  );
};

export const register = async ({ username, password, email, identity_number, role }) => {
  return apiCall(
    () =>
      axiosNoAuth.post(`${AUTH_BASE}/register`, {
        username,
        password,
        email,
        identity_number,
        role,
      }),
    { label: 'register' }
  );
};

export const refreshTokens = async ({ accessToken, refreshToken }) => {
  return apiCall(
    () =>
      axiosNoAuth.post(`${AUTH_BASE}/refresh`, {
        access_token: accessToken || '',
        refresh_token: refreshToken || '',
      }),
    { label: 'refreshTokens' }
  );
};

export const getMe = async () => {
  return apiCall(() => axiosClient.get(`${AUTH_BASE}/me`), { label: 'getMe' });
};
