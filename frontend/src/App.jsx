import { Routes, Route, Navigate } from 'react-router-dom'
import {
  LayoutDashboard,
  Users,
  ScrollText,
  Users2,
  Coins,
  Percent,
  ShieldAlert,
  FileBarChart2,
  BrainCircuit,
} from 'lucide-react'

import ProtectedRoute from './components/ProtectedRoute'
import AppLayout from './layouts/AppLayout'

import Login from './pages/Login'
import NotFound from './pages/NotFound'

import BrokerDashboard from './pages/broker/BrokerDashboard'
import ClientsList from './pages/broker/ClientsList'
import ClientForm from './pages/broker/ClientForm'
import ClientDetail from './pages/broker/ClientDetail'
import BuildingForm from './pages/broker/BuildingForm'
import BuildingDetail from './pages/broker/BuildingDetail'
import PoliciesList from './pages/broker/PoliciesList'
import PolicyForm from './pages/broker/PolicyForm'
import PolicyDetail from './pages/broker/PolicyDetail'

import AdminDashboard from './pages/admin/AdminDashboard'
import BrokersList from './pages/admin/BrokersList'
import BrokerForm from './pages/admin/BrokerForm'
import Currencies from './pages/admin/Currencies'
import FeeConfigurations from './pages/admin/FeeConfigurations'
import RiskFactorConfigurations from './pages/admin/RiskFactorConfigurations'
import Reports from './pages/admin/Reports'
import AiInsights from './pages/admin/AiInsights'

import { useAuth } from './contexts/AuthContext'

const BROKER_NAV = [
  { to: '/broker', label: 'Dashboard', icon: LayoutDashboard, end: true },
  { to: '/broker/clients', label: 'Clients', icon: Users },
  { to: '/broker/policies', label: 'Policies', icon: ScrollText },
]

const ADMIN_NAV = [
  { to: '/admin', label: 'Admin Dashboard', icon: LayoutDashboard, end: true },
  { to: '/admin/brokers', label: 'Brokers', icon: Users2 },
  { to: '/admin/currencies', label: 'Currencies', icon: Coins },
  { to: '/admin/fees', label: 'Fees', icon: Percent },
  { to: '/admin/risk-factors', label: 'Risk Adjustments', icon: ShieldAlert },
  { to: '/admin/reports', label: 'Reports', icon: FileBarChart2 },
  { to: '/admin/ai', label: 'AI Insights', icon: BrainCircuit },
]

function RootRedirect() {
  const { user, ready } = useAuth()
  if (!ready) return null
  if (!user) return <Navigate to="/login" replace />
  return <Navigate to={user.role === 'ADMIN' ? '/admin' : '/broker'} replace />
}

export default function App() {
  return (
    <Routes>
      <Route path="/" element={<RootRedirect />} />
      <Route path="/login" element={<Login />} />

      <Route
        element={
          <ProtectedRoute role="BROKER">
            <AppLayout sidebarItems={BROKER_NAV} subtitle="Broker" />
          </ProtectedRoute>
        }
      >
        <Route path="/broker" element={<BrokerDashboard />} />
        <Route path="/broker/clients" element={<ClientsList />} />
        <Route path="/broker/clients/new" element={<ClientForm />} />
        <Route path="/broker/clients/:id" element={<ClientDetail />} />
        <Route path="/broker/clients/:id/edit" element={<ClientForm />} />
        <Route
          path="/broker/clients/:clientId/buildings/new"
          element={<BuildingForm />}
        />
        <Route path="/broker/buildings/:buildingId" element={<BuildingDetail />} />
        <Route path="/broker/buildings/:buildingId/edit" element={<BuildingForm />} />
        <Route path="/broker/policies" element={<PoliciesList />} />
        <Route path="/broker/policies/new" element={<PolicyForm />} />
        <Route path="/broker/policies/:id" element={<PolicyDetail />} />
      </Route>

      <Route
        element={
          <ProtectedRoute role="ADMIN">
            <AppLayout sidebarItems={ADMIN_NAV} subtitle="Admin" />
          </ProtectedRoute>
        }
      >
        <Route path="/admin" element={<AdminDashboard />} />
        <Route path="/admin/brokers" element={<BrokersList />} />
        <Route path="/admin/brokers/new" element={<BrokerForm />} />
        <Route path="/admin/brokers/:id/edit" element={<BrokerForm />} />
        <Route path="/admin/currencies" element={<Currencies />} />
        <Route path="/admin/fees" element={<FeeConfigurations />} />
        <Route path="/admin/risk-factors" element={<RiskFactorConfigurations />} />
        <Route path="/admin/reports" element={<Reports />} />
        <Route path="/admin/ai" element={<AiInsights />} />
      </Route>

      <Route path="*" element={<NotFound />} />
    </Routes>
  )
}
