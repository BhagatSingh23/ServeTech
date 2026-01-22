import { useState } from "react";

function AuthPage() {
  const [mode, setMode] = useState("login"); 

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
            onClick={() => setMode("login")}
          >
            Log In
          </button>

          <button
            className={`w-1/2 py-1 md:py-2 font-medium ${
              mode === "signup"
                ? "bg-red-600 text-white"
                : "bg-gray-100 text-gray-700"
            }`}
            onClick={() => setMode("signup")}
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
                className="border rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
                placeholder="Enter username or Phone Number"
                required
              />
            </div>

            <div className="flex flex-col">
              <label className="text-[0.75em] md:text-sm font-medium mb-1">Password</label>
              <input
                type="password"
                className="border rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
                placeholder="Enter password"
                required
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
          <form className="flex flex-col gap-4">
            <h2 className="text-[0.85em] md:text-2xl font-bold text-center mb-2">
              Create Account
            </h2>

            {/* First + Last Name */}
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <div className="flex flex-col">
                <label className="text-[0.75em] md:text-sm font-medium mb-1">First Name</label>
                <input
                  type="text"
                  className="border rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
                  placeholder="First name"
                  required
                />
              </div>

              <div className="flex flex-col ">
                <label className="text-[0.75em] md:text-sm font-medium mb-1">Last Name</label>
                <input
                  type="text"
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
                className="border rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
                required
              />
            </div>

            {/* Gender */}
            <div className="flex flex-col">
              <label className="text-[0.75em] md:text-sm font-medium mb-1">Gender</label>
              <select
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

            {/* City */}
            <div className="flex flex-col">
              <label className="text-[0.75em] md:text-sm font-medium mb-1">City</label>
              <input
                type="text"
                className="border rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
                placeholder="Enter city"
                required
              />
            </div>

            {/* Mobile Number */}
            <div className="flex flex-col">
              <label className="text-[0.75em] md:text-sm font-medium mb-1">Mobile Number</label>
              <input
                type="tel"
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
                type="password"
                className="border rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
                placeholder="Create password"
                required
              />
            </div>

            {/* Re-enter password for confirmation */}
            <div className="flex flex-col">
              <label className="text-[0.75em] md:text-sm font-medium mb-1">Confirm Password</label>
              <input
                type="password"
                className="border rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
                placeholder="Re-enter password to confirm"
                required
              />
            </div>

            {/* Enter OTP */}
            <div className="flex flex-col">
              <label className="text-[0.75em] md:text-sm font-medium mb-1">Enter OTP</label>
              <input
                type="number"
                className="border rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
                placeholder="Enter OTP sent to your number"
                required
              />
            </div>

            <button
              type="submit"
              className="mt-4 bg-red-600 text-white py-2 rounded-md hover:bg-red-700  hover:font-bold transition"
            >
              Sign Up
            </button>
          </form>
        )}
      </div>
    </div>
  );
}

export default AuthPage;
