import { createContext, useContext, useState, useCallback } from 'react';

const ToastContext = createContext(null);

let toastIdCounter = 0;

const typeConfig = {
  success: {
    bg: 'bg-green-500/10 border-green-500/30',
    icon: (
      <svg className="h-5 w-5 text-green-400" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
        <path strokeLinecap="round" strokeLinejoin="round" d="M5 13l4 4L19 7" />
      </svg>
    ),
    title: 'Success',
  },
  error: {
    bg: 'bg-red-500/10 border-red-500/30',
    icon: (
      <svg className="h-5 w-5 text-red-400" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
        <path strokeLinecap="round" strokeLinejoin="round" d="M6 18L18 6M6 6l12 12" />
      </svg>
    ),
    title: 'Error',
  },
  warning: {
    bg: 'bg-amber-500/10 border-amber-500/30',
    icon: (
      <svg className="h-5 w-5 text-amber-400" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
        <path strokeLinecap="round" strokeLinejoin="round" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-2.5L13.732 4c-.77-.833-1.964-.833-2.732 0L3.34 16.5c-.77.833.192 2.5 1.732 2.5z" />
      </svg>
    ),
    title: 'Warning',
  },
  info: {
    bg: 'bg-blue-500/10 border-blue-500/30',
    icon: (
      <svg className="h-5 w-5 text-blue-400" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
        <path strokeLinecap="round" strokeLinejoin="round" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
      </svg>
    ),
    title: 'Info',
  },
};

export const useToast = () => {
  const context = useContext(ToastContext);
  if (!context) {
    throw new Error('useToast must be used within a ToastProvider');
  }
  return context;
};

export const ToastProvider = ({ children }) => {
  const [toasts, setToasts] = useState([]);

  const removeToast = useCallback((id) => {
    setToasts((prev) => prev.filter((t) => t.id !== id));
  }, []);

  const addToast = useCallback((message, type = 'info', duration = 5000) => {
    const id = ++toastIdCounter;
    const toast = { id, message, type };

    setToasts((prev) => [...prev, toast]);

    if (duration > 0) {
      setTimeout(() => removeToast(id), duration);
    }

    return id;
  }, [removeToast]);

  const toast = useCallback(
    {
      success: (message, duration) => addToast(message, 'success', duration),
      error: (message, duration) => addToast(message, 'error', duration),
      warning: (message, duration) => addToast(message, 'warning', duration),
      info: (message, duration) => addToast(message, 'info', duration),
    },
    [addToast]
  );

  return (
    <ToastContext.Provider value={toast}>
      {children}

      {/* Toast container - top right */}
      <div className="fixed top-4 right-4 z-[100] flex flex-col gap-3 pointer-events-none">
        {toasts.map((t) => {
          const config = typeConfig[t.type] || typeConfig.info;
          return (
            <div
              key={t.id}
              className={`pointer-events-auto w-80 max-w-[calc(100vw-2rem)] rounded-lg border ${config.bg} bg-gray-800 p-4 shadow-xl animate-[slideInRight_0.3s_ease-out]`}
            >
              <div className="flex items-start gap-3">
                <div className="flex-shrink-0 mt-0.5">{config.icon}</div>
                <div className="flex-1 min-w-0">
                  <p className="text-sm font-medium text-white">{config.title}</p>
                  <p className="mt-1 text-sm text-gray-300">{t.message}</p>
                </div>
                <button
                  onClick={() => removeToast(t.id)}
                  className="flex-shrink-0 text-gray-500 hover:text-gray-300 transition-colors cursor-pointer"
                >
                  <svg className="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                    <path strokeLinecap="round" strokeLinejoin="round" d="M6 18L18 6M6 6l12 12" />
                  </svg>
                </button>
              </div>
            </div>
          );
        })}
      </div>

      <style>{`
        @keyframes slideInRight {
          from { opacity: 0; transform: translateX(100%); }
          to { opacity: 1; transform: translateX(0); }
        }
      `}</style>
    </ToastContext.Provider>
  );
};
