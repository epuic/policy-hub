import { Outlet } from 'react-router-dom'
import Sidebar from '../components/layout/Sidebar'
import Topbar from '../components/layout/Topbar'

export default function AppLayout({ sidebarItems, title, subtitle }) {
  return (
    <div className="flex min-h-screen">
      <Sidebar items={sidebarItems} title="Insurance" subtitle={subtitle} />
      <div className="flex-1 flex flex-col min-w-0">
        <Topbar title={title} />
        <main className="flex-1 p-5 md:p-6 overflow-x-hidden">
          <div className="max-w-[1400px] mx-auto w-full animate-fade-in">
            <Outlet />
          </div>
        </main>
      </div>
    </div>
  )
}
