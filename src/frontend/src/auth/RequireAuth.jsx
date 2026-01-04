import React from 'react';
import { Navigate, useLocation } from 'react-router-dom';
import { refreshTokens } from '../api/authApi';
import { clearTokens, getAccessToken, getRefreshToken, setTokens } from './tokenStorage';

let bootstrapRefreshInFlight = null;

function base64UrlDecode(input) {
  if (typeof input !== 'string' || input.length === 0) return null;
  // Convert base64url -> base64
  const base64 = input.replace(/-/g, '+').replace(/_/g, '/');
  const padded = base64.padEnd(base64.length + ((4 - (base64.length % 4)) % 4), '=');
  try {
    return atob(padded);
  } catch {
    return null;
  }
}

function isAccessTokenUsable(accessToken, leewaySeconds = 30) {
  if (!accessToken) return false;
  const parts = String(accessToken).split('.');
  if (parts.length < 2) {
    // Not a JWT; cannot safely check expiry client-side.
    return true;
  }

  const decoded = base64UrlDecode(parts[1]);
  if (!decoded) return true;

  try {
    const payload = JSON.parse(decoded);
    const exp = payload?.exp;
    if (typeof exp !== 'number') return true;

    const expiresAtMs = exp * 1000;
    const nowMs = Date.now();
    return nowMs < expiresAtMs - leewaySeconds * 1000;
  } catch {
    return true;
  }
}

export default function RequireAuth({ children }) {
  const location = useLocation();
  const [status, setStatus] = React.useState('checking'); // checking | authenticated | unauthenticated

  React.useEffect(() => {
    let isMounted = true;

    async function bootstrapAuth() {
      const accessToken = getAccessToken();
      const refreshToken = getRefreshToken();

      if (accessToken && isAccessTokenUsable(accessToken)) {
        if (isMounted) setStatus('authenticated');
        return;
      }

      if (!refreshToken) {
        clearTokens();
        if (isMounted) setStatus('unauthenticated');
        return;
      }

      try {
        if (!bootstrapRefreshInFlight) {
          bootstrapRefreshInFlight = refreshTokens({ accessToken, refreshToken }).finally(() => {
            bootstrapRefreshInFlight = null;
          });
        }

        const newTokens = await bootstrapRefreshInFlight;
        if (newTokens?.access_token && newTokens?.refresh_token) {
          setTokens({ accessToken: newTokens.access_token, refreshToken: newTokens.refresh_token });
          if (isMounted) setStatus('authenticated');
          return;
        }

        clearTokens();
        if (isMounted) setStatus('unauthenticated');
      } catch {
        clearTokens();
        if (isMounted) setStatus('unauthenticated');
      }
    }

    bootstrapAuth();
    return () => {
      isMounted = false;
    };
  }, []);

  if (status === 'checking') return null;

  if (status === 'unauthenticated') {
    return <Navigate to="/login" replace state={{ from: location }} />;
  }

  return children;
}
