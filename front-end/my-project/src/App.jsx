import { useState } from 'react'
import './App.css'
import Navbar from './NavBar'
import AuthPage from './AuthPage'
import Account from './Account'
import DashBoard from './DashBoard'
import Home from './Home'
import { Routes, Route } from 'react-router'


function App() {
  const [count, setCount] = useState(0)

  return (
      <div className="contentbox bg-red-400 flex flex-col min-h-screen">
        <Navbar/>
        <div className=' flex-1 bg-[#0d1117] text-[#f0f6fc]'>
          <Routes>
            <Route path='/' element={<Home/>}/>
            <Route path='/AuthPage' element={<AuthPage/>}/>
            <Route path='/Account' element={<Account/>}/>
            <Route path='/DashBoard' element={<DashBoard/>}/>

          </Routes>
        </div>
      </div>
  )
}

export default App
