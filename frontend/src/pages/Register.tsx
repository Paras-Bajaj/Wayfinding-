import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

export default function Register() {
  const [username, setUsername] = useState('')
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)
  const { register } = useAuth()
  const nav = useNavigate()

  const submit = async (e: React.FormEvent) => {
    e.preventDefault()
    setError('')
    setLoading(true)
    try {
      await register(username, email, password)
      nav('/dashboard')
    } catch (err: any) {
      setError(err.response?.data?.message || 'Registration failed')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="min-h-screen flex items-center justify-center bg-slate-50 p-4">
      <form
        onSubmit={submit}
        className="bg-white p-8 rounded-xl shadow-sm border border-slate-200 w-full max-w-sm"
      >
        <h1 className="text-2xl font-semibold mb-1 text-brand-700">Create Account</h1>
        <p className="text-sm text-slate-500 mb-6">Start navigating your campus</p>

        {error && (
          <div className="mb-4 p-2 bg-red-50 text-red-700 text-sm rounded border border-red-200">
            {error}
          </div>
        )}

        <label className="block text-sm mb-1 text-slate-700">Username</label>
        <input
          className="w-full mb-4 px-3 py-2 border border-slate-300 rounded-md focus:ring-2 focus:ring-brand-500 outline-none"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
          required
        />

        <label className="block text-sm mb-1 text-slate-700">Email</label>
        <input
          type="email"
          className="w-full mb-4 px-3 py-2 border border-slate-300 rounded-md focus:ring-2 focus:ring-brand-500 outline-none"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          required
        />

        <label className="block text-sm mb-1 text-slate-700">Password</label>
        <input
          type="password"
          className="w-full mb-6 px-3 py-2 border border-slate-300 rounded-md focus:ring-2 focus:ring-brand-500 outline-none"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          minLength={6}
          required
        />

        <button
          disabled={loading}
          className="w-full bg-brand-600 hover:bg-brand-700 disabled:opacity-60 text-white py-2 rounded-md font-medium"
        >
          {loading ? 'Creating...' : 'Create account'}
        </button>

        <p className="text-sm text-slate-600 text-center mt-4">
          Have an account?{' '}
          <Link to="/login" className="text-brand-600 hover:underline">
            Sign in
          </Link>
        </p>
      </form>
    </div>
  )
}