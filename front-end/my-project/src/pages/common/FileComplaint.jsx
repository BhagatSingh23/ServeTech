import { useState, useEffect } from 'react';
import DashboardLayout from '../../components/common/DashboardLayout';
import LoadingSpinner from '../../components/common/LoadingSpinner';
import EmptyState from '../../components/common/EmptyState';
import Badge from '../../components/common/Badge';
import Button from '../../components/common/Button';
import Modal from '../../components/common/Modal';
import { useToast } from '../../components/common/Toast';
import { fileComplaint, getMyComplaints } from '../../api/complaints';
import { formatDate, timeAgo } from '../../utils/formatDate';

const COMPLAINT_TYPES = [
  { value: 'UNFAIR_PAYMENT', label: 'Unfair Payment' },
  { value: 'DELAYED_PAYMENT', label: 'Delayed Payment' },
  { value: 'INCOMPLETE_PAYMENT', label: 'Incomplete Payment' },
  { value: 'POOR_WORK_QUALITY', label: 'Poor Work Quality' },
  { value: 'UNPROFESSIONAL_BEHAVIOR', label: 'Unprofessional Behavior' },
  { value: 'SAFETY_VIOLATION', label: 'Safety Violation' },
  { value: 'CONTRACT_BREACH', label: 'Contract Breach' },
  { value: 'HARASSMENT', label: 'Harassment' },
  { value: 'DISCRIMINATION', label: 'Discrimination' },
  { value: 'WORK_ENVIRONMENT', label: 'Work Environment' },
  { value: 'COMMUNICATION_ISSUES', label: 'Communication Issues' },
  { value: 'OTHER', label: 'Other' },
];

const PRIORITY_OPTIONS = [
  { value: 'LOW', label: 'Low' },
  { value: 'MEDIUM', label: 'Medium' },
  { value: 'HIGH', label: 'High' },
  { value: 'CRITICAL', label: 'Critical' },
];

const STATUS_COLORS = {
  SUBMITTED: 'bg-blue-500/20 text-blue-400',
  UNDER_REVIEW: 'bg-amber-500/20 text-amber-400',
  INVESTIGATING: 'bg-purple-500/20 text-purple-400',
  RESOLVED: 'bg-green-500/20 text-green-400',
  REJECTED: 'bg-red-500/20 text-red-400',
  CLOSED: 'bg-slate-500/20 text-slate-400',
  ESCALATED: 'bg-orange-500/20 text-orange-400',
};

