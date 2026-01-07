import React from "react";
import { Navigate, useLocation } from "react-router-dom";
import { getCurrentRole } from "./authInfo";

export default function RequireRole({ allowedRoles, children }) {
  const location = useLocation();
  const role = getCurrentRole();

  if (!Array.isArray(allowedRoles) || allowedRoles.length === 0) return children;

  if (!role || !allowedRoles.includes(role)) {
    return <Navigate to="/" replace state={{ from: location }} />;
  }

  return children;
}
