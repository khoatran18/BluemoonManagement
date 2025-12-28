import axiosClient from "./axiosClient";

export const getResidents = async (params = {}) => {
    try {
        const response = await axiosClient.get(`/residents`, {
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
        console.error('Error fetching residents:', error);
        throw error;
    }
}