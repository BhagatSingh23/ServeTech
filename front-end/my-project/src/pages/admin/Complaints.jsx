import { useState, useEffect } from 'react';
import DashboardLayout from '../../components/common/DashboardLayout';
import LoadingSpinner from '../../components/common/LoadingSpinner';
import EmptyState from '../../components/common/EmptyState';
import Badge from '../../components/common/Badge';
import Modal from '../../components/common/Modal';
import Button from '../../components/common/Button';
import { useToast } from '../../components/common/Toast';
import { getAllComplaints, assignComplaint, resolveComplaint } from '../../api/admin';
import { formatDate, timeAgo } from '../../utils/formatDate';

const TABS = [
  { key: '', label: 'All' },
  { key: 'OPEN', label: 'Submitted' },
  { key: 'IN_PROGRESS', label: 'Under Review' },
  { key: 'RESOLVED', label: 'Resolved' },
  { key: 'DISMISSED', label: 'Closed' },
];

const Complaints = () => {
  const [complaints, setComplaints] = useState([]);
  const [loading, setLoading] = useState(true);
  const [activeTab, setActiveTab] = useState('');
  const [detailModal, setDetailModal] = useState({ open: false, complaint: null });
  const [resolveModal, setResolveModal] = useState({ open: false, complaint: null });
  const [resolutionNotes, setResolutionNotes] = useState('');
  const [actionLoading, setActionLoading] = useState({});
  const toast = useToast();

  useEffect(() => {
    fetchComplaints();
  }, [activeTab]);

  const fetchComplaints = async () => {
    setLoading(true);
    try {
      const response = await getAllComplaints(activeTab || undefined);
      setComplaints(response.data.data || []);
    } catch {
      toast.error('Failed to load complaints');
    } finally {
      setLoading(false);
    }
  };

  const handleAssign = async (complaintId) => {
    setActionLoading((prev) => ({ ...prev, [complaintId]: 'assign' }));
    try {
      await assignComplaint(complaintId);
      toast.success('Complaint assigned to you');
      fetchComplaints();
    } catch (error) {
      toast.error(error.response?.data?.message || 'Failed to assign complaint');
    } finally {
      setActionLoading((prev) => ({ ...prev, [complaintId]: null }));
    }
  };

  const handleResolve = async () => {
    const complaint = resolveModal.complaint;
    if (!complaint) return;

    const complaintId = complaint.complaintId || complaint.id;
    setActionLoading((prev) => ({ ...prev, [complaintId]: 'resolve' }));
    try {
      await resolveComplaint(complaintId, { resolutionNotes: resolutionNotes.trim() });
      toast.success('Complaint resolved successfully');
      setResolveModal({ open: false, complaint: null });
      setResolutionNotes('');
      fetchComplaints();
    } catch (error) {
      toast.error(error.response?.data?.message || 'Failed to resolve complaint');
    } finally {
      setActionLoading((prev) => ({ ...prev, [complaintId]: null }));
    }
  };

  return (
    <DashboardLayout pageTitle="Complaints">
      {/* Tab Bar */}
      <div className="bg-slate-800/50 rounded-lg p-1 inline-flex gap-1 mb-6 flex-wrap">
        {TABS.map((tab) => (
          <button
            key={tab.key}
            onClick={() => setActiveTab(tab.key)}
            className={`px-4 py-2 rounded-md text-sm font-medium transition-all duration-200 cursor-pointer ${
              activeTab === tab.key
                ? 'bg-amber-500 text-black'
                : 'text-slate-400 hover:text-white hover:bg-slate-700'
            }`}
          >
            {tab.label}
          </button>
        ))}
      </div>

      {/* Complaints List */}
      {loading ? (
        <LoadingSpinner text="Loading complaints..." />
      ) : complaints.length === 0 ? (
        <EmptyState
          icon="📢"
          title="No complaints found"
          description={activeTab ? 'No complaints match this filter.' : 'No complaints have been submitted.'}
        />
      ) : (
        <div className="space-y-4">
          {complaints.map((complaint) => {
            const complaintId = complaint.complaintId || complaint.id;
            return (
              <div
                key={complaintId}
                className="bg-slate-800 rounded-xl shadow-lg border border-slate-700 p-5 hover:border-slate-600 transition-all duration-200"
              >
                <div className="flex flex-col sm:flex-row sm:items-start justify-between gap-3 mb-3">
                  <div className="flex-1 min-w-0">
                    <div className="flex items-center gap-2 flex-wrap mb-1">
                      <span className="text-xs text-slate-500 font-mono">#{complaintId}</span>
                      <Badge status={complaint.status} type="complaint" />
                    </div>
                    <h3
                      className="text-white font-semibold cursor-pointer hover:text-amber-400 transition-colors"
                      onClick={() => setDetailModal({ open: true, complaint })}
                    >
                      {complaint.subject || 'No Subject'}
                    </h3>
                  </div>
                  <span className="text-xs text-slate-500 flex-shrink-0">{timeAgo(complaint.createdAt)}</span>
                </div>

                <p className="text-sm text-slate-400 mb-3 line-clamp-2">{complaint.description}</p>

                <div className="grid grid-cols-2 sm:grid-cols-4 gap-3 mb-4 text-sm">
                  <div>
                    <p className="text-xs text-slate-500">Complainant</p>
                    <p className="text-white">{complaint.complainantName || complaint.raisedByName || '—'}</p>
                  </div>
                  <div>
                    <p className="text-xs text-slate-500">Accused</p>
                    <p className="text-white">{complaint.accusedName || complaint.againstName || '—'}</p>
                  </div>
                  <div>
                    <p className="text-xs text-slate-500">Date</p>
                    <p className="text-white">{formatDate(complaint.createdAt)}</p>
                  </div>
                  <div>
                    <p className="text-xs text-slate-500">Assigned To</p>
                    <p className="text-white">{complaint.assignedTo || complaint.assigneeName || 'Unassigned'}</p>
                  </div>
                </div>

                {/* Actions */}
                <div className="flex items-center justify-end gap-2 pt-3 border-t border-slate-700">
                  <Button
                    variant="ghost"
                    size="sm"
                    onClick={() => setDetailModal({ open: true, complaint })}
                  >
                    View Details
                  </Button>
                  {(complaint.status === 'OPEN') && (
                    <Button
                      variant="secondary"
                      size="sm"
                      loading={actionLoading[complaintId] === 'assign'}
                      onClick={() => handleAssign(complaintId)}
                    >
                      Assign to Me
                    </Button>
                  )}
                  {(complaint.status === 'IN_PROGRESS' || complaint.status === 'OPEN') && (
                    <Button
                      variant="primary"
                      size="sm"
                      onClick={() => setResolveModal({ open: true, complaint })}
                    >
                      Resolve
                    </Button>
                  )}
                </div>
              </div>
            );
          })}
        </div>
      )}

      {/* Detail Modal */}
      <Modal
        isOpen={detailModal.open}
        onClose={() => setDetailModal({ open: false, complaint: null })}
        title="Complaint Details"
        size="lg"
      >
        {detailModal.complaint && <ComplaintDetail complaint={detailModal.complaint} />}
      </Modal>

      {/* Resolve Modal */}
      <Modal
        isOpen={resolveModal.open}
        onClose={() => {
          setResolveModal({ open: false, complaint: null });
          setResolutionNotes('');
        }}
        title="Resolve Complaint"
      >
        <div className="space-y-4">
          <p className="text-slate-300">
            Resolving complaint{' '}
            <span className="text-white font-semibold">#{resolveModal.complaint?.complaintId || resolveModal.complaint?.id}</span>:{' '}
            <span className="text-amber-400">{resolveModal.complaint?.subject}</span>
          </p>
          <div>
            <label className="block text-sm font-medium text-slate-300 mb-1.5">Resolution Notes</label>
            <textarea
              value={resolutionNotes}
              onChange={(e) => setResolutionNotes(e.target.value)}
              rows={4}
              placeholder="Describe how this complaint was resolved..."
              className="w-full bg-slate-700 border border-slate-600 text-white rounded-lg px-4 py-2.5 focus:border-amber-400 focus:ring-1 focus:ring-amber-400 outline-none transition-all placeholder-slate-500 resize-none"
            />
          </div>
          <div className="flex justify-end gap-3 pt-2">
            <Button
              variant="secondary"
              onClick={() => {
                setResolveModal({ open: false, complaint: null });
                setResolutionNotes('');
              }}
            >
              Cancel
            </Button>
            <Button
              variant="primary"
              loading={actionLoading[resolveModal.complaint?.complaintId || resolveModal.complaint?.id] === 'resolve'}
              onClick={handleResolve}
            >
              Mark as Resolved
            </Button>
          </div>
        </div>
      </Modal>
    </DashboardLayout>
  );
};

