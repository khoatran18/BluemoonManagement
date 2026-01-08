/**
 * Global API call wrapper.
 *
 * Goals:
 * - Centralize backend `success:false` handling
 * - Normalize Axios/network/HTTP errors into a consistent Error shape
 * - Keep API modules lean (no repetitive try/catch)
 */

export class ApiError extends Error {
  /**
   * @param {string} message 
   * @param {object} [options]
   * @param {number} [options.status]
   * @param {string} [options.code]
   * @param {any} [options.details]
   * @param {any} [options.raw]
   */
  constructor(message, options = {}) {
    super(message || 'Request failed');
    this.name = 'ApiError';
    this.status = options.status;
    this.code = options.code;
    this.details = options.details;
    this.raw = options.raw;
  }
}

function isObject(value) {
  return value !== null && typeof value === 'object' && !Array.isArray(value);
}

function extractMessageFromPayload(payload) {
  if (!payload) return null;
  if (typeof payload === 'string') return payload;
  if (isObject(payload)) {
    if (typeof payload.message === 'string' && payload.message.trim()) return payload.message;
    if (typeof payload.error === 'string' && payload.error.trim()) return payload.error;
    if (typeof payload.title === 'string' && payload.title.trim()) return payload.title;
  }
  return null;
}

function normalizeAxiosLikeError(error) {
  const status = error?.response?.status;
  const data = error?.response?.data;

  const payloadMessage = extractMessageFromPayload(data);

  const code = typeof error?.code === 'string' ? error.code : undefined;

  const isNetworkError = !!error?.request && !error?.response;

  const message =
    payloadMessage ||
    (typeof error?.message === 'string' && error.message.trim() ? error.message : null) ||
    (isNetworkError ? 'Network error. Please check your connection.' : null) ||
    'Request failed';

  return new ApiError(message, {
    status,
    code,
    details: data,
    raw: error,
  });
}

/**
 * @template T
 * @param {() => Promise<any>} requestFn 
 * @param {object} [options]
 * @param {boolean} [options.requireSuccessFlag=true] 
 * @param {(response:any)=>T} [options.pick]
 * @param {string} [options.label] 
 * @returns {Promise<T>}
 */
export async function apiCall(requestFn, options = {}) {
  const { requireSuccessFlag = true, pick, label } = options;

  try {
    const response = await requestFn();

    if (requireSuccessFlag) {
      const payload = response?.data;
      if (isObject(payload) && Object.prototype.hasOwnProperty.call(payload, 'success')) {
        if (!payload.success) {
          const message = extractMessageFromPayload(payload) || 'Request failed';
          throw new ApiError(message, {
            status: response?.status,
            details: payload,
            raw: response,
          });
        }
      }
    }

    if (typeof pick === 'function') return pick(response);

    const payload = response?.data;
    if (isObject(payload) && Object.prototype.hasOwnProperty.call(payload, 'data')) {
      return payload.data;
    }

    return payload;
  } catch (error) {
    const normalized = error instanceof ApiError ? error : normalizeAxiosLikeError(error);
    if (label) {

      console.error(`[api call ] ${label}:`, normalized);
    }
    throw normalized;
  }
}