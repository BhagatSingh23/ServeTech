import { useState, useRef, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { useToast } from '../../components/common/Toast';
import Button from '../../components/common/Button';

const Login = () => {
  const [step, setStep] = useState(1); // 1 = credentials, 2 = OTP
  const [loginMethod, setLoginMethod] = useState('phone'); // 'phone' or 'email'
  const [identifier, setIdentifier] = useState(''); // phone or email
  const [password, setPassword] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [otp, setOtp] = useState(['', '', '', '', '', '']);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [otpTimer, setOtpTimer] = useState(0);

  const otpRefs = useRef([]);

  const { loginStep1, loginStep2, googleLogin } = useAuth();
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
  const validateEmail = (value) => /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value);

  // Initialize Google Sign-In
  useEffect(() => {
    // Add Google script if it doesn't exist
    if (!document.getElementById('google-jssdk')) {
      const script = document.createElement('script');
      script.id = 'google-jssdk';
      script.src = 'https://accounts.google.com/gsi/client';
      script.async = true;
      script.defer = true;
      document.head.appendChild(script);
    }
  }, []);

  const handleGoogleCallback = async (response) => {
    setLoading(true);
    setError('');
    const result = await googleLogin(response.credential);
    if (result.success) {
      toast.success('Login successful!');
      const roles = result.user?.roles || [];
      if (roles.includes('ROLE_ADMIN')) navigate('/admin/dashboard', { replace: true });
      else if (roles.includes('ROLE_CLIENT')) navigate('/client/dashboard', { replace: true });
      else if (roles.includes('ROLE_WORKER')) navigate('/worker/dashboard', { replace: true });
      else navigate('/', { replace: true });
    } else {
      setError(result.message || 'Google Sign-In failed.');
      toast.error(result.message || 'Google Sign-In failed.');
    }
    setLoading(false);
  };

  useEffect(() => {
    // Wait for the script to load and initialize the button
    const checkGoogle = setInterval(() => {
      if (window.google && document.getElementById('google-signin-btn')) {
        clearInterval(checkGoogle);
        window.google.accounts.id.initialize({
          client_id: '99999999999-placeholder.apps.googleusercontent.com', // Replace with actual Client ID
          callback: handleGoogleCallback,
          context: 'signin',
          ux_mode: 'popup',
        });
        window.google.accounts.id.renderButton(
          document.getElementById('google-signin-btn'),
          { theme: 'outline', size: 'large', width: '100%' }
        );
      }
    }, 100);
    return () => clearInterval(checkGoogle);
  }, [step]); // re-render button if step changes (though we only show it on step 1)

  // Step 1: Validate credentials and send OTP
  const handleCredentialsSubmit = async (e) => {
    e.preventDefault();
    setError('');

    if (loginMethod === 'phone' && !validatePhone(identifier)) {
      setError('Please enter a valid 10-digit Indian phone number');
      return;
    }
    if (loginMethod === 'email' && !validateEmail(identifier)) {
      setError('Please enter a valid email address');
      return;
    }
    if (password.length < 6) {
      setError('Password must be at least 6 characters');
      return;
    }

    setLoading(true);
    try {
      const result = await loginStep1({ usernameOrPhone: identifier, password });
      if (result.success) {
        toast.success('OTP sent successfully!');
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
      const result = await loginStep2(identifier, otpString);
      if (result.success) {
        toast.success('Login successful!');
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
      const result = await loginStep1({ usernameOrPhone: identifier, password });
      if (result.success) {
        toast.success('OTP sent successfully!');
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
    <div className="min-h-screen flex flex-col items-center justify-center bg-slate-950 relative overflow-x-hidden overflow-y-auto px-4 py-16">
      {/* Background effects */}
      <div className="absolute inset-0 bg-gradient-to-br from-blue-950 via-slate-900 to-purple-950 opacity-80" />
      <div className="absolute top-0 left-0 w-full h-full overflow-hidden pointer-events-none">
        <div className="absolute top-[-10%] left-[-10%] w-[50vw] h-[50vw] bg-blue-500/20 rounded-full blur-[100px] animate-[pulse_8s_ease-in-out_infinite]" />
        <div className="absolute bottom-[-10%] right-[-10%] w-[50vw] h-[50vw] bg-purple-500/20 rounded-full blur-[100px] animate-[pulse_10s_ease-in-out_infinite_reverse]" />
        <div className="absolute top-[20%] right-[10%] w-[30vw] h-[30vw] bg-amber-500/10 rounded-full blur-[80px] animate-[pulse_12s_ease-in-out_infinite]" />
      </div>

      <div className="absolute top-4 right-4"></div>

      <div className="relative w-full max-w-md my-auto pt-8">
        {/* Branding */}
        <div className="text-center mb-8 animate-[fadeIn_0.5s_ease-out]">
          <div className="inline-flex items-center justify-center h-16 w-16 rounded-xl bg-gradient-to-tr from-amber-500 to-amber-300 mb-4 shadow-lg shadow-amber-500/30 overflow-hidden">
            <img src="/logo.png" alt="ServeTech Logo" className="h-full w-full object-cover" />
          </div>
          <h1 className="text-3xl font-bold text-white tracking-tight">
            Welcome to Serve<span className="text-transparent bg-clip-text bg-gradient-to-r from-amber-400 to-amber-200">Tech</span>
          </h1>
          <p className="mt-2 text-slate-400">
            {step === 1 ? ('Sign in to your account') : ('Enter the OTP sent to your device')}
          </p>
        </div>

        {/* Step indicator */}
        <div className="flex items-center justify-center gap-3 mb-6">
          <div className={`flex items-center justify-center h-8 w-8 rounded-full text-sm font-bold transition-all ${step >= 1 ? 'bg-amber-500 text-black' : 'bg-slate-700 text-slate-400'}`}>1</div>
          <div className={`w-12 h-0.5 transition-all ${step >= 2 ? 'bg-amber-500' : 'bg-slate-700'}`} />
          <div className={`flex items-center justify-center h-8 w-8 rounded-full text-sm font-bold transition-all ${step >= 2 ? 'bg-amber-500 text-black' : 'bg-slate-700 text-slate-400'}`}>2</div>
        </div>

        {/* Login Card */}
        <div className="bg-slate-900/60 backdrop-blur-2xl rounded-3xl shadow-2xl border border-slate-700/50 p-8 animate-[slideUp_0.4s_ease-out] relative overflow-hidden">
          {/* Subtle inner border glow */}
          <div className="absolute inset-0 border border-white/5 rounded-3xl pointer-events-none" />
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
              {/* Login Method Toggle */}
              <div className="flex p-1 bg-slate-900/50 rounded-xl">
                <button
                  type="button"
                  onClick={() => { setLoginMethod('phone'); setIdentifier(''); setError(''); }}
                  className={`flex-1 py-2 text-sm font-medium rounded-lg transition-all ${loginMethod === 'phone' ? 'bg-slate-700 text-white shadow-md' : 'text-slate-400 hover:text-slate-300'}`}
                >
                  Phone
                </button>
                <button
                  type="button"
                  onClick={() => { setLoginMethod('email'); setIdentifier(''); setError(''); }}
                  className={`flex-1 py-2 text-sm font-medium rounded-lg transition-all ${loginMethod === 'email' ? 'bg-slate-700 text-white shadow-md' : 'text-slate-400 hover:text-slate-300'}`}
                >
                  Email
                </button>
              </div>

              {/* Identifier */}
              <div>
                <label className="block text-sm font-medium text-slate-300 mb-1.5">
                  {loginMethod === 'phone' ? ('Phone Number') : ('Email Address')}
                </label>
                <div className="relative">
                  {loginMethod === 'phone' && <span className="absolute left-4 top-1/2 -translate-y-1/2 text-slate-400 text-sm font-medium">+91</span>}
                  <input
                    type={loginMethod === 'phone' ? 'tel' : 'email'}
                    value={identifier}
                    onChange={(e) => {
                      const val = loginMethod === 'phone' ? e.target.value.replace(/\D/g, '').slice(0, 10) : e.target.value;
                      setIdentifier(val);
                      setError('');
                    }}
                    placeholder={loginMethod === 'phone' ? '10-digit number' : 'name@example.com'}
                    className={`w-full bg-slate-950/60 border border-slate-700 text-white rounded-xl pr-4 py-3 focus:border-amber-400 focus:ring-2 focus:ring-amber-400/20 outline-none transition-all placeholder-slate-500 hover:border-slate-600 ${loginMethod === 'phone' ? 'pl-12' : 'pl-4'}`}
                    maxLength={loginMethod === 'phone' ? 10 : 100}
                  />
                </div>
              </div>

              {/* Password */}
              <div>
                <label className="block text-sm font-medium text-slate-300 mb-1.5">Password</label>
                <div className="relative group">
                  <input
                    type={showPassword ? 'text' : 'password'}
                    value={password}
                    onChange={(e) => {
                      setPassword(e.target.value);
                      setError('');
                    }}
                    placeholder="Enter your password"
                    className="w-full bg-slate-950/60 border border-slate-700 text-white rounded-xl px-4 py-3 pr-12 focus:border-amber-400 focus:ring-2 focus:ring-amber-400/20 outline-none transition-all placeholder-slate-500 hover:border-slate-600"
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
              <Button type="submit" variant="primary" size="lg" loading={loading} fullWidth className="bg-gradient-to-r from-amber-500 to-amber-400 text-black shadow-lg shadow-amber-500/20">
                Verify & Send OTP →
              </Button>

              <div className="relative my-6">
                <div className="absolute inset-0 flex items-center">
                  <div className="w-full border-t border-slate-700"></div>
                </div>
                <div className="relative flex justify-center text-sm">
                  <span className="px-2 bg-slate-800 text-slate-400">Or continue with</span>
                </div>
              </div>

              {/* Google Sign In Button Container */}
              <div id="google-signin-btn" className="w-full flex justify-center"></div>

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
                  OTP sent to <span className="text-amber-400 font-semibold">{loginMethod === 'phone' ? '+91 ' : ''}{identifier}</span>
                </p>
                <p className="text-slate-500 text-xs mt-1">Check your device for the OTP code</p>
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
                    className="w-12 h-14 text-center text-xl font-bold bg-slate-900/50 border border-slate-600 text-white rounded-xl focus:border-amber-400 focus:ring-2 focus:ring-amber-400/20 outline-none transition-all"
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
                <Button type="submit" variant="primary" size="lg" loading={loading} fullWidth className="bg-gradient-to-r from-amber-500 to-amber-400 text-black">
                  Verify OTP & Login
                </Button>
              </div>
            </form>
          )}

          {/* Register link */}
          <div className="mt-6 text-center">
            <p className="text-sm text-slate-400">
              Don't have an account?{' '}
              <Link to="/register" className="text-amber-400 hover:text-amber-300 font-medium transition-colors">
                Create Account
              </Link>
            </p>
          </div>
        </div>
      </div>
      <style>{`
        @keyframes fadeIn {
          from { opacity: 0; transform: translateY(-10px); }
          to { opacity: 1; transform: translateY(0); }
        }
        @keyframes slideUp {
          from { opacity: 0; transform: translateY(20px); }
          to { opacity: 1; transform: translateY(0); }
        }
      `}</style>
    </div>
  );
};

export default Login;
