import React from "react";
import { Navigate } from "react-router-dom";
import { getCurrentRole, ROLE } from "./authInfo";

export default function RoleLanding() {
  const role = getCurrentRole();

  if (role === ROLE.Citizen) return <Navigate to="/my-apartment" replace />;
  if (role === ROLE.FeeCollector) return <Navigate to="/overview" replace />;
  if (role === ROLE.Admin) return <Navigate to="/overview" replace />;

  // Fallback: behave like admin landing.
  return <Navigate to="/overview" replace />;
}
