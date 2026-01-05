import React from "react";
import "./LoadingSpinner.css";

export default function LoadingSpinner({ className = "" }) {
  const classes = ["loading-spinner", className].filter(Boolean).join(" ");

  return (
    <div className={classes} role="status" aria-label="Loading">
      <div className="loading-spinner__circle" />
    </div>
  );
}
