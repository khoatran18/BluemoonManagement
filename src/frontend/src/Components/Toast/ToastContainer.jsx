import React, { useState, useImperativeHandle, forwardRef } from 'react';
import Toast from './Toast';

let idCounter = 1;

const ToastContainer = forwardRef(function ToastContainer(props, ref) {
  const [toasts, setToasts] = useState([]);

  useImperativeHandle(ref, () => ({
    addToast: (toast) => {
      const id = `t_${Date.now()}_${idCounter++}`;
      const t = { id, message: toast.message || '', variant: toast.variant || 'success', duration: toast.duration || 4000 };
      // Replace behavior: push new toast on top; keep multiple
      setToasts((prev) => [t, ...prev]);
      return id;
    },
    removeToast: (id) => {
      setToasts((prev) => prev.filter(t => t.id !== id));
    }
  }));

  const handleClose = (id) => {
    setToasts((prev) => prev.filter(t => t.id !== id));
  };

  return (
    <div className="toast-wrapper" aria-live="polite" aria-atomic="true">
      {toasts.map(t => (
        <Toast key={t.id} open={true} message={t.message} variant={t.variant} duration={t.duration} onClose={() => handleClose(t.id)} />
      ))}
    </div>
  );
});

export default ToastContainer;
