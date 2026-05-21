import { Link } from 'react-router-dom'
import Button from '../components/ui/Button'
import { Home } from 'lucide-react'

export default function NotFound() {
  return (
    <div className="min-h-screen flex items-center justify-center gradient-bg">
      <div className="text-center">
        <div className="text-8xl font-bold bg-gradient-to-br from-brand-500 to-brand-700 bg-clip-text text-transparent">
          404
        </div>
        <h1 className="mt-4 text-xl font-semibold text-slate-900 dark:text-slate-100">
          Page not found
        </h1>
        <p className="mt-1 text-sm text-slate-500">
          The route you tried to access was not found.
        </p>
        <Link to="/">
          <Button className="mt-6" leftIcon={<Home className="h-4 w-4" />}>
            Back home
          </Button>
        </Link>
      </div>
    </div>
  )
}
