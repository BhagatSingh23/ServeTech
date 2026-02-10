import { useState } from "react";

function AuthPage() {
  const [mode, setMode] = useState("login");
  const [showOtp, setShowOtp] = useState(false); 
  const [Password,setPassword] = useState();
  const [ConfirmPassword,setConfirmPassword] = useState();

  // for login 
  const [Username,setUsername] = useState("");
  const [LoginPassword, setLoginPassword] = useState();


  const handleSignupSubmit = async (e) => {
    e.preventDefault();
  
    const formElement = e.target;
    const formData = new FormData(formElement);
    const payload = Object.fromEntries(formData.entries());
  
    if (!showOtp) {

      if (!payload.email) {
        alert("Please enter your email first.");
        return;
      }
  
      try {
        //  API Call to send OTP
        const otpResponse = await fetch('http://localhost:8080/api/auth/send-otp', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ email: payload.email }), 
        });
  
        const otpResult = await otpResponse.json();
  
        if (otpResponse.ok) {
          setShowOtp(true); 
          alert("OTP sent to your email!");
        } else {
          alert(`Failed to send OTP: ${otpResult.message}`);
        }
      } catch (error) {
        console.error("OTP Error:", error);
        alert("Could not send OTP. Check your connection.");
      }
  
    } else {
      
      if (payload.password !== payload.confirmPassword) {
        alert("Passwords do not match!");
        return;
      }
  
      try {
        const response = await fetch('http://localhost:8080/api/auth/signup', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(payload), 
        });
  
        const result = await response.json();
  
        if (response.ok) {
          alert("Account created successfully!");
        } else {
          alert(`Error: ${result.message || 'Signup failed'}`);
        }
      } catch (error) {
        console.error("Signup Error:", error);
        alert("Could not connect to the server.");
      }
    }
  };

  const handleLoginSubmit = async (e) => {
    e.preventDefault();
  
    if (!Username || !LoginPassword) {   // should not be empty fields
      alert("Please enter both username and password");
      return;
    }
  
    const data = {
      username: Username,
      password: LoginPassword,
    };
  
    try {
      // Send request to backend
      const response = await fetch("http://localhost:8080/api/auth/login", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(data),
      });
  
      const result = await response.json();
  
      if (response.ok) {
        console.log("Login successful:", result);
  
  
        alert("Login successful!");
      } else {
        alert(result.message || "Invalid credentials");
      }
    } catch (error) {
      console.error("Login error:", error);
      alert("Unable to connect to server");
    }
  };
  

  return (
    <div className="min-h-full w-full bg-[#0d1117] flex items-center justify-center px-4 text-black">
      <div className="w-full h-full max-w-md bg-white rounded-xl shadow-lg p-6">
        
        {/* Toggle Buttons */}
        <div className="flex mb-6 border rounded-md overflow-hidden">
          <button
            className={`w-1/2 py-1 md:py-2 font-medium ${
              mode === "login"
                ? "bg-red-600 text-white"
                : "bg-gray-100 text-gray-700"
            }`}
            onClick={() => {
              setMode("login");
              setShowOtp(false); 
            }}
          >
            Log In
          </button>

          <button
            className={`w-1/2 py-1 md:py-2 font-medium ${
              mode === "signup"
                ? "bg-red-600 text-white"
                : "bg-gray-100 text-gray-700"
            }`}
            onClick={() => {
              setMode("signup");
              setShowOtp(false); 
            }}
          >
            Sign Up
          </button>
        </div>

        {/* Login Form */}
        {mode === "login" && (
          <form className="flex flex-col gap-4">
            <h2 className="text-[0.85em] md:text-2xl font-bold text-center mb-2">
              Welcome Back
            </h2>

            <div className="flex flex-col">
              <label className="text-[0.75em] md:text-sm font-medium mb-1">Username</label>
              <input
                type="text"
                value={Username}
                className="border rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
                placeholder="Enter username or Mobile Number"
                required
                onChange={(e)=>{setUsername(e.target.value)}}
              />
            </div>

            <div className="flex flex-col">
              <label className="text-[0.75em] md:text-sm font-medium mb-1">Password</label>
              <input
                type="password"
                value={LoginPassword}
                className="border rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
                placeholder="Enter password"
                required
                onChange={(e)=>{setLoginPassword(e.target.value)}}
              />
            </div>

            <button
              type="submit"
              className="mt-4 bg-red-600 hover:font-bold  py-2 rounded-md hover:bg-red-700 text-white transition"
            >
              Log In
            </button>
          </form>
        )}

        {/* Sign Up Form */}
        {mode === "signup" && (
          <form className="flex flex-col gap-4" onSubmit={handleSignupSubmit}>
            <h2 className="text-[0.85em] md:text-2xl font-bold text-center mb-2">
              Create Account
            </h2>

            {/* First + Last Name */}
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <div className="flex flex-col">
                <label className="text-[0.75em] md:text-sm font-medium mb-1">First Name</label>
                <input
                  type="text"
                  name="Firstname"
                  className="border rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
                  placeholder="First name"
                  required
                />
              </div>

              <div className="flex flex-col ">
                <label className="text-[0.75em] md:text-sm font-medium mb-1">Last Name</label>
                <input
                  type="text"
                  name="Lastname"
                  className="border rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
                  placeholder="Last name"
                  required
                />
              </div>
            </div>

            {/* DOB */}
            <div className="flex flex-col">
              <label className="text-[0.75em] md:text-sm font-medium mb-1">Date of Birth</label>
              <input
                type="date"
                name="Dob"
                className="border rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
                required
              />
            </div>

            {/* Gender */}
            <div className="flex flex-col">
              <label className="text-[0.75em] md:text-sm font-medium mb-1">Gender</label>
              <select
               name="Gender"
                className="border rounded-md px-3 py-2 bg-white focus:outline-none focus:ring-2 focus:ring-blue-500"
                defaultValue=""
                required
              >
                <option value="" disabled>
                  Select gender
                </option>
                <option value="Male">Male</option>
                <option value="Female">Female</option>
                <option value="Other">Other</option>
              </select>
            </div>

            {/* Pincode */}
            <div className="flex flex-col">
                <label className="text-[0.75em] md:text-sm font-medium mb-1">Pincode</label>
                <input
                  type="text"
                  inputMode="numeric"
                  name="Pincode"
                  maxLength={6}
                  minLength={6}
                  pattern="\d{6}"
                  className="border rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
                  placeholder="Enter 6-digit pincode"
                  onChange={(e) => {
                    if (!/[0-9]/.test(e.key)) {
                      e.preventDefault();
                    }
                  }}
                  required
                />
            </div>


            {/* Mobile Number */}
            <div className="flex flex-col">
              <label className="text-[0.75em] md:text-sm font-medium mb-1">Mobile Number</label>
              <input
                type="tel"
                name="Mobile"
                pattern="[0-9]{10}"
                className="border rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
                placeholder="10-digit number"
                required
              />
            </div>

            {/* Password */}
            <div className="flex flex-col">
              <label className="text-[0.75em] md:text-sm font-medium mb-1">Password</label>
              <input
                value={Password}
                type="password"
                name="Password"
                className="border rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
                placeholder="Create password"
                required
                onChange={(e)=>{setPassword(e.target.value)}}
              />
            </div>

            {/* Re-enter password for confirmation */}
            <div className="flex flex-col">
              <label className="text-[0.75em] md:text-sm font-medium mb-1">Confirm Password</label>
              <input
                type="password"
                value={ConfirmPassword}
                name="ConfirmPasswird"
                className="border rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
                placeholder="Re-enter password to confirm"
                required
                onChange={(e)=>{setConfirmPassword(e.target.value)}}
              />
            </div>

            
            {showOtp && (
              <div className="OtpBox flex flex-col">
                <label className="text-[0.75em] md:text-sm font-medium mb-1">Enter OTP</label>
                <input
                  type="number"
                  name="Otp"
                  className="border rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
                  placeholder="Enter OTP sent to your number"
                  required
                />
              </div>
            )}

            <button
              type="submit"
              className="mt-4 bg-red-600 text-white py-2 rounded-md hover:bg-red-700  hover:font-bold transition"
            >
              {showOtp ? "Verify & Sign Up" : "Sign Up"}
            </button>
          </form>
        )}
      </div>
    </div>
  );
}

export default AuthPage;