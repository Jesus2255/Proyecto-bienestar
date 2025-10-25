import React, { useEffect, useState } from 'react';
import { useCitaStore } from '../../hooks/StoreCitas';
import { useClienteStore } from '../../hooks/StoreClientes';
import { useServicioStore } from '../../hooks/StoreService';
import { toast } from 'sonner';

export const CitasTemplate = () => {
    const { 
        citas,
        isLoading, 
        error, 
        success,
        getCitas,
        createCita, 
        updateCita, 
        deleteCita,
        clearError,
        clearSuccess 
    } = useCitaStore();

    const { clientes, getClientes } = useClienteStore();
    const { servicios, getServicios } = useServicioStore();

    const [showForm, setShowForm] = useState(false);
    const [editingCita, setEditingCita] = useState(null);
    const [formData, setFormData] = useState({
        clienteId: '',
        servicioId: '',
        fecha: '',
        hora: '',
        notas: ''
    });

    useEffect(() => {
        getClientes();
        getServicios();
        getCitas();
    }, []);

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
            // Combinar fecha y hora en un solo campo fechaHora
            const fechaHora = `${formData.fecha}T${formData.hora}:00`;
            
            const citaData = {
                clienteId: parseInt(formData.clienteId),
                servicioId: parseInt(formData.servicioId),
                fechaHora: fechaHora,
                notas: formData.notas
            };

            console.log('Enviando datos de cita:', citaData);

            if (editingCita) {
                await updateCita(editingCita.id, citaData);
            } else {
                await createCita(citaData);
            }
            setShowForm(false);
            setEditingCita(null);
            setFormData({ clienteId: '', servicioId: '', fecha: '', hora: '', notas: '' });
        } catch (err) {
            // Error manejado en el store
        }
    };

    const handleEdit = (cita) => {
        if (cita.fechaHora) {
            const fechaHora = new Date(cita.fechaHora);
            const fecha = fechaHora.toISOString().split('T')[0];
            const hora = fechaHora.toTimeString().slice(0, 5);
            
            setEditingCita(cita);
            setFormData({
                clienteId: cita.cliente?.id || '',
                servicioId: cita.servicio?.id || '',
                fecha: fecha,
                hora: hora,
                notas: cita.notas || ''
            });
            setShowForm(true);
        }
    };

    const handleDelete = async (id) => {
        if (window.confirm('¿Estás seguro de eliminar esta cita?')) {
            await deleteCita(id);
        }
    };

    // Función para formatear la fecha y hora
    const formatFechaHora = (fechaHora) => {
        if (!fechaHora) return '';
        const date = new Date(fechaHora);
        return date.toLocaleString('es-ES', {
            year: 'numeric',
            month: '2-digit',
            day: '2-digit',
            hour: '2-digit',
            minute: '2-digit'
        });
    };

    return (
        <div className="p-6">
            <div className="flex justify-between items-center mb-6">
                <h1 className="text-2xl font-bold text-gray-800">Gestión de Citas</h1>
                <button
                    onClick={() => {
                        setShowForm(true);
                        setEditingCita(null);
                        setFormData({ clienteId: '', servicioId: '', fecha: '', hora: '', notas: '' });
                    }}
                    className="bg-green-600 text-white px-4 py-2 rounded-md hover:bg-green-700"
                >
                    Nueva Cita
                </button>
            </div>

            {/* Formulario de Cita */}
            {showForm && (
                <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4">
                    <div className="bg-white rounded-lg p-6 w-full max-w-md">
                        <h2 className="text-xl font-bold mb-4">
                            {editingCita ? 'Editar Cita' : 'Nueva Cita'}
                        </h2>
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
                                    value={formData.servicioId}
                                    onChange={(e) => setFormData({...formData, servicioId: e.target.value})}
                                    className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                                >
                                    <option value="">Seleccionar servicio</option>
                                    {servicios.map(servicio => (
                                        <option key={servicio.id} value={servicio.id}>
                                            {servicio.nombre}
                                        </option>
                                    ))}
                                </select>
                            </div>
                            <div className="grid grid-cols-2 gap-4">
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
                                <div>
                                    <label className="block text-sm font-medium text-gray-700">Hora</label>
                                    <input
                                        type="time"
                                        required
                                        value={formData.hora}
                                        onChange={(e) => setFormData({...formData, hora: e.target.value})}
                                        className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                                    />
                                </div>
                            </div>
                            <div>
                                <label className="block text-sm font-medium text-gray-700">Notas</label>
                                <textarea
                                    value={formData.notas}
                                    onChange={(e) => setFormData({...formData, notas: e.target.value})}
                                    className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                                    rows="3"
                                />
                            </div>
                            <div className="flex gap-2 justify-end">
                                <button
                                    type="button"
                                    onClick={() => setShowForm(false)}
                                    className="px-4 py-2 text-gray-600 border border-gray-300 rounded-md hover:bg-gray-50"
                                >
                                    Cancelar
                                </button>
                                <button
                                    type="submit"
                                    disabled={isLoading}
                                    className="px-4 py-2 bg-green-600 text-white rounded-md hover:bg-green-700 disabled:opacity-50"
                                >
                                    {isLoading ? 'Guardando...' : (editingCita ? 'Actualizar' : 'Crear Cita')}
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            )}

            {/* Lista de Citas */}
            <div className="bg-white rounded-lg shadow overflow-hidden">
                {isLoading ? (
                    <div className="p-8 text-center">Cargando citas...</div>
                ) : (
                    <div className="overflow-x-auto">
                        <table className="min-w-full divide-y divide-gray-200">
                            <thead className="bg-gray-50">
                                <tr>
                                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                        Cliente
                                    </th>
                                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                        Servicio
                                    </th>
                                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                        Fecha y Hora
                                    </th>
                                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                        Notas
                                    </th>
                                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                        Acciones
                                    </th>
                                </tr>
                            </thead>
                            <tbody className="bg-white divide-y divide-gray-200">
                                {citas.map((cita) => (
                                    <tr key={cita.id} className="hover:bg-gray-50">
                                        <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                                            {cita.cliente?.nombre || 'N/A'}
                                        </td>
                                        <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                                            {cita.servicio?.nombre || 'N/A'}
                                        </td>
                                        <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                                            {formatFechaHora(cita.fechaHora)}
                                        </td>
                                        <td className="px-6 py-4 text-sm text-gray-500">
                                            {cita.notas || 'Sin notas'}
                                        </td>
                                        <td className="px-6 py-4 whitespace-nowrap text-sm font-medium space-x-2">
                                            <button
                                                onClick={() => handleEdit(cita)}
                                                className="text-blue-600 hover:text-blue-900"
                                            >
                                                Editar
                                            </button>
                                            <button
                                                onClick={() => handleDelete(cita.id)}
                                                className="text-red-600 hover:text-red-900"
                                            >
                                                Eliminar
                                            </button>
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                        {citas.length === 0 && (
                            <div className="p-8 text-center text-gray-500">
                                No hay citas programadas
                            </div>
                        )}
                    </div>
                )}
            </div>
        </div>
    );
};