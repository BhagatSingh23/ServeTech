import { useState, useRef, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { useToast } from '../../components/common/Toast';
import { getLocationByPincode, sendOtp, verifyOtp } from '../../api/auth';
import Button from '../../components/common/Button';

const STEPS = ['Role', 'Phone & OTP', 'Personal Info', 'Location'];

const GENDER_OPTIONS = [
  { value: 'MALE', label: 'Male' },
  { value: 'FEMALE', label: 'Female' },
  { value: 'OTHER', label: 'Other' },
];

const Register = () => {
  const [step, setStep] = useState(1);
  const [loading, setLoading] = useState(false);
  const [pincodeLoading, setPincodeLoading] = useState(false);
  const [otpSent, setOtpSent] = useState(false);
  const [otpVerified, setOtpVerified] = useState(false);
  const [otpTimer, setOtpTimer] = useState(0);
  const [otp, setOtp] = useState(['', '', '', '', '', '']);

  const otpRefs = useRef([]);

  const [formData, setFormData] = useState({
    role: '',
    firstName: '',
    lastName: '',
    phone: '',
    password: '',
    confirmPassword: '',
    dateOfBirth: '',
    gender: '',
    pincode: '',
    block: '',
    district: '',
    state: '',
  });

  const [errors, setErrors] = useState({});

  const { register } = useAuth();
  const toast = useToast();
  const navigate = useNavigate();

  // OTP countdown timer
  useEffect(() => {
    if (otpTimer > 0) {
      const t = setTimeout(() => setOtpTimer(otpTimer - 1), 1000);
      return () => clearTimeout(t);
    }
  }, [otpTimer]);

  const updateField = (name, value) => {
    setFormData((prev) => ({ ...prev, [name]: value }));
    setErrors((prev) => ({ ...prev, [name]: '' }));
  };

  // === Validation per step ===
  const validateStep1 = () => {
    if (!formData.role) {
      setErrors({ role: 'Please select a role' });
      return false;
    }
    return true;
  };

  const validateStep2 = () => {
    const errs = {};
    if (!/^[6-9]\d{9}$/.test(formData.phone)) errs.phone = 'Enter a valid 10-digit Indian phone number';
    if (!otpVerified) errs.otp = 'Please verify OTP first';
    if (Object.keys(errs).length) { setErrors(errs); return false; }
    return true;
  };

  const validateStep3 = () => {
    const errs = {};
    if (!formData.firstName.trim() || formData.firstName.trim().length < 2) errs.firstName = 'First name must be at least 2 characters';
    if (!formData.lastName.trim() || formData.lastName.trim().length < 2) errs.lastName = 'Last name must be at least 2 characters';
    if (formData.password.length < 6) errs.password = 'Password must be at least 6 characters';
    if (formData.password !== formData.confirmPassword) errs.confirmPassword = 'Passwords do not match';
    if (!formData.dateOfBirth) errs.dateOfBirth = 'Date of birth is required';
    if (!formData.gender) errs.gender = 'Gender is required';
    if (Object.keys(errs).length) { setErrors(errs); return false; }
    return true;
  };

  const validateStep4 = () => {
    const errs = {};
    if (!/^[1-9]\d{5}$/.test(formData.pincode)) errs.pincode = 'Enter a valid 6-digit pincode';
    if (Object.keys(errs).length) { setErrors(errs); return false; }
    return true;
  };

  const handleNext = () => {
    const validators = [validateStep1, validateStep2, validateStep3, validateStep4];
    if (validators[step - 1]()) {
      setStep(step + 1);
      setErrors({});
    }
  };

  const handleBack = () => {
    if (step > 1) setStep(step - 1);
  };

  // === OTP handlers ===
  const handleSendOtp = async () => {
    if (!/^[6-9]\d{9}$/.test(formData.phone)) {
      setErrors({ phone: 'Enter a valid 10-digit Indian phone number' });
      return;
    }
    setLoading(true);
    try {
      await sendOtp({ phoneNumber: formData.phone, purpose: 'REGISTRATION' });
      setOtpSent(true);
      setOtpTimer(120);
      toast.success('OTP sent! Check your Spring Boot console.');
      setTimeout(() => otpRefs.current[0]?.focus(), 100);
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed to send OTP');
    } finally {
      setLoading(false);
    }
  };

  const handleVerifyOtp = async () => {
    const otpString = otp.join('');
    if (otpString.length !== 6) {
      setErrors({ otp: 'Enter the complete 6-digit OTP' });
      return;
    }
    setLoading(true);
    try {
      await verifyOtp({ phoneNumber: formData.phone, otp: otpString, purpose: 'REGISTRATION' });
      setOtpVerified(true);
      toast.success('Phone verified successfully!');
    } catch (err) {
      setErrors({ otp: err.response?.data?.message || 'Invalid OTP' });
    } finally {
      setLoading(false);
    }
  };

  const handleOtpChange = (index, value) => {
    if (!/^\d*$/.test(value)) return;
    const newOtp = [...otp];
    newOtp[index] = value.slice(-1);
    setOtp(newOtp);
    setErrors((prev) => ({ ...prev, otp: '' }));
    if (value && index < 5) otpRefs.current[index + 1]?.focus();
  };

  const handleOtpKeyDown = (index, e) => {
    if (e.key === 'Backspace' && !otp[index] && index > 0) otpRefs.current[index - 1]?.focus();
  };

  const handleOtpPaste = (e) => {
    e.preventDefault();
    const pasted = e.clipboardData.getData('text').replace(/\D/g, '').slice(0, 6);
    if (pasted.length === 6) {
      setOtp(pasted.split(''));
      otpRefs.current[5]?.focus();
    }
  };

  // === Pincode lookup ===
  const handlePincodeLookup = async (pincode) => {
    updateField('pincode', pincode);
    if (pincode.length === 6) {
      setPincodeLoading(true);
      try {
        const response = await getLocationByPincode(pincode);
        const location = response.data.data;
        if (location) {
          setFormData((prev) => ({
            ...prev,
            block: location.block || '',
            district: location.district || '',
            state: location.state || '',
          }));
        }
      } catch {
        toast.warning('Could not fetch location. You can enter manually.');
      } finally {
        setPincodeLoading(false);
      }
    } else {
      setFormData((prev) => ({ ...prev, block: '', district: '', state: '' }));
    }
  };

  // === Submit ===
  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!validateStep4()) return;

    setLoading(true);
    try {
      const payload = {
        firstName: formData.firstName.trim(),
        lastName: formData.lastName.trim(),
        phoneNumber: formData.phone,
        password: formData.password,
        dateOfBirth: formData.dateOfBirth,
        gender: formData.gender,
        role: 'ROLE_' + formData.role,
        pincode: formData.pincode,
        otp: otp.join(''),
      };

      const result = await register(payload);
      if (result.success) {
        toast.success('Registration successful! Welcome to ServeTech.');
        if (formData.role === 'CLIENT') navigate('/client/dashboard', { replace: true });
        else navigate('/worker/dashboard', { replace: true });
      } else {
        toast.error(result.message || 'Registration failed');
        setErrors({ submit: result.message });
      }
    } catch {
      toast.error('An unexpected error occurred');
    } finally {
      setLoading(false);
    }
  };

  // === Step Indicator ===
  const renderStepIndicator = () => (
    <div className="flex items-center justify-center gap-1.5 mb-8">
      {STEPS.map((label, index) => {
        const stepNum = index + 1;
        const isActive = stepNum === step;
        const isCompleted = stepNum < step;
        return (
          <div key={label} className="flex items-center gap-1.5">
            <div className="flex flex-col items-center">
              <div className={`flex items-center justify-center h-9 w-9 rounded-full text-xs font-bold transition-all duration-300 ${
                isActive ? 'bg-amber-500 text-black scale-110 shadow-lg shadow-amber-500/30' :
                isCompleted ? 'bg-green-500 text-white' : 'bg-slate-700 text-slate-400'
              }`}>
                {isCompleted ? '✓' : stepNum}
              </div>
              <span className={`text-[10px] mt-1 font-medium ${isActive ? 'text-amber-400' : isCompleted ? 'text-green-400' : 'text-slate-500'}`}>
                {label}
              </span>
            </div>
            {index < STEPS.length - 1 && (
              <div className={`w-8 h-0.5 mb-4 transition-all ${isCompleted ? 'bg-green-500' : 'bg-slate-700'}`} />
            )}
          </div>
        );
      })}
    </div>
  );

  // === Step 1: Choose Role ===
  const renderStep1 = () => (
    <div className="space-y-4">
      <h2 className="text-xl font-semibold text-white text-center mb-6">Choose Your Role</h2>
      <div className="grid grid-cols-2 gap-4">
        {[
          { value: 'WORKER', label: 'Worker', desc: 'Find jobs & earn money', icon: '🔧' },
          { value: 'CLIENT', label: 'Client', desc: 'Hire workers for tasks', icon: '📋' },
        ].map((r) => (
          <button
            key={r.value}
            type="button"
            onClick={() => updateField('role', r.value)}
            className={`p-5 rounded-xl border-2 text-center transition-all duration-200 cursor-pointer ${
              formData.role === r.value
                ? 'border-amber-400 bg-amber-500/10 shadow-lg shadow-amber-500/10'
                : 'border-slate-600 bg-slate-700/30 hover:border-slate-500 hover:bg-slate-700/50'
            }`}
          >
            <div className="text-3xl mb-2">{r.icon}</div>
            <h3 className={`font-bold text-lg ${formData.role === r.value ? 'text-amber-400' : 'text-white'}`}>{r.label}</h3>
            <p className="text-xs text-slate-400 mt-1">{r.desc}</p>
          </button>
        ))}
      </div>
      {errors.role && <p className="text-red-400 text-xs text-center">{errors.role}</p>}
    </div>
  );

  // === Step 2: Phone & OTP ===
  const renderStep2 = () => (
    <div className="space-y-5">
      <h2 className="text-xl font-semibold text-white text-center mb-6">Verify Your Phone</h2>

      {/* Phone Input */}
      <div>
        <label className="block text-sm font-medium text-slate-300 mb-1.5">Phone Number</label>
        <div className="flex gap-2">
          <div className="relative flex-1">
            <span className="absolute left-4 top-1/2 -translate-y-1/2 text-slate-400 text-sm font-medium">+91</span>
            <input
              type="tel"
              value={formData.phone}
              onChange={(e) => {
                updateField('phone', e.target.value.replace(/\D/g, '').slice(0, 10));
                setOtpSent(false);
                setOtpVerified(false);
                setOtp(['', '', '', '', '', '']);
              }}
              placeholder="10-digit number"
              className="w-full bg-slate-700/50 border border-slate-600 text-white rounded-xl pl-12 pr-4 py-3 focus:border-amber-400 focus:ring-2 focus:ring-amber-400/20 outline-none transition-all placeholder-slate-500"
              maxLength={10}
              disabled={otpVerified}
            />
          </div>
          {!otpVerified && (
            <Button
              type="button"
              variant={otpSent ? 'secondary' : 'primary'}
              onClick={handleSendOtp}
              loading={loading && !otpSent}
              disabled={otpTimer > 0 && otpSent}
            >
              {otpSent ? (otpTimer > 0 ? `${otpTimer}s` : 'Resend') : 'Send OTP'}
            </Button>
          )}
        </div>
        {errors.phone && <p className="text-red-400 text-xs mt-1">{errors.phone}</p>}
      </div>

      {/* OTP Verified Badge */}
      {otpVerified && (
        <div className="flex items-center gap-2 bg-green-500/10 border border-green-500/30 rounded-xl px-4 py-3">
          <svg className="h-5 w-5 text-green-400" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
            <path strokeLinecap="round" strokeLinejoin="round" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
          </svg>
          <span className="text-green-400 text-sm font-medium">Phone number verified ✓</span>
        </div>
      )}

      {/* OTP Input (only show if sent and not verified) */}
      {otpSent && !otpVerified && (
        <div className="space-y-4">
          <div>
            <label className="block text-sm font-medium text-slate-300 mb-2 text-center">Enter 6-digit OTP</label>
            <p className="text-xs text-slate-500 text-center mb-3">Check your Spring Boot console for the OTP code</p>
            <div className="flex justify-center gap-2.5">
              {otp.map((digit, idx) => (
                <input
                  key={idx}
                  ref={(el) => (otpRefs.current[idx] = el)}
                  type="text"
                  inputMode="numeric"
                  maxLength={1}
                  value={digit}
                  onChange={(e) => handleOtpChange(idx, e.target.value)}
                  onKeyDown={(e) => handleOtpKeyDown(idx, e)}
                  onPaste={idx === 0 ? handleOtpPaste : undefined}
                  className="w-11 h-13 text-center text-lg font-bold bg-slate-700/50 border border-slate-600 text-white rounded-xl focus:border-amber-400 focus:ring-2 focus:ring-amber-400/20 outline-none transition-all"
                />
              ))}
            </div>
            {errors.otp && <p className="text-red-400 text-xs mt-2 text-center">{errors.otp}</p>}
          </div>
          <Button type="button" variant="primary" fullWidth onClick={handleVerifyOtp} loading={loading}>
            Verify OTP
          </Button>
        </div>
      )}
    </div>
  );

  // === Step 3: Personal Info ===
  const renderStep3 = () => (
    <div className="space-y-4">
      <h2 className="text-xl font-semibold text-white text-center mb-6">Personal Information</h2>

      <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
        <div>
          <label className="block text-sm font-medium text-slate-300 mb-1.5">First Name</label>
          <input type="text" value={formData.firstName} onChange={(e) => updateField('firstName', e.target.value)}
            placeholder="Enter first name"
            className="w-full bg-slate-700/50 border border-slate-600 text-white rounded-xl px-4 py-3 focus:border-amber-400 focus:ring-2 focus:ring-amber-400/20 outline-none transition-all placeholder-slate-500" />
          {errors.firstName && <p className="text-red-400 text-xs mt-1">{errors.firstName}</p>}
        </div>
        <div>
          <label className="block text-sm font-medium text-slate-300 mb-1.5">Last Name</label>
          <input type="text" value={formData.lastName} onChange={(e) => updateField('lastName', e.target.value)}
            placeholder="Enter last name"
            className="w-full bg-slate-700/50 border border-slate-600 text-white rounded-xl px-4 py-3 focus:border-amber-400 focus:ring-2 focus:ring-amber-400/20 outline-none transition-all placeholder-slate-500" />
          {errors.lastName && <p className="text-red-400 text-xs mt-1">{errors.lastName}</p>}
        </div>
      </div>

      <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
        <div>
          <label className="block text-sm font-medium text-slate-300 mb-1.5">Password</label>
          <input type="password" value={formData.password} onChange={(e) => updateField('password', e.target.value)}
            placeholder="Min 6 characters"
            className="w-full bg-slate-700/50 border border-slate-600 text-white rounded-xl px-4 py-3 focus:border-amber-400 focus:ring-2 focus:ring-amber-400/20 outline-none transition-all placeholder-slate-500" />
          {errors.password && <p className="text-red-400 text-xs mt-1">{errors.password}</p>}
        </div>
        <div>
          <label className="block text-sm font-medium text-slate-300 mb-1.5">Confirm Password</label>
          <input type="password" value={formData.confirmPassword} onChange={(e) => updateField('confirmPassword', e.target.value)}
            placeholder="Re-enter password"
            className="w-full bg-slate-700/50 border border-slate-600 text-white rounded-xl px-4 py-3 focus:border-amber-400 focus:ring-2 focus:ring-amber-400/20 outline-none transition-all placeholder-slate-500" />
          {errors.confirmPassword && <p className="text-red-400 text-xs mt-1">{errors.confirmPassword}</p>}
        </div>
      </div>

      <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
        <div>
          <label className="block text-sm font-medium text-slate-300 mb-1.5">Date of Birth</label>
          <input type="date" value={formData.dateOfBirth} onChange={(e) => updateField('dateOfBirth', e.target.value)}
            className="w-full bg-slate-700/50 border border-slate-600 text-white rounded-xl px-4 py-3 focus:border-amber-400 focus:ring-2 focus:ring-amber-400/20 outline-none transition-all [color-scheme:dark]" />
          {errors.dateOfBirth && <p className="text-red-400 text-xs mt-1">{errors.dateOfBirth}</p>}
        </div>
        <div>
          <label className="block text-sm font-medium text-slate-300 mb-1.5">Gender</label>
          <select value={formData.gender} onChange={(e) => updateField('gender', e.target.value)}
            className="w-full bg-slate-700/50 border border-slate-600 text-white rounded-xl px-4 py-3 focus:border-amber-400 focus:ring-2 focus:ring-amber-400/20 outline-none transition-all">
            <option value="">Select gender</option>
            {GENDER_OPTIONS.map((opt) => (
              <option key={opt.value} value={opt.value}>{opt.label}</option>
            ))}
          </select>
          {errors.gender && <p className="text-red-400 text-xs mt-1">{errors.gender}</p>}
        </div>
      </div>
    </div>
  );

  // === Step 4: Location ===
  const renderStep4 = () => (
    <div className="space-y-4">
      <h2 className="text-xl font-semibold text-white text-center mb-6">Your Location</h2>

      <div>
        <label className="block text-sm font-medium text-slate-300 mb-1.5">Pincode</label>
        <div className="relative">
          <input
            type="text"
            value={formData.pincode}
            onChange={(e) => handlePincodeLookup(e.target.value.replace(/\D/g, '').slice(0, 6))}
            placeholder="Enter 6-digit pincode"
            className="w-full bg-slate-700/50 border border-slate-600 text-white rounded-xl px-4 py-3 focus:border-amber-400 focus:ring-2 focus:ring-amber-400/20 outline-none transition-all placeholder-slate-500"
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

      {(formData.block || formData.district || formData.state) && (
        <div className="bg-slate-700/30 rounded-xl p-4 border border-slate-600/50 space-y-3">
          <div className="flex items-center gap-2 text-sm text-green-400 mb-2">
            <svg className="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
              <path strokeLinecap="round" strokeLinejoin="round" d="M5 13l4 4L19 7" />
            </svg>
            Location found
          </div>
          <div className="grid grid-cols-1 sm:grid-cols-3 gap-3">
            <div>
              <label className="block text-xs text-slate-400 mb-1">Block/Area</label>
              <input type="text" value={formData.block} onChange={(e) => updateField('block', e.target.value)}
                className="w-full bg-slate-700/50 border border-slate-600 text-white rounded-lg px-3 py-2 text-sm focus:border-amber-400 outline-none transition-all" />
            </div>
            <div>
              <label className="block text-xs text-slate-400 mb-1">District</label>
              <input type="text" value={formData.district} readOnly
                className="w-full bg-slate-600/50 border border-slate-600 text-slate-300 rounded-lg px-3 py-2 text-sm" />
            </div>
            <div>
              <label className="block text-xs text-slate-400 mb-1">State</label>
              <input type="text" value={formData.state} readOnly
                className="w-full bg-slate-600/50 border border-slate-600 text-slate-300 rounded-lg px-3 py-2 text-sm" />
            </div>
          </div>
        </div>
      )}

      {errors.submit && (
        <div className="bg-red-500/10 border border-red-500/30 rounded-lg px-4 py-3 text-sm text-red-400">
          {errors.submit}
        </div>
      )}
    </div>
  );

  return (
    <div className="min-h-screen flex items-center justify-center bg-slate-900 relative overflow-hidden px-4 py-8">
      <div className="absolute inset-0 bg-gradient-to-br from-slate-900 via-slate-800 to-slate-900" />
      <div className="absolute top-0 right-0 w-[500px] h-[500px] bg-amber-500/5 rounded-full blur-3xl" />
      <div className="absolute bottom-0 left-0 w-[400px] h-[400px] bg-amber-500/3 rounded-full blur-3xl" />

      <div className="relative w-full max-w-lg">
        {/* Branding */}
        <div className="text-center mb-6">
          <div className="inline-flex items-center justify-center h-14 w-14 rounded-xl bg-amber-500 mb-4 shadow-lg shadow-amber-500/20">
            <span className="text-black font-bold text-2xl">S</span>
          </div>
          <h1 className="text-3xl font-bold text-white">
            Join Serve<span className="text-amber-400">Tech</span>
          </h1>
          <p className="mt-2 text-slate-400">Create your account in just a few steps</p>
        </div>

        {/* Step Indicator */}
        {renderStepIndicator()}

        {/* Form Card */}
        <div className="bg-slate-800/80 backdrop-blur-sm rounded-2xl shadow-xl border border-slate-700/50 p-8">
          <form onSubmit={handleSubmit}>
            {step === 1 && renderStep1()}
            {step === 2 && renderStep2()}
            {step === 3 && renderStep3()}
            {step === 4 && renderStep4()}

            {/* Navigation Buttons */}
            <div className="flex items-center justify-between mt-8 gap-4">
              {step > 1 ? (
                <Button variant="secondary" onClick={handleBack} type="button">
                  ← Back
                </Button>
              ) : (
                <div />
              )}

              {step < 4 ? (
                <Button variant="primary" onClick={handleNext} type="button">
                  Next →
                </Button>
              ) : (
                <Button type="submit" variant="primary" loading={loading}>
                  Create Account
                </Button>
              )}
            </div>
          </form>

          {/* Login link */}
          <div className="mt-6 text-center">
            <p className="text-sm text-slate-400">
              Already have an account?{' '}
              <Link to="/login" className="text-amber-400 hover:text-amber-300 font-medium transition-colors">
                Sign In
              </Link>
            </p>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Register;
