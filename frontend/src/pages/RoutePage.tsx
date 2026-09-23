import { useState } from 'react'
import { computeRoute } from '../api/client'
import type { RouteResponse } from '../types'
import FloorMap from '../components/FloorMap'

const NODE_OPTIONS = [
  { id: 1, label: '1 — Reception' },
  { id: 2, label: '2 — Corridor A' },
  { id: 3, label: '3 — Corridor B' },
  { id: 4, label: '4 — Meeting Room 4B' },
  { id: 5, label: '5 — Stairs' },
  { id: 6, label: '6 — Lift' },
  { id: 7, label: '7 — Washroom' },
]

export default function RoutePage() {
  const [startId, setStartId] = useState(1)
  const [endId, setEndId] = useState(4)
  const [wheelchair, setWheelchair] = useState(false)
  const [peakHours, setPeakHours] = useState(false)
  const [loading, setLoading] = useState(false)
  const [result, setResult] = useState<RouteResponse | null>(null)
  const [error, setError] = useState('')

  const submit = async (e: React.FormEvent) => {
    e.preventDefault()
    setError('')
    setResult(null)
    setLoading(true)
    try {
      const res = await computeRoute({ startId, endId, wheelchair, peakHours })
      setResult(res)
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to compute route')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="max-w-6xl mx-auto p-6">
      <h1 className="text-2xl font-semibold mb-6">Find a Route</h1>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <form onSubmit={submit} className="bg-white p-5 rounded-xl border border-slate-200 h-fit">
          <label className="block text-sm mb-1 text-slate-700">Start</label>
          <select
            value={startId}
            onChange={(e) => setStartId(Number(e.target.value))}
            className="w-full mb-4 px-3 py-2 border border-slate-300 rounded-md"
          >
            {NODE_OPTIONS.map((n) => (
              <option key={n.id} value={n.id}>{n.label}</option>
            ))}
          </select>

          <label className="block text-sm mb-1 text-slate-700">Destination</label>
          <select
            value={endId}
            onChange={(e) => setEndId(Number(e.target.value))}
            className="w-full mb-4 px-3 py-2 border border-slate-300 rounded-md"
          >
            {NODE_OPTIONS.map((n) => (
              <option key={n.id} value={n.id}>{n.label}</option>
            ))}
          </select>

          <label className="flex items-center gap-2 mb-2">
            <input
              type="checkbox"
              checked={wheelchair}
              onChange={(e) => setWheelchair(e.target.checked)}
            />
            <span className="text-sm">Wheelchair accessible</span>
          </label>

          <label className="flex items-center gap-2 mb-4">
            <input
              type="checkbox"
              checked={peakHours}
              onChange={(e) => setPeakHours(e.target.checked)}
            />
            <span className="text-sm">Avoid peak-hour congestion</span>
          </label>

          <button
            disabled={loading}
            className="w-full bg-brand-600 hover:bg-brand-700 text-white py-2 rounded-md font-medium disabled:opacity-60"
          >
            {loading ? 'Computing...' : 'Get Route'}
          </button>
        </form>

        <div className="lg:col-span-2 space-y-4">
          {error && (
            <div className="bg-red-50 border border-red-200 text-red-700 p-3 rounded-md text-sm">
              {error}
            </div>
          )}

          <FloorMap route={result || undefined} />

          {result && (
            <div className="bg-white p-5 rounded-xl border border-slate-200">
              <div className="flex justify-between items-center mb-4">
                <h2 className="font-semibold">Turn-by-turn Directions</h2>
                <div className="text-sm text-slate-600">
                  Total:{' '}
                  <span className="font-semibold text-brand-700">{result.distance} m</span>
                  {result.cached && (
                    <span className="ml-2 text-xs bg-slate-100 px-2 py-0.5 rounded">cached</span>
                  )}
                </div>
              </div>

              <ol className="space-y-2">
                {result.directions.map((step) => (
                  <li key={step.step} className="flex items-start gap-3">
                    <span className="flex-shrink-0 w-6 h-6 rounded-full bg-brand-100 text-brand-700 text-xs font-semibold flex items-center justify-center">
                      {step.step}
                    </span>
                    <div>
                      <div className="text-sm">{step.instruction}</div>
                      <div className="text-xs text-slate-500">{step.type}</div>
                    </div>
                  </li>
                ))}
              </ol>
            </div>
          )}
        </div>
      </div>
    </div>
  )
}