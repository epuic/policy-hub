import { useEffect, useState } from 'react'
import { Plus, Percent, Pencil } from 'lucide-react'
import { Card } from '../../components/ui/Card'
import Button from '../../components/ui/Button'
import Badge from '../../components/ui/Badge'
import Input from '../../components/ui/Input'
import Select from '../../components/ui/Select'
import Spinner from '../../components/ui/Spinner'
import Modal from '../../components/ui/Modal'
import EmptyState from '../../components/ui/EmptyState'
import Pagination from '../../components/ui/Pagination'
import { Table, THead, TH, TBody, TR, TD } from '../../components/ui/Table'
import { feesApi } from '../../api/fees'
import { useToast } from '../../contexts/ToastContext'
import { apiError } from '../../lib/api'
import { FEE_TYPES, labelFor } from '../../utils/constants'
import { formatDate } from '../../lib/utils'

export default function FeeConfigurations() {
  const toast = useToast()
  const [data, setData] = useState({ content: [], totalPages: 0, number: 0 })
  const [loading, setLoading] = useState(true)
  const [page, setPage] = useState(0)
  const [open, setOpen] = useState(false)
  const [editing, setEditing] = useState(null)
  const [form, setForm] = useState({
    name: '',
    type: 'BROKER_COMMISSION',
    percentage: 0,
    effectiveFrom: '',
    effectiveTo: '',
    active: true,
  })
  const [saving, setSaving] = useState(false)

  const fetch = async () => {
    setLoading(true)
    try {
      setData(await feesApi.list({ page, size: 10 }))
    } catch (err) {
      toast.error(apiError(err))
    } finally {
      setLoading(false)
    }
  }
  useEffect(() => {
    fetch()
    // eslint-disable-next-line
  }, [page])

  const openCreate = () => {
    setEditing(null)
    setForm({
      name: '',
      type: 'BROKER_COMMISSION',
      percentage: 0,
      effectiveFrom: '',
      effectiveTo: '',
      active: true,
    })
    setOpen(true)
  }

  const openEdit = (f) => {
    setEditing(f)
    setForm({
      name: f.name,
      type: f.type,
      percentage: f.percentage,
      effectiveFrom: f.effectiveFrom || '',
      effectiveTo: f.effectiveTo || '',
      active: f.active,
    })
    setOpen(true)
  }

  const save = async (e) => {
    e.preventDefault()
    setSaving(true)
    try {
      const payload = {
        ...form,
        percentage: Number(form.percentage),
        effectiveFrom: form.effectiveFrom || null,
        effectiveTo: form.effectiveTo || null,
      }
      if (editing) {
        await feesApi.update(editing.id, payload)
        toast.success('Taxă actualizată')
      } else {
        await feesApi.create(payload)
        toast.success('Taxă creată')
      }
      setOpen(false)
      fetch()
    } catch (err) {
      toast.error(apiError(err))
    } finally {
      setSaving(false)
    }
  }

  return (
    <div className="space-y-5">
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-3">
        <div>
          <h2 className="text-2xl font-bold text-slate-900 dark:text-slate-100">
            Configurări de taxe
          </h2>
          <p className="text-sm text-slate-500 dark:text-slate-400 mt-1">
            Comisioane, ajustări și taxe administrative
          </p>
        </div>
        <Button leftIcon={<Plus className="h-4 w-4" />} onClick={openCreate}>
          Configurare nouă
        </Button>
      </div>

      <Card>
        {loading ? (
          <Spinner />
        ) : data.content.length === 0 ? (
          <EmptyState icon={Percent} title="Nicio configurare" />
        ) : (
          <>
            <Table>
              <THead>
                <TR>
                  <TH>Nume</TH>
                  <TH>Tip</TH>
                  <TH>Procent</TH>
                  <TH>Valabilitate</TH>
                  <TH>Activ</TH>
                  <TH className="text-right">Acțiuni</TH>
                </TR>
              </THead>
              <TBody>
                {data.content.map((f) => (
                  <TR key={f.id}>
                    <TD className="font-medium text-slate-900 dark:text-slate-100">
                      {f.name}
                    </TD>
                    <TD>
                      <Badge variant="brand">{labelFor(FEE_TYPES, f.type)}</Badge>
                    </TD>
                    <TD className="font-semibold">{f.percentage}%</TD>
                    <TD className="text-xs text-slate-500">
                      {formatDate(f.effectiveFrom)} → {formatDate(f.effectiveTo)}
                    </TD>
                    <TD>
                      <Badge variant={f.active ? 'success' : 'muted'}>
                        {f.active ? 'ACTIV' : 'INACTIV'}
                      </Badge>
                    </TD>
                    <TD>
                      <div className="flex justify-end">
                        <Button
                          size="icon"
                          variant="ghost"
                          onClick={() => openEdit(f)}
                        >
                          <Pencil className="h-4 w-4" />
                        </Button>
                      </div>
                    </TD>
                  </TR>
                ))}
              </TBody>
            </Table>
            <Pagination
              page={data.number || 0}
              totalPages={data.totalPages || 0}
              onChange={setPage}
            />
          </>
        )}
      </Card>

      <Modal
        open={open}
        onClose={() => setOpen(false)}
        title={editing ? 'Editează taxă' : 'Taxă nouă'}
      >
        <form onSubmit={save} className="space-y-4">
          <Input
            label="Nume"
            value={form.name}
            onChange={(e) => setForm({ ...form, name: e.target.value })}
            required
          />
          <Select
            label="Tip"
            value={form.type}
            onChange={(e) => setForm({ ...form, type: e.target.value })}
            options={FEE_TYPES}
          />
          <Input
            type="number"
            step="0.01"
            label="Procent (%)"
            value={form.percentage}
            onChange={(e) => setForm({ ...form, percentage: e.target.value })}
            min={0}
            max={100}
            required
          />
          <div className="grid grid-cols-2 gap-3">
            <Input
              type="date"
              label="Începând de la"
              value={form.effectiveFrom}
              onChange={(e) => setForm({ ...form, effectiveFrom: e.target.value })}
            />
            <Input
              type="date"
              label="Până la"
              value={form.effectiveTo}
              onChange={(e) => setForm({ ...form, effectiveTo: e.target.value })}
            />
          </div>
          <label className="flex items-center gap-2 text-sm text-slate-700 dark:text-slate-200">
            <input
              type="checkbox"
              className="accent-brand-500 h-4 w-4"
              checked={form.active}
              onChange={(e) => setForm({ ...form, active: e.target.checked })}
            />
            Activă
          </label>
          <div className="flex justify-end gap-2 pt-2">
            <Button variant="outline" type="button" onClick={() => setOpen(false)}>
              Anulează
            </Button>
            <Button type="submit" loading={saving}>
              Salvează
            </Button>
          </div>
        </form>
      </Modal>
    </div>
  )
}
