import { useEffect, useState } from 'react'
import { Link, useNavigate, useParams } from 'react-router-dom'
import {
  ArrowLeft,
  Pencil,
  Plus,
  Building2,
  Mail,
  Phone,
  MapPin,
  Hash,
  Globe,
} from 'lucide-react'
import { Card, CardBody, CardHeader } from '../../components/ui/Card'
import Button from '../../components/ui/Button'
import Badge from '../../components/ui/Badge'
import Spinner from '../../components/ui/Spinner'
import EmptyState from '../../components/ui/EmptyState'
import { Table, THead, TH, TBody, TR, TD } from '../../components/ui/Table'
import { clientsApi } from '../../api/clients'
import { useToast } from '../../contexts/ToastContext'
import { apiError } from '../../lib/api'
import { formatMoney, formatNumber } from '../../lib/utils'
import { labelFor, CLIENT_TYPES, BUILDING_TYPES } from '../../utils/constants'

export default function ClientDetail() {
  const { id } = useParams()
  const navigate = useNavigate()
  const toast = useToast()
  const [client, setClient] = useState(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    setLoading(true)
    clientsApi
      .get(id, { page: 0, size: 20 })
      .then(setClient)
      .catch((err) => toast.error(apiError(err)))
      .finally(() => setLoading(false))
  }, [id])

  if (loading) return <Spinner label="Se încarcă clientul..." />
  if (!client) return null

  const buildings = client.buildings?.content || []

  const InfoRow = ({ icon: Icon, label, value }) => (
    <div className="flex items-start gap-3">
      <div className="rounded-lg bg-slate-100 dark:bg-slate-800 p-2 text-slate-500">
        <Icon className="h-4 w-4" />
      </div>
      <div className="min-w-0 flex-1">
        <div className="text-[11px] uppercase tracking-wider text-slate-400">
          {label}
        </div>
        <div className="text-sm text-slate-800 dark:text-slate-200 truncate">
          {value || '—'}
        </div>
      </div>
    </div>
  )

  return (
    <div className="space-y-5">
      <div className="flex items-center justify-between gap-3">
        <Button
          variant="ghost"
          size="sm"
          leftIcon={<ArrowLeft className="h-4 w-4" />}
          onClick={() => navigate('/broker/clients')}
        >
          Clienți
        </Button>
        <div className="flex gap-2">
          <Button
            variant="outline"
            leftIcon={<Pencil className="h-4 w-4" />}
            onClick={() => navigate(`/broker/clients/${id}/edit`)}
          >
            Editează
          </Button>
          <Button
            leftIcon={<Plus className="h-4 w-4" />}
            onClick={() => navigate(`/broker/clients/${id}/buildings/new`)}
          >
            Clădire nouă
          </Button>
        </div>
      </div>

      <Card>
        <CardBody>
          <div className="flex items-start justify-between gap-4">
            <div>
              <h2 className="text-2xl font-bold text-slate-900 dark:text-slate-100">
                {client.name}
              </h2>
              <div className="flex items-center gap-2 mt-2">
                <Badge status={client.type}>
                  {labelFor(CLIENT_TYPES, client.type)}
                </Badge>
                <span className="text-xs text-slate-500">
                  {client.countryCode}
                </span>
              </div>
            </div>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4 mt-6">
            <InfoRow
              icon={Hash}
              label={client.type === 'COMPANY' ? 'CUI' : 'CNP'}
              value={client.identificationNumber}
            />
            <InfoRow icon={Mail} label="Email" value={client.email} />
            <InfoRow icon={Phone} label="Telefon" value={client.phone} />
            <InfoRow icon={Globe} label="Țară" value={client.countryCode} />
            <InfoRow icon={MapPin} label="Adresă" value={client.address} />
          </div>
        </CardBody>
      </Card>

      <Card>
        <CardHeader
          title="Clădiri"
          subtitle={`${formatNumber(client.buildings?.totalElements || 0)} clădiri`}
          actions={
            <Button
              size="sm"
              leftIcon={<Plus className="h-3.5 w-3.5" />}
              onClick={() => navigate(`/broker/clients/${id}/buildings/new`)}
            >
              Adaugă
            </Button>
          }
        />
        {buildings.length === 0 ? (
          <EmptyState
            icon={Building2}
            title="Nicio clădire"
            message="Adaugă prima clădire pentru acest client"
          />
        ) : (
          <Table>
            <THead>
              <TR>
                <TH>Adresă</TH>
                <TH>Oraș / Județ</TH>
                <TH>Tip</TH>
                <TH>An</TH>
                <TH>Suprafață</TH>
                <TH>Valoare asigurată</TH>
              </TR>
            </THead>
            <TBody>
              {buildings.map((b) => (
                <TR
                  key={b.id}
                  onClick={() => navigate(`/broker/buildings/${b.id}`)}
                >
                  <TD className="font-medium text-slate-900 dark:text-slate-100">
                    {b.fullAddress}
                  </TD>
                  <TD>
                    <div className="text-xs">
                      <div>{b.cityName}</div>
                      <div className="text-slate-400">
                        {b.countyName}, {b.countryName}
                      </div>
                    </div>
                  </TD>
                  <TD>
                    <Badge status={b.type}>{labelFor(BUILDING_TYPES, b.type)}</Badge>
                  </TD>
                  <TD>{b.constructionYear}</TD>
                  <TD>{formatNumber(b.surfaceArea)} m²</TD>
                  <TD className="font-medium">{formatMoney(b.insuredValue, 'RON')}</TD>
                </TR>
              ))}
            </TBody>
          </Table>
        )}
      </Card>
    </div>
  )
}
