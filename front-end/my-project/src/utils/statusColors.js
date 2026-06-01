/**
 * Status color maps for all entity statuses in ServeTech.
 * Each status maps to { bg, text, dot } Tailwind classes.
 */

// Work Request statuses
export const workRequestStatusColors = {
  DRAFT: { bg: 'bg-gray-500/20', text: 'text-gray-400', dot: 'bg-gray-400' },
  OPEN: { bg: 'bg-blue-500/20', text: 'text-blue-400', dot: 'bg-blue-400' },
  IN_PROGRESS: { bg: 'bg-amber-500/20', text: 'text-amber-400', dot: 'bg-amber-400' },
  COMPLETED: { bg: 'bg-green-500/20', text: 'text-green-400', dot: 'bg-green-400' },
  CANCELLED: { bg: 'bg-red-500/20', text: 'text-red-400', dot: 'bg-red-400' },
  CLOSED: { bg: 'bg-gray-500/20', text: 'text-gray-400', dot: 'bg-gray-400' },
};

// Work Progress statuses
export const workProgressStatusColors = {
  NOT_STARTED: { bg: 'bg-gray-500/20', text: 'text-gray-400', dot: 'bg-gray-400' },
  IN_PROGRESS: { bg: 'bg-amber-500/20', text: 'text-amber-400', dot: 'bg-amber-400' },
  ON_HOLD: { bg: 'bg-orange-500/20', text: 'text-orange-400', dot: 'bg-orange-400' },
  COMPLETED: { bg: 'bg-green-500/20', text: 'text-green-400', dot: 'bg-green-400' },
  CANCELLED: { bg: 'bg-red-500/20', text: 'text-red-400', dot: 'bg-red-400' },
};

// Payment statuses
export const paymentStatusColors = {
  PENDING: { bg: 'bg-amber-500/20', text: 'text-amber-400', dot: 'bg-amber-400' },
  PAID: { bg: 'bg-green-500/20', text: 'text-green-400', dot: 'bg-green-400' },
  FAILED: { bg: 'bg-red-500/20', text: 'text-red-400', dot: 'bg-red-400' },
  REFUNDED: { bg: 'bg-purple-500/20', text: 'text-purple-400', dot: 'bg-purple-400' },
};

// Application statuses
export const applicationStatusColors = {
  PENDING: { bg: 'bg-amber-500/20', text: 'text-amber-400', dot: 'bg-amber-400' },
  ACCEPTED: { bg: 'bg-green-500/20', text: 'text-green-400', dot: 'bg-green-400' },
  REJECTED: { bg: 'bg-red-500/20', text: 'text-red-400', dot: 'bg-red-400' },
  WITHDRAWN: { bg: 'bg-gray-500/20', text: 'text-gray-400', dot: 'bg-gray-400' },
};

// Complaint statuses
export const complaintStatusColors = {
  OPEN: { bg: 'bg-blue-500/20', text: 'text-blue-400', dot: 'bg-blue-400' },
  IN_PROGRESS: { bg: 'bg-amber-500/20', text: 'text-amber-400', dot: 'bg-amber-400' },
  RESOLVED: { bg: 'bg-green-500/20', text: 'text-green-400', dot: 'bg-green-400' },
  DISMISSED: { bg: 'bg-gray-500/20', text: 'text-gray-400', dot: 'bg-gray-400' },
};

// Account statuses
export const accountStatusColors = {
  ACTIVE: { bg: 'bg-green-500/20', text: 'text-green-400', dot: 'bg-green-400' },
  INACTIVE: { bg: 'bg-gray-500/20', text: 'text-gray-400', dot: 'bg-gray-400' },
  SUSPENDED: { bg: 'bg-red-500/20', text: 'text-red-400', dot: 'bg-red-400' },
  PENDING_VERIFICATION: { bg: 'bg-amber-500/20', text: 'text-amber-400', dot: 'bg-amber-400' },
};

// Default fallback
const defaultStatusColor = { bg: 'bg-gray-500/20', text: 'text-gray-400', dot: 'bg-gray-400' };

/**
 * Get status colors by type and status value.
 * @param {string} type - One of: 'workRequest', 'workProgress', 'payment', 'application', 'complaint', 'account'
 * @param {string} status - The status value
 * @returns {{ bg: string, text: string, dot: string }}
 */
export const getStatusColors = (type, status) => {
  const colorMaps = {
    workRequest: workRequestStatusColors,
    workProgress: workProgressStatusColors,
    payment: paymentStatusColors,
    application: applicationStatusColors,
    complaint: complaintStatusColors,
    account: accountStatusColors,
  };

  const map = colorMaps[type];
  if (!map) return defaultStatusColor;

  return map[status] || defaultStatusColor;
};

/**
 * Format status string for display (replace underscores with spaces, title case)
 * @param {string} status - Raw status string like 'IN_PROGRESS'
 * @returns {string} Formatted like 'In Progress'
 */
export const formatStatus = (status) => {
  if (!status) return '—';
  return status
    .replace(/_/g, ' ')
    .toLowerCase()
    .replace(/\b\w/g, (c) => c.toUpperCase());
};
