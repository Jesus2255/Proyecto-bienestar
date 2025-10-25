import { create } from "zustand";
import axios from "./config/axiosConfig";

export const useAuthStore = create((set, get) => ({
    // Estados
    isLoading: true, 
    isAuthenticated: false, 
    user: null, 
    error: null,

    login: async (username, password) => {
        set({ isLoading: true, error: null });
        
        try {
            // POST a /login (Spring Security procesa en raíz, no en /api)
            const formData = new URLSearchParams();
            formData.append('username', username);
            formData.append('password', password);
            
            // Configurar baseURL temporal para login (está fuera de /api)
            const loginResponse = await axios.post('http://localhost:8080/login', formData, {
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                withCredentials: true
            });
            
            // Guardar token en localStorage
            localStorage.setItem('authToken', 'authenticated');
            
            // Obtener información del usuario desde /api/auth/user-info
            const userInfoResponse = await axios.get('/auth/user-info');
            const userData = userInfoResponse.data;
            
            if (userData.success) {
                const user = { 
                    username: userData.username, 
                    roles: [userData.role],
                    authenticated: true 
                };
                
                set({ 
                    isAuthenticated: true, 
                    user: user,
                    isLoading: false 
                });
                
                return user;
            } else {
                throw new Error('Error al obtener información del usuario');
            }
            
        } catch (error) {
            console.error('Error en login:', error);
            const errorMessage = error.response?.data?.message || 'Credenciales incorrectas';
            
            set({ 
                error: errorMessage, 
                isLoading: false 
            });
            
            throw new Error(errorMessage);
        }
    },

    logout: async () => {
        try {
            // Llamar al endpoint de logout del backend (en raíz, no en /api)
            await axios.post('http://localhost:8080/logout', {}, {
                withCredentials: true
            });
        } catch (error) {
            console.error('Error en logout:', error);
        } finally {
            // Limpiar estado local independientemente del resultado
            localStorage.removeItem('authToken');
            set({ 
                isAuthenticated: false, 
                user: null,
                isLoading: false 
            });
        }
    },
    clearError: () => set({ error: null }),

    initializeAuth: async () => {
        const token = localStorage.getItem('authToken');
        
        if (token) {
            try {
                // Verificar sesión con el backend
                const userInfoResponse = await axios.get('/auth/user-info');
                const userData = userInfoResponse.data;
                
                if (userData.success) {
                    set({ 
                        isAuthenticated: true,
                        user: { 
                            username: userData.username, 
                            roles: [userData.role],
                            authenticated: true 
                        },
                        isLoading: false
                    });
                    return;
                }
            } catch (error) {
                console.error('Sesión expirada o inválida:', error);
                localStorage.removeItem('authToken');
            }
        }
        
        // Si no hay token o falló la verificación
        set({ 
            isAuthenticated: false,
            user: null,
            isLoading: false
        });
    }
}));