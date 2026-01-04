import axios from "axios";
import { refreshTokens } from "./authApi";
import {
  clearTokens,
  getAccessToken,
  getRefreshToken,
  setTokens,
} from "../auth/tokenStorage";

const API_BASE_URL = 'https://project-it3180-production.up.railway.app';

const axiosClient = axios.create({
  baseURL: `${API_BASE_URL}/api/v1`,
  timeout: 50000,
  headers: {
    "Content-Type": "application/json",
  },
});

axiosClient.interceptors.request.use((config) => {
  const token = getAccessToken();
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

let refreshInFlight = null;

function redirectToLogin() {
  // Avoid react-router hook usage inside axios module
  if (window.location.pathname !== "/login") {
    window.location.assign("/login");
  }
}

axiosClient.interceptors.response.use(
  (response) => response,
  async (error) => {
    const status = error?.response?.status;
    const originalRequest = error?.config;

    if (status !== 401 || !originalRequest) {
      return Promise.reject(error);
    }

    if (originalRequest._retry) {
      // Already retried once; treat as invalid session
      clearTokens();
      redirectToLogin();
      return Promise.reject(error);
    }

    const refreshToken = getRefreshToken();
    if (!refreshToken) {
      clearTokens();
      redirectToLogin();
      return Promise.reject(error);
    }

    originalRequest._retry = true;

    try {
      if (!refreshInFlight) {
        const accessToken = getAccessToken();
        refreshInFlight = refreshTokens({ accessToken, refreshToken }).finally(() => {
          refreshInFlight = null;
        });
      }

      const newTokens = await refreshInFlight;
      if (newTokens?.access_token && newTokens?.refresh_token) {
        setTokens({
          accessToken: newTokens.access_token,
          refreshToken: newTokens.refresh_token,
        });
      } else if (newTokens?.AccessToken && newTokens?.RefreshToken) {
        // Defensive: handle accidental PascalCase mapping
        setTokens({ accessToken: newTokens.AccessToken, refreshToken: newTokens.RefreshToken });
      } else {
        clearTokens();
        redirectToLogin();
        return Promise.reject(error);
      }

      const updatedAccessToken = getAccessToken();
      if (updatedAccessToken) {
        originalRequest.headers = originalRequest.headers || {};
        originalRequest.headers.Authorization = `Bearer ${updatedAccessToken}`;
      }

      return axiosClient(originalRequest);
    } catch (refreshErr) {
      clearTokens();
      redirectToLogin();
      return Promise.reject(refreshErr);
    }
  }
);

export default axiosClient;
