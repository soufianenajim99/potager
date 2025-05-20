import React, { useState, useEffect } from 'react';
import { getParcels } from '../api/gardenApi';
import type { Parcel } from '../models/types';
const ParcelGrid: React.FC = () => {
    const [parcels, setParcels] = useState<Parcel[]>([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const fetchParcels = async () => {
            try {
                console.log('Fetching parcels...');
                const data = await getParcels();
                console.log('Received parcels:', data);
                setParcels(data);
                setLoading(false);
            } catch (error) {
                console.error('Error fetching parcels:', error);
                setLoading(false);
            }
        };

        fetchParcels();
        const interval = setInterval(fetchParcels, 2000);
        return () => clearInterval(interval);
    }, []);

    // Safely calculate grid dimensions with fallback values
    const getGridDimensions = () => {
        if (parcels.length === 0) return { cols: 1, rows: 1 };

        const validParcels = parcels.filter(p =>
            typeof p.xCoordinate === 'number' &&
            typeof p.yCoordinate === 'number'
        );

        if (validParcels.length === 0) return { cols: 1, rows: 1 };

        const xCoords = validParcels.map(p => p.xCoordinate);
        const yCoords = validParcels.map(p => p.yCoordinate);

        const minX = Math.min(...xCoords);
        const minY = Math.min(...yCoords);
        const maxX = Math.max(...xCoords);
        const maxY = Math.max(...yCoords);

        const cols = maxX - minX + 1;
        const rows = maxY - minY + 1;

        return { cols, rows, minX, minY };
    };

    const { cols, rows, minX, minY } = getGridDimensions();

    const renderGrid = () => {
        try {
            const grid = Array(rows).fill(null).map(() => Array(cols).fill(null));

            parcels.forEach(parcel => {
                if (typeof parcel.xCoordinate === 'number' && typeof parcel.yCoordinate === 'number') {
                    const gridX = parcel.xCoordinate - minX;
                    const gridY = parcel.yCoordinate - minY;
                    if (gridY >= 0 && gridY < rows && gridX >= 0 && gridX < cols) {
                        grid[gridY][gridX] = parcel;
                    }
                }
            });

            return (
                <div className="overflow-auto max-h-[70vh] border border-gray-200 rounded-lg shadow-inner bg-slate-50">
                    <div className="inline-block min-w-full">
                        {grid.map((row, y) => (
                            <div key={`row-${y}`} className="flex">
                                {row.map((parcel, x) => (
                                    <div
                                        key={`cell-${x}-${y}`}
                                        className={`
                                            w-24 h-24 flex flex-col justify-between p-2
                                            border border-gray-200 transition-all duration-200
                                            ${parcel ? getHumidityClass(parcel.humidityLevel) + ' cursor-pointer' : 'bg-gray-50 hover:bg-gray-100'}
                                            relative group
                                        `}
                                        title={parcel ? `Parcel (${x + minX},${y + minY})` : `Empty (${x + minX},${y + minY})`}
                                    >
                                        {/* Coordinate indicator */}
                                        <div className="absolute top-1 left-1 text-xs text-gray-500 font-mono bg-white/80 px-1 rounded">
                                            {x + minX},{y + minY}
                                        </div>

                                        {parcel ? (
                                            <>
                                                <div className="flex justify-end w-full">
                                                    <button className="h-5 w-5 opacity-60 hover:opacity-100 text-gray-500 hover:text-gray-700">
                                                        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className="h-3 w-3">
                                                            <circle cx="11" cy="11" r="8"></circle>
                                                            <path d="m21 21-4.3-4.3"></path>
                                                        </svg>
                                                    </button>
                                                </div>
                                                <div className="flex flex-col gap-1 items-center mt-auto">
                                                    <div className="flex items-center gap-1 text-xs">
                                                        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className="h-3 w-3 text-blue-500">
                                                            <path d="M12 22a7 7 0 0 0 7-7c0-2-1-3.9-3-5.5s-3.5-4-4-6.5c-.5 2.5-2 4.9-4 6.5C6 11.1 5 13 5 15a7 7 0 0 0 7 7z"></path>
                                                        </svg>
                                                        <span>{Math.round(parcel.humidityLevel)}%</span>
                                                    </div>
                                                    <div className="flex items-center gap-1 text-xs">
                                                        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className="h-3 w-3 text-green-600">
                                                            <path d="M7 20h10"></path>
                                                            <path d="M10 20c5.5-2.5.8-6.4 3-10"></path>
                                                            <path d="M9.5 9.4c1.1.8 1.8 2.2 2.3 3.7-2 .4-3.6.4-4.9-.3-1.2-.6-2-1.9-2.4-3.6.7.2 1.5.4 2.4.5"></path>
                                                            <path d="M14.1 6a7 7 0 0 0-1.1 4c1.9-.1 3.3-.6 4.3-1.4 1-1 1.6-2.3 1.7-4.6-1.1.4-2 .9-2.8 1.5"></path>
                                                        </svg>
                                                        <span>{parcel.plants.length}</span>
                                                    </div>
                                                    <div className="flex items-center gap-1 text-xs">
                                                        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className="h-3 w-3 text-red-500">
                                                            <path d="m8 2 1.88 1.88"></path>
                                                            <path d="M14.12 3.88 16 2"></path>
                                                            <path d="M9 7.13v-1a3.003 3.003 0 1 1 6 0v1"></path>
                                                            <path d="M12 20c-3.3 0-6-2.7-6-6v-3a4 4 0 0 1 4-4h4a4 4 0 0 1 4 4v3c0 3.3-2.7 6-6 6"></path>
                                                            <path d="M12 20v-9"></path>
                                                            <path d="M6.53 9C4.6 8.8 3 7.1 3 5"></path>
                                                            <path d="M6 13H2"></path>
                                                            <path d="M3 21c0-2.1 1.7-3.9 3.8-4"></path>
                                                            <path d="M20.97 5c0 2.1-1.6 3.8-3.5 4"></path>
                                                            <path d="M22 13h-4"></path>
                                                            <path d="M17.2 17c2.1.1 3.8 1.9 3.8 4"></path>
                                                        </svg>
                                                        <span>{parcel.insects.length}</span>
                                                    </div>
                                                </div>

                                                {/* Enhanced Tooltip */}
                                                <div className="absolute hidden group-hover:block bg-white p-3 rounded-lg shadow-lg z-10 border border-gray-200 w-56 bottom-full mb-2 left-1/2 transform -translate-x-1/2 transition-opacity duration-150">
                                                    <div className="absolute -bottom-2 left-1/2 transform -translate-x-1/2 w-4 h-4 rotate-45 bg-white border-r border-b border-gray-200"></div>
                                                    <h4 className="font-medium text-sm border-b pb-2 mb-2">Parcel Details</h4>
                                                    <div className="grid grid-cols-2 gap-2 text-xs">
                                                        <div className="text-gray-500">Coordinates:</div>
                                                        <div className="font-medium">({x + minX}, {y + minY})</div>
                                                        <div className="text-gray-500">Humidity:</div>
                                                        <div className="font-medium flex items-center gap-1">
                                                            <span className={`w-2 h-2 rounded-full ${parcel.humidityLevel < 25 ? 'bg-red-500' : parcel.humidityLevel < 50 ? 'bg-yellow-500' : parcel.humidityLevel < 75 ? 'bg-blue-500' : 'bg-emerald-500'}`}></span>
                                                            {Math.round(parcel.humidityLevel)}%
                                                        </div>
                                                        <div className="text-gray-500">Plants:</div>
                                                        <div className="font-medium">{parcel.plants.length} {parcel.plants.length === 1 ? 'plant' : 'plants'}</div>
                                                        <div className="text-gray-500">Insects:</div>
                                                        <div className="font-medium">{parcel.insects.length} {parcel.insects.length === 1 ? 'insect' : 'insects'}</div>
                                                    </div>
                                                </div>
                                            </>
                                        ) : (
                                            <span className="m-auto text-gray-300 text-xs italic">Empty</span>
                                        )}
                                    </div>
                                ))}
                            </div>
                        ))}
                    </div>
                </div>
            );
        } catch (error) {
            console.error('Error rendering grid:', error);
            return (
                <div className="text-red-500 p-4 bg-red-50 rounded border border-red-200">
                    <p>Error rendering grid. Check console for details.</p>
                </div>
            );
        }
    };

    return (
        <div className="bg-white rounded-lg shadow-lg">
            <div className="flex justify-between items-center p-4 border-b border-gray-200">
                <h2 className="text-xl font-bold text-gray-800">Garden Parcel Grid</h2>
                <div className="flex items-center space-x-3">
                    <button
                        onClick={handleManualRefresh}
                        className="inline-flex items-center px-3 py-1.5 text-sm border border-gray-200 rounded-md hover:bg-gray-50 transition-colors"
                        disabled={refreshing}
                    >
                        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className={`h-3 w-3 mr-1 ${refreshing ? 'animate-spin' : ''}`}>
                            <path d="M21 2v6h-6"></path>
                            <path d="M3 12a9 9 0 0 1 15-6.7L21 8"></path>
                            <path d="M3 22v-6h6"></path>
                            <path d="M21 12a9 9 0 0 1-15 6.7L3 16"></path>
                        </svg>
                        Refresh
                    </button>
                    <span className="inline-flex items-center px-3 py-1 text-xs font-medium bg-gray-100 text-gray-600 rounded-full">
                        Grid: {cols} × {rows}
                    </span>
                    <span className="inline-flex items-center px-3 py-1 text-xs font-medium bg-gray-100 text-gray-600 rounded-full">
                        Parcels: {parcels.length}
                    </span>
                </div>
            </div>
            <div className="p-4">
                {loading ? (
                    <div className="flex flex-col items-center justify-center py-20">
                        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-green-500 mb-4"></div>
                        <p className="text-gray-500">Loading garden parcels...</p>
                    </div>
                ) : (
                    renderGrid()
                )}
            </div>
        </div>
    );
};

export default ParcelGrid;