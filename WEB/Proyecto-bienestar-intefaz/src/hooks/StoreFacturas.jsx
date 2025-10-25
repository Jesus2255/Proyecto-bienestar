import { create } from "zustand";
import axios from "axios";

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080/api";

export const useFacturaStore = create((set, get) => ({
    // Estados
    isLoading: false,
    isFacturaCreated: false,
    facturaData: null,
    facturas: [],
    facturasFiltradas: [],
    totalMonto: 0,
    error: null,
    success: null,

    // Setters
    setFacturaCreated: (value) => set({ isFacturaCreated: value }),
    setFacturaData: (data) => set({ facturaData: data }),

    // Obtener todas las facturas
    getFacturas: async () => {
        set({ isLoading: true, error: null, success: null });
        try {
            const response = await axios.get(`${API_BASE_URL}/facturas`);
            set({ facturas: response.data, isLoading: false });
            return response.data;
        } catch (error) {
            const errorMessage = error.response?.data?.message || error.message || "Error al cargar facturas";
            set({ error: errorMessage, isLoading: false });
            throw error;
        }
    },

    // Crear factura
    createFactura: async (facturaInfo) => {
        set({ isLoading: true, error: null, success: null });
        try {
            const response = await axios.post(`${API_BASE_URL}/facturas`, facturaInfo);
            const { facturas } = get();
            set({ 
                facturaData: response.data, 
                facturas: [...facturas, response.data],
                isFacturaCreated: true,
                isLoading: false,
                success: "Factura creada exitosamente"
            });
            return response.data;
        } catch (error) {
            set({ 
                error: error.response?.data || error.message, 
                isLoading: false 
            });
            throw error;
        }
    },

    // Obtener facturas por cliente
    getFacturasByCliente: async (clienteId) => {
        set({ isLoading: true, error: null, success: null });
        try {
            const response = await axios.get(`${API_BASE_URL}/facturas/cliente/${clienteId}`);
            const facturas = response.data;
            const totalMonto = facturas.reduce((sum, factura) => sum + (factura.monto || 0), 0);
            
            set({ 
                facturas,
                facturasFiltradas: facturas,
                totalMonto,
                isLoading: false 
            });
            return facturas;
        } catch (error) {
            set({ 
                error: error.response?.data || error.message, 
                isLoading: false 
            });
            throw error;
        }
    },

    // Limpiar estados
    clearError: () => set({ error: null }),
    clearSuccess: () => set({ success: null }),
    clearFacturaData: () => set({ facturaData: null, isFacturaCreated: false })
}));