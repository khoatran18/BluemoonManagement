import React, { useEffect, useState, useRef } from 'react';
import './Toast.css';

export default function Toast({ open, message, variant = 'success', duration = 4000, onClose }) {
  const [visible, setVisible] = useState(false);
  const [exiting, setExiting] = useState(false);
  const [hasAppeared, setHasAppeared] = useState(false);
  const exitRef = useRef(null);

  useEffect(() => {
    if (open) {
      setExiting(false);
      setVisible(true);
      if (!hasAppeared) setHasAppeared(true);
    }
  }, [open, hasAppeared]);

  useEffect(() => {
    if (!visible) return;

    const hideTimer = setTimeout(() => {
      setExiting(true);
      exitRef.current = setTimeout(() => {
        setVisible(false);
        if (onClose) onClose();
      }, 300);
    }, duration);

    return () => {
      clearTimeout(hideTimer);
      if (exitRef.current) {
        clearTimeout(exitRef.current);
        exitRef.current = null;
      }
    };
  }, [visible, duration, onClose]);

  if (!visible && !open) return null;

  const cls = `toast ${variant === 'error' ? 'toast-error' : 'toast-success'} ${exiting ? 'toast-exit' : hasAppeared ? 'toast-enter' : ''}`;

  return (
    <div className={cls} role="status" aria-live="polite">
      <div className="toast-icon" aria-hidden>
        {variant === 'error' ? (
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none">
            <path d="M6 6L18 18M6 18L18 6"
              stroke="currentColor"
              strokeWidth="2"
              strokeLinecap="round"
            />
          </svg>
        ) : (
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none">
            <path d="M20 6L9 17l-5-5"
              stroke="currentColor"
              strokeWidth="2"
              strokeLinecap="round"
            />
          </svg>
        )}
      </div>

      <div className="toast-body">{message}</div>

      <div className="toast-progress" style={{ ['--duration']: duration + 'ms' }} />
    </div>
  );
}
