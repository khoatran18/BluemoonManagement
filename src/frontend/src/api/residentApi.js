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
export const getResidentDetail = async (residentId) => {
  try {
    const response = await axiosClient.get(`/residents/${residentId}`);
    return response.data.data;
  } catch (error) {
    console.error('Error fetching resident details:', error);
    throw error;
  }
};

export const createResident = async (residentData) => {
  try {
    const response = await axiosClient.post('/residents', {
      full_name: residentData.full_name,
      email: residentData.email || "",
      phone_number: residentData.phone_number || "",
      apartment_id: residentData.apartment_id,
    });
    return response.data;
  } catch (error) {
    console.error('Error creating resident:', error);
    throw error;
  }
};

export const updateResident = async (residentId, residentData) => {
  try {
    const response = await axiosClient.put(`/residents/${residentId}`, {
      resident_id: residentId,
      full_name: residentData.full_name,
      email: residentData.email || "",
      phone_number: residentData.phone_number || "",
    });
    return response.data;
  } catch (error) {
    console.error('Error updating resident:', error);
    throw error;
  }
};

export const deleteResident = async (residentId) => {
  try {
    const response = await axiosClient.delete(`/residents/${residentId}`, {
      data: { id: residentId }
    });
    return response.data;
  } catch (error) {
    console.error('Error deleting resident:', error);
    throw error;
  }
};
