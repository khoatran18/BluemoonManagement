import React from "react";
import "./Button.css";

export default function AddButton({ onClick, children, ariaLabel = "Thêm", className = "" }) {
  return (
    <button className={"primary-btn " + className} onClick={onClick} type="button" aria-label={ariaLabel}>
      <span className="icon" aria-hidden>
        <svg
          xmlns="http://www.w3.org/2000/svg"
          strokeWidth={1}
          stroke="currentColor"
          fill="currentColor"
          viewBox="0 0 24 24"
          width="18"
          height="18"
        >
          <path fillRule="evenodd" d="M12 2a1 1 0 011 1v8h8a1 1 0 110 2h-8v8a1 1 0 11-2 0v-8H3a1 1 0 110-2h8V3a1 1 0 011-1z" clipRule="evenodd" />
        </svg>
      </span>

      <span className="label">{children}</span>
    </button>
  );
}
