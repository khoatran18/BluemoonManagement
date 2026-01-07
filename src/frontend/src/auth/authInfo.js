import { getAccessToken } from "./tokenStorage";
import { getAccountIdFromToken, getRoleFromToken } from "./jwt";

export const ROLE = {
  Admin: "Admin",
  FeeCollector: "FeeCollector",
  Citizen: "Citizen",
};

export function getCurrentRole() {
  const token = getAccessToken();
  const role = getRoleFromToken(token);
  if (role === ROLE.Admin || role === ROLE.FeeCollector || role === ROLE.Citizen) return role;
  return null;
}

export function getCurrentAccountId() {
  const token = getAccessToken();
  return getAccountIdFromToken(token);
}
