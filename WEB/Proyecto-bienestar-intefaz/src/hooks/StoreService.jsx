import { create } from "zustand";
import axios from './config/axiosConfig';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080/api";

export const useServicioStore = create((set, get) => ({
    // Estados
    isLoading: false,
    servicios: [],
    servicioSeleccionado: null,
    error: null,
    success: null,

    // Obtener todos los servicios
    getServicios: async () => {
        set({ isLoading: true, error: null });
        try {
            const response = await axios.get(`${API_BASE_URL}/servicios`);
            set({ servicios: response.data, isLoading: false });
            return response.data;
        } catch (error) {
            set({ error: error.response?.data || error.message, isLoading: false });
            throw error;
        }
    },

    // Crear servicio
    createServicio: async (servicioData) => {
        set({ isLoading: true, error: null, success: null });
        try {
            const response = await axios.post(`${API_BASE_URL}/servicios`, servicioData);
            const { servicios } = get();
            set({ 
                servicios: [...servicios, response.data],
                isLoading: false,
                success: "Servicio creado exitosamente"
            });
            return response.data;
        } catch (error) {
            set({ error: error.response?.data || error.message, isLoading: false });
            throw error;
        }
    },

    // Actualizar servicio
    updateServicio: async (id, servicioData) => {
        set({ isLoading: true, error: null, success: null });
        try {
            const response = await axios.put(`${API_BASE_URL}/servicios/${id}`, servicioData);
            const { servicios } = get();
            const serviciosActualizados = servicios.map(servicio => 
                servicio.id === id ? response.data : servicio
            );
            set({ 
                servicios: serviciosActualizados,
                servicioSeleccionado: response.data,
                isLoading: false,
                success: "Servicio actualizado exitosamente"
            });
            return response.data;
        } catch (error) {
            set({ error: error.response?.data || error.message, isLoading: false });
            throw error;
        }
    },

    // Eliminar servicio
    deleteServicio: async (id) => {
        set({ isLoading: true, error: null, success: null });
        try {
            await axios.delete(`${API_BASE_URL}/servicios/${id}`);
            const { servicios } = get();
            const serviciosFiltrados = servicios.filter(servicio => servicio.id !== id);
            set({ 
                servicios: serviciosFiltrados,
                isLoading: false,
                success: "Servicio eliminado exitosamente"
            });
        } catch (error) {
            set({ error: error.response?.data || error.message, isLoading: false });
            throw error;
        }
    },

    // Limpiar mensajes
    clearError: () => set({ error: null }),
    clearSuccess: () => set({ success: null }),
    clearServicioSeleccionado: () => set({ servicioSeleccionado: null })
}));