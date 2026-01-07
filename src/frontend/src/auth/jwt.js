function base64UrlDecode(input) {
  if (typeof input !== "string" || input.length === 0) return null;
  const base64 = input.replace(/-/g, "+").replace(/_/g, "/");
  const padded = base64.padEnd(base64.length + ((4 - (base64.length % 4)) % 4), "=");
  try {
    return atob(padded);
  } catch {
    return null;
  }
}

export function parseJwtPayload(token) {
  if (!token) return null;
  const parts = String(token).split(".");
  if (parts.length < 2) return null;

  const decoded = base64UrlDecode(parts[1]);
  if (!decoded) return null;

  try {
    return JSON.parse(decoded);
  } catch {
    return null;
  }
}

export function getRoleFromToken(token) {
  const payload = parseJwtPayload(token);
  const role = payload?.role;
  return typeof role === "string" ? role : null;
}

export function getAccountIdFromToken(token) {
  const payload = parseJwtPayload(token);
  const raw = payload?.accountId;
  if (raw === undefined || raw === null) return null;
  const num = Number(raw);
  return Number.isFinite(num) ? num : null;
}
