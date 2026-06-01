import Button from './Button';

const EmptyState = ({ icon = '📭', title, description, actionLabel, onAction }) => {
  return (
    <div className="flex flex-col items-center justify-center py-16 px-6 text-center">
      <div className="flex items-center justify-center h-20 w-20 rounded-2xl bg-gray-800 border border-gray-700 mb-5">
        <span className="text-4xl">{icon}</span>
      </div>
      <h3 className="text-lg font-semibold text-white mb-2">{title}</h3>
      {description && (
        <p className="text-sm text-gray-400 max-w-sm mb-6">{description}</p>
      )}
      {actionLabel && onAction && (
        <Button variant="primary" size="md" onClick={onAction}>
          {actionLabel}
        </Button>
      )}
    </div>
  );
};

export default EmptyState;
