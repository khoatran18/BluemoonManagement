import axiosClient from './axiosClient';
import { apiCall } from './apiCall';

export const getAdjustments = async (params = {}) => {
  return apiCall(
    () =>
      axiosClient.get('/adjustments', {
        params,
        paramsSerializer: {
          indexes: null,
        },
      }),
    { label: 'getAdjustments' }
  );
};

export const getAdjustmentDetails = async (adjustmentId) => {
  return apiCall(() => axiosClient.get(`/adjustments/${adjustmentId}`), {
    label: 'getAdjustmentDetails',
    pick: (res) => res.data,
  });
};

export const createAdjustment = async (params) => {
  return apiCall(
    () =>
      axiosClient.post('/adjustments', {
        fee_id: params.fee_id,
        adjustment_amount: params.adjustment_amount,
        adjustment_type: params.adjustment_type,
        reason: params.reason || '',
        effective_date: params.effective_date || null,
        expiry_date: params.expiry_date || null,
      }),
    { label: 'createAdjustment', pick: (res) => res.data }
  );
};

export const updateAdjustment = async (adjustmentId, params) => {
  return apiCall(
    () =>
      axiosClient.put(`/adjustments/${adjustmentId}`, {
        adjustment_id: adjustmentId,
        fee_id: params.fee_id,
        adjustment_amount: params.adjustment_amount,
        adjustment_type: params.adjustment_type,
        reason: params.reason || '',
        effective_date: params.effective_date || null,
        expiry_date: params.expiry_date || null,
      }),
    { label: 'updateAdjustment', pick: (res) => res.data }
  );
};

export const deleteAdjustment = async (adjustmentId) => {
  return apiCall(() => axiosClient.delete(`/adjustments/${adjustmentId}`), {
    label: 'deleteAdjustment',
    pick: (res) => res.data,
  });
};