const FileComplaintPage = () => {
  const [complaints, setComplaints] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [detailModal, setDetailModal] = useState({ open: false, complaint: null });

  const [formData, setFormData] = useState({
    accusedId: '',
    assignmentId: '',
    complaintType: '',
    subject: '',
    description: '',
    priority: 'MEDIUM',
  });

  const [errors, setErrors] = useState({});
  const toast = useToast();

  useEffect(() => {
    fetchComplaints();
  }, []);

  const fetchComplaints = async () => {
    setLoading(true);
    try {
      const response = await getMyComplaints();
      setComplaints(response.data.data || []);
    } catch {
      toast.error('Failed to load complaints');
    } finally {
      setLoading(false);
    }
  };

  const validate = () => {
    const errs = {};
    if (!formData.accusedId) errs.accusedId = 'Please enter the ID of the person you are complaining against';
    if (!formData.complaintType) errs.complaintType = 'Please select a complaint type';
    if (!formData.subject.trim()) errs.subject = 'Subject is required';
    if (!formData.description.trim()) errs.description = 'Description is required';
    if (formData.description.trim().length < 20) errs.description = 'Please provide more detail (at least 20 characters)';
    setErrors(errs);
    return Object.keys(errs).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!validate()) return;

    setSubmitting(true);
    try {
      const payload = {
        accusedId: Number(formData.accusedId),
        complaintType: formData.complaintType,
        subject: formData.subject,
        description: formData.description,
        priority: formData.priority,
      };
      if (formData.assignmentId) {
        payload.assignmentId = Number(formData.assignmentId);
      }

      await fileComplaint(payload);
      toast.success('Complaint filed successfully!');
      setFormData({
        accusedId: '',
        assignmentId: '',
        complaintType: '',
        subject: '',
        description: '',
        priority: 'MEDIUM',
      });
      setShowForm(false);
      fetchComplaints();
    } catch (error) {
      toast.error(error.response?.data?.message || 'Failed to file complaint');
    } finally {
      setSubmitting(false);
    }
  };

  const handleChange = (field, value) => {
    setFormData(prev => ({ ...prev, [field]: value }));
    if (errors[field]) setErrors(prev => ({ ...prev, [field]: '' }));
  };

  const formatStatus = (status) => {
    return (status || '').replace(/_/g, ' ');
  };

  return (
    <DashboardLayout pageTitle="Complaints">
      <div className="max-w-4xl mx-auto space-y-6">

        {/* Header */}
        <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4">
          <div>
            <h2 className="text-xl font-semibold text-white">My Complaints</h2>
            <p className="text-sm text-slate-400 mt-1">File and track your complaints</p>
          </div>
          <Button
            variant="primary"
            onClick={() => setShowForm(!showForm)}
          >
            {showForm ? '✕ Cancel' : '📢 File a Complaint'}
          </Button>
        </div>

        {/* File Complaint Form */}
        {showForm && (
          <div className="bg-slate-800/80 backdrop-blur-xl rounded-2xl border border-slate-700/80 p-6 shadow-xl animate-[fadeIn_0.2s_ease-out]">
            <h3 className="text-lg font-semibold text-white mb-5 flex items-center gap-2">
              <span className="text-2xl">📋</span> New Complaint
            </h3>
            <form onSubmit={handleSubmit} className="space-y-5">
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-5">
                {/* Accused User ID */}
                <div>
                  <label className="block text-sm font-medium text-slate-300 mb-1.5">Accused User ID *</label>
                  <input
                    type="number"
                    value={formData.accusedId}
                    onChange={(e) => handleChange('accusedId', e.target.value)}
                    placeholder="Enter user ID"
                    className="w-full px-4 py-2.5 bg-slate-900/80 border border-slate-600 rounded-lg text-white text-sm focus:ring-2 focus:ring-amber-500 focus:border-transparent outline-none transition-all placeholder:text-slate-500"
                  />
                  {errors.accusedId && <p className="text-xs text-red-400 mt-1">{errors.accusedId}</p>}
                </div>

                {/* Assignment ID (optional) */}
                <div>
                  <label className="block text-sm font-medium text-slate-300 mb-1.5">Assignment ID <span className="text-slate-500">(optional)</span></label>
                  <input
                    type="number"
                    value={formData.assignmentId}
                    onChange={(e) => handleChange('assignmentId', e.target.value)}
                    placeholder="Related assignment"
                    className="w-full px-4 py-2.5 bg-slate-900/80 border border-slate-600 rounded-lg text-white text-sm focus:ring-2 focus:ring-amber-500 focus:border-transparent outline-none transition-all placeholder:text-slate-500"
                  />
                </div>

                {/* Complaint Type */}
                <div>
                  <label className="block text-sm font-medium text-slate-300 mb-1.5">Complaint Type *</label>
                  <select
                    value={formData.complaintType}
                    onChange={(e) => handleChange('complaintType', e.target.value)}
                    className="w-full px-4 py-2.5 bg-slate-900/80 border border-slate-600 rounded-lg text-white text-sm focus:ring-2 focus:ring-amber-500 focus:border-transparent outline-none transition-all appearance-none cursor-pointer"
                  >
                    <option value="">Select type...</option>
                    {COMPLAINT_TYPES.map(opt => (
                      <option key={opt.value} value={opt.value}>{opt.label}</option>
                    ))}
                  </select>
                  {errors.complaintType && <p className="text-xs text-red-400 mt-1">{errors.complaintType}</p>}
                </div>

                {/* Priority */}
                <div>
                  <label className="block text-sm font-medium text-slate-300 mb-1.5">Priority</label>
                  <select
                    value={formData.priority}
                    onChange={(e) => handleChange('priority', e.target.value)}
                    className="w-full px-4 py-2.5 bg-slate-900/80 border border-slate-600 rounded-lg text-white text-sm focus:ring-2 focus:ring-amber-500 focus:border-transparent outline-none transition-all appearance-none cursor-pointer"
                  >
                    {PRIORITY_OPTIONS.map(opt => (
                      <option key={opt.value} value={opt.value}>{opt.label}</option>
                    ))}
                  </select>
                </div>
              </div>

              {/* Subject */}
              <div>
                <label className="block text-sm font-medium text-slate-300 mb-1.5">Subject *</label>
                <input
                  type="text"
                  value={formData.subject}
                  onChange={(e) => handleChange('subject', e.target.value)}
                  placeholder="Brief summary of your complaint"
                  maxLength={200}
                  className="w-full px-4 py-2.5 bg-slate-900/80 border border-slate-600 rounded-lg text-white text-sm focus:ring-2 focus:ring-amber-500 focus:border-transparent outline-none transition-all placeholder:text-slate-500"
                />
                {errors.subject && <p className="text-xs text-red-400 mt-1">{errors.subject}</p>}
              </div>

              {/* Description */}
              <div>
                <label className="block text-sm font-medium text-slate-300 mb-1.5">Description *</label>
                <textarea
                  value={formData.description}
                  onChange={(e) => handleChange('description', e.target.value)}
                  placeholder="Provide detailed information about the issue..."
                  rows={5}
                  maxLength={3000}
                  className="w-full px-4 py-2.5 bg-slate-900/80 border border-slate-600 rounded-lg text-white text-sm focus:ring-2 focus:ring-amber-500 focus:border-transparent outline-none transition-all placeholder:text-slate-500 resize-none"
                />
                <div className="flex justify-between mt-1">
                  {errors.description && <p className="text-xs text-red-400">{errors.description}</p>}
                  <p className="text-xs text-slate-500 ml-auto">{formData.description.length}/3000</p>
                </div>
              </div>

              {/* Submit */}
              <div className="flex justify-end gap-3 pt-2">
                <Button variant="ghost" onClick={() => setShowForm(false)}>Cancel</Button>
                <Button variant="primary" type="submit" disabled={submitting}>
                  {submitting ? 'Submitting...' : 'Submit Complaint'}
                </Button>
              </div>
            </form>
          </div>
        )}

        {/* Complaints List */}
        {loading ? (
          <LoadingSpinner />
        ) : complaints.length === 0 ? (
          <EmptyState
            icon="📢"
            title="No complaints filed"
            description="If you face any issues, you can file a complaint using the button above."
          />
        ) : (
          <div className="space-y-3">
            {complaints.map((c) => (
              <div
                key={c.id}
                className="bg-slate-800/60 backdrop-blur-xl rounded-xl border border-slate-700/60 p-5 hover:border-slate-600 transition-all cursor-pointer"
                onClick={() => setDetailModal({ open: true, complaint: c })}
              >
                <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3">
                  <div className="flex-1 min-w-0">
                    <div className="flex items-center gap-2 mb-1.5">
                      <span className={`inline-flex items-center px-2 py-0.5 rounded-full text-xs font-medium ${STATUS_COLORS[c.status] || 'bg-slate-500/20 text-slate-400'}`}>
                        {formatStatus(c.status)}
                      </span>
                      <span className="text-xs text-slate-500">{c.complaintId}</span>
                    </div>
                    <h4 className="text-sm font-semibold text-white truncate">{c.subject}</h4>
                    <p className="text-xs text-slate-400 mt-1 line-clamp-2">{c.description}</p>
                  </div>
                  <div className="flex flex-col items-end gap-1 flex-shrink-0">
                    <span className="text-xs text-slate-500">{formatDate(c.createdAt)}</span>
                    <span className="text-xs text-slate-400">Against: {c.accusedName || '—'}</span>
                  </div>
                </div>
              </div>
            ))}
          </div>
        )}

        {/* Detail Modal */}
        <Modal
          isOpen={detailModal.open}
          onClose={() => setDetailModal({ open: false, complaint: null })}
          title="Complaint Details"
        >
          {detailModal.complaint && (
            <div className="space-y-4">
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <p className="text-xs text-slate-400 mb-0.5">Complaint ID</p>
                  <p className="text-sm text-white font-medium">{detailModal.complaint.complaintId}</p>
                </div>
                <div>
                  <p className="text-xs text-slate-400 mb-0.5">Status</p>
                  <span className={`inline-flex items-center px-2 py-0.5 rounded-full text-xs font-medium ${STATUS_COLORS[detailModal.complaint.status] || 'bg-slate-500/20 text-slate-400'}`}>
                    {formatStatus(detailModal.complaint.status)}
                  </span>
                </div>
                <div>
                  <p className="text-xs text-slate-400 mb-0.5">Category</p>
                  <p className="text-sm text-white">{formatStatus(detailModal.complaint.category)}</p>
                </div>
                <div>
                  <p className="text-xs text-slate-400 mb-0.5">Priority</p>
                  <p className="text-sm text-white">{detailModal.complaint.priority}</p>
                </div>
                <div>
                  <p className="text-xs text-slate-400 mb-0.5">Against</p>
                  <p className="text-sm text-white">{detailModal.complaint.accusedName || '—'}</p>
                </div>
                <div>
                  <p className="text-xs text-slate-400 mb-0.5">Filed At</p>
                  <p className="text-sm text-white">{formatDate(detailModal.complaint.createdAt)}</p>
                </div>
              </div>

              <div className="border-t border-slate-700 pt-4">
                <p className="text-xs text-slate-400 mb-1">Subject</p>
                <p className="text-sm text-white font-medium">{detailModal.complaint.subject}</p>
              </div>

              <div>
                <p className="text-xs text-slate-400 mb-1">Description</p>
                <p className="text-sm text-slate-300 whitespace-pre-wrap">{detailModal.complaint.description}</p>
              </div>

              {detailModal.complaint.assignedAdminName && (
                <div className="border-t border-slate-700 pt-4">
                  <p className="text-xs text-slate-400 mb-1">Assigned Admin</p>
                  <p className="text-sm text-white">{detailModal.complaint.assignedAdminName}</p>
                </div>
              )}

              {detailModal.complaint.resolution && (
                <div className="border-t border-slate-700 pt-4">
                  <p className="text-xs text-slate-400 mb-1">Resolution</p>
                  <p className="text-sm text-slate-300 whitespace-pre-wrap">{detailModal.complaint.resolution}</p>
                </div>
              )}
            </div>
          )}
        </Modal>
      </div>

      <style>{`
        @keyframes fadeIn {
          from { opacity: 0; transform: translateY(-8px); }
          to { opacity: 1; transform: translateY(0); }
        }
      `}</style>
    </DashboardLayout>
  );
};

export default FileComplaintPage;
