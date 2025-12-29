import axiosClient from "./axiosClient";
import { apiCall } from "./apiCall";

export const getResidents = async (params = {}) => {
        return apiCall(
            () =>
                axiosClient.get(`/residents`, {
                    params,
                    paramsSerializer: {
                        indexes: null,
                    },
                }),
            { label: 'getResidents' }
        );
}