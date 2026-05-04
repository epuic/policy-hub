import { useEffect, useMemo, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { ArrowLeft, Save } from 'lucide-react'
import { Card, CardBody, CardHeader } from '../../components/ui/Card'
import Input from '../../components/ui/Input'
import Select from '../../components/ui/Select'
import Button from '../../components/ui/Button'
import { buildingsApi } from '../../api/buildings'
import { geographyApi } from '../../api/geography'
import { useToast } from '../../contexts/ToastContext'
import { apiError } from '../../lib/api'
import { BUILDING_TYPES, RISK_FACTOR_TYPES } from '../../utils/constants'

export default function BuildingForm() {
  const { clientId, buildingId } = useParams()
  const isEdit = !!buildingId
  const navigate = useNavigate()
  const toast = useToast()

  const [form, setForm] = useState({
    street: '',
    number: '',
    cityId: '',
    constructionYear: new Date().getFullYear(),
    type: 'RESIDENTIAL',
    numberOfFloors: 1,
    surfaceArea: 0,
    insuredValue: 0,
    riskFactorTypes: [],
  })
  const [countries, setCountries] = useState([])
  const [counties, setCounties] = useState([])
  const [cities, setCities] = useState([])
  const [selectedCountry, setSelectedCountry] = useState('')
  const [selectedCounty, setSelectedCounty] = useState('')
  const [loading, setLoading] = useState(false)
  const [saving, setSaving] = useState(false)

  useEffect(() => {
    geographyApi.countries({ page: 0, size: 100 }).then((d) =>
      setCountries(d.content || []),
    )
  }, [])

  useEffect(() => {
    if (!selectedCountry) {
      setCounties([])
      return
    }
    geographyApi.counties(selectedCountry, { page: 0, size: 200 }).then((d) =>
      setCounties(d.content || []),
    )
  }, [selectedCountry])

  useEffect(() => {
    if (!selectedCounty) {
      setCities([])
      return
    }
    geographyApi
      .cities(selectedCounty, { page: 0, size: 200, buildingSize: 1 })
      .then((d) => setCities((d.content || []).map((c) => ({ id: c.id, name: c.name }))))
  }, [selectedCounty])

  useEffect(() => {
    if (!isEdit) return
    setLoading(true)
    buildingsApi
      .get(buildingId)
      .then(async (b) => {
        // Try to infer address parts from fullAddress (street + number combined server-side)
        setForm({
          street: b.fullAddress?.replace(/\s*\d+\s*$/, '').trim() || '',
          number: (b.fullAddress?.match(/\d+$/) || [''])[0],
          cityId: '',
          constructionYear: b.constructionYear,
          type: b.type,
          numberOfFloors: b.numberOfFloors,
          surfaceArea: b.surfaceArea,
          insuredValue: b.insuredValue,
          riskFactorTypes: b.riskFactorTypes || [],
        })
      })
      .catch((err) => toast.error(apiError(err)))
      .finally(() => setLoading(false))
  }, [buildingId])

  const change = (k) => (e) =>
    setForm({ ...form, [k]: e.target.value })

  const toggleRisk = (type) => {
    setForm((f) => ({
      ...f,
      riskFactorTypes: f.riskFactorTypes.includes(type)
        ? f.riskFactorTypes.filter((t) => t !== type)
        : [...f.riskFactorTypes, type],
    }))
  }

  const submit = async (e) => {
    e.preventDefault()
    setSaving(true)
    try {
      const payload = {
        ...form,
        cityId: Number(form.cityId),
        constructionYear: Number(form.constructionYear),
        numberOfFloors: Number(form.numberOfFloors),
        surfaceArea: Number(form.surfaceArea),
        insuredValue: Number(form.insuredValue),
      }
      if (isEdit) {
        await buildingsApi.update(buildingId, payload)
        toast.success('Clădire actualizată')
        navigate(`/broker/buildings/${buildingId}`)
      } else {
        const created = await buildingsApi.create(clientId, payload)
        toast.success('Clădire creată')
        navigate(`/broker/buildings/${created.id}`)
      }
    } catch (err) {
      toast.error(apiError(err))
    } finally {
      setSaving(false)
    }
  }

  return (
    <div className="space-y-5 max-w-4xl">
      <Button
        variant="ghost"
        size="sm"
        leftIcon={<ArrowLeft className="h-4 w-4" />}
        onClick={() => navigate(-1)}
      >
        Înapoi
      </Button>

      <Card>
        <CardHeader
          title={isEdit ? 'Editează clădire' : 'Clădire nouă'}
          subtitle="Completează detaliile clădirii asigurate"
        />
        <CardBody>
          <form onSubmit={submit} className="space-y-6">
            <div>
              <h4 className="text-xs uppercase tracking-wider text-slate-500 mb-3">
                Adresa
              </h4>
              <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
                <div className="md:col-span-3">
                  <Input
                    label="Stradă"
                    value={form.street}
                    onChange={change('street')}
                    required
                  />
                </div>
                <Input
                  label="Număr"
                  value={form.number}
                  onChange={change('number')}
                  required
                />
                <Select
                  label="Țară"
                  value={selectedCountry}
                  onChange={(e) => {
                    setSelectedCountry(e.target.value)
                    setSelectedCounty('')
                    setForm((f) => ({ ...f, cityId: '' }))
                  }}
                  placeholder="Selectează țara"
                  options={countries.map((c) => ({ value: c.id, label: c.name }))}
                />
                <Select
                  label="Județ"
                  value={selectedCounty}
                  onChange={(e) => {
                    setSelectedCounty(e.target.value)
                    setForm((f) => ({ ...f, cityId: '' }))
                  }}
                  placeholder="Selectează județul"
                  options={counties.map((c) => ({ value: c.id, label: c.name }))}
                  disabled={!selectedCountry}
                />
                <Select
                  label="Oraș"
                  value={form.cityId}
                  onChange={change('cityId')}
                  placeholder="Selectează orașul"
                  options={cities.map((c) => ({ value: c.id, label: c.name }))}
                  disabled={!selectedCounty}
                  required={!isEdit}
                />
              </div>
            </div>

            <div>
              <h4 className="text-xs uppercase tracking-wider text-slate-500 mb-3">
                Caracteristici
              </h4>
              <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                <Select
                  label="Tip"
                  value={form.type}
                  onChange={change('type')}
                  options={BUILDING_TYPES}
                />
                <Input
                  type="number"
                  label="An construcție"
                  value={form.constructionYear}
                  onChange={change('constructionYear')}
                  min={1800}
                  max={new Date().getFullYear()}
                  required
                />
                <Input
                  type="number"
                  label="Număr de etaje"
                  value={form.numberOfFloors}
                  onChange={change('numberOfFloors')}
                  min={1}
                  required
                />
                <Input
                  type="number"
                  step="0.01"
                  label="Suprafață (m²)"
                  value={form.surfaceArea}
                  onChange={change('surfaceArea')}
                  min={0}
                  required
                />
                <div className="md:col-span-2">
                  <Input
                    type="number"
                    step="0.01"
                    label="Valoare asigurată (RON)"
                    value={form.insuredValue}
                    onChange={change('insuredValue')}
                    min={0}
                    required
                  />
                </div>
              </div>
            </div>

            <div>
              <h4 className="text-xs uppercase tracking-wider text-slate-500 mb-3">
                Factori de risc
              </h4>
              <div className="grid grid-cols-2 md:grid-cols-4 gap-2">
                {RISK_FACTOR_TYPES.map((r) => {
                  const checked = form.riskFactorTypes.includes(r.value)
                  return (
                    <label
                      key={r.value}
                      className={`flex items-center gap-2 rounded-lg border px-3 py-2 cursor-pointer transition text-sm ${
                        checked
                          ? 'border-brand-500 bg-brand-50 dark:bg-brand-950/40 text-brand-700 dark:text-brand-300'
                          : 'border-slate-200 dark:border-slate-700 hover:bg-slate-50 dark:hover:bg-slate-800'
                      }`}
                    >
                      <input
                        type="checkbox"
                        checked={checked}
                        onChange={() => toggleRisk(r.value)}
                        className="accent-brand-500"
                      />
                      {r.label}
                    </label>
                  )
                })}
              </div>
            </div>

            <div className="flex gap-2 justify-end">
              <Button variant="outline" type="button" onClick={() => navigate(-1)}>
                Anulează
              </Button>
              <Button
                type="submit"
                loading={saving || loading}
                leftIcon={<Save className="h-4 w-4" />}
              >
                {isEdit ? 'Salvează' : 'Creează'}
              </Button>
            </div>
          </form>
        </CardBody>
      </Card>
    </div>
  )
}
