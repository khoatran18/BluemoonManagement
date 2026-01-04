import axios from 'axios';

const API_BASE_URL = 'https://project-it3180-production.up.railway.app';

const axiosNoAuth = axios.create({
  baseURL: `${API_BASE_URL}/api/v1`,
  timeout: 50000,
  headers: {
    'Content-Type': 'application/json',
  },
});

export default axiosNoAuth;
