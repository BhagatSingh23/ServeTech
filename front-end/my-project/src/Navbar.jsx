import { Link } from 'react-router-dom'

export default function Navbar()
{
    const parentstyles='parentNavBox flex justify-between h-10 w-full bg-[#010409] items-center px-2';

    return(
        <div className={parentstyles}>

            <div className="leftsection flex text-white">
                <Link className='text-red-600 font-bold text-[0.25em] md:text-[1.55em]' to='/'>ServeTech</Link>
            </div>

            <div className="rightsection flex text-white md:gap-4">
                <Link className='hover:bg-[#0d1117] text-[#f0f6fc] px-1 rounded-sm text-[0.000009em] md:text-[1em] text-nowrap' to='/'>Home</Link>
                <Link className='hover:bg-[#0d1117] text-[#f0f6fc] px-1 rounded-sm text-[0.000009em] md:text-[1em] text-nowrap' to='/Dashboard' >Dashboard</Link>
                <Link className='hover:bg-[#0d1117] text-[#f0f6fc] px-1 rounded-sm text-[0.000009em] md:text-[1em] text-nowrap' to='/Account' >My Account</Link>
                <Link className='hover:bg-[#0d1117] text-[#f0f6fc] px-1 rounded-sm text-[0.000009em] md:text-[1em] text-nowrap' to='/AuthPage' >Sign Up</Link>
            </div>

        </div>
    )
}