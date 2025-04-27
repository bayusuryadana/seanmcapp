import ReactDOM from 'react-dom/client'
import { Home } from './Home.tsx'
import './index.css'
import { createBrowserRouter, RouterProvider } from "react-router-dom"
import { Wallet } from './wallet/Wallet.tsx'
import { WalletLogin } from './wallet/WalletLogin.tsx'
import { UserProvider } from './wallet/UserContext.tsx'
import { StrictMode } from 'react'
import { WalletDashboard } from './wallet/WalletDashboard.tsx'

const router = createBrowserRouter([
  {
    path: "/",
    element: <Home />
  },
  {
    path: "/wallet/login",
    element: <WalletLogin />
  },
  {
    element: <Wallet />,
    children: [
      {
        path: "/wallet",
        element: <WalletDashboard />
      },
    ]
  },
]);

ReactDOM.createRoot(document.getElementById('root')!).render(
  <StrictMode>
      <UserProvider>
        <RouterProvider router={router} />
      </UserProvider>
  </StrictMode>,
)
