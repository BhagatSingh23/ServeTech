import React from "react";

function DashBoard() {

  const bookings = [
    {
      id: 1,
      employer: "Rahul Sharma",
      role: "Painter",
      days: 3,
      amount: 4500,
      status: "Received",
      location: "Delhi",
      email: "rahulsharma@example.com",
      date: "2024-06-15"
    },
    {
      id: 2,
      employer: "Anita Verma",
      role: "Carpenter",
      days: 2,
      amount: 3000,
      status: "Pending",
      location: "Mumbai",
      email: "anitaverma@example.com",
      date: "2024-06-20"
    },
    {
      id: 3,
      employer: "Suresh Yadav",
      role: "Labour",
      days: 5,
      amount: 5000,
      status: "Received",
      location: "Bangalore",
      email: "sureshyadav@example.com",
      date: "2024-06-10"
    }
  ];

  const totalEarnings = bookings
    .filter(booking => booking.status === "Received")
    .reduce((total, booking) => total + booking.amount, 0);

  return (
    <div className="p-8 bg-[#0d1117] min-h-screen text-white">

      {/* Earnings Card */}
      <div className="relative bg-[#161b22] border border-[#30363d] 
                      p-8 rounded-2xl mb-10 shadow-lg 
                      hover:shadow-2xl transition duration-300">

        <h1 className="text-lg text-gray-400 mb-2 tracking-wide">
          Total Earnings
        </h1>

        <p className="text-4xl font-bold text-green-400">
          ₹ {totalEarnings}
        </p>
      </div>


      {/* Section Title */}
      <h2 className="text-2xl font-semibold mb-6 tracking-wide">
        Previous Bookings
      </h2>


      {/* Booking Cards */}
      <div className="flex flex-col gap-6">

        {bookings.map((booking) => (
          <div
            key={booking.id}
            className="group bg-[#161b22] border border-[#30363d] 
                       rounded-2xl p-6 
                       flex flex-col lg:flex-row justify-between 
                       transition duration-300
                       hover:border-amber-500/50 hover:shadow-xl
                       hover:-translate-y-1"
          >

            {/* Left Info */}
            <div className="grid grid-cols-2 gap-x-10 gap-y-3 text-sm md:text-base">

              <div>
                <p className="text-gray-400">Employer</p>
                <p className="font-semibold">{booking.employer}</p>
              </div>

              <div>
                <p className="text-gray-400">Role</p>
                <p className="font-semibold">{booking.role}</p>
              </div>

              <div>
                <p className="text-gray-400">Days</p>
                <p className="font-semibold">{booking.days}</p>
              </div>

              <div>
                <p className="text-gray-400">Amount</p>
                <p className="font-semibold text-green-400">
                  ₹ {booking.amount}
                </p>
              </div>

              <div>
                <p className="text-gray-400">Location</p>
                <p className="font-semibold">{booking.location}</p>
              </div>

              <div>
                <p className="text-gray-400">Payment Status</p>
                <span
                  className={`px-3 py-1 text-xs rounded-full font-medium inline-block
                  ${booking.status === "Received"
                      ? "bg-green-500/20 text-green-400 border border-green-500/40"
                      : "bg-red-500/20 text-red-400 border border-red-500/40"
                    }`}
                >
                  {booking.status}
                </span>
              </div>

              <div>
                <p className="text-gray-400">Date [start]</p>
                <p className="font-semibold">{booking.date}</p>
              </div>

            </div>

            {/* Raise Complaint Button */}
            <div className="mt-6 lg:mt-0 flex items-center">
              <p
              onClick={() => alert(`Issue raised by ${booking.employer}`)}
                className="bg-transparent border border-red-600 text-red-500 
                           px-5 py-2.5 rounded-xl 
                           font-medium tracking-wide
                           transition duration-300
                           hover:bg-red-600 
                           hover:text-white
                           hover:shadow-lg"
              >
                Raise Complaint
              </p>
            </div>

          </div>
        ))}

      </div>

    </div>
  );
}

export default DashBoard;