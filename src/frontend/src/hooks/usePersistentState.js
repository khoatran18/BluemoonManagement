import { useEffect, useState } from "react";

const isBrowser = () => typeof window !== "undefined";

const safeParse = (raw, fallback) => {
  if (raw == null) return fallback;
  try {
    return JSON.parse(raw);
  } catch {
    return fallback;
  }
};

/**
 * React state persisted to localStorage.
 * - Uses JSON serialization.
 * - Falls back gracefully if storage is unavailable.
 */
export const usePersistentState = (key, defaultValue) => {
  const [value, setValue] = useState(() => {
    if (!isBrowser()) return defaultValue;
    try {
      const raw = window.localStorage.getItem(key);
      return safeParse(raw, defaultValue);
    } catch {
      return defaultValue;
    }
  });

  useEffect(() => {
    if (!isBrowser()) return;
    try {
      window.localStorage.setItem(key, JSON.stringify(value));
    } catch {
      // ignore write errors (private mode, quota, etc.)
    }
  }, [key, value]);

  return [value, setValue];
};
