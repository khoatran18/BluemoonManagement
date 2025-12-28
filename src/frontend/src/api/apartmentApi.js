import axiosClient from "./axiosClient";

export const getApartments = async (params = {}) => {
    try {
        const response = await axiosClient.get(`/apartments`, {
        params,
        paramsSerializer: {
            indexes: null
        }
        });
        if (!response.data.success) {
          throw new Error(response.data.message);
        }
        return response.data.data;
    } catch (error) {
        console.error('Error fetching apartments:', error);
        throw error;
    }
}

export const getApartmentDetail = async (apartmentId) => {
    try {
        const response = await axiosClient.get(`/apartments/${apartmentId}`);
        if (!response.data.success) {
          throw new Error(response.data.message);
        }
        return response.data.data;
    } catch (error) {
        console.error('Error fetching apartment detail:', error);
        throw error;
    }
}
