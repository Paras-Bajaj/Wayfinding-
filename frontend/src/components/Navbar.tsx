import { Link, useNavigate, useLocation } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

const links = [
  { to: '/dashboard', label: 'Dashboard' },
  { to: '/route', label: 'Route' },
  { to: '/multi-stop', label: 'Multi-Stop' },
  { to: '/poi', label: 'POI' },
  { to: '/monitoring', label: 'Monitoring' },
  { to: '/admin', label: 'Admin' },
]

export default function Navbar() {
  const { username, logout } = useAuth()
  const nav = useNavigate()
  const loc = useLocation()

  return (
    <nav className="bg-white border-b border-slate-200">
      <div className="max-w-7xl mx-auto px-4 flex items-center justify-between h-14">
        <Link to="/dashboard" className="font-semibold text-brand-700 text-lg">
          🧭 Indoor Wayfinding
        </Link>
        <div className="flex items-center gap-1">
          {links.map((l) => (
            <Link
              key={l.to}
              to={l.to}
              className={`px-3 py-1.5 rounded-md text-sm ${
                loc.pathname === l.to
                  ? 'bg-brand-100 text-brand-700 font-medium'
                  : 'text-slate-600 hover:bg-slate-100'
              }`}
            >
              {l.label}
            </Link>
          ))}
        </div>
        <div className="flex items-center gap-3">
          <span className="text-sm text-slate-600">👤 {username}</span>
          <button
            onClick={() => {
              logout()
              nav('/login')
            }}
            className="text-sm px-3 py-1.5 rounded-md bg-slate-100 hover:bg-slate-200"
          >
            Logout
          </button>
        </div>
      </div>
    </nav>
  )
}