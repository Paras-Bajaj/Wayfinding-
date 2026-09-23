import { createContext, useContext, useState, useEffect } from 'react'
import type { ReactNode } from 'react'
import { loginUser, registerUser } from '../api/client'
import type { AuthResponse } from '../types'

interface AuthContextType {
  token: string | null
  username: string | null
  login: (username: string, password: string) => Promise<void>
  register: (username: string, email: string, password: string) => Promise<void>
  logout: () => void
  isAuthenticated: boolean
}

const AuthContext = createContext<AuthContextType | undefined>(undefined)

export function AuthProvider({ children }: { children: ReactNode }) {
  const [token, setToken] = useState<string | null>(null)
  const [username, setUsername] = useState<string | null>(null)

  useEffect(() => {
    const t = localStorage.getItem('token')
    const u = localStorage.getItem('username')
    if (t) setToken(t)
    if (u) setUsername(u)
  }, [])

  const store = (res: AuthResponse) => {
    localStorage.setItem('token', res.token)
    localStorage.setItem('username', res.username)
    setToken(res.token)
    setUsername(res.username)
  }

  const login = async (u: string, p: string) => {
    const res = await loginUser({ username: u, password: p })
    store(res)
  }

  const register = async (u: string, e: string, p: string) => {
    const res = await registerUser({ username: u, email: e, password: p })
    store(res)
  }

  const logout = () => {
    localStorage.removeItem('token')
    localStorage.removeItem('username')
    setToken(null)
    setUsername(null)
  }

  return (
    <AuthContext.Provider
      value={{ token, username, login, register, logout, isAuthenticated: !!token }}
    >
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  const ctx = useContext(AuthContext)
  if (!ctx) throw new Error('useAuth must be used inside AuthProvider')
  return ctx
}