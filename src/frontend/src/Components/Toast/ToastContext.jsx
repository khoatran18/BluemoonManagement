import React, { createContext, useContext, useRef } from 'react';
import ToastContainer from './ToastContainer';

const ToastContext = createContext(null);

export const ToastProvider = ({ children }) => {
  const ref = useRef(null);

  const add = (opts) => {
    if (ref.current && typeof ref.current.addToast === 'function') {
      return ref.current.addToast(opts);
    }
  };

  const remove = (id) => {
    if (ref.current && typeof ref.current.removeToast === 'function') {
      return ref.current.removeToast(id);
    }
  };

  return (
    <ToastContext.Provider value={{ addToast: add, removeToast: remove }}>
      {children}
      <ToastContainer ref={ref} />
    </ToastContext.Provider>
  );
};

export const useToasts = () => {
  const ctx = useContext(ToastContext);
  if (!ctx) throw new Error('useToasts must be used within ToastProvider');
  return ctx;
};

export default ToastContext;