const ComplaintDetail = ({ complaint }) => {
  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between">
        <div>
          <span className="text-xs text-slate-500 font-mono">#{complaint.complaintId || complaint.id}</span>
          <h3 className="text-white font-semibold text-lg mt-1">{complaint.subject || 'No Subject'}</h3>
        </div>
        <Badge status={complaint.status} type="complaint" />
      </div>

      <div className="bg-slate-700/50 rounded-lg p-4 border border-slate-600">
        <p className="text-xs text-slate-400 mb-1">Description</p>
        <p className="text-sm text-slate-300 whitespace-pre-wrap">{complaint.description}</p>
      </div>

      <div className="grid grid-cols-2 gap-4">
        <div>
          <p className="text-xs text-slate-400">Complainant</p>
          <p className="text-sm text-white">{complaint.complainantName || complaint.raisedByName || '—'}</p>
          <p className="text-xs text-slate-500">{complaint.complainantPhone || ''}</p>
        </div>
        <div>
          <p className="text-xs text-slate-400">Accused</p>
          <p className="text-sm text-white">{complaint.accusedName || complaint.againstName || '—'}</p>
          <p className="text-xs text-slate-500">{complaint.accusedPhone || ''}</p>
        </div>
        <div>
          <p className="text-xs text-slate-400">Filed On</p>
          <p className="text-sm text-white">{formatDate(complaint.createdAt)}</p>
        </div>
        <div>
          <p className="text-xs text-slate-400">Assigned To</p>
          <p className="text-sm text-white">{complaint.assignedTo || complaint.assigneeName || 'Unassigned'}</p>
        </div>
      </div>

      {complaint.assignmentId && (
        <div className="bg-slate-700/50 rounded-lg p-4 border border-slate-600">
          <p className="text-xs text-slate-400 mb-1">Related Assignment</p>
          <p className="text-sm text-white">Assignment #{complaint.assignmentId}</p>
          {complaint.assignmentTitle && (
            <p className="text-sm text-slate-300">{complaint.assignmentTitle}</p>
          )}
        </div>
      )}

      {complaint.evidence && (
        <div>
          <p className="text-xs text-slate-400 mb-1">Evidence</p>
          <p className="text-sm text-slate-300">{complaint.evidence}</p>
        </div>
      )}

      {complaint.resolutionNotes && (
        <div className="bg-green-500/10 border border-green-500/30 rounded-lg p-4">
          <p className="text-xs text-green-400 mb-1">Resolution</p>
          <p className="text-sm text-slate-300">{complaint.resolutionNotes}</p>
          {complaint.resolvedAt && (
            <p className="text-xs text-slate-500 mt-2">Resolved on {formatDate(complaint.resolvedAt)}</p>
          )}
        </div>
      )}
    </div>
  );
};

export default Complaints;
