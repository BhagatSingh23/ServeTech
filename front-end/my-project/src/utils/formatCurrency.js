/**
 * Format amount to Indian Rupee currency
 * @param {number} amount - The amount to format
 * @returns {string} Formatted currency string like '₹1,234.56'
 */
export const formatCurrency = (amount) => {
  if (amount === null || amount === undefined || isNaN(amount)) return '₹0.00';

  return new Intl.NumberFormat('en-IN', {
    style: 'currency',
    currency: 'INR',
    minimumFractionDigits: 2,
    maximumFractionDigits: 2,
  }).format(amount);
};
