import { getStatusColors, formatStatus } from '../../utils/statusColors';

const Badge = ({ status, type = 'default' }) => {
  const typeMap = {
    default: 'workRequest',
    workRequest: 'workRequest',
    workProgress: 'workProgress',
    payment: 'payment',
    application: 'application',
    complaint: 'complaint',
    account: 'account',
  };

  const colors = getStatusColors(typeMap[type] || 'workRequest', status);

  return (
    <span
      className={`inline-flex items-center gap-1.5 rounded-full px-2.5 py-1 text-xs font-medium ${colors.bg} ${colors.text}`}
    >
      <span className={`h-1.5 w-1.5 rounded-full ${colors.dot}`} />
      {formatStatus(status)}
    </span>
  );
};

export default Badge;
