# Insurance Platform - Frontend

Frontend React pentru platforma de asigurări de clădiri. Comunică cu backendul Spring Boot (`/api`).

## Tech Stack

- **React 18** + **Vite 5** (JavaScript)
- **React Router v6** — rutare SPA
- **TailwindCSS 3** — styling + dark/light mode
- **Axios** — client HTTP cu interceptori JWT
- **lucide-react** — iconițe

## Structură

```
src/
├── api/            # servicii HTTP per resursă (clients, buildings, policies, ...)
├── components/
│   ├── ui/         # componente UI reutilizabile (Button, Input, Card, Modal, Table, ...)
│   ├── layout/     # Sidebar, Topbar, ThemeToggle
│   └── dashboard/  # StatCard
├── contexts/       # AuthContext, ThemeContext, ToastContext
├── layouts/        # AppLayout (sidebar + topbar + outlet)
├── lib/            # api axios instance, JWT decode, utils (format date/money)
├── pages/
│   ├── broker/     # dashboard broker + clienți, clădiri, politici
│   └── admin/      # dashboard admin + brokeri, monede, taxe, risc, rapoarte
├── utils/          # constante (enums, labels)
├── App.jsx         # router principal
├── main.jsx        # entry point
└── index.css       # tailwind + CSS vars pentru teme
```

## Rulare

### Prerequisite
- Node.js 18+
- Backendul Spring Boot pornit pe `http://localhost:8080`

### Instalare
```bash
cd frontend
npm install
```

### Development
```bash
npm run dev
```
Aplicația va rula pe `http://localhost:5173`. Vite proxy-ează `/api` către `http://localhost:8080`.

### Build producție
```bash
npm run build
npm run preview
```

## Autentificare

Aplicația are două roluri:
- **BROKER** → `/broker` (gestionare clienți, clădiri, politici)
- **ADMINISTRATOR** → `/admin` (brokeri, monede, taxe, ajustări risc, rapoarte)

Token-ul JWT se salvează în `localStorage` și este atașat automat la toate cererile prin interceptor.

## Teme

Toggle dark/light în Topbar (persistent în `localStorage`).

## Comunicare cu backendul

- Toate endpoint-urile pornesc cu `/api` (proxy către Spring Boot în dev)
- Formatele datelor și enum-urile se potrivesc exact cu DTO-urile din `com.endava.insurance.insurance_service`
- Erorile sunt extrase din `ErrorResponseDTO.message` / `details` și afișate în toast-uri
