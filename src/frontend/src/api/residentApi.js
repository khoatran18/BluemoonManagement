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
};

export const getResidentDetail = async (residentId) => {
  return apiCall(() => axiosClient.get(`/residents/${residentId}`), {
    label: 'getResidentDetail',
  });
};

export const createResident = async (residentData) => {
  return apiCall(
    () =>
      axiosClient.post('/residents', {
        full_name: residentData.full_name,
        email: residentData.email || "",
        phone_number: residentData.phone_number || "",
        apartment_id: residentData.apartment_id,
      }),
    {
      label: 'createResident',
      pick: (res) => res.data,
    }
  );
};

export const editResident = async (residentId, residentData) => {
  return apiCall(
    () =>
      axiosClient.put(`/residents/${residentId}`, {
        full_name: residentData.full_name,
        email: residentData.email || "",
        phone_number: residentData.phone_number || "",
      }),
    {
      label: 'editResident',
      pick: (res) => res.data,
    }
  );
};

export const deleteResident = async (residentId) => {
  return apiCall(
    () => axiosClient.delete(`/residents/${residentId}`, { data: { id: residentId } }),
    {
      label: 'deleteResident',
      pick: (res) => res.data,
    }
  );
};

export const getDeleteResidentHistories = async (params = {}) => {
  return apiCall(
    () =>
      axiosClient.get(`/delete-resident-histories`, {
        params,
        paramsSerializer: {
          indexes: null,
        },
      }),
    { label: 'getDeleteResidentHistories' }
  );
};

export const getDeleteResidentHistoryDetail = async (historyId) => {
  return apiCall(() => axiosClient.get(`/delete-resident-histories/${historyId}`), {
    label: 'getDeleteResidentHistoryDetail',
  });
};
