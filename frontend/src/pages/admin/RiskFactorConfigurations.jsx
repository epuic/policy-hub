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
import { geographyApi } from '../../api/geography'
import { useToast } from '../../contexts/ToastContext'
import { apiError } from '../../lib/api'
import {
  BUILDING_TYPES,
  RISK_FACTOR_CONFIG_LEVELS,
  RISK_FACTOR_TYPES,
  labelFor,
} from '../../utils/constants'
import { numberOrNull } from '../../lib/utils'

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
  const [countries, setCountries] = useState([])
  const [counties, setCounties] = useState([])
  const [cities, setCities] = useState([])
  const [selectedCountry, setSelectedCountry] = useState('')
  const [selectedCounty, setSelectedCounty] = useState('')
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

  useEffect(() => {
    geographyApi
      .adminCountries({ page: 0, size: 200 })
      .then((d) => setCountries(d.content || []))
      .catch((err) => toast.error(apiError(err)))
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [])

  useEffect(() => {
    if (!selectedCountry) {
      setCounties([])
      return
    }
    geographyApi
      .adminCounties(selectedCountry, { page: 0, size: 300 })
      .then((d) => setCounties(d.content || []))
      .catch((err) => toast.error(apiError(err)))
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [selectedCountry])

  useEffect(() => {
    if (!selectedCounty) {
      setCities([])
      return
    }
    geographyApi
      .adminCities(selectedCounty, { page: 0, size: 500, buildingSize: 1 })
      .then((d) => setCities(d.content || []))
      .catch((err) => toast.error(apiError(err)))
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [selectedCounty])

  const openCreate = () => {
    setEditing(null)
    setForm({
      level: 'COUNTRY',
      referenceId: '',
      adjustmentPercentage: 0,
      active: true,
    })
    setSelectedCountry('')
    setSelectedCounty('')
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
    setSelectedCountry(r.level === 'COUNTRY' ? r.referenceId || '' : '')
    setSelectedCounty(r.level === 'COUNTY' ? r.referenceId || '' : '')
    setOpen(true)
  }

  const changeLevel = (e) => {
    setForm({ ...form, level: e.target.value, referenceId: '' })
    setSelectedCountry('')
    setSelectedCounty('')
  }

  const selectCountry = (countryId) => {
    setSelectedCountry(countryId)
    setSelectedCounty('')
    setForm({ ...form, referenceId: form.level === 'COUNTRY' ? countryId : '' })
  }

  const selectCounty = (countyId) => {
    setSelectedCounty(countyId)
    setForm({ ...form, referenceId: form.level === 'COUNTY' ? countyId : '' })
  }

  const referenceOptions = () => {
    if (form.level === 'COUNTRY') {
      return countries.map((c) => ({ value: c.id, label: c.name }))
    }
    if (form.level === 'COUNTY') {
      return counties.map((c) => ({ value: c.id, label: c.name }))
    }
    if (form.level === 'CITY') {
      return cities.map((c) => ({ value: c.id, label: c.name }))
    }
    if (form.level === 'BUILDING_TYPE') {
      return BUILDING_TYPES
    }
    if (form.level === 'RISK_FACTOR_TYPE') {
      return RISK_FACTOR_TYPES
    }
    return []
  }

  const currentReferenceOption = () => {
    if (!editing?.referenceId || editing.referenceId !== form.referenceId) return null
    const exists = referenceOptions().some(
      (option) => String(option.value) === String(form.referenceId),
    )
    if (exists) return null
    return {
      value: editing.referenceId,
      label: editing.referenceName || editing.referenceId,
    }
  }

  const referenceSelectOptions = () => {
    const current = currentReferenceOption()
    return current ? [current, ...referenceOptions()] : referenceOptions()
  }

  const save = async (e) => {
    e.preventDefault()
    setSaving(true)
    try {
      const payload = {
        ...form,
        adjustmentPercentage: numberOrNull(form.adjustmentPercentage),
        referenceId: form.referenceId || null,
      }
      if (editing) {
        await riskFactorsApi.update(editing.id, payload)
        toast.success('Adjustment updated')
      } else {
        await riskFactorsApi.create(payload)
        toast.success('Adjustment created')
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
            Risk Factor Adjustments
          </h2>
          <p className="text-sm text-slate-500 dark:text-slate-400 mt-1">
            Percentage configuration by level
          </p>
        </div>
        <Button leftIcon={<Plus className="h-4 w-4" />} onClick={openCreate}>
          New Adjustment
        </Button>
      </div>

      <Card>
        {loading ? (
          <Spinner />
        ) : data.content.length === 0 ? (
          <EmptyState icon={ShieldAlert} title="No adjustments" />
        ) : (
          <>
            <Table>
              <THead>
                <TR>
                  <TH>Level</TH>
                  <TH>Reference</TH>
                  <TH>Adjustment</TH>
                  <TH>Active</TH>
                  <TH className="text-right">Actions</TH>
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
                      {r.referenceName || r.referenceId || '-'}
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
                        {r.active ? 'ACTIVE' : 'INACTIVE'}
                      </Badge>
                    </TD>
                    <TD>
                      <div className="flex justify-end">
                        <Button size="icon" variant="ghost" onClick={() => openEdit(r)}>
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
        title={editing ? 'Edit Adjustment' : 'New Adjustment'}
      >
        <form onSubmit={save} noValidate className="space-y-4">
          <Select
            label="Level"
            value={form.level}
            onChange={changeLevel}
            options={RISK_FACTOR_CONFIG_LEVELS}
          />
          {(form.level === 'COUNTRY' ||
            form.level === 'COUNTY' ||
            form.level === 'CITY') && (
            <Select
              label="Country"
              value={selectedCountry}
              onChange={(e) => selectCountry(e.target.value)}
              placeholder="Select country"
              options={countries.map((c) => ({ value: c.id, label: c.name }))}
            />
          )}
          {(form.level === 'COUNTY' || form.level === 'CITY') && (
            <Select
              label="County"
              value={selectedCounty}
              onChange={(e) => selectCounty(e.target.value)}
              placeholder="Select county"
              options={counties.map((c) => ({ value: c.id, label: c.name }))}
              disabled={!selectedCountry && !form.referenceId}
            />
          )}
          {(form.level === 'CITY' ||
            form.level === 'BUILDING_TYPE' ||
            form.level === 'RISK_FACTOR_TYPE') && (
            <Select
              label="Reference"
              value={form.referenceId}
              onChange={(e) => setForm({ ...form, referenceId: e.target.value })}
              placeholder={
                form.level === 'CITY' ? 'Select city' : 'Select reference'
              }
              options={referenceSelectOptions()}
              disabled={form.level === 'CITY' && !selectedCounty}
            />
          )}
          <Input
            type="number"
            step="0.01"
            label="Adjustment Percentage (%)"
            value={form.adjustmentPercentage}
            onChange={(e) =>
              setForm({ ...form, adjustmentPercentage: e.target.value })
            }
            min={-100}
            max={100}
            required
            hint="Negative value means discount"
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
