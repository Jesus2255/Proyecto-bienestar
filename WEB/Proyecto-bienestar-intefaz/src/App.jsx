import React, { useEffect } from 'react';
import { Toaster } from 'sonner';
import { useAuthStore } from './hooks/StoreLogin';
import MyRouter from './routes/routes';


function App() {
  const { initializeAuth } = useAuthStore();

  useEffect(() => {
    initializeAuth();
  }, []);

  return (
    <>
      <MyRouter />
      <Toaster position="top-right" />
    </>
  );
}

export default App;