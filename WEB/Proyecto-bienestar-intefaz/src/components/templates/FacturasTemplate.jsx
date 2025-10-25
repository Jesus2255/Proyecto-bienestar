import React, { useEffect, useState } from 'react';
import { useFacturaStore } from '../../hooks/StoreFacturas';
import { useClienteStore } from '../../hooks/StoreClientes';
import { useServicioStore } from '../../hooks/StoreService';
import { toast } from 'sonner';

export const FacturasTemplate = () => {
    const { 
        isLoading, 
        error, 
        success,
        facturas,
        totalMonto,
        createFactura, 
        getFacturas,
        getFacturasByCliente,
        clearError,
        clearSuccess
    } = useFacturaStore();

    const { clientes, getClientes } = useClienteStore();
    const { servicios, getServicios } = useServicioStore();

    const [showForm, setShowForm] = useState(false);
    const [selectedCliente, setSelectedCliente] = useState('');
    const [selectedServicio, setSelectedServicio] = useState('');
    const [mostrarTodas, setMostrarTodas] = useState(true); 
    const [formData, setFormData] = useState({
        clienteId: '',
        servicioId: '',
        total: '',
        fecha: new Date().toISOString().split('T')[0],
        descripcion: ''
    });

    useEffect(() => {
        getClientes();
        getServicios();
        getFacturas(); 
    }, []);

    useEffect(() => {
        if (selectedCliente) {
            getFacturasByCliente(selectedCliente);
            setMostrarTodas(false);
        }
    }, [selectedCliente]);

    useEffect(() => {
        // Calcular total automáticamente cuando se selecciona un servicio
        if (selectedServicio) {
            const servicioSeleccionado = servicios.find(s => s.id === parseInt(selectedServicio));
            if (servicioSeleccionado && servicioSeleccionado.precio) {
                setFormData(prev => ({
                    ...prev,
                    total: servicioSeleccionado.precio.toString(),
                    servicioId: selectedServicio
                }));
            }
        }
    }, [selectedServicio, servicios]);

    useEffect(() => {
        if (error) {
            toast.error(typeof error === 'object' ? JSON.stringify(error) : error);
            clearError();
        }
        if (success) {
            toast.success(success);
            clearSuccess();
        }
    }, [error, success, clearError, clearSuccess]);

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            const facturaData = {
                clienteId: parseInt(formData.clienteId),
                servicioId: parseInt(formData.servicioId),
                total: parseFloat(formData.total),
                fecha: formData.fecha,
                descripcion: formData.descripcion
            };

            console.log('Enviando datos de factura:', facturaData);
            
            await createFactura(facturaData);
            setShowForm(false);
            setFormData({
                clienteId: '',
                servicioId: '',
                total: '',
                fecha: new Date().toISOString().split('T')[0],
                descripcion: ''
            });
            setSelectedServicio('');
            
            // ✅ Recargar las facturas después de crear una nueva
            if (selectedCliente) {
                getFacturasByCliente(selectedCliente);
            } else {
                getFacturas();
            }
        } catch (err) {
            // Error manejado en el store
        }
    };

    const handleMostrarTodas = () => {
        setSelectedCliente('');
        setMostrarTodas(true);
        getFacturas();
    };

    // Calcular total general de todas las facturas
    const totalGeneral = facturas.reduce((sum, factura) => sum + (factura.monto || factura.total || 0), 0);

    return (
        <div className="p-6">
            <div className="flex justify-between items-center mb-6">
                <h1 className="text-2xl font-bold text-gray-800">Gestión de Facturas</h1>
                <button
                    onClick={() => setShowForm(true)}
                    className="bg-purple-600 text-white px-4 py-2 rounded-md hover:bg-purple-700"
                >
                    Nueva Factura
                </button>
            </div>

            {/* Formulario de Factura (igual que antes) */}
            {showForm && (
                <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4">
                    <div className="bg-white rounded-lg p-6 w-full max-w-md">
                        <h2 className="text-xl font-bold mb-4">Nueva Factura</h2>
                        <form onSubmit={handleSubmit} className="space-y-4">
                            <div>
                                <label className="block text-sm font-medium text-gray-700">Cliente</label>
                                <select
                                    required
                                    value={formData.clienteId}
                                    onChange={(e) => setFormData({...formData, clienteId: e.target.value})}
                                    className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                                >
                                    <option value="">Seleccionar cliente</option>
                                    {clientes.map(cliente => (
                                        <option key={cliente.id} value={cliente.id}>
                                            {cliente.nombre}
                                        </option>
                                    ))}
                                </select>
                            </div>
                            
                            <div>
                                <label className="block text-sm font-medium text-gray-700">Servicio</label>
                                <select
                                    required
                                    value={selectedServicio}
                                    onChange={(e) => setSelectedServicio(e.target.value)}
                                    className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                                >
                                    <option value="">Seleccionar servicio</option>
                                    {servicios.map(servicio => (
                                        <option key={servicio.id} value={servicio.id}>
                                            {servicio.nombre} - ${servicio.precio?.toFixed(2)}
                                        </option>
                                    ))}
                                </select>
                            </div>

                            <div className="grid grid-cols-2 gap-4">
                                <div>
                                    <label className="block text-sm font-medium text-gray-700">Total a Pagar ($)</label>
                                    <input
                                        type="number"
                                        step="0.01"
                                        required
                                        value={formData.total}
                                        onChange={(e) => setFormData({...formData, total: e.target.value})}
                                        className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                                        placeholder="0.00"
                                    />
                                </div>
                                <div>
                                    <label className="block text-sm font-medium text-gray-700">Fecha</label>
                                    <input
                                        type="date"
                                        required
                                        value={formData.fecha}
                                        onChange={(e) => setFormData({...formData, fecha: e.target.value})}
                                        className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                                    />
                                </div>
                            </div>
                            
                            <div>
                                <label className="block text-sm font-medium text-gray-700">Descripción</label>
                                <textarea
                                    required
                                    value={formData.descripcion}
                                    onChange={(e) => setFormData({...formData, descripcion: e.target.value})}
                                    className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                                    rows="3"
                                    placeholder="Descripción del servicio facturado..."
                                />
                            </div>

                            {/* Resumen de la factura */}
                            {formData.total && (
                                <div className="bg-gray-50 p-3 rounded-md">
                                    <h4 className="font-medium text-gray-700">Resumen de Factura:</h4>
                                    <p className="text-sm text-gray-600">
                                        <strong>Total:</strong> ${parseFloat(formData.total || 0).toFixed(2)}
                                    </p>
                                </div>
                            )}

                            <div className="flex gap-2 justify-end">
                                <button
                                    type="button"
                                    onClick={() => {
                                        setShowForm(false);
                                        setSelectedServicio('');
                                    }}
                                    className="px-4 py-2 text-gray-600 border border-gray-300 rounded-md hover:bg-gray-50"
                                >
                                    Cancelar
                                </button>
                                <button
                                    type="submit"
                                    disabled={isLoading}
                                    className="px-4 py-2 bg-purple-600 text-white rounded-md hover:bg-purple-700 disabled:opacity-50"
                                >
                                    {isLoading ? 'Creando...' : 'Crear Factura'}
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            )}

            {/* Selector de vista */}
            <div className="bg-white rounded-lg shadow p-4 mb-6">
                <div className="flex flex-col space-y-4">
                    <div className="flex space-x-4">
                        <button
                            onClick={handleMostrarTodas}
                            className={`px-4 py-2 rounded-md ${
                                mostrarTodas 
                                ? 'bg-blue-600 text-white' 
                                : 'bg-gray-200 text-gray-700 hover:bg-gray-300'
                            }`}
                        >
                            Ver Todas las Facturas
                        </button>
                        
                        <div className="flex-1">
                            <label className="block text-sm font-medium text-gray-700 mb-2">
                                Ver facturas por cliente:
                            </label>
                            <select
                                value={selectedCliente}
                                onChange={(e) => {
                                    setSelectedCliente(e.target.value);
                                    if (e.target.value) {
                                        setMostrarTodas(false);
                                    }
                                }}
                                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                            >
                                <option value="">Seleccionar cliente</option>
                                {clientes.map(cliente => (
                                    <option key={cliente.id} value={cliente.id}>
                                        {cliente.nombre}
                                    </option>
                                ))}
                            </select>
                        </div>
                    </div>
                    
                    {/* Resumen general */}
                    <div className="bg-blue-50 p-3 rounded-md">
                        <h3 className="font-semibold text-blue-800">
                            {mostrarTodas ? 'Resumen General' : `Facturas de ${clientes.find(c => c.id == selectedCliente)?.nombre || 'Cliente'}`}
                        </h3>
                        <p className="text-blue-600">
                            Total: <strong>${(mostrarTodas ? totalGeneral : totalMonto).toFixed(2)}</strong> | 
                            Cantidad: <strong>{facturas.length}</strong> facturas
                        </p>
                    </div>
                </div>
            </div>

            {/* Lista de Facturas */}
            <div className="bg-white rounded-lg shadow overflow-hidden">
                {isLoading ? (
                    <div className="p-8 text-center">Cargando facturas...</div>
                ) : (
                    <>
                        <div className="bg-gray-50 px-6 py-4 border-b">
                            <h3 className="text-lg font-semibold">
                                {mostrarTodas ? 'Todas las Facturas' : `Facturas del Cliente`}
                            </h3>
                        </div>
                        <div className="overflow-x-auto">
                            <table className="min-w-full divide-y divide-gray-200">
                                <thead className="bg-gray-50">
                                    <tr>
                                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                                            ID
                                        </th>
                                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                                            Cliente
                                        </th>
                                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                                            Total
                                        </th>
                                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                                            Fecha
                                        </th>
                                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                                            Descripción
                                        </th>
                                    </tr>
                                </thead>
                                <tbody className="bg-white divide-y divide-gray-200">
                                    {facturas.map((factura) => (
                                        <tr key={factura.id} className="hover:bg-gray-50">
                                            <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                                                {factura.id}
                                            </td>
                                            <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                                                {factura.cliente?.nombre || 'N/A'}
                                            </td>
                                            <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                                                <strong>${(factura.monto || factura.total || 0).toFixed(2)}</strong>
                                            </td>
                                            <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                                                {new Date(factura.fecha).toLocaleDateString()}
                                            </td>
                                            <td className="px-6 py-4 text-sm text-gray-500">
                                                {factura.descripcion}
                                            </td>
                                        </tr>
                                    ))}
                                </tbody>
                            </table>
                            {facturas.length === 0 && (
                                <div className="p-8 text-center text-gray-500">
                                    No hay facturas {mostrarTodas ? 'registradas' : 'para este cliente'}
                                </div>
                            )}
                        </div>
                    </>
                )}
            </div>
        </div>
    );
};