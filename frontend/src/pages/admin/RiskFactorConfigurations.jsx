import { useEffect, useState } from 'react'
import { Plus, ShieldAlert, Pencil } from 'lucide-react'
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
import { riskFactorsApi } from '../../api/riskFactors'
import { useToast } from '../../contexts/ToastContext'
import { apiError } from '../../lib/api'
import { RISK_FACTOR_CONFIG_LEVELS, labelFor } from '../../utils/constants'

export default function RiskFactorConfigurations() {
  const toast = useToast()
  const [data, setData] = useState({ content: [], totalPages: 0, number: 0 })
  const [loading, setLoading] = useState(true)
  const [page, setPage] = useState(0)
  const [open, setOpen] = useState(false)
  const [editing, setEditing] = useState(null)
  const [form, setForm] = useState({
    level: 'COUNTRY',
    referenceId: '',
    adjustmentPercentage: 0,
    active: true,
  })
  const [saving, setSaving] = useState(false)

  const fetch = async () => {
    setLoading(true)
    try {
      setData(await riskFactorsApi.list({ page, size: 10 }))
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
      level: 'COUNTRY',
      referenceId: '',
      adjustmentPercentage: 0,
      active: true,
    })
    setOpen(true)
  }

  const openEdit = (r) => {
    setEditing(r)
    setForm({
      level: r.level,
      referenceId: r.referenceId || '',
      adjustmentPercentage: r.adjustmentPercentage,
      active: r.active,
    })
    setOpen(true)
  }

  const save = async (e) => {
    e.preventDefault()
    setSaving(true)
    try {
      const payload = {
        ...form,
        adjustmentPercentage: Number(form.adjustmentPercentage),
        referenceId: form.referenceId || null,
      }
      if (editing) {
        await riskFactorsApi.update(editing.id, payload)
        toast.success('Ajustare actualizată')
      } else {
        await riskFactorsApi.create(payload)
        toast.success('Ajustare creată')
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
            Ajustări factori de risc
          </h2>
          <p className="text-sm text-slate-500 dark:text-slate-400 mt-1">
            Configurări procentuale per nivel
          </p>
        </div>
        <Button leftIcon={<Plus className="h-4 w-4" />} onClick={openCreate}>
          Ajustare nouă
        </Button>
      </div>

      <Card>
        {loading ? (
          <Spinner />
        ) : data.content.length === 0 ? (
          <EmptyState icon={ShieldAlert} title="Nicio ajustare" />
        ) : (
          <>
            <Table>
              <THead>
                <TR>
                  <TH>Nivel</TH>
                  <TH>Referință</TH>
                  <TH>Ajustare</TH>
                  <TH>Activ</TH>
                  <TH className="text-right">Acțiuni</TH>
                </TR>
              </THead>
              <TBody>
                {data.content.map((r) => (
                  <TR key={r.id}>
                    <TD>
                      <Badge variant="brand">
                        {labelFor(RISK_FACTOR_CONFIG_LEVELS, r.level)}
                      </Badge>
                    </TD>
                    <TD className="text-slate-700 dark:text-slate-200">
                      {r.referenceName || r.referenceId || '—'}
                    </TD>
                    <TD>
                      <span
                        className={`font-semibold ${
                          r.adjustmentPercentage >= 0
                            ? 'text-rose-600 dark:text-rose-400'
                            : 'text-emerald-600 dark:text-emerald-400'
                        }`}
                      >
                        {r.adjustmentPercentage > 0 ? '+' : ''}
                        {r.adjustmentPercentage}%
                      </span>
                    </TD>
                    <TD>
                      <Badge variant={r.active ? 'success' : 'muted'}>
                        {r.active ? 'ACTIV' : 'INACTIV'}
                      </Badge>
                    </TD>
                    <TD>
                      <div className="flex justify-end">
                        <Button
                          size="icon"
                          variant="ghost"
                          onClick={() => openEdit(r)}
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
        title={editing ? 'Editează ajustare' : 'Ajustare nouă'}
      >
        <form onSubmit={save} className="space-y-4">
          <Select
            label="Nivel"
            value={form.level}
            onChange={(e) => setForm({ ...form, level: e.target.value })}
            options={RISK_FACTOR_CONFIG_LEVELS}
          />
          <Input
            label="ID referință"
            value={form.referenceId}
            onChange={(e) => setForm({ ...form, referenceId: e.target.value })}
            hint="ID țară/județ/oraș sau valoare enum (opțional)"
          />
          <Input
            type="number"
            step="0.01"
            label="Procent ajustare (%)"
            value={form.adjustmentPercentage}
            onChange={(e) =>
              setForm({ ...form, adjustmentPercentage: e.target.value })
            }
            min={-100}
            max={100}
            required
            hint="Valoare negativă pentru discount"
          />
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
