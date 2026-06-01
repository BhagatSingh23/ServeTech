import { useState, useRef, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { useToast } from '../../components/common/Toast';
import Button from '../../components/common/Button';

const Login = () => {
  const [step, setStep] = useState(1); // 1 = credentials, 2 = OTP
  const [phone, setPhone] = useState('');
  const [password, setPassword] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [otp, setOtp] = useState(['', '', '', '', '', '']);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [otpTimer, setOtpTimer] = useState(0);

  const otpRefs = useRef([]);

  const { loginStep1, loginStep2 } = useAuth();
  const toast = useToast();
  const navigate = useNavigate();

  // OTP countdown timer
  useEffect(() => {
    if (otpTimer > 0) {
      const t = setTimeout(() => setOtpTimer(otpTimer - 1), 1000);
      return () => clearTimeout(t);
    }
  }, [otpTimer]);

  const validatePhone = (value) => /^[6-9]\d{9}$/.test(value);

  // Step 1: Validate credentials and send OTP
  const handleCredentialsSubmit = async (e) => {
    e.preventDefault();
    setError('');

    if (!validatePhone(phone)) {
      setError('Please enter a valid 10-digit Indian phone number');
      return;
    }
    if (password.length < 6) {
      setError('Password must be at least 6 characters');
      return;
    }

    setLoading(true);
    try {
      const result = await loginStep1({ usernameOrPhone: phone, password });
      if (result.success) {
        toast.success('OTP sent! Check your Spring Boot console.');
        setStep(2);
        setOtpTimer(120);
        setTimeout(() => otpRefs.current[0]?.focus(), 100);
      } else {
        setError(result.message || 'Invalid credentials.');
        toast.error(result.message || 'Login failed');
      }
    } catch {
      setError('Something went wrong. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  // Step 2: Verify OTP
  const handleOtpSubmit = async (e) => {
    e.preventDefault();
    const otpString = otp.join('');
    if (otpString.length !== 6) {
      setError('Please enter the complete 6-digit OTP');
      return;
    }

    setLoading(true);
    setError('');
    try {
      const result = await loginStep2(phone, otpString);
      if (result.success) {
        toast.success('Login successful! Welcome back.');
        const user = result.user;
        const roles = user?.roles || [];
        if (roles.includes('ROLE_ADMIN')) navigate('/admin/dashboard', { replace: true });
        else if (roles.includes('ROLE_CLIENT')) navigate('/client/dashboard', { replace: true });
        else if (roles.includes('ROLE_WORKER')) navigate('/worker/dashboard', { replace: true });
        else navigate('/', { replace: true });
      } else {
        setError(result.message || 'Invalid OTP.');
        toast.error(result.message || 'OTP verification failed');
      }
    } catch {
      setError('Something went wrong. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  // OTP input handlers
  const handleOtpChange = (index, value) => {
    if (!/^\d*$/.test(value)) return;
    const newOtp = [...otp];
    newOtp[index] = value.slice(-1);
    setOtp(newOtp);
    setError('');
    if (value && index < 5) {
      otpRefs.current[index + 1]?.focus();
    }
  };

  const handleOtpKeyDown = (index, e) => {
    if (e.key === 'Backspace' && !otp[index] && index > 0) {
      otpRefs.current[index - 1]?.focus();
    }
  };

  const handleOtpPaste = (e) => {
    e.preventDefault();
    const pasted = e.clipboardData.getData('text').replace(/\D/g, '').slice(0, 6);
    if (pasted.length === 6) {
      setOtp(pasted.split(''));
      otpRefs.current[5]?.focus();
    }
  };

  const handleResendOtp = async () => {
    if (otpTimer > 0) return;
    setLoading(true);
    try {
      const result = await loginStep1({ usernameOrPhone: phone, password });
      if (result.success) {
        toast.success('OTP resent! Check your Spring Boot console.');
        setOtpTimer(120);
        setOtp(['', '', '', '', '', '']);
        otpRefs.current[0]?.focus();
      }
    } catch {
      toast.error('Failed to resend OTP');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-slate-900 relative overflow-hidden px-4">
      {/* Background effects */}
      <div className="absolute inset-0 bg-gradient-to-br from-slate-900 via-slate-800 to-slate-900" />
      <div className="absolute top-0 left-1/2 -translate-x-1/2 w-[600px] h-[600px] bg-amber-500/5 rounded-full blur-3xl" />
      <div className="absolute bottom-0 right-0 w-[400px] h-[400px] bg-amber-500/3 rounded-full blur-3xl" />

      <div className="relative w-full max-w-md">
        {/* Branding */}
        <div className="text-center mb-8">
          <div className="inline-flex items-center justify-center h-14 w-14 rounded-xl bg-amber-500 mb-4 shadow-lg shadow-amber-500/20">
            <span className="text-black font-bold text-2xl">S</span>
          </div>
          <h1 className="text-3xl font-bold text-white">
            Welcome to Serve<span className="text-amber-400">Tech</span>
          </h1>
          <p className="mt-2 text-slate-400">
            {step === 1 ? 'Sign in to your account' : 'Enter the OTP sent to your phone'}
          </p>
        </div>

        {/* Step indicator */}
        <div className="flex items-center justify-center gap-3 mb-6">
          <div className={`flex items-center justify-center h-8 w-8 rounded-full text-sm font-bold transition-all ${step >= 1 ? 'bg-amber-500 text-black' : 'bg-slate-700 text-slate-400'}`}>1</div>
          <div className={`w-12 h-0.5 transition-all ${step >= 2 ? 'bg-amber-500' : 'bg-slate-700'}`} />
          <div className={`flex items-center justify-center h-8 w-8 rounded-full text-sm font-bold transition-all ${step >= 2 ? 'bg-amber-500 text-black' : 'bg-slate-700 text-slate-400'}`}>2</div>
        </div>

        {/* Login Card */}
        <div className="bg-slate-800/80 backdrop-blur-sm rounded-2xl shadow-xl border border-slate-700/50 p-8">
          {error && (
            <div className="bg-red-500/10 border border-red-500/30 rounded-lg px-4 py-3 text-sm text-red-400 flex items-center gap-2 mb-5">
              <svg className="h-4 w-4 flex-shrink-0" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                <path strokeLinecap="round" strokeLinejoin="round" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-2.5L13.732 4c-.77-.833-1.964-.833-2.732 0L3.34 16.5c-.77.833.192 2.5 1.732 2.5z" />
              </svg>
              {error}
            </div>
          )}

          {step === 1 && (
            <form onSubmit={handleCredentialsSubmit} className="space-y-5">
              {/* Phone Number */}
              <div>
                <label className="block text-sm font-medium text-slate-300 mb-1.5">Phone Number</label>
                <div className="relative">
                  <span className="absolute left-4 top-1/2 -translate-y-1/2 text-slate-400 text-sm font-medium">+91</span>
                  <input
                    type="tel"
                    value={phone}
                    onChange={(e) => {
                      const val = e.target.value.replace(/\D/g, '').slice(0, 10);
                      setPhone(val);
                      setError('');
                    }}
                    placeholder="Enter 10-digit number"
                    className="w-full bg-slate-700/50 border border-slate-600 text-white rounded-xl pl-12 pr-4 py-3 focus:border-amber-400 focus:ring-2 focus:ring-amber-400/20 outline-none transition-all placeholder-slate-500"
                    maxLength={10}
                  />
                </div>
              </div>

              {/* Password */}
              <div>
                <label className="block text-sm font-medium text-slate-300 mb-1.5">Password</label>
                <div className="relative">
                  <input
                    type={showPassword ? 'text' : 'password'}
                    value={password}
                    onChange={(e) => {
                      setPassword(e.target.value);
                      setError('');
                    }}
                    placeholder="Enter your password"
                    className="w-full bg-slate-700/50 border border-slate-600 text-white rounded-xl px-4 py-3 pr-12 focus:border-amber-400 focus:ring-2 focus:ring-amber-400/20 outline-none transition-all placeholder-slate-500"
                  />
                  <button
                    type="button"
                    onClick={() => setShowPassword(!showPassword)}
                    className="absolute right-3 top-1/2 -translate-y-1/2 text-slate-400 hover:text-slate-300 transition-colors cursor-pointer"
                  >
                    {showPassword ? (
                      <svg className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                        <path strokeLinecap="round" strokeLinejoin="round" d="M13.875 18.825A10.05 10.05 0 0112 19c-4.478 0-8.268-2.943-9.543-7a9.97 9.97 0 011.563-3.029m5.858.908a3 3 0 114.243 4.243M9.878 9.878l4.242 4.242M9.878 9.878L6.59 6.59m7.532 7.532l3.29 3.29M3 3l18 18" />
                      </svg>
                    ) : (
                      <svg className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                        <path strokeLinecap="round" strokeLinejoin="round" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                        <path strokeLinecap="round" strokeLinejoin="round" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
                      </svg>
                    )}
                  </button>
                </div>
              </div>

              {/* Submit */}
              <Button type="submit" variant="primary" size="lg" loading={loading} fullWidth>
                Verify & Send OTP →
              </Button>
            </form>
          )}

          {step === 2 && (
            <form onSubmit={handleOtpSubmit} className="space-y-6">
              <div className="text-center">
                <div className="inline-flex items-center justify-center h-16 w-16 rounded-2xl bg-amber-500/10 border border-amber-500/20 mb-4">
                  <svg className="h-8 w-8 text-amber-400" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={1.5}>
                    <path strokeLinecap="round" strokeLinejoin="round" d="M16.5 10.5V6.75a4.5 4.5 0 10-9 0v3.75m-.75 11.25h10.5a2.25 2.25 0 002.25-2.25v-6.75a2.25 2.25 0 00-2.25-2.25H6.75a2.25 2.25 0 00-2.25 2.25v6.75a2.25 2.25 0 002.25 2.25z" />
                  </svg>
                </div>
                <p className="text-slate-300 text-sm">
                  OTP sent to <span className="text-amber-400 font-semibold">+91 {phone}</span>
                </p>
                <p className="text-slate-500 text-xs mt-1">Check your Spring Boot console for the OTP code</p>
              </div>

              {/* OTP Input Boxes */}
              <div className="flex justify-center gap-3">
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
                    className="w-12 h-14 text-center text-xl font-bold bg-slate-700/50 border border-slate-600 text-white rounded-xl focus:border-amber-400 focus:ring-2 focus:ring-amber-400/20 outline-none transition-all"
                  />
                ))}
              </div>

              {/* Timer & Resend */}
              <div className="text-center">
                {otpTimer > 0 ? (
                  <p className="text-sm text-slate-400">
                    Resend OTP in <span className="text-amber-400 font-semibold">{Math.floor(otpTimer / 60)}:{(otpTimer % 60).toString().padStart(2, '0')}</span>
                  </p>
                ) : (
                  <button type="button" onClick={handleResendOtp} className="text-sm text-amber-400 hover:text-amber-300 font-medium transition-colors cursor-pointer">
                    Resend OTP
                  </button>
                )}
              </div>

              <div className="flex gap-3">
                <Button type="button" variant="secondary" onClick={() => { setStep(1); setOtp(['', '', '', '', '', '']); setError(''); }}>
                  ← Back
                </Button>
                <Button type="submit" variant="primary" size="lg" loading={loading} fullWidth>
                  Verify OTP & Login
                </Button>
              </div>
            </form>
          )}

          {/* Register link */}
          <div className="mt-6 text-center">
            <p className="text-sm text-slate-400">
              Don&apos;t have an account?{' '}
              <Link to="/register" className="text-amber-400 hover:text-amber-300 font-medium transition-colors">
                Create Account
              </Link>
            </p>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Login;
