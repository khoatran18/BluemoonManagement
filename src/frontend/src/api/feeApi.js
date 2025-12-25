import axios from 'axios';

// Base URL for the API, adjust if needed
const API_BASE_URL = 'http://localhost:8080/api/v1'; // Assuming backend runs on 8080

// Function to get fees list
export const getFees = async (params = {}) => {
  try {
    const response = await axios.get(`${API_BASE_URL}/fees`, {
      params,
      headers: {
        // Add authorization header if needed
        // 'Authorization': `Bearer ${token}`
      }
    });
    return response.data;
  } catch (error) {
    console.error('Error fetching fees:', error);
    throw error;
  }
};