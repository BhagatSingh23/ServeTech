import { useState, useEffect } from 'react';
import DashboardLayout from '../../components/common/DashboardLayout';
import LoadingSpinner from '../../components/common/LoadingSpinner';
import Badge from '../../components/common/Badge';
import { useToast } from '../../components/common/Toast';
import { getWorkerProfile, updateWorkerProfile, toggleAvailability } from '../../api/worker';
import { getMyRatings } from '../../api/ratings';
import { formatCurrency } from '../../utils/formatCurrency';
import { formatDate } from '../../utils/formatDate';

const SKILLS = [
  'PAINTER', 'PLUMBER', 'ELECTRICIAN', 'CARPENTER', 'MASON',
  'WELDER', 'DRIVER', 'COOK', 'GARDENER', 'CLEANER', 'HELPER', 'LABOUR', 'OTHER',
];

const WorkerProfile = () => {
const [profile, setProfile] = useState(null);
  const [ratings, setRatings] = useState([]);
  const [loading, setLoading] = useState(true);
  const [editMode, setEditMode] = useState(false);
  const [saving, setSaving] = useState(false);
  const [toggling, setToggling] = useState(false);

  const [editData, setEditData] = useState({
    bio: '',
    skills: [],
    dailyWage: '',
    experienceYears: '',
  });

  const toast = useToast();

  useEffect(() => {
    fetchProfile();
  }, []);

  const fetchProfile = async () => {
    setLoading(true);
    try {
      const profileRes = await getWorkerProfile();
      const p = profileRes.data.data;
      setProfile(p);
      setEditData({
        bio: p.bio || '',
        skills: p.skills || [],
        dailyWage: p.dailyWage || '',
        experienceYears: p.experienceYears || '',
      });
    } catch {
      toast.error('Failed to load profile');
    }

    try {
      const ratingsRes = await getMyRatings();
      setRatings(ratingsRes.data.data || []);
    } catch {
    }
    setLoading(false);
  };

  const handleToggleAvailability = async () => {
    setToggling(true);
    try {
      const response = await toggleAvailability();
      const updated = response.data.data;
      setProfile((prev) => ({
        ...prev,
        availableForWork: updated?.availableForWork ?? !prev?.availableForWork,
      }));
      toast.success('Availability updated');
    } catch {
      toast.error('Failed to update availability');
    } finally {
      setToggling(false);
    }
  };

  const handleSave = async () => {
    setSaving(true);
    try {
      const skillIds = editData.skills.map((skill) => SKILLS.indexOf(skill) + 1).filter((id) => id > 0);

      await updateWorkerProfile({
        bio: editData.bio.trim(),
        skillIds: skillIds,
        dailyWage: Number(editData.dailyWage),
        experienceYears: Number(editData.experienceYears),
      });
      setEditMode(false);
      toast.success('Profile updated successfully');
      fetchProfile();
    } catch {
      toast.error('Failed to update profile');
    } finally {
      setSaving(false);
    }
  };

  const toggleSkill = (skill) => {
    setEditData((prev) => ({
      ...prev,
      skills: prev.skills.includes(skill)
        ? prev.skills.filter((s) => s !== skill)
        : [...prev.skills, skill],
    }));
  };

  if (loading) {
    return (
      <DashboardLayout pageTitle="Profile">
        <LoadingSpinner text="Loading profile..." />
      </DashboardLayout>
    );
  }

  if (!profile) {
    return (
      <DashboardLayout pageTitle="Profile">
        <div className="text-center text-slate-400 py-12">Could not load profile data.</div>
      </DashboardLayout>
    );
  }

  return (
    <DashboardLayout pageTitle="Profile">
      <div className="max-w-4xl mx-auto space-y-6">
        {/* Profile Header */}
        <div className="bg-slate-800/80 backdrop-blur-xl rounded-xl shadow-lg border border-slate-700/80 p-6">
          <div className="flex flex-col sm:flex-row items-start justify-between gap-4">
            <div className="flex items-center gap-4">
              <div className="h-16 w-16 rounded-full bg-slate-700/80 border-2 border-amber-500/30 flex items-center justify-center text-amber-400 text-2xl font-bold shadow-lg">
                {(profile.firstName || profile.name || 'U').charAt(0).toUpperCase()}
              </div>
              <div>
                <h2 className="text-xl font-bold text-white">
                  {profile.firstName} {profile.lastName || ''}
                </h2>
                <p className="text-sm text-slate-400">{profile.phoneNumber}</p>
                <div className="flex items-center gap-2 mt-1">
                  <Badge
                    status={profile.verificationStatus || 'PENDING_VERIFICATION'}
                    type="account"
                  />
                </div>
              </div>
            </div>

            <div className="flex items-center gap-3">
              {!editMode ? (
                <button 
                  className="px-4 py-2 rounded-lg bg-slate-700/50 text-white font-semibold border border-slate-600/50 hover:bg-slate-600/80 transition-colors duration-200 shadow-md cursor-pointer flex items-center gap-2"
                  onClick={() => setEditMode(true)}
                >
                  <span>✏️</span> Update Profile
                </button>
              ) : (
                <div className="flex gap-2">
                  <button 
                    className="px-4 py-2 rounded-lg bg-slate-700/50 text-slate-300 font-semibold hover:bg-slate-600/80 transition-colors duration-200 cursor-pointer"
                    onClick={() => setEditMode(false)}
                  >
                    Cancel
                  </button>
                  <button 
                    className="px-4 py-2 rounded-lg bg-gradient-to-r from-amber-500 to-amber-400 text-black font-semibold hover:scale-105 transition-transform duration-200 shadow-md cursor-pointer flex items-center gap-2 disabled:opacity-70 disabled:hover:scale-100"
                    disabled={saving}
                    onClick={handleSave}
                  >
                    {saving && <span className="animate-spin text-black border-2 border-black border-t-transparent rounded-full w-4 h-4 mr-1 inline-block"></span>}
                    Save
                  </button>
                </div>
              )}
            </div>
          </div>

          {/* Availability Toggle */}
          <div className="mt-6 flex items-center justify-between bg-slate-700/30 rounded-lg p-4 border border-slate-600/30 backdrop-blur-sm">
            <div>
              <p className="text-white font-medium">Status</p>
              <p className="text-sm text-slate-400">
                {profile.availableForWork ? 'You are visible to clients looking for workers' : 'You are hidden from job searches'}
              </p>
            </div>
            <button
              onClick={handleToggleAvailability}
              disabled={toggling}
              className="cursor-pointer"
            >
              <div
                className={`relative w-14 h-7 rounded-full transition-all duration-300 shadow-inner ${
                  profile.availableForWork ? 'bg-gradient-to-r from-green-500 to-green-400' : 'bg-gradient-to-r from-red-500 to-red-400'
                } ${toggling ? 'opacity-50' : ''}`}
              >
                <div
                  className={`absolute top-0.5 left-0.5 h-6 w-6 bg-white rounded-full transition-transform duration-300 flex items-center justify-center text-xs shadow-md ${
                    profile.availableForWork ? 'translate-x-7 text-green-500' : 'text-red-500'
                  }`}
                >
                  {profile.availableForWork ? '✓' : '✕'}
                </div>
              </div>
            </button>
          </div>
        </div>

        {/* Profile Details */}
        <div className="bg-slate-800/80 backdrop-blur-xl rounded-xl shadow-lg border border-slate-700/80 p-6">
          <h3 className="text-lg font-semibold text-white mb-4">Profile Details</h3>

          {editMode ? (
            <div className="space-y-5 animate-[fadeIn_0.2s_ease-in]">
              {/* Bio */}
              <div>
                <label className="block text-sm font-medium text-slate-300 mb-1.5">Bio</label>
                <textarea
                  value={editData.bio}
                  onChange={(e) => setEditData((prev) => ({ ...prev, bio: e.target.value }))}
                  rows={4}
                  placeholder="Tell clients about yourself and your experience..."
                  className="w-full bg-slate-700/50 backdrop-blur-sm border border-slate-600/80 text-white rounded-lg px-4 py-2.5 focus:border-amber-400 focus:ring-1 focus:ring-amber-400 outline-none transition-all placeholder-slate-500 resize-none"
                />
              </div>

              {/* Skills */}
              <div>
                <label className="block text-sm font-medium text-slate-300 mb-2">Skills</label>
                <div className="flex flex-wrap gap-2">
                  {SKILLS.map((skill) => (
                    <button
                      key={skill}
                      type="button"
                      onClick={() => toggleSkill(skill)}
                      className={`px-3 py-1.5 rounded-full text-xs font-medium transition-all duration-200 cursor-pointer ${
                        editData.skills.includes(skill)
                          ? 'bg-amber-500 text-black shadow-lg shadow-amber-500/20'
                          : 'bg-slate-700/50 text-slate-300 hover:bg-slate-600 border border-slate-600/50'
                      }`}
                    >
                      {skill}
                    </button>
                  ))}
                </div>
              </div>

              {/* Wage & Experience */}
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-slate-300 mb-1.5">Amount (₹)</label>
                  <input
                    type="number"
                    value={editData.dailyWage}
                    onChange={(e) => setEditData((prev) => ({ ...prev, dailyWage: e.target.value }))}
                    min="0"
                    className="w-full bg-slate-700/50 backdrop-blur-sm border border-slate-600/80 text-white rounded-lg px-4 py-2.5 focus:border-amber-400 focus:ring-1 focus:ring-amber-400 outline-none transition-all"
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium text-slate-300 mb-1.5">Experience (Years) (Years)</label>
                  <input
                    type="number"
                    value={editData.experienceYears}
                    onChange={(e) => setEditData((prev) => ({ ...prev, experienceYears: e.target.value }))}
                    min="0"
                    className="w-full bg-slate-700/50 backdrop-blur-sm border border-slate-600/80 text-white rounded-lg px-4 py-2.5 focus:border-amber-400 focus:ring-1 focus:ring-amber-400 outline-none transition-all"
                  />
                </div>
              </div>
            </div>
          ) : (
            <div className="space-y-4">
              {/* Bio */}
              <div>
                <p className="text-xs text-slate-500 mb-1">Bio</p>
                <p className="text-sm text-slate-300">
                  {profile.bio || 'No bio added yet.'}
                </p>
              </div>

              {/* Location */}
              <div className="grid grid-cols-2 sm:grid-cols-4 gap-4 bg-slate-700/30 border border-slate-600/30 rounded-lg p-4">
                <div>
                  <p className="text-xs text-slate-500">Pincode</p>
                  <p className="text-sm text-white">{profile.pincode || '—'}</p>
                </div>
                <div>
                  <p className="text-xs text-slate-500">Block</p>
                  <p className="text-sm text-white">{profile.block || '—'}</p>
                </div>
                <div>
                  <p className="text-xs text-slate-500">District</p>
                  <p className="text-sm text-white">{profile.district || '—'}</p>
                </div>
                <div>
                  <p className="text-xs text-slate-500">State</p>
                  <p className="text-sm text-white">{profile.state || '—'}</p>
                </div>
              </div>

              {/* Skills */}
              <div>
                <p className="text-xs text-slate-500 mb-2">Skills</p>
                {(profile.skills && profile.skills.length > 0) ? (
                  <div className="flex flex-wrap gap-2">
                    {profile.skills.map((skill) => (
                      <span
                        key={skill}
                        className="bg-amber-500/10 border border-amber-500/20 text-amber-400 text-xs font-medium px-3 py-1 rounded-full"
                      >
                        {skill}
                      </span>
                    ))}
                  </div>
                ) : (
                  <p className="text-sm text-slate-400">No skills added yet.</p>
                )}
              </div>

              {/* Wage & Experience */}
              <div className="grid grid-cols-2 sm:grid-cols-3 gap-4 bg-slate-700/30 border border-slate-600/30 rounded-lg p-4">
                <div>
                  <p className="text-xs text-slate-500">Amount</p>
                  <p className="text-sm text-amber-400 font-semibold">
                    {formatCurrency(profile.dailyWage || profile.wagePerDay || 0)}/day
                  </p>
                </div>
                <div>
                  <p className="text-xs text-slate-500">Experience (Years)</p>
                  <p className="text-sm text-white">
                    {profile.experienceYears || profile.experience || 0} years
                  </p>
                </div>
                <div>
                  <p className="text-xs text-slate-500">Rating</p>
                  <p className="text-sm text-white flex items-center gap-1">
                    {profile.averageRating ? `${Number(profile.averageRating).toFixed(1)} ⭐` : 'Not rated yet'}
                  </p>
                </div>
              </div>
            </div>
          )}
        </div>

        {/* Ratings Section */}
        <div className="bg-slate-800/80 backdrop-blur-xl rounded-xl shadow-lg border border-slate-700/80 p-6">
          <h3 className="text-lg font-semibold text-white mb-4 flex items-center gap-2">
            <span>⭐</span> Ratings Received
          </h3>
          {ratings.length === 0 ? (
            <p className="text-sm text-slate-400 text-center py-6">No ratings received yet.</p>
          ) : (
            <div className="space-y-3">
              {ratings.map((rating) => (
                <div
                  key={rating.id || rating.ratingId}
                  className="bg-slate-700/30 rounded-lg p-4 border border-slate-600/30 hover:border-slate-500/50 transition-colors duration-200"
                >
                  <div className="flex items-start justify-between mb-2">
                    <div>
                      <p className="text-sm text-white font-medium">{rating.raterName || rating.clientName || 'Client'}</p>
                      <p className="text-xs text-slate-400">{rating.jobTitle || rating.workRequestTitle || ''}</p>
                    </div>
                    <div className="flex items-center gap-1 bg-amber-500/10 border border-amber-500/20 text-amber-400 text-sm font-semibold px-2.5 py-1 rounded-full">
                      {rating.score || rating.rating} ⭐
                    </div>
                  </div>
                  {rating.review && (
                    <p className="text-sm text-slate-300 mt-2">{rating.review}</p>
                  )}
                  <p className="text-xs text-slate-500 mt-2">{formatDate(rating.createdAt || rating.ratedAt)}</p>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>
    </DashboardLayout>
  );
};

export default WorkerProfile;
