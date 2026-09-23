import { useState } from 'react'
import { reloadGraph, invalidateCache } from '../api/client'

export default function AdminPage() {
  const [msg, setMsg] = useState('')
  const [error, setError] = useState('')

  const run = async (label: string, fn: () => Promise<any>) => {
    setMsg('')
    setError('')
    try {
      const res = await fn()
      setMsg(`${label}: ${res.message || 'OK'}`)
    } catch (err: any) {
      setError(err.response?.data?.message || `${label} failed`)
    }
  }

  return (
    <div className="max-w-2xl mx-auto p-6">
      <h1 className="text-2xl font-semibold mb-6">Admin</h1>

      {msg && (
        <div className="mb-4 bg-green-50 border border-green-200 text-green-700 p-3 rounded-md text-sm">
          {msg}
        </div>
      )}

      {error && (
        <div className="mb-4 bg-red-50 border border-red-200 text-red-700 p-3 rounded-md text-sm">
          {error}
        </div>
      )}

      <div className="space-y-4">
        <div className="bg-white p-5 rounded-xl border border-slate-200">
          <h2 className="font-semibold mb-1">Reload Graph</h2>
          <p className="text-sm text-slate-500 mb-3">
            Rebuilds the in-memory graph from the database.
          </p>
          <button
            onClick={() => run('Reload', reloadGraph)}
            className="bg-brand-600 hover:bg-brand-700 text-white px-4 py-2 rounded-md text-sm"
          >
            Reload Graph
          </button>
        </div>

        <div className="bg-white p-5 rounded-xl border border-slate-200">
          <h2 className="font-semibold mb-1">Invalidate Cache</h2>
          <p className="text-sm text-slate-500 mb-3">
            Clears the route LRU cache. Use after layout changes.
          </p>
          <button
            onClick={() => run('Invalidate', invalidateCache)}
            className="bg-slate-800 hover:bg-slate-900 text-white px-4 py-2 rounded-md text-sm"
          >
            Invalidate Cache
          </button>
        </div>
      </div>
    </div>
  )
}