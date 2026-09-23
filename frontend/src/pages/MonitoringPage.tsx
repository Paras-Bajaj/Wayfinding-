import { useEffect, useState } from 'react'
import { getCacheStats } from '../api/client'
import type { CacheStats } from '../types'

export default function MonitoringPage() {
  const [stats, setStats] = useState<CacheStats | null>(null)
  const [error, setError] = useState('')
  const [lastUpdated, setLastUpdated] = useState<Date | null>(null)

  const load = async () => {
    try {
      const data = await getCacheStats()
      setStats(data)
      setLastUpdated(new Date())
      setError('')
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to load stats')
    }
  }

  useEffect(() => {
    load()
    const id = setInterval(load, 5000)
    return () => clearInterval(id)
  }, [])

  const cards = stats
    ? [
        { label: 'Cache Hits',    value: stats.hitCount },
        { label: 'Cache Misses',  value: stats.missCount },
        { label: 'Hit Rate',      value: `${(stats.hitRate * 100).toFixed(1)}%` },
        { label: 'Entries',       value: stats.size },
        { label: 'Evictions',     value: stats.evictionCount },
      ]
    : []

  return (
    <div className="max-w-5xl mx-auto p-6">
      <div className="flex items-center justify-between mb-6">
        <h1 className="text-2xl font-semibold">Monitoring</h1>
        <button
          onClick={load}
          className="text-sm px-3 py-1.5 bg-slate-100 hover:bg-slate-200 rounded-md"
        >
          Refresh
        </button>
      </div>

      {error && (
        <div className="bg-red-50 border border-red-200 text-red-700 p-3 rounded-md text-sm mb-4">
          {error}
        </div>
      )}

      <div className="grid grid-cols-2 md:grid-cols-5 gap-4 mb-6">
        {cards.map((c) => (
          <div key={c.label} className="bg-white p-4 rounded-xl border border-slate-200">
            <div className="text-xs text-slate-500">{c.label}</div>
            <div className="text-2xl font-semibold text-brand-700">{c.value}</div>
          </div>
        ))}
      </div>

      <div className="bg-white p-5 rounded-xl border border-slate-200">
        <h2 className="font-semibold mb-2">Raw Response</h2>
        <pre className="text-xs bg-slate-50 p-3 rounded overflow-auto">
          {JSON.stringify(stats, null, 2)}
        </pre>
        {lastUpdated && (
          <div className="text-xs text-slate-500 mt-2">
            Last updated: {lastUpdated.toLocaleTimeString()}
          </div>
        )}
      </div>
    </div>
  )
}