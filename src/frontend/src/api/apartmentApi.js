import axiosClient from "./axiosClient";
import { apiCall } from "./apiCall";

export const getApartments = async (params = {}) => {
        return apiCall(
            () =>
                axiosClient.get(`/apartments`, {
                    params,
                    paramsSerializer: {
                        indexes: null,
                    },
                }),
            { label: 'getApartments' }
        );
}

export const getApartmentDetail = async (apartmentId) => {
        return apiCall(
            () => axiosClient.get(`/apartments/${apartmentId}`),
            { label: 'getApartmentDetail' }
        );
}
