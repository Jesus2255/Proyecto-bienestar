import { create } from "zustand";

export const useAuthStore = create((set, get) => ({
    // Estados
    isLoading: true, 
    isAuthenticated: false, 
    user: null, 
    error: null,

    login: async (username, password) => {
        set({ isLoading: true, error: null });

        await new Promise(resolve => setTimeout(resolve, 1000));
        
        if (username === 'admin' && password === '1234') {
            const user = { 
                username: username, 
                roles: ['ADMIN'],
                authenticated: true 
            };
            set({ 
                isAuthenticated: true, 
                user: user,
                isLoading: false 
            });
            return user;
        } else {
            set({ 
                error: 'Credenciales incorrectas. Usa: admin / 1234', 
                isLoading: false 
            });
            throw new Error('Credenciales incorrectas');
        }
    },

    logout: async () => {
        localStorage.removeItem('authToken');
        set({ 
            isAuthenticated: false, 
            user: null,
            isLoading: false 
        });
    },
    clearError: () => set({ error: null }),

    initializeAuth: () => {
        const token = localStorage.getItem('authToken');
        
        if (token) {
            set({ 
                isAuthenticated: true,
                user: { 
                    username: 'admin', 
                    roles: ['ADMIN'],
                    authenticated: true 
                },
                isLoading: false
            });
        } else {
            set({ 
                isAuthenticated: false,
                user: null,
                isLoading: false
            });
        }
    }
}));