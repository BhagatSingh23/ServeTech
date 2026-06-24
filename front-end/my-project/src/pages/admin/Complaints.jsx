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
  { key: 'SUBMITTED', label: 'Submitted' },
  { key: 'UNDER_REVIEW', label: 'Under Review' },
  { key: 'RESOLVED', label: 'Resolved' },
  { key: 'CLOSED', label: 'Closed' },
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
      <div className="bg-slate-800/60 backdrop-blur-md rounded-xl p-1.5 inline-flex gap-1.5 mb-6 flex-wrap shadow-lg border border-slate-700/50">
        {TABS.map((tab) => (
          <button
            key={tab.key}
            onClick={() => setActiveTab(tab.key)}
            className={`px-5 py-2.5 rounded-lg text-sm font-medium transition-all duration-300 cursor-pointer ${
              activeTab === tab.key
                ? 'bg-gradient-to-r from-amber-500 to-amber-400 text-black shadow-md shadow-amber-500/20 scale-105'
                : 'text-slate-400 hover:text-white hover:bg-slate-700/80 hover:scale-105'
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
        <div className="space-y-5">
          {complaints.map((complaint) => {
            const complaintId = complaint.complaintId || complaint.id;
            return (
              <div
                key={complaintId}
                className="bg-slate-800/80 backdrop-blur-xl rounded-2xl shadow-xl border border-slate-700/80 p-6 hover:border-amber-500/50 transition-all duration-300 hover:shadow-amber-500/10 group"
              >
                <div className="flex flex-col sm:flex-row sm:items-start justify-between gap-4 mb-4">
                  <div className="flex-1 min-w-0">
                    <div className="flex items-center gap-3 flex-wrap mb-2">
                      <span className="text-xs text-slate-400 font-mono bg-slate-900/50 px-2 py-1 rounded-md border border-slate-700">#{complaintId}</span>
                      <Badge status={complaint.status} type="complaint" />
                    </div>
                    <h3
                      className="text-xl text-white font-bold cursor-pointer group-hover:text-amber-400 transition-colors"
                      onClick={() => setDetailModal({ open: true, complaint })}
                    >
                      {complaint.subject || 'No Subject'}
                    </h3>
                  </div>
                  <span className="text-xs text-slate-400 font-medium flex-shrink-0 bg-slate-900/40 px-3 py-1.5 rounded-full border border-slate-700/50">
                    {timeAgo(complaint.createdAt)}
                  </span>
                </div>

                <p className="text-sm text-slate-300 mb-5 line-clamp-2 leading-relaxed">{complaint.description}</p>

                <div className="grid grid-cols-2 sm:grid-cols-4 gap-4 mb-5 p-4 rounded-xl bg-slate-900/40 border border-slate-700/50">
                  <div>
                    <p className="text-xs text-slate-500 uppercase tracking-wider font-semibold mb-1">Complainant</p>
                    <p className="text-white text-sm font-medium">{complaint.complainantName || complaint.raisedByName || '—'}</p>
                  </div>
                  <div>
                    <p className="text-xs text-slate-500 uppercase tracking-wider font-semibold mb-1">Accused</p>
                    <p className="text-white text-sm font-medium">{complaint.accusedName || complaint.againstName || '—'}</p>
                  </div>
                  <div>
                    <p className="text-xs text-slate-500 uppercase tracking-wider font-semibold mb-1">Date</p>
                    <p className="text-white text-sm font-medium">{formatDate(complaint.createdAt)}</p>
                  </div>
                  <div>
                    <p className="text-xs text-slate-500 uppercase tracking-wider font-semibold mb-1">Assigned To</p>
                    <p className="text-white text-sm font-medium">{complaint.assignedTo || complaint.assigneeName || 'Unassigned'}</p>
                  </div>
                </div>

                {/* Actions */}
                <div className="flex items-center justify-end gap-3 pt-4 border-t border-slate-700/50">
                  <Button
                    variant="ghost"
                    size="sm"
                    onClick={() => setDetailModal({ open: true, complaint })}
                    className="hover:bg-slate-700/50 text-slate-300 transition-all"
                  >
                    View Details
                  </Button>
                  {(complaint.status === 'OPEN') && (
                    <Button
                      variant="secondary"
                      size="sm"
                      loading={actionLoading[complaintId] === 'assign'}
                      onClick={() => handleAssign(complaintId)}
                      className="bg-slate-700/80 border-slate-600 hover:border-amber-500/50 transition-all"
                    >
                      Assign to Me
                    </Button>
                  )}
                  {(complaint.status === 'IN_PROGRESS' || complaint.status === 'OPEN') && (
                    <Button
                      variant="primary"
                      size="sm"
                      onClick={() => setResolveModal({ open: true, complaint })}
                      className="bg-gradient-to-r from-amber-500 to-amber-400 text-black border-none hover:scale-105 transition-transform shadow-lg shadow-amber-500/20"
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
        <div className="space-y-5 p-2">
          <p className="text-slate-300 text-lg">
            Resolving complaint{' '}
            <span className="text-white font-bold bg-slate-800 px-2 py-0.5 rounded border border-slate-700">#{resolveModal.complaint?.complaintId || resolveModal.complaint?.id}</span>:{' '}
            <span className="text-amber-400 block mt-2 font-semibold text-xl">{resolveModal.complaint?.subject}</span>
          </p>
          <div>
            <label className="block text-sm font-medium text-slate-300 mb-2">Resolution Notes</label>
            <textarea
              value={resolutionNotes}
              onChange={(e) => setResolutionNotes(e.target.value)}
              rows={5}
              placeholder="Describe how this complaint was resolved..."
              className="w-full bg-slate-900/80 border border-slate-600/80 text-white rounded-xl px-4 py-3 focus:border-amber-400 focus:ring-1 focus:ring-amber-400 outline-none transition-all placeholder-slate-500 resize-none shadow-inner"
            />
          </div>
          <div className="flex justify-end gap-3 pt-4 border-t border-slate-700/50">
            <Button
              variant="ghost"
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
              className="bg-gradient-to-r from-green-500 to-emerald-400 text-black border-none hover:scale-105 transition-transform"
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
    <div className="space-y-5">
      <div className="flex items-center justify-between pb-4 border-b border-slate-700/50">
        <div>
          <span className="text-xs text-slate-400 font-mono bg-slate-800 px-2 py-1 rounded border border-slate-700">#{complaint.complaintId || complaint.id}</span>
          <h3 className="text-white font-bold text-2xl mt-3">{complaint.subject || 'No Subject'}</h3>
        </div>
        <Badge status={complaint.status} type="complaint" />
      </div>

      <div className="bg-slate-800/60 backdrop-blur-sm rounded-xl p-5 border border-slate-700/60 shadow-inner">
        <p className="text-xs text-slate-400 uppercase tracking-wider font-semibold mb-2">Description</p>
        <p className="text-sm text-slate-200 whitespace-pre-wrap leading-relaxed">{complaint.description}</p>
      </div>

      <div className="grid grid-cols-2 gap-5 p-5 bg-slate-900/40 rounded-xl border border-slate-700/50">
        <div>
          <p className="text-xs text-slate-500 uppercase tracking-wider font-semibold mb-1">Complainant</p>
          <p className="text-base text-white font-medium">{complaint.complainantName || complaint.raisedByName || '—'}</p>
          <p className="text-sm text-slate-400 mt-0.5">{complaint.complainantPhone || ''}</p>
        </div>
        <div>
          <p className="text-xs text-slate-500 uppercase tracking-wider font-semibold mb-1">Accused</p>
          <p className="text-base text-white font-medium">{complaint.accusedName || complaint.againstName || '—'}</p>
          <p className="text-sm text-slate-400 mt-0.5">{complaint.accusedPhone || ''}</p>
        </div>
        <div>
          <p className="text-xs text-slate-500 uppercase tracking-wider font-semibold mb-1">Filed On</p>
          <p className="text-base text-white font-medium">{formatDate(complaint.createdAt)}</p>
        </div>
        <div>
          <p className="text-xs text-slate-500 uppercase tracking-wider font-semibold mb-1">Assigned To</p>
          <p className="text-base text-white font-medium">{complaint.assignedTo || complaint.assigneeName || 'Unassigned'}</p>
        </div>
      </div>

      {complaint.assignmentId && (
        <div className="bg-slate-800/60 backdrop-blur-sm rounded-xl p-5 border border-slate-700/60">
          <p className="text-xs text-slate-400 uppercase tracking-wider font-semibold mb-2">Related Assignment</p>
          <p className="text-base text-white font-medium">Assignment #{complaint.assignmentId}</p>
          {complaint.assignmentTitle && (
            <p className="text-sm text-slate-300 mt-1">{complaint.assignmentTitle}</p>
          )}
        </div>
      )}

      {complaint.evidence && (
        <div className="bg-slate-800/60 backdrop-blur-sm rounded-xl p-5 border border-slate-700/60">
          <p className="text-xs text-slate-400 uppercase tracking-wider font-semibold mb-2">Evidence</p>
          <p className="text-sm text-slate-300">{complaint.evidence}</p>
        </div>
      )}

      {complaint.resolutionNotes && (
        <div className="bg-gradient-to-br from-green-500/10 to-emerald-500/5 border border-green-500/30 rounded-xl p-5 shadow-lg">
          <p className="text-xs text-green-400 uppercase tracking-wider font-bold mb-2 flex items-center gap-2">
            <span>✓</span> Resolution
          </p>
          <p className="text-sm text-slate-200 whitespace-pre-wrap leading-relaxed">{complaint.resolutionNotes}</p>
          {complaint.resolvedAt && (
            <p className="text-xs text-slate-400 mt-3 font-medium">Resolved on {formatDate(complaint.resolvedAt)}</p>
          )}
        </div>
      )}
    </div>
  );
};

export default Complaints;
