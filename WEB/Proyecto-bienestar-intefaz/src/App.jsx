import React, { useEffect } from 'react';
import { Toaster } from 'sonner';
import { useAuthStore } from './hooks/StoreLogin';
import MyRouter from './routes/routes';


function App() {
  const { initializeAuth } = useAuthStore();

  useEffect(() => {
    // Inicializar autenticación al cargar la app
    initializeAuth();
  }, [initializeAuth]);

  return (
    <>
      <MyRouter />
      <Toaster position="top-right" />
    </>
  );
}

export default App;