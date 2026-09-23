import type { RouteResponse } from '../types'

// Node coordinates (must match DataSeeder in the backend)
const NODES: Record<number, { x: number; y: number; label: string; type: string }> = {
  1: { x: 0,  y: 0,  label: 'Reception',       type: 'ENTRANCE' },
  2: { x: 10, y: 0,  label: 'Corridor A',      type: 'CORRIDOR' },
  3: { x: 20, y: 0,  label: 'Corridor B',      type: 'CORRIDOR' },
  4: { x: 30, y: 0,  label: 'Meeting Room 4B', type: 'ROOM' },
  5: { x: 15, y: 5,  label: 'Stairs',          type: 'STAIRS' },
  6: { x: 15, y: -5, label: 'Lift',            type: 'LIFT' },
  7: { x: 22, y: 3,  label: 'Washroom',        type: 'ROOM' },
}

const EDGES: [number, number][] = [
  [1, 2], [2, 3], [3, 4], [2, 5], [2, 6], [3, 7],
]

const SCALE = 18
const PADDING = 60

function toSvg(x: number, y: number) {
  return { cx: PADDING + x * SCALE, cy: PADDING + y * SCALE }
}

export default function FloorMap({ route }: { route?: RouteResponse }) {
  const pathSet = new Set<string>()
  if (route?.path) {
    for (let i = 0; i < route.path.length - 1; i++) {
      const a = route.path[i], b = route.path[i + 1]
      pathSet.add(`${Math.min(a, b)}-${Math.max(a, b)}`)
    }
  }

  const width = PADDING * 2 + 30 * SCALE
  const height = PADDING * 2 + 10 * SCALE

  return (
    <div className="bg-white rounded-lg border border-slate-200 p-4">
      <h3 className="font-semibold text-sm mb-2 text-slate-700">Floor Map</h3>
      <svg viewBox={`0 0 ${width} ${height}`} className="w-full max-w-3xl">
        {EDGES.map(([a, b]) => {
          const A = toSvg(NODES[a].x, NODES[a].y)
          const B = toSvg(NODES[b].x, NODES[b].y)
          const key = `${Math.min(a, b)}-${Math.max(a, b)}`
          const onPath = pathSet.has(key)
          return (
            <line
              key={key}
              x1={A.cx} y1={A.cy} x2={B.cx} y2={B.cy}
              stroke={onPath ? '#2563eb' : '#cbd5e1'}
              strokeWidth={onPath ? 4 : 2}
              strokeLinecap="round"
            />
          )
        })}

        {Object.entries(NODES).map(([id, n]) => {
          const p = toSvg(n.x, n.y)
          const onPath = route?.path?.includes(Number(id))
          const isStart = route?.path?.[0] === Number(id)
          const isEnd = route?.path?.[route.path.length - 1] === Number(id)

          let fill = '#f1f5f9'
          if (isStart) fill = '#22c55e'
          else if (isEnd) fill = '#ef4444'
          else if (onPath) fill = '#3b82f6'

          return (
            <g key={id}>
              <circle cx={p.cx} cy={p.cy} r={10} fill={fill} stroke="#0f172a" strokeWidth="1.5" />
              <text
                x={p.cx} y={p.cy + 4}
                textAnchor="middle"
                fontSize="10"
                fill={onPath ? 'white' : '#0f172a'}
                fontWeight="600"
              >
                {id}
              </text>
              <text x={p.cx} y={p.cy + 24} textAnchor="middle" fontSize="9" fill="#475569">
                {n.label}
              </text>
            </g>
          )
        })}
      </svg>
    </div>
  )
}