import { Link } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

const cards = [
  { to: '/route',      title: 'Find a Route',       desc: 'Shortest path between two points',  icon: '🧭' },
  { to: '/multi-stop', title: 'Multi-Stop',         desc: 'Visit several stops in best order', icon: '📍' },
  { to: '/poi',        title: 'Points of Interest', desc: 'Nearest washroom, exit, coffee',    icon: '🚻' },
  { to: '/monitoring', title: 'Monitoring',         desc: 'Cache hit rate & metrics',          icon: '📊' },
  { to: '/admin',      title: 'Admin',              desc: 'Reload graph, invalidate cache',    icon: '⚙️' },
]

export default function Dashboard() {
  const { username } = useAuth()

  return (
    <div className="max-w-5xl mx-auto p-6">
      <h1 className="text-2xl font-semibold mb-1">Welcome back, {username} 👋</h1>
      <p className="text-slate-600 mb-8">What would you like to do?</p>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        {cards.map((c) => (
          <Link
            key={c.to}
            to={c.to}
            className="bg-white p-5 rounded-xl border border-slate-200 hover:border-brand-500 hover:shadow-sm transition"
          >
            <div className="text-3xl mb-2">{c.icon}</div>
            <div className="font-semibold">{c.title}</div>
            <div className="text-sm text-slate-500">{c.desc}</div>
          </Link>
        ))}
      </div>
    </div>
  )
}