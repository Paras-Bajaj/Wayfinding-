export interface AuthResponse {
  token: string
  tokenType: string
  username: string
  expiresIn: number
}

export interface DirectionStep {
  step: number
  nodeId: number
  name: string
  type: string
  instruction: string
}

export interface RouteResponse {
  status: string
  path: number[]
  distance: number
  directions: DirectionStep[]
  wheelchairAccessible: boolean
  cached: boolean
}

export interface PoiResponse {
  poiId: number
  nodeId: number
  name: string
  type: string
  distance: number
  path: number[]
}

export interface CacheStats {
  hitCount: number
  missCount: number
  hitRate: number
  evictionCount: number
  size: number
}

export interface ApiError {
  status: string
  errorType: string
  message: string
  timestamp: string
}