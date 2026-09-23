import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { AuthProvider } from './context/AuthContext'
import ProtectedRoute from './components/ProtectedRoute'
import Navbar from './components/Navbar'
import Login from './pages/Login'
import Register from './pages/Register'
import Dashboard from './pages/Dashboard'
import RoutePage from './pages/RoutePage'
import MultiStopPage from './pages/MultiStopPage'
import PoiPage from './pages/PoiPage'
import MonitoringPage from './pages/MonitoringPage'
import AdminPage from './pages/AdminPage'

function ProtectedLayout({ children }: { children: React.ReactNode }) {
  return (
    <ProtectedRoute>
      <Navbar />
      <main>{children}</main>
    </ProtectedRoute>
  )
}

export default function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route path="/dashboard" element={<ProtectedLayout><Dashboard /></ProtectedLayout>} />
          <Route path="/route" element={<ProtectedLayout><RoutePage /></ProtectedLayout>} />
          <Route path="/multi-stop" element={<ProtectedLayout><MultiStopPage /></ProtectedLayout>} />
          <Route path="/poi" element={<ProtectedLayout><PoiPage /></ProtectedLayout>} />
          <Route path="/monitoring" element={<ProtectedLayout><MonitoringPage /></ProtectedLayout>} />
          <Route path="/admin" element={<ProtectedLayout><AdminPage /></ProtectedLayout>} />
          <Route path="/" element={<Navigate to="/dashboard" replace />} />
          <Route path="*" element={<Navigate to="/dashboard" replace />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  )
}