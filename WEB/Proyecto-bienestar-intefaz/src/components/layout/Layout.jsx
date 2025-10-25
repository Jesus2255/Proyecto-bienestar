import React from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { useAuthStore } from '../../hooks/StoreLogin';

export const Layout = ({ children }) => {
    const location = useLocation();
    const navigate = useNavigate();
    const { user, logout, isAuthenticated } = useAuthStore();

    const navigation = [
        { name: 'Clientes', href: '/clientes', current: location.pathname === '/clientes' },
        { name: 'Citas', href: '/citas', current: location.pathname === '/citas' },
        { name: 'Facturas', href: '/facturas', current: location.pathname === '/facturas' },
        { name: 'Servicios', href: '/servicios', current: location.pathname === '/servicios' },
    ];

    const handleLogout = async () => {
        await logout();
        navigate('/login');
    };

    return (
        <div className="min-h-screen bg-gray-100">
            {/* Navigation */}
            <nav className="bg-white shadow-sm border-b">
                <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
                    <div className="flex justify-between h-16">
                        <div className="flex">
                            <div className="shrink-0 flex items-center">
                                <h1 className="text-xl font-bold text-gray-800">
                                    Sistema Bienestar
                                </h1>
                            </div>
                            {isAuthenticated && (
                                <div className="hidden sm:ml-6 sm:flex sm:space-x-8">
                                    {navigation.map((item) => (
                                        <Link
                                            key={item.name}
                                            to={item.href}
                                            className={`${
                                                item.current
                                                    ? 'border-blue-500 text-gray-900'
                                                    : 'border-transparent text-gray-500 hover:border-gray-300 hover:text-gray-700'
                                            } inline-flex items-center px-1 pt-1 border-b-2 text-sm font-medium`}
                                        >
                                            {item.name}
                                        </Link>
                                    ))}
                                </div>
                            )}
                        </div>
                        {isAuthenticated && user && (
                            <div className="flex items-center space-x-4">
                                <span className="text-sm text-gray-700">
                                    Hola, {user.username}
                                </span>
                                <button
                                    onClick={handleLogout}
                                    className="text-sm text-red-600 hover:text-red-800"
                                >
                                    Cerrar sesión
                                </button>
                            </div>
                        )}
                    </div>
                </div>
            </nav>

            {/* Main content */}
            <main>
                {children}
            </main>
        </div>
    );
};

