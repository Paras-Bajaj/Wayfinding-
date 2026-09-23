import { useState } from 'react'
import { nearestPoi } from '../api/client'
import type { PoiResponse } from '../types'

const NODE_OPTIONS = [
  { id: 1, label: 'Reception' },
  { id: 2, label: 'Corridor A' },
  { id: 3, label: 'Corridor B' },
  { id: 4, label: 'Meeting Room 4B' },
  { id: 5, label: 'Stairs' },
  { id: 6, label: 'Lift' },
  { id: 7, label: 'Washroom' },
]

const POI_TYPES = ['WASHROOM', 'WATER', 'EXIT', 'COFFEE']

export default function PoiPage() {
  const [fromNode, setFromNode] = useState(1)
  const [type, setType] = useState('WASHROOM')
  const [wheelchair, setWheelchair] = useState(false)
  const [result, setResult] = useState<PoiResponse | null>(null)
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  const submit = async (e: React.FormEvent) => {
    e.preventDefault()
    setError('')
    setResult(null)
    setLoading(true)
    try {
      const res = await nearestPoi(fromNode, type, wheelchair)
      setResult(res)
    } catch (err: any) {
      setError(err.response?.data?.message || 'No POI found')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="max-w-3xl mx-auto p-6">
      <h1 className="text-2xl font-semibold mb-6">Points of Interest</h1>

      <form onSubmit={submit} className="bg-white p-5 rounded-xl border border-slate-200 mb-6">
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <div>
            <label className="block text-sm mb-1">From</label>
            <select
              value={fromNode}
              onChange={(e) => setFromNode(Number(e.target.value))}
              className="w-full px-3 py-2 border border-slate-300 rounded-md"
            >
              {NODE_OPTIONS.map((n) => (
                <option key={n.id} value={n.id}>{n.label}</option>
              ))}
            </select>
          </div>

          <div>
            <label className="block text-sm mb-1">Type</label>
            <select
              value={type}
              onChange={(e) => setType(e.target.value)}
              className="w-full px-3 py-2 border border-slate-300 rounded-md"
            >
              {POI_TYPES.map((t) => (
                <option key={t} value={t}>{t}</option>
              ))}
            </select>
          </div>

          <div className="flex items-end">
            <label className="flex items-center gap-2">
              <input
                type="checkbox"
                checked={wheelchair}
                onChange={(e) => setWheelchair(e.target.checked)}
              />
              <span className="text-sm">Wheelchair</span>
            </label>
          </div>
        </div>

        <button
          disabled={loading}
          className="mt-4 bg-brand-600 hover:bg-brand-700 text-white px-6 py-2 rounded-md font-medium disabled:opacity-60"
        >
          {loading ? 'Searching...' : 'Find nearest'}
        </button>
      </form>

      {error && (
        <div className="bg-red-50 border border-red-200 text-red-700 p-3 rounded-md text-sm mb-4">
          {error}
        </div>
      )}

      {result && (
        <div className="bg-white p-5 rounded-xl border border-slate-200">
          <h2 className="font-semibold text-lg">{result.name}</h2>
          <div className="text-sm text-slate-600 mb-3">
            Type: {result.type} · Distance: <span className="font-semibold text-brand-700">{result.distance} m</span>
          </div>
          <div className="text-xs text-slate-500">
            Path: {result.path.join(' → ')}
          </div>
        </div>
      )}
    </div>
  )
}