const colorClasses = {
  amber: {
    border: 'border-l-amber-500',
    iconBg: 'bg-amber-500/10',
    iconText: 'text-amber-400',
    trend: 'text-amber-400',
  },
  green: {
    border: 'border-l-green-500',
    iconBg: 'bg-green-500/10',
    iconText: 'text-green-400',
    trend: 'text-green-400',
  },
  blue: {
    border: 'border-l-blue-500',
    iconBg: 'bg-blue-500/10',
    iconText: 'text-blue-400',
    trend: 'text-blue-400',
  },
  red: {
    border: 'border-l-red-500',
    iconBg: 'bg-red-500/10',
    iconText: 'text-red-400',
    trend: 'text-red-400',
  },
};

const StatsCard = ({ icon, label, value, trend, color = 'amber' }) => {
  const colors = colorClasses[color] || colorClasses.amber;
  const isPositive = trend && trend.startsWith('+');

  return (
    <div
      className={`bg-slate-800 rounded-xl shadow-lg border border-slate-700 border-l-4 ${colors.border} p-5 transition-all duration-200 hover:shadow-xl hover:border-slate-600`}
    >
      <div className="flex items-start justify-between">
        <div className="flex-1 min-w-0 pr-3">
          <p className="text-sm font-medium text-slate-400 truncate">{label}</p>
          <p className="mt-2 text-3xl font-bold text-white tracking-tight truncate">{value}</p>
          {trend && (
            <div className="mt-2 flex items-center gap-1">
              <span className={`text-xs font-medium ${isPositive ? 'text-green-400' : 'text-red-400'}`}>
                {trend}
              </span>
              <span className="text-xs text-slate-500">vs last month</span>
            </div>
          )}
        </div>
        <div className={`flex-shrink-0 h-12 w-12 rounded-lg ${colors.iconBg} flex items-center justify-center`}>
          <span className={`text-2xl ${colors.iconText}`}>{icon}</span>
        </div>
      </div>
    </div>
  );
};

export default StatsCard;
