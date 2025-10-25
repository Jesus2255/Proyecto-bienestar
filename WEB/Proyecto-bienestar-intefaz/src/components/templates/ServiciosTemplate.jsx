import React, { useEffect, useState } from 'react';
import { useServicioStore } from '../../hooks/StoreService';
import { toast } from 'sonner';

export const ServiciosTemplate = () => {
    const { 
        servicios, 
        isLoading, 
        error, 
        success,
        getServicios, 
        createServicio, 
        updateServicio, 
        deleteServicio,
        clearError,
        clearSuccess
    } = useServicioStore();

    const [showForm, setShowForm] = useState(false);
    const [editingServicio, setEditingServicio] = useState(null);
    const [formData, setFormData] = useState({
        nombre: '',
        descripcion: '',
        precio: '',
        duracion: ''
    });

    useEffect(() => {
        getServicios();
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
    }, [error, success]);

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            const servicioData = {
                ...formData,
                precio: parseFloat(formData.precio),
                duracion: parseInt(formData.duracion)
            };

            if (editingServicio) {
                await updateServicio(editingServicio.id, servicioData);
            } else {
                await createServicio(servicioData);
            }
            setShowForm(false);
            setEditingServicio(null);
            setFormData({ nombre: '', descripcion: '', precio: '', duracion: '' });
        } catch (err) {
            // Error manejado en el store
        }
    };

    const handleEdit = (servicio) => {
        setEditingServicio(servicio);
        setFormData({
            nombre: servicio.nombre || '',
            descripcion: servicio.descripcion || '',
            precio: servicio.precio || '',
            duracion: servicio.duracion || ''
        });
        setShowForm(true);
    };

    const handleDelete = async (id) => {
        if (window.confirm('¿Estás seguro de eliminar este servicio?')) {
            await deleteServicio(id);
        }
    };

    return (
        <div className="p-6">
            <div className="flex justify-between items-center mb-6">
                <h1 className="text-2xl font-bold text-gray-800">Gestión de Servicios</h1>
                <button
                    onClick={() => {
                        setShowForm(true);
                        setEditingServicio(null);
                        setFormData({ nombre: '', descripcion: '', precio: '', duracion: '' });
                    }}
                    className="bg-indigo-600 text-white px-4 py-2 rounded-md hover:bg-indigo-700"
                >
                    Nuevo Servicio
                </button>
            </div>

            {/* Formulario */}
            {showForm && (
                <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4">
                    <div className="bg-white rounded-lg p-6 w-full max-w-md">
                        <h2 className="text-xl font-bold mb-4">
                            {editingServicio ? 'Editar Servicio' : 'Nuevo Servicio'}
                        </h2>
                        <form onSubmit={handleSubmit} className="space-y-4">
                            <div>
                                <label className="block text-sm font-medium text-gray-700">Nombre</label>
                                <input
                                    type="text"
                                    required
                                    value={formData.nombre}
                                    onChange={(e) => setFormData({...formData, nombre: e.target.value})}
                                    className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-500"
                                />
                            </div>
                            <div>
                                <label className="block text-sm font-medium text-gray-700">Descripción</label>
                                <textarea
                                    value={formData.descripcion}
                                    onChange={(e) => setFormData({...formData, descripcion: e.target.value})}
                                    className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-500"
                                    rows="3"
                                />
                            </div>
                            <div className="grid grid-cols-2 gap-4">
                                <div>
                                    <label className="block text-sm font-medium text-gray-700">Precio ($)</label>
                                    <input
                                        type="number"
                                        step="0.01"
                                        required
                                        value={formData.precio}
                                        onChange={(e) => setFormData({...formData, precio: e.target.value})}
                                        className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-500"
                                    />
                                </div>
                                <div>
                                    <label className="block text-sm font-medium text-gray-700">Duración (min)</label>
                                    <input
                                        type="number"
                                        required
                                        value={formData.duracion}
                                        onChange={(e) => setFormData({...formData, duracion: e.target.value})}
                                        className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-500"
                                    />
                                </div>
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
                                    className="px-4 py-2 bg-indigo-600 text-white rounded-md hover:bg-indigo-700 disabled:opacity-50"
                                >
                                    {isLoading ? 'Guardando...' : (editingServicio ? 'Actualizar' : 'Crear')}
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            )}

            {/* Lista de Servicios */}
            <div className="bg-white rounded-lg shadow overflow-hidden">
                {isLoading ? (
                    <div className="p-8 text-center">Cargando servicios...</div>
                ) : (
                    <div className="overflow-x-auto">
                        <table className="min-w-full divide-y divide-gray-200">
                            <thead className="bg-gray-50">
                                <tr>
                                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                        Nombre
                                    </th>
                                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                        Descripción
                                    </th>
                                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                        Precio
                                    </th>
                                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                        Duración
                                    </th>
                                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                        Acciones
                                    </th>
                                </tr>
                            </thead>
                            <tbody className="bg-white divide-y divide-gray-200">
                                {servicios.map((servicio) => (
                                    <tr key={servicio.id} className="hover:bg-gray-50">
                                        <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                                            {servicio.nombre}
                                        </td>
                                        <td className="px-6 py-4 text-sm text-gray-500">
                                            {servicio.descripcion}
                                        </td>
                                        <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                                            ${servicio.precio?.toFixed(2)}
                                        </td>
                                        <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                                            {servicio.duracion} min
                                        </td>
                                        <td className="px-6 py-4 whitespace-nowrap text-sm font-medium space-x-2">
                                            <button
                                                onClick={() => handleEdit(servicio)}
                                                className="text-indigo-600 hover:text-indigo-900"
                                            >
                                                Editar
                                            </button>
                                            <button
                                                onClick={() => handleDelete(servicio.id)}
                                                className="text-red-600 hover:text-red-900"
                                            >
                                                Eliminar
                                            </button>
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                        {servicios.length === 0 && (
                            <div className="p-8 text-center text-gray-500">
                                No hay servicios registrados
                            </div>
                        )}
                    </div>
                )}
            </div>
        </div>
    );
};