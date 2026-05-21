import { useEffect, useState } from 'react'
import { Plus, Coins, Pencil } from 'lucide-react'
import { Card } from '../../components/ui/Card'
import Button from '../../components/ui/Button'
import Badge from '../../components/ui/Badge'
import Input from '../../components/ui/Input'
import Spinner from '../../components/ui/Spinner'
import Modal from '../../components/ui/Modal'
import EmptyState from '../../components/ui/EmptyState'
import Pagination from '../../components/ui/Pagination'
import { Table, THead, TH, TBody, TR, TD } from '../../components/ui/Table'
import { currenciesApi } from '../../api/currencies'
import { useToast } from '../../contexts/ToastContext'
import { apiError } from '../../lib/api'
import { numberOrNull } from '../../lib/utils'

export default function Currencies() {
  const toast = useToast()
  const [data, setData] = useState({ content: [], totalPages: 0, number: 0 })
  const [loading, setLoading] = useState(true)
  const [page, setPage] = useState(0)
  const [open, setOpen] = useState(false)
  const [editing, setEditing] = useState(null)
  const [form, setForm] = useState({
    code: '',
    name: '',
    exchangeRateToBase: 1,
    active: true,
  })
  const [saving, setSaving] = useState(false)

  const fetch = async () => {
    setLoading(true)
    try {
      setData(await currenciesApi.list({ page, size: 10 }))
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
    setForm({ code: '', name: '', exchangeRateToBase: 1, active: true })
    setOpen(true)
  }

  const openEdit = (c) => {
    setEditing(c)
    setForm({
      code: c.code,
      name: c.name,
      exchangeRateToBase: c.exchangeRateToBase,
      active: c.active,
    })
    setOpen(true)
  }

  const save = async (e) => {
    e.preventDefault()
    setSaving(true)
    try {
      const payload = {
        ...form,
        exchangeRateToBase: numberOrNull(form.exchangeRateToBase),
      }
      if (editing) {
        await currenciesApi.update(editing.id, payload)
        toast.success('Currency updated')
      } else {
        await currenciesApi.create(payload)
        toast.success('Currency created')
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
            Currencies
          </h2>
          <p className="text-sm text-slate-500 dark:text-slate-400 mt-1">
            Configure currencies and exchange rates
          </p>
        </div>
        <Button leftIcon={<Plus className="h-4 w-4" />} onClick={openCreate}>
          New Currency
        </Button>
      </div>

      <Card>
        {loading ? (
          <Spinner />
        ) : data.content.length === 0 ? (
          <EmptyState icon={Coins} title="No currencies configured" />
        ) : (
          <>
            <Table>
              <THead>
                <TR>
                  <TH>Code</TH>
                  <TH>Name</TH>
                  <TH>Base Rate</TH>
                  <TH>Active</TH>
                  <TH className="text-right">Actions</TH>
                </TR>
              </THead>
              <TBody>
                {data.content.map((c) => (
                  <TR key={c.id}>
                    <TD className="font-mono text-sm font-semibold">{c.code}</TD>
                    <TD>{c.name}</TD>
                    <TD className="font-mono">{c.exchangeRateToBase}</TD>
                    <TD>
                      <Badge variant={c.active ? 'success' : 'muted'}>
                        {c.active ? 'ACTIVE' : 'INACTIVE'}
                      </Badge>
                    </TD>
                    <TD>
                      <div className="flex justify-end">
                        <Button size="icon" variant="ghost" onClick={() => openEdit(c)}>
                          <Pencil className="h-4 w-4" />
                        </Button>
                      </div>
                    </TD>
                  </TR>
                ))}
              </TBody>
            </Table>
            <Pagination page={data.number || 0} totalPages={data.totalPages || 0} onChange={setPage} />
          </>
        )}
      </Card>

      <Modal
        open={open}
        onClose={() => setOpen(false)}
        title={editing ? 'Edit Currency' : 'New Currency'}
      >
        <form onSubmit={save} noValidate className="space-y-4">
          <Input
            label="Code"
            value={form.code}
            onChange={(e) => setForm({ ...form, code: e.target.value.toUpperCase() })}
            disabled={!!editing}
            maxLength={10}
            required
            hint="ex: RON, EUR, USD"
          />
          <Input
            label="Name"
            value={form.name}
            onChange={(e) => setForm({ ...form, name: e.target.value })}
            required
          />
          <Input
            type="number"
            step="any"
            label="Base Rate"
            value={form.exchangeRateToBase}
            onChange={(e) =>
              setForm({ ...form, exchangeRateToBase: e.target.value })
            }
            min={0.000001}
            required
          />
          <label className="flex items-center gap-2 text-sm text-slate-700 dark:text-slate-200">
            <input
              type="checkbox"
              className="accent-brand-500 h-4 w-4"
              checked={form.active}
              onChange={(e) => setForm({ ...form, active: e.target.checked })}
            />
            Active
          </label>
          <div className="flex justify-end gap-2 pt-2">
            <Button variant="outline" type="button" onClick={() => setOpen(false)}>
              Cancel
            </Button>
            <Button type="submit" loading={saving}>
              Save
            </Button>
          </div>
        </form>
      </Modal>
    </div>
  )
}
