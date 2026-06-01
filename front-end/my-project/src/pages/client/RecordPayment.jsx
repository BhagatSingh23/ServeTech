import { useState, useEffect } from 'react';
import DashboardLayout from '../../components/common/DashboardLayout';
import LoadingSpinner from '../../components/common/LoadingSpinner';
import EmptyState from '../../components/common/EmptyState';
import Badge from '../../components/common/Badge';
import Button from '../../components/common/Button';
import { useToast } from '../../components/common/Toast';
import { recordPayment, getClientPayments, getPaymentsForAssignment } from '../../api/payments';
import api from '../../api/axios';
import { formatCurrency } from '../../utils/formatCurrency';
import { formatDate } from '../../utils/formatDate';

const PAYMENT_METHODS = [
  { value: 'CASH', label: '💵 Cash' },
  { value: 'UPI', label: '📱 UPI' },
  { value: 'BANK_TRANSFER', label: '🏦 Bank Transfer' },
  { value: 'OTHER', label: '📋 Other' },
];

const RecordPayment = () => {
  const [assignments, setAssignments] = useState([]);
  const [selectedAssignment, setSelectedAssignment] = useState('');
  const [paymentHistory, setPaymentHistory] = useState([]);
  const [historyLoading, setHistoryLoading] = useState(false);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);

  const [formData, setFormData] = useState({
    amount: '',
    paymentMethod: 'CASH',
    referenceNumber: '',
    notes: '',
  });

  const [errors, setErrors] = useState({});
  const toast = useToast();

  useEffect(() => {
    fetchAssignments();
  }, []);

  const fetchAssignments = async () => {
    try {
      // Fetch assignments that the client has
      const response = await api.get('/client/assignments');
      setAssignments(response.data.data || []);
    } catch {
      toast.error('Failed to load assignments');
    } finally {
      setLoading(false);
    }
  };

  const handleAssignmentChange = async (assignmentId) => {
    setSelectedAssignment(assignmentId);
    setFormData((prev) => ({ ...prev, amount: '' }));
    if (assignmentId) {
      setHistoryLoading(true);
      try {
        const response = await getPaymentsForAssignment(assignmentId);
        setPaymentHistory(response.data.data || []);
      } catch {
        setPaymentHistory([]);
      } finally {
        setHistoryLoading(false);
      }
    } else {
      setPaymentHistory([]);
    }
  };

  const getSelectedAssignmentData = () => {
    return assignments.find(
      (a) => String(a.assignmentId || a.id) === String(selectedAssignment)
    );
  };

  const validate = () => {
    const newErrors = {};
    if (!selectedAssignment) newErrors.assignment = 'Select an assignment';
    if (!formData.amount || Number(formData.amount) <= 0) newErrors.amount = 'Enter a valid amount';

    const assignment = getSelectedAssignmentData();
    if (assignment && Number(formData.amount) > (assignment.pendingAmount || assignment.totalAmount || Infinity)) {
      newErrors.amount = 'Amount exceeds pending balance';
    }

    if (!formData.paymentMethod) newErrors.paymentMethod = 'Select a payment method';
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!validate()) return;

    setSubmitting(true);
    try {
      await recordPayment({
        assignmentId: Number(selectedAssignment),
        amount: Number(formData.amount),
        paymentMethod: formData.paymentMethod,
        referenceNumber: formData.referenceNumber.trim() || undefined,
        notes: formData.notes.trim() || undefined,
      });
      toast.success('Payment recorded successfully!');
      setFormData({ amount: '', paymentMethod: 'CASH', referenceNumber: '', notes: '' });
      handleAssignmentChange(selectedAssignment);
      fetchAssignments();
    } catch (error) {
      toast.error(error.response?.data?.message || 'Failed to record payment');
    } finally {
      setSubmitting(false);
    }
  };

  const assignment = getSelectedAssignmentData();

  if (loading) {
    return (
      <DashboardLayout pageTitle="Record Payment">
        <LoadingSpinner text="Loading assignments..." />
      </DashboardLayout>
    );
  }

  return (
    <DashboardLayout pageTitle="Record Payment">
      <div className="max-w-3xl mx-auto space-y-6">
        {/* Payment Form */}
        <div className="bg-slate-800 rounded-xl shadow-lg border border-slate-700 p-6">
          <h3 className="text-lg font-semibold text-white mb-4">💰 Record a Payment</h3>

          <form onSubmit={handleSubmit} className="space-y-4">
            {/* Assignment Selector */}
            <div>
              <label className="block text-sm font-medium text-slate-300 mb-1.5">Select Assignment</label>
              <select
                value={selectedAssignment}
                onChange={(e) => handleAssignmentChange(e.target.value)}
                className="w-full bg-slate-700 border border-slate-600 text-white rounded-lg px-4 py-2.5 focus:border-amber-400 focus:ring-1 focus:ring-amber-400 outline-none transition-all"
              >
                <option value="">Choose an assignment...</option>
                {assignments.map((a) => (
                  <option key={a.assignmentId || a.id} value={a.assignmentId || a.id}>
                    {a.workerName || 'Worker'} — {a.jobTitle || a.workRequestTitle || 'Job'} (Pending: {formatCurrency(a.pendingAmount || 0)})
                  </option>
                ))}
              </select>
              {errors.assignment && <p className="text-red-400 text-xs mt-1">{errors.assignment}</p>}
            </div>

            {/* Assignment Info */}
            {assignment && (
              <div className="bg-slate-700/50 rounded-lg p-4 border border-slate-600">
                <div className="grid grid-cols-2 sm:grid-cols-3 gap-3 text-sm">
                  <div>
                    <p className="text-xs text-slate-400">Worker</p>
                    <p className="text-white font-medium">{assignment.workerName || '—'}</p>
                  </div>
                  <div>
                    <p className="text-xs text-slate-400">Total Amount</p>
                    <p className="text-white">{formatCurrency(assignment.totalAmount || 0)}</p>
                  </div>
                  <div>
                    <p className="text-xs text-slate-400">Pending</p>
                    <p className="text-red-400 font-semibold">{formatCurrency(assignment.pendingAmount || 0)}</p>
                  </div>
                </div>
              </div>
            )}

            {/* Amount */}
            <div>
              <label className="block text-sm font-medium text-slate-300 mb-1.5">
                Amount (₹)
                {assignment?.pendingAmount && (
                  <span className="text-xs text-slate-500 ml-2">Max: {formatCurrency(assignment.pendingAmount)}</span>
                )}
              </label>
              <input
                type="number"
                value={formData.amount}
                onChange={(e) => {
                  setFormData((prev) => ({ ...prev, amount: e.target.value }));
                  setErrors((prev) => ({ ...prev, amount: '' }));
                }}
                min="1"
                max={assignment?.pendingAmount || undefined}
                placeholder="Enter payment amount"
                className="w-full bg-slate-700 border border-slate-600 text-white rounded-lg px-4 py-2.5 focus:border-amber-400 focus:ring-1 focus:ring-amber-400 outline-none transition-all placeholder-slate-500"
              />
              {errors.amount && <p className="text-red-400 text-xs mt-1">{errors.amount}</p>}
            </div>

            {/* Payment Method */}
            <div>
              <label className="block text-sm font-medium text-slate-300 mb-2">Payment Method</label>
              <div className="grid grid-cols-2 sm:grid-cols-4 gap-2">
                {PAYMENT_METHODS.map((method) => (
                  <button
                    key={method.value}
                    type="button"
                    onClick={() => setFormData((prev) => ({ ...prev, paymentMethod: method.value }))}
                    className={`px-3 py-2.5 rounded-lg text-sm font-medium transition-all duration-200 cursor-pointer border ${
                      formData.paymentMethod === method.value
                        ? 'bg-amber-500/10 border-amber-500 text-amber-400'
                        : 'bg-slate-700 border-slate-600 text-slate-300 hover:bg-slate-600'
                    }`}
                  >
                    {method.label}
                  </button>
                ))}
              </div>
              {errors.paymentMethod && <p className="text-red-400 text-xs mt-1">{errors.paymentMethod}</p>}
            </div>

            {/* Reference Number */}
            <div>
              <label className="block text-sm font-medium text-slate-300 mb-1.5">Reference Number (Optional)</label>
              <input
                type="text"
                value={formData.referenceNumber}
                onChange={(e) => setFormData((prev) => ({ ...prev, referenceNumber: e.target.value }))}
                placeholder="Transaction ID or reference"
                className="w-full bg-slate-700 border border-slate-600 text-white rounded-lg px-4 py-2.5 focus:border-amber-400 focus:ring-1 focus:ring-amber-400 outline-none transition-all placeholder-slate-500"
              />
            </div>

            {/* Notes */}
            <div>
              <label className="block text-sm font-medium text-slate-300 mb-1.5">Notes (Optional)</label>
              <textarea
                value={formData.notes}
                onChange={(e) => setFormData((prev) => ({ ...prev, notes: e.target.value }))}
                rows={2}
                placeholder="Any additional notes..."
                className="w-full bg-slate-700 border border-slate-600 text-white rounded-lg px-4 py-2.5 focus:border-amber-400 focus:ring-1 focus:ring-amber-400 outline-none transition-all placeholder-slate-500 resize-none"
              />
            </div>

            <Button variant="primary" type="submit" loading={submitting} fullWidth size="lg">
              Confirm Payment
            </Button>
          </form>
        </div>

        {/* Payment History */}
        {selectedAssignment && (
          <div className="bg-slate-800 rounded-xl shadow-lg border border-slate-700 p-6">
            <h3 className="text-lg font-semibold text-white mb-4">📜 Payment History</h3>
            {historyLoading ? (
              <LoadingSpinner text="Loading payment history..." size="sm" />
            ) : paymentHistory.length === 0 ? (
              <p className="text-sm text-slate-400 text-center py-6">No payments recorded yet for this assignment.</p>
            ) : (
              <div className="space-y-3">
                {paymentHistory.map((payment) => (
                  <div
                    key={payment.id || payment.paymentId}
                    className="flex items-center justify-between bg-slate-700/50 rounded-lg p-4 border border-slate-600"
                  >
                    <div className="flex items-center gap-3">
                      <div className="h-10 w-10 rounded-lg bg-green-500/10 flex items-center justify-center text-green-400">
                        💰
                      </div>
                      <div>
                        <p className="text-sm text-white font-medium">{formatCurrency(payment.amount)}</p>
                        <p className="text-xs text-slate-400">
                          {payment.paymentMethod || 'Cash'} • {formatDate(payment.paidAt || payment.createdAt)}
                        </p>
                        {payment.referenceNumber && (
                          <p className="text-xs text-slate-500">Ref: {payment.referenceNumber}</p>
                        )}
                      </div>
                    </div>
                    <Badge status={payment.status || 'PAID'} type="payment" />
                  </div>
                ))}
              </div>
            )}
          </div>
        )}
      </div>
    </DashboardLayout>
  );
};

export default RecordPayment;
