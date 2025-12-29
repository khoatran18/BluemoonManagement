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
export const createApartment = async (apartmentData) => {
  try {
    const response = await axiosClient.post('/apartments', {
      building: apartmentData.building,
      room_number: apartmentData.room_number,
    });
    return response.data;
  } catch (error) {
    console.error('Error creating apartment:', error);
    throw error;
  }
};

export const updateApartment = async (apartmentId, apartmentData) => {
  try {
    const response = await axiosClient.put(`/apartments/${apartmentId}`, {
      apartment_id: apartmentId,
      building: apartmentData.building,
      room_number: apartmentData.room_number,
      head_resident_id: apartmentData.head_resident_id || null,
      residents: apartmentData.residents || [],
    });
    return response.data;
  } catch (error) {
    console.error('Error updating apartment:', error);
    throw error;
  }
};

export const deleteApartment = async (apartmentId) => {
  try {
    const response = await axiosClient.delete(`/apartments/${apartmentId}`, {
      data: { id: apartmentId }
    });
    return response.data;
  } catch (error) {
    console.error('Error deleting apartment:', error);
    throw error;
  }
};

