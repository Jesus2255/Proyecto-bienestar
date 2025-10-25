import { createBrowserRouter, RouterProvider, Navigate } from "react-router-dom";
import { CitasPages } from "../pages/CitasPages";
import { ClientesPage } from "../pages/ClientePage";
import { FacturasPage } from "../pages/FacturasPage";
import { ServiciosPage } from "../pages/ServiciosPages";
import { LoginPage } from "../pages/LoginPage";
import { Layout } from "../components/layout/Layout";
import { ProtectedRoute } from "./protectedRoutes"; 
const ProtectedLayout = ({ children }) => (
  <ProtectedRoute>
    <Layout>
      {children}
    </Layout>
  </ProtectedRoute>
);

const router = createBrowserRouter([
  {
    path: "/login",
    element: <LoginPage />,
  },
  {
    path: "/",
    element: <Navigate to="/clientes" replace />,
  },
  {
    path: "/clientes",
    element: (
      <ProtectedLayout>
        <ClientesPage />
      </ProtectedLayout>
    ),
  },
  {
    path: "/citas",
    element: (
      <ProtectedLayout>
        <CitasPages />
      </ProtectedLayout>
    ),
  },
  {
    path: "/facturas",
    element: (
      <ProtectedLayout>
        <FacturasPage />
      </ProtectedLayout>
    ),
  },
  {
    path: "/servicios",
    element: (
      <ProtectedLayout>
        <ServiciosPage />
      </ProtectedLayout>
    ),
  },
  {
    path: "*",
    element: <Navigate to="/clientes" replace />,
  },
]);

const MyRouter = () => {
  return <RouterProvider router={router} />;
}

export default MyRouter;