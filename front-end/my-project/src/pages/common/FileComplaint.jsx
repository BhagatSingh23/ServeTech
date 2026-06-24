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
  SUBMITTED: 'bg-blue-500/20 text-blue-400 border border-blue-500/30',
  UNDER_REVIEW: 'bg-amber-500/20 text-amber-400 border border-amber-500/30',
  INVESTIGATING: 'bg-purple-500/20 text-purple-400 border border-purple-500/30',
  RESOLVED: 'bg-green-500/20 text-green-400 border border-green-500/30',
  REJECTED: 'bg-red-500/20 text-red-400 border border-red-500/30',
  CLOSED: 'bg-slate-500/20 text-slate-400 border border-slate-500/30',
  ESCALATED: 'bg-orange-500/20 text-orange-400 border border-orange-500/30',
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
      <div className="max-w-4xl mx-auto space-y-8">

        {/* Header */}
        <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4 p-6 bg-slate-800/60 backdrop-blur-md rounded-2xl border border-slate-700/50 shadow-lg">
          <div>
            <h2 className="text-2xl font-bold text-white bg-clip-text text-transparent bg-gradient-to-r from-amber-200 to-amber-500">My Complaints</h2>
            <p className="text-sm text-slate-400 mt-1">File and track your complaints</p>
          </div>
          <Button
            variant="primary"
            onClick={() => setShowForm(!showForm)}
            className="bg-gradient-to-r from-amber-500 to-amber-400 text-black hover:scale-105 transition-transform shadow-lg shadow-amber-500/20 border-none font-bold"
          >
            {showForm ? `✕ Cancel` : `📢 File a Complaint`}
          </Button>
        </div>

        {/* File Complaint Form */}
        {showForm && (
          <div className="bg-slate-800/80 backdrop-blur-xl rounded-2xl border border-slate-700/80 p-8 shadow-2xl animate-[fadeIn_0.3s_ease-out]">
            <h3 className="text-xl font-bold text-white mb-6 flex items-center gap-3">
              <span className="text-2xl p-2 bg-slate-700/50 rounded-xl">📋</span> File a Complaint
            </h3>
            <form onSubmit={handleSubmit} className="space-y-6">
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-6">
                {/* Accused User ID */}
                <div>
                  <label className="block text-sm font-semibold text-slate-300 mb-2 uppercase tracking-wider">Accused User ID *</label>
                  <input
                    type="number"
                    value={formData.accusedId}
                    onChange={(e) => handleChange('accusedId', e.target.value)}
                    placeholder="Enter user ID"
                    className="w-full px-5 py-3 bg-slate-900/60 border border-slate-600/60 rounded-xl text-white text-sm focus:ring-2 focus:ring-amber-500 focus:border-transparent outline-none transition-all shadow-inner placeholder:text-slate-500 hover:border-slate-500"
                  />
                  {errors.accusedId && <p className="text-xs text-red-400 mt-2">{errors.accusedId}</p>}
                </div>

                {/* Assignment ID (optional) */}
                <div>
                  <label className="block text-sm font-semibold text-slate-300 mb-2 uppercase tracking-wider">Assignment ID <span className="text-slate-500 normal-case font-normal">(optional)</span></label>
                  <input
                    type="number"
                    value={formData.assignmentId}
                    onChange={(e) => handleChange('assignmentId', e.target.value)}
                    placeholder="Related assignment"
                    className="w-full px-5 py-3 bg-slate-900/60 border border-slate-600/60 rounded-xl text-white text-sm focus:ring-2 focus:ring-amber-500 focus:border-transparent outline-none transition-all shadow-inner placeholder:text-slate-500 hover:border-slate-500"
                  />
                </div>

                {/* Complaint Type */}
                <div>
                  <label className="block text-sm font-semibold text-slate-300 mb-2 uppercase tracking-wider">Complaint Type *</label>
                  <select
                    value={formData.complaintType}
                    onChange={(e) => handleChange('complaintType', e.target.value)}
                    className="w-full px-5 py-3 bg-slate-900/60 border border-slate-600/60 rounded-xl text-white text-sm focus:ring-2 focus:ring-amber-500 focus:border-transparent outline-none transition-all appearance-none cursor-pointer hover:border-slate-500 shadow-inner"
                  >
                    <option value="">Select type...</option>
                    {COMPLAINT_TYPES.map(opt => (
                      <option key={opt.value} value={opt.value}>{opt.label}</option>
                    ))}
                  </select>
                  {errors.complaintType && <p className="text-xs text-red-400 mt-2">{errors.complaintType}</p>}
                </div>

                {/* Priority */}
                <div>
                  <label className="block text-sm font-semibold text-slate-300 mb-2 uppercase tracking-wider">Priority</label>
                  <select
                    value={formData.priority}
                    onChange={(e) => handleChange('priority', e.target.value)}
                    className="w-full px-5 py-3 bg-slate-900/60 border border-slate-600/60 rounded-xl text-white text-sm focus:ring-2 focus:ring-amber-500 focus:border-transparent outline-none transition-all appearance-none cursor-pointer hover:border-slate-500 shadow-inner"
                  >
                    {PRIORITY_OPTIONS.map(opt => (
                      <option key={opt.value} value={opt.value}>{opt.label}</option>
                    ))}
                  </select>
                </div>
              </div>

              {/* Subject */}
              <div>
                <label className="block text-sm font-semibold text-slate-300 mb-2 uppercase tracking-wider">Subject *</label>
                <input
                  type="text"
                  value={formData.subject}
                  onChange={(e) => handleChange('subject', e.target.value)}
                  placeholder="Brief summary of your complaint"
                  maxLength={200}
                  className="w-full px-5 py-3 bg-slate-900/60 border border-slate-600/60 rounded-xl text-white text-sm focus:ring-2 focus:ring-amber-500 focus:border-transparent outline-none transition-all shadow-inner placeholder:text-slate-500 hover:border-slate-500"
                />
                {errors.subject && <p className="text-xs text-red-400 mt-2">{errors.subject}</p>}
              </div>

              {/* Description */}
              <div>
                <label className="block text-sm font-semibold text-slate-300 mb-2 uppercase tracking-wider">Description *</label>
                <textarea
                  value={formData.description}
                  onChange={(e) => handleChange('description', e.target.value)}
                  placeholder="Provide detailed information about the issue..."
                  rows={5}
                  maxLength={3000}
                  className="w-full px-5 py-3 bg-slate-900/60 border border-slate-600/60 rounded-xl text-white text-sm focus:ring-2 focus:ring-amber-500 focus:border-transparent outline-none transition-all shadow-inner placeholder:text-slate-500 resize-none hover:border-slate-500"
                />
                <div className="flex justify-between mt-2">
                  {errors.description && <p className="text-xs text-red-400">{errors.description}</p>}
                  <p className="text-xs text-slate-500 ml-auto bg-slate-800/50 px-2 py-1 rounded">{formData.description.length}/3000</p>
                </div>
              </div>

              {/* Submit */}
              <div className="flex justify-end gap-4 pt-4 border-t border-slate-700/50">
                <Button variant="ghost" onClick={() => setShowForm(false)} className="hover:bg-slate-700/50 px-6">
                  Cancel
                </Button>
                <Button variant="primary" type="submit" disabled={submitting} className="bg-gradient-to-r from-amber-500 to-amber-400 text-black border-none hover:scale-105 transition-transform shadow-lg shadow-amber-500/20 px-8 font-bold">
                  {submitting ? 'Submitting...' : ('Submit Complaint')}
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
            title="No complaints found"
            description="If you face any issues, you can file a complaint using the button above."
          />
        ) : (
          <div className="space-y-4">
            {complaints.map((c) => (
              <div
                key={c.id}
                className="bg-slate-800/70 backdrop-blur-xl rounded-2xl border border-slate-700/70 p-6 hover:border-amber-500/50 transition-all duration-300 cursor-pointer shadow-lg hover:shadow-amber-500/10 group"
                onClick={() => setDetailModal({ open: true, complaint: c })}
              >
                <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
                  <div className="flex-1 min-w-0">
                    <div className="flex items-center gap-3 mb-2">
                      <span className={`inline-flex items-center px-2.5 py-1 rounded-full text-xs font-bold ${STATUS_COLORS[c.status] || 'bg-slate-500/20 text-slate-400 border border-slate-500/30'}`}>
                        {formatStatus(c.status)}
                      </span>
                      <span className="text-xs text-slate-400 font-mono bg-slate-900/50 px-2 py-0.5 rounded border border-slate-700/50">{c.complaintId}</span>
                    </div>
                    <h4 className="text-lg font-bold text-white truncate group-hover:text-amber-400 transition-colors">{c.subject}</h4>
                    <p className="text-sm text-slate-400 mt-1.5 line-clamp-2 leading-relaxed">{c.description}</p>
                  </div>
                  <div className="flex flex-col items-end gap-2 flex-shrink-0 bg-slate-900/30 p-3 rounded-xl border border-slate-700/30">
                    <span className="text-xs font-medium text-slate-400">{formatDate(c.createdAt)}</span>
                    <span className="text-sm font-semibold text-slate-300">Against: <span className="text-white">{c.accusedName || '—'}</span></span>
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
            <div className="space-y-6">
              <div className="grid grid-cols-2 gap-5 p-5 bg-slate-900/40 rounded-xl border border-slate-700/50">
                <div>
                  <p className="text-xs text-slate-500 uppercase tracking-wider font-semibold mb-1">Complaint ID</p>
                  <p className="text-sm text-white font-bold font-mono">{detailModal.complaint.complaintId}</p>
                </div>
                <div>
                  <p className="text-xs text-slate-500 uppercase tracking-wider font-semibold mb-1">Status</p>
                  <span className={`inline-flex items-center px-2 py-0.5 rounded-full text-xs font-bold ${STATUS_COLORS[detailModal.complaint.status] || 'bg-slate-500/20 text-slate-400 border border-slate-500/30'}`}>
                    {formatStatus(detailModal.complaint.status)}
                  </span>
                </div>
                <div>
                  <p className="text-xs text-slate-500 uppercase tracking-wider font-semibold mb-1">Category</p>
                  <p className="text-sm text-white font-medium">{formatStatus(detailModal.complaint.category)}</p>
                </div>
                <div>
                  <p className="text-xs text-slate-500 uppercase tracking-wider font-semibold mb-1">Priority</p>
                  <p className="text-sm text-white font-medium">{detailModal.complaint.priority}</p>
                </div>
                <div>
                  <p className="text-xs text-slate-500 uppercase tracking-wider font-semibold mb-1">Against</p>
                  <p className="text-sm text-white font-medium">{detailModal.complaint.accusedName || '—'}</p>
                </div>
                <div>
                  <p className="text-xs text-slate-500 uppercase tracking-wider font-semibold mb-1">Filed At</p>
                  <p className="text-sm text-white font-medium">{formatDate(detailModal.complaint.createdAt)}</p>
                </div>
              </div>

              <div className="bg-slate-800/60 backdrop-blur-sm rounded-xl p-5 border border-slate-700/60 shadow-inner">
                <p className="text-xs text-slate-400 uppercase tracking-wider font-semibold mb-2">Subject</p>
                <p className="text-lg text-white font-bold">{detailModal.complaint.subject}</p>
              </div>

              <div className="bg-slate-800/60 backdrop-blur-sm rounded-xl p-5 border border-slate-700/60 shadow-inner">
                <p className="text-xs text-slate-400 uppercase tracking-wider font-semibold mb-2">Description</p>
                <p className="text-sm text-slate-200 whitespace-pre-wrap leading-relaxed">{detailModal.complaint.description}</p>
              </div>

              {detailModal.complaint.assignedAdminName && (
                <div className="bg-slate-800/60 backdrop-blur-sm rounded-xl p-5 border border-slate-700/60">
                  <p className="text-xs text-slate-400 uppercase tracking-wider font-semibold mb-2">Assigned Admin</p>
                  <p className="text-sm text-white font-medium">{detailModal.complaint.assignedAdminName}</p>
                </div>
              )}

              {detailModal.complaint.resolution && (
                <div className="bg-gradient-to-br from-green-500/10 to-emerald-500/5 border border-green-500/30 rounded-xl p-5 shadow-lg">
                  <p className="text-xs text-green-400 uppercase tracking-wider font-bold mb-2 flex items-center gap-2">
                    <span>✓</span> Resolution
                  </p>
                  <p className="text-sm text-slate-200 whitespace-pre-wrap leading-relaxed">{detailModal.complaint.resolution}</p>
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
