import { create } from "zustand";
import axios from "axios";

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080/api";

export const useCitaStore = create((set, get) => ({
    // Estados
    isLoading: false,
    isCitaCreated: false,
    citaData: null,
    citas: [],
    error: null,
    success: null,

    // Setters
    setCitaCreated: (value) => set({ isCitaCreated: value }),
    setCitaData: (data) => set({ citaData: data }),

    // Obtener todas las citas
    getCitas: async () => {
        set({ isLoading: true, error: null, success: null });
        try {
            console.log('Obteniendo citas desde:', `${API_BASE_URL}/citas`);
            const response = await axios.get(`${API_BASE_URL}/citas`);
            console.log('Citas obtenidas:', response.data);
            set({ citas: response.data, isLoading: false });
            return response.data;
        } catch (error) {
            console.error('Error al obtener citas:', error);
            const errorMessage = error.response?.data?.message || error.message || "Error al cargar citas";
            set({ error: errorMessage, isLoading: false });
            throw error;
        }
    },

    // Agendar cita
    createCita: async (citaInfo) => {
        set({ isLoading: true, error: null, success: null });
        try {
            console.log('Creando cita:', citaInfo);
            const response = await axios.post(`${API_BASE_URL}/citas`, citaInfo);
            const { citas } = get();
            set({ 
                citaData: response.data, 
                citas: [...citas, response.data],
                isCitaCreated: true,
                isLoading: false,
                success: "Cita creada exitosamente"
            });
            return response.data;
        } catch (error) {
            console.error('Error al crear cita:', error);
            const errorMessage = error.response?.data?.message || error.message || "Error al crear cita";
            set({ error: errorMessage, isLoading: false });
            throw error;
        }
    },

    // Actualizar cita
    updateCita: async (id, citaData) => {
        set({ isLoading: true, error: null, success: null });
        try {
            const response = await axios.put(`${API_BASE_URL}/citas/${id}`, citaData);
            const { citas } = get();
            const citasActualizadas = citas.map(cita => 
                cita.id === id ? response.data : cita
            );
            set({ 
                citaData: response.data,
                citas: citasActualizadas,
                isLoading: false,
                success: "Cita actualizada exitosamente"
            });
            return response.data;
        } catch (error) {
            const errorMessage = error.response?.data?.message || error.message || "Error al actualizar cita";
            set({ error: errorMessage, isLoading: false });
            throw error;
        }
    },

    // Cancelar cita
    deleteCita: async (id) => {
        set({ isLoading: true, error: null, success: null });
        try {
            await axios.delete(`${API_BASE_URL}/citas/${id}`);
            const { citas } = get();
            const citasFiltradas = citas.filter(cita => cita.id !== id);
            set({ 
                citaData: null,
                citas: citasFiltradas,
                isCitaCreated: false,
                isLoading: false,
                success: "Cita eliminada exitosamente"
            });
        } catch (error) {
            const errorMessage = error.response?.data?.message || error.message || "Error al eliminar cita";
            set({ error: errorMessage, isLoading: false });
            throw error;
        }
    },

    // Limpiar estados
    clearError: () => set({ error: null }),
    clearSuccess: () => set({ success: null }),
    clearCitaData: () => set({ citaData: null, isCitaCreated: false })
}));