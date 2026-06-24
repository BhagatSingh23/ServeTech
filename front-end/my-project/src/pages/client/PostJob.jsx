import { useState, useEffect } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import DashboardLayout from '../../components/common/DashboardLayout';
import Button from '../../components/common/Button';
import LoadingSpinner from '../../components/common/LoadingSpinner';
import { useToast } from '../../components/common/Toast';
import { createWorkRequest, getWorkRequestById, updateWorkRequest } from '../../api/client';
import { getLocationByPincode } from '../../api/auth';
import { formatCurrency } from '../../utils/formatCurrency';

const SKILLS = [
  'PAINTER', 'PLUMBER', 'ELECTRICIAN', 'CARPENTER', 'MASON',
  'WELDER', 'DRIVER', 'COOK', 'GARDENER', 'CLEANER', 'HELPER', 'LABOUR', 'OTHER',
];

const getTodayMin = () => {
  const now = new Date();
  now.setMinutes(now.getMinutes() - now.getTimezoneOffset());
  return now.toISOString().slice(0, 16);
};

const PostJob = () => {
const [searchParams] = useSearchParams();
  const editId = searchParams.get('edit');
  const isEditMode = !!editId;

  const [formData, setFormData] = useState({
    title: '',
    description: '',
    skillsRequired: [],
    pincode: '',
    address: '',
    block: '',
    district: '',
    state: '',
    startDate: '',
    endDate: '',
    workersNeeded: 1,
    wagePerDay: '',
    negotiable: true,
    urgent: false,
  });

  const [errors, setErrors] = useState({});
  const [loading, setLoading] = useState(false);
  const [pageLoading, setPageLoading] = useState(false);
  const [pincodeLoading, setPincodeLoading] = useState(false);
  const toast = useToast();
  const navigate = useNavigate();

  useEffect(() => {
    if (isEditMode) loadJobData();
  }, [editId]);

  const loadJobData = async () => {
    setPageLoading(true);
    try {
      const response = await getWorkRequestById(editId);
      const job = response.data.data;
      if (job) {
        const startDate = job.startDate ? (job.startDate.includes('T') ? job.startDate.slice(0, 16) : job.startDate + 'T09:00') : '';
        const endDate = job.endDate ? (job.endDate.includes('T') ? job.endDate.slice(0, 16) : job.endDate + 'T18:00') : '';
        setFormData({
          title: job.title || '',
          description: job.description || '',
          skillsRequired: job.requiredSkills || job.skills || [],
          pincode: job.pincode || '',
          address: job.workAddress || '',
          block: '',
          district: '',
          state: '',
          startDate,
          endDate,
          workersNeeded: job.workersNeeded || 1,
          wagePerDay: job.offeredWagePerDay || '',
          negotiable: job.isNegotiable !== false,
          urgent: job.isUrgent || false,
        });
        if (job.pincode && job.pincode.length === 6) {
          try {
            const locRes = await getLocationByPincode(job.pincode);
            const loc = locRes.data.data;
            setFormData((prev) => ({
              ...prev,
              block: loc.block || loc.area || '',
              district: loc.district || '',
              state: loc.state || '',
            }));
          } catch { /* ignore */ }
        }
      }
    } catch {
      toast.error('Failed to load job data');
      navigate('/client/my-jobs');
    } finally {
      setPageLoading(false);
    }
  };

  const updateField = (field, value) => {
    setFormData((prev) => ({ ...prev, [field]: value }));
    setErrors((prev) => ({ ...prev, [field]: '' }));
  };

  const toggleSkill = (skill) => {
    setFormData((prev) => ({
      ...prev,
      skillsRequired: prev.skillsRequired.includes(skill)
        ? prev.skillsRequired.filter((s) => s !== skill)
        : [...prev.skillsRequired, skill],
    }));
    setErrors((prev) => ({ ...prev, skillsRequired: '' }));
  };

  const handlePincodeLookup = async (pincode) => {
    updateField('pincode', pincode);
    if (pincode.length === 6) {
      setPincodeLoading(true);
      try {
        const response = await getLocationByPincode(pincode);
        const loc = response.data.data;
        setFormData((prev) => ({
          ...prev,
          block: loc.block || loc.area || '',
          district: loc.district || '',
          state: loc.state || '',
        }));
      } catch {
        toast.warning('Could not fetch location for this pincode');
      } finally {
        setPincodeLoading(false);
      }
    }
  };

  const calculateBudget = () => {
    const { wagePerDay, startDate, endDate, workersNeeded } = formData;
    if (!wagePerDay || !startDate || !endDate || !workersNeeded) return null;
    const start = new Date(startDate);
    const end = new Date(endDate);
    start.setHours(0, 0, 0, 0);
    end.setHours(0, 0, 0, 0);
    const days = Math.max(1, Math.round((end - start) / (1000 * 60 * 60 * 24)) + 1);
    return Number(wagePerDay) * days * Number(workersNeeded);
  };

  const durationDays = () => {
    if (!formData.startDate || !formData.endDate) return 0;
    const start = new Date(formData.startDate);
    const end = new Date(formData.endDate);
    start.setHours(0, 0, 0, 0);
    end.setHours(0, 0, 0, 0);
    return Math.max(1, Math.round((end - start) / (1000 * 60 * 60 * 24)) + 1);
  };

  const validate = () => {
    const newErrors = {};
    if (!formData.title.trim()) newErrors.title = 'Title is required';
    if (!formData.description.trim()) newErrors.description = 'Description is required';
    if (formData.skillsRequired.length === 0) newErrors.skillsRequired = 'Select at least one skill';
    if (!/^\d{6}$/.test(formData.pincode)) newErrors.pincode = 'Enter a valid 6-digit pincode';
    if (!formData.startDate) newErrors.startDate = 'Start date is required';
    if (!formData.endDate) newErrors.endDate = 'End date is required';
    if (formData.startDate && formData.endDate && new Date(formData.endDate) < new Date(formData.startDate)) {
      newErrors.endDate = 'End date must be after start date';
    }
    if (!formData.workersNeeded || Number(formData.workersNeeded) < 1) newErrors.workersNeeded = 'At least 1 worker needed';
    if (!formData.wagePerDay || Number(formData.wagePerDay) <= 0) newErrors.wagePerDay = 'Enter a valid wage';
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!validate()) return;
    setLoading(true);
    try {
      const payload = {
        title: formData.title.trim(),
        description: formData.description.trim(),
        skillIds: formData.skillsRequired.map((skill) => SKILLS.indexOf(skill) + 1),
        pincode: formData.pincode,
        workAddress: formData.address.trim(),
        startDate: formData.startDate,
        endDate: formData.endDate,
        estimatedDurationDays: durationDays(),
        workersNeeded: Number(formData.workersNeeded),
        offeredWagePerDay: Number(formData.wagePerDay),
        isUrgent: formData.urgent,
        isNegotiable: formData.negotiable,
      };

      if (isEditMode) {
        await updateWorkRequest(editId, payload);
        toast.success('Job posted successfully!');
      } else {
        await createWorkRequest(payload);
        toast.success('Job posted successfully!');
      }
      navigate('/client/my-jobs');
    } catch (error) {
      toast.error(error.response?.data?.message || 'Failed to post job');
    } finally {
      setLoading(false);
    }
  };

  const budget = calculateBudget();
  const todayMin = getTodayMin();

  if (pageLoading) {
    return (
      <DashboardLayout pageTitle={isEditMode ? 'Edit Job' : 'Post a New Job'}>
        <LoadingSpinner text="Loading job data..." />
      </DashboardLayout>
    );
  }

  return (
    <DashboardLayout pageTitle={isEditMode ? 'Edit Job' : 'Post a New Job'}>
      <div className="max-w-3xl mx-auto">
        <form onSubmit={handleSubmit} className="space-y-6">
          <div className="bg-slate-800/80 backdrop-blur-xl rounded-xl shadow-lg border border-slate-700/80 p-6 transition-all duration-300 hover:shadow-amber-500/10">
            <h3 className="text-lg font-semibold text-white mb-4">Job Details</h3>
            <div className="space-y-4">
              <div>
                <label className="block text-sm font-medium text-slate-300 mb-1.5">Job Title</label>
                <input
                  type="text"
                  value={formData.title}
                  onChange={(e) => updateField('title', e.target.value)}
                  placeholder="e.g., Need plumber for bathroom renovation"
                  className="w-full bg-slate-700/50 border border-slate-600/50 text-white rounded-lg px-4 py-2.5 focus:border-amber-400 focus:ring-1 focus:ring-amber-400 outline-none transition-all placeholder-slate-500 hover:bg-slate-700/70"
                />
                {errors.title && <p className="text-red-400 text-xs mt-1">{errors.title}</p>}
              </div>

              <div>
                <label className="block text-sm font-medium text-slate-300 mb-1.5">Description</label>
                <textarea
                  value={formData.description}
                  onChange={(e) => updateField('description', e.target.value)}
                  rows={4}
                  placeholder="Describe the work in detail..."
                  className="w-full bg-slate-700/50 border border-slate-600/50 text-white rounded-lg px-4 py-2.5 focus:border-amber-400 focus:ring-1 focus:ring-amber-400 outline-none transition-all placeholder-slate-500 resize-none hover:bg-slate-700/70"
                />
                {errors.description && <p className="text-red-400 text-xs mt-1">{errors.description}</p>}
              </div>

              <div>
                <label className="block text-sm font-medium text-slate-300 mb-2">Skills Required</label>
                <div className="flex flex-wrap gap-2">
                  {SKILLS.map((skill) => (
                    <button
                      key={skill}
                      type="button"
                      onClick={() => toggleSkill(skill)}
                      className={`px-3 py-1.5 rounded-full text-xs font-medium transition-all duration-200 cursor-pointer ${
                        formData.skillsRequired.includes(skill)
                          ? 'bg-gradient-to-r from-amber-500 to-amber-400 text-black shadow-lg shadow-amber-500/20 scale-105'
                          : 'bg-slate-700/50 text-slate-300 hover:bg-slate-600 border border-slate-600/50'
                      }`}
                    >
                      {skill}
                    </button>
                  ))}
                </div>
                {errors.skillsRequired && <p className="text-red-400 text-xs mt-1">{errors.skillsRequired}</p>}
              </div>

              <div className="flex items-center gap-3">
                <label className="flex items-center gap-2 cursor-pointer select-none group">
                  <div
                    onClick={() => updateField('urgent', !formData.urgent)}
                    className={`relative w-11 h-6 rounded-full transition-all duration-300 cursor-pointer ${
                      formData.urgent ? 'bg-red-500 shadow-lg shadow-red-500/30' : 'bg-slate-600 group-hover:bg-slate-500'
                    }`}
                  >
                    <div
                      className={`absolute top-0.5 left-0.5 h-5 w-5 bg-white rounded-full transition-transform duration-300 ${
                        formData.urgent ? 'translate-x-5' : ''
                      }`}
                    />
                  </div>
                  <span className="text-sm text-slate-300 group-hover:text-white transition-colors">Mark as Urgent</span>
                </label>
              </div>
            </div>
          </div>

          <div className="bg-slate-800/80 backdrop-blur-xl rounded-xl shadow-lg border border-slate-700/80 p-6 transition-all duration-300 hover:shadow-amber-500/10">
            <h3 className="text-lg font-semibold text-white mb-4">Location</h3>
            <div className="space-y-4">
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-slate-300 mb-1.5">Pincode</label>
                  <div className="relative">
                    <input
                      type="text"
                      value={formData.pincode}
                      onChange={(e) => handlePincodeLookup(e.target.value.replace(/\D/g, '').slice(0, 6))}
                      placeholder="6-digit pincode"
                      className="w-full bg-slate-700/50 border border-slate-600/50 text-white rounded-lg px-4 py-2.5 focus:border-amber-400 focus:ring-1 focus:ring-amber-400 outline-none transition-all placeholder-slate-500 hover:bg-slate-700/70"
                      maxLength={6}
                    />
                    {pincodeLoading && (
                      <div className="absolute right-3 top-1/2 -translate-y-1/2">
                        <div className="h-5 w-5 animate-spin rounded-full border-2 border-amber-500 border-t-transparent" />
                      </div>
                    )}
                  </div>
                  {errors.pincode && <p className="text-red-400 text-xs mt-1">{errors.pincode}</p>}
                </div>
                <div>
                  <label className="block text-sm font-medium text-slate-300 mb-1.5">Address (Optional)</label>
                  <input
                    type="text"
                    value={formData.address}
                    onChange={(e) => updateField('address', e.target.value)}
                    placeholder="Street address or landmark"
                    className="w-full bg-slate-700/50 border border-slate-600/50 text-white rounded-lg px-4 py-2.5 focus:border-amber-400 focus:ring-1 focus:ring-amber-400 outline-none transition-all placeholder-slate-500 hover:bg-slate-700/70"
                  />
                </div>
              </div>

              {(formData.block || formData.district || formData.state) && (
                <div className="grid grid-cols-3 gap-3 bg-slate-700/30 backdrop-blur-sm rounded-lg p-3 border border-slate-600/50">
                  <div>
                    <p className="text-xs text-slate-400">Block/Area</p>
                    <p className="text-sm text-white">{formData.block || '—'}</p>
                  </div>
                  <div>
                    <p className="text-xs text-slate-400">District</p>
                    <p className="text-sm text-white">{formData.district || '—'}</p>
                  </div>
                  <div>
                    <p className="text-xs text-slate-400">State</p>
                    <p className="text-sm text-white">{formData.state || '—'}</p>
                  </div>
                </div>
              )}
            </div>
          </div>

          <div className="bg-slate-800/80 backdrop-blur-xl rounded-xl shadow-lg border border-slate-700/80 p-6 transition-all duration-300 hover:shadow-amber-500/10">
            <h3 className="text-lg font-semibold text-white mb-4">Schedule & Timing</h3>
            <div className="space-y-4">
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-slate-300 mb-1.5">Start Date</label>
                  <input
                    type="datetime-local"
                    value={formData.startDate}
                    onChange={(e) => updateField('startDate', e.target.value)}
                    min={todayMin}
                    className="w-full bg-slate-700/50 border border-slate-600/50 text-white rounded-lg px-4 py-2.5 focus:border-amber-400 focus:ring-1 focus:ring-amber-400 outline-none transition-all hover:bg-slate-700/70 [color-scheme:dark]"
                  />
                  {errors.startDate && <p className="text-red-400 text-xs mt-1">{errors.startDate}</p>}
                </div>
                <div>
                  <label className="block text-sm font-medium text-slate-300 mb-1.5">End Date</label>
                  <input
                    type="datetime-local"
                    value={formData.endDate}
                    onChange={(e) => updateField('endDate', e.target.value)}
                    min={formData.startDate || todayMin}
                    className="w-full bg-slate-700/50 border border-slate-600/50 text-white rounded-lg px-4 py-2.5 focus:border-amber-400 focus:ring-1 focus:ring-amber-400 outline-none transition-all hover:bg-slate-700/70 [color-scheme:dark]"
                  />
                  {errors.endDate && <p className="text-red-400 text-xs mt-1">{errors.endDate}</p>}
                </div>
              </div>

              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-slate-300 mb-1.5">Workers Needed</label>
                  <input
                    type="number"
                    value={formData.workersNeeded}
                    onChange={(e) => updateField('workersNeeded', e.target.value)}
                    min="1"
                    className="w-full bg-slate-700/50 border border-slate-600/50 text-white rounded-lg px-4 py-2.5 focus:border-amber-400 focus:ring-1 focus:ring-amber-400 outline-none transition-all hover:bg-slate-700/70"
                  />
                  {errors.workersNeeded && <p className="text-red-400 text-xs mt-1">{errors.workersNeeded}</p>}
                </div>
                <div>
                  <label className="block text-sm font-medium text-slate-300 mb-1.5">Wage Per Day (₹)</label>
                  <input
                    type="number"
                    value={formData.wagePerDay}
                    onChange={(e) => updateField('wagePerDay', e.target.value)}
                    min="1"
                    placeholder="e.g., 500"
                    className="w-full bg-slate-700/50 border border-slate-600/50 text-white rounded-lg px-4 py-2.5 focus:border-amber-400 focus:ring-1 focus:ring-amber-400 outline-none transition-all placeholder-slate-500 hover:bg-slate-700/70"
                  />
                  {errors.wagePerDay && <p className="text-red-400 text-xs mt-1">{errors.wagePerDay}</p>}
                </div>
              </div>

              <div className="flex items-center gap-3">
                <label className="flex items-center gap-2 cursor-pointer select-none group">
                  <div
                    onClick={() => updateField('negotiable', !formData.negotiable)}
                    className={`relative w-11 h-6 rounded-full transition-all duration-300 cursor-pointer ${
                      formData.negotiable ? 'bg-green-500 shadow-lg shadow-green-500/30' : 'bg-slate-600 group-hover:bg-slate-500'
                    }`}
                  >
                    <div
                      className={`absolute top-0.5 left-0.5 h-5 w-5 bg-white rounded-full transition-transform duration-300 ${
                        formData.negotiable ? 'translate-x-5' : ''
                      }`}
                    />
                  </div>
                  <span className="text-sm text-slate-300 group-hover:text-white transition-colors">Wage is Negotiable</span>
                </label>
              </div>

              {budget !== null && (
                <div className="bg-amber-500/10 backdrop-blur-md border border-amber-500/30 rounded-lg p-4 transition-all hover:bg-amber-500/20">
                  <p className="text-sm text-amber-400 font-medium mb-2">💰 Estimated Amount</p>
                  <div className="flex items-center justify-between">
                    <p className="text-sm text-slate-300">
                      {formatCurrency(formData.wagePerDay)} × {durationDays()} days × {formData.workersNeeded} worker{Number(formData.workersNeeded) > 1 ? 's' : ''}
                    </p>
                    <p className="text-xl font-bold text-amber-400 drop-shadow-md">{formatCurrency(budget)}</p>
                  </div>
                </div>
              )}
            </div>
          </div>

          <div className="flex justify-end gap-3">
            <Button variant="secondary" onClick={() => navigate('/client/my-jobs')} type="button" className="hover:bg-slate-700">
              Cancel
            </Button>
            <Button variant="primary" type="submit" loading={loading} size="lg" className="bg-gradient-to-r from-amber-500 to-amber-400 text-black hover:from-amber-400 hover:to-amber-300 transform hover:-translate-y-0.5 transition-all shadow-lg shadow-amber-500/25 border-0">
              {isEditMode ? 'Update Job' : 'Post Job'}
            </Button>
          </div>
        </form>
      </div>
    </DashboardLayout>
  );
};

export default PostJob;
