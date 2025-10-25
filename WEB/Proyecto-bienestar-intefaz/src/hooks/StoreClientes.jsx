import { create } from "zustand";
import axios from './config/axiosConfig';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080/api";

export const useClienteStore = create((set, get) => ({
    // Estados
    isLoading: false,
    clientes: [],
    clienteSeleccionado: null,
    error: null,
    success: null,

    // Obtener todos los clientes
    getClientes: async () => {
        set({ isLoading: true, error: null });
        try {
            const response = await axios.get(`${API_BASE_URL}/clientes`);
            set({ clientes: response.data, isLoading: false });
            return response.data;
        } catch (error) {
            set({ error: error.response?.data || error.message, isLoading: false });
            throw error;
        }
    },

    // Obtener cliente por ID
    getClienteById: async (id) => {
        set({ isLoading: true, error: null });
        try {
            const response = await axios.get(`${API_BASE_URL}/clientes/${id}`);
            set({ clienteSeleccionado: response.data, isLoading: false });
            return response.data;
        } catch (error) {
            set({ error: error.response?.data || error.message, isLoading: false });
            throw error;
        }
    },

    // Crear cliente
    createCliente: async (clienteData) => {
        set({ isLoading: true, error: null, success: null });
        try {
            const response = await axios.post(`${API_BASE_URL}/clientes`, clienteData);
            const { clientes } = get();
            set({ 
                clientes: [...clientes, response.data],
                isLoading: false,
                success: "Cliente creado exitosamente"
            });
            return response.data;
        } catch (error) {
            set({ error: error.response?.data || error.message, isLoading: false });
            throw error;
        }
    },

    // Actualizar cliente
    updateCliente: async (id, clienteData) => {
        set({ isLoading: true, error: null, success: null });
        try {
            const response = await axios.put(`${API_BASE_URL}/clientes/${id}`, clienteData);
            const { clientes } = get();
            const clientesActualizados = clientes.map(cliente => 
                cliente.id === id ? response.data : cliente
            );
            set({ 
                clientes: clientesActualizados,
                clienteSeleccionado: response.data,
                isLoading: false,
                success: "Cliente actualizado exitosamente"
            });
            return response.data;
        } catch (error) {
            set({ error: error.response?.data || error.message, isLoading: false });
            throw error;
        }
    },

    // Eliminar cliente
    deleteCliente: async (id) => {
        set({ isLoading: true, error: null, success: null });
        try {
            await axios.delete(`${API_BASE_URL}/clientes/${id}`);
            const { clientes } = get();
            const clientesFiltrados = clientes.filter(cliente => cliente.id !== id);
            set({ 
                clientes: clientesFiltrados,
                isLoading: false,
                success: "Cliente eliminado exitosamente"
            });
        } catch (error) {
            set({ error: error.response?.data || error.message, isLoading: false });
            throw error;
        }
    },

    // Limpiar mensajes
    clearError: () => set({ error: null }),
    clearSuccess: () => set({ success: null }),
    clearClienteSeleccionado: () => set({ clienteSeleccionado: null })
}));