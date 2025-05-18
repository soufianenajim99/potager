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
        const interval = setInterval(fetchParcels, 5000);
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
                <div className="overflow-auto max-h-[70vh] border border-gray-200 rounded-lg shadow-inner">
                    <div className="inline-block min-w-full">
                        {grid.map((row, y) => (
                            <div key={`row-${y}`} className="flex">
                                {row.map((parcel, x) => (
                                    <div
                                        key={`cell-${x}-${y}`}
                                        className={`
                                            w-20 h-20 flex flex-col items-center justify-center 
                                            text-xs border border-gray-200 transition-all
                                            ${parcel ?
                                            'bg-green-50 hover:bg-green-100 cursor-pointer' :
                                            'bg-gray-50 hover:bg-gray-100'}
                                            relative group
                                        `}
                                        title={parcel ? `Parcel (${x + minX},${y + minY})` : `Empty (${x + minX},${y + minY})`}
                                    >
                                        {/* Coordinate indicator */}
                                        <span className="absolute top-1 left-1 text-[0.6rem] text-gray-400">
                                            {x + minX},{y + minY}
                                        </span>

                                        {parcel ? (
                                            <>
                                                <div className="flex items-center gap-1">
                                                    <span className="text-blue-500">💧</span>
                                                    <span>{Math.round(parcel.humidityLevel)}%</span>
                                                </div>
                                                <div className="flex items-center gap-1">
                                                    <span className="text-green-600">🌱</span>
                                                    <span>{parcel.plants.length}</span>
                                                </div>
                                                <div className="flex items-center gap-1">
                                                    <span className="text-red-500">🐛</span>
                                                    <span>{parcel.insects.length}</span>
                                                </div>

                                                {/* Tooltip-like expanded info on hover */}
                                                <div className="absolute hidden group-hover:block bg-white p-2 rounded shadow-lg z-10 border border-gray-200 w-48 bottom-full mb-2">
                                                    <h4 className="font-medium text-sm">Parcel Details</h4>
                                                    <div className="grid grid-cols-2 gap-1 text-xs mt-1">
                                                        <span>Coordinates:</span>
                                                        <span>({x + minX}, {y + minY})</span>
                                                        <span>Humidity:</span>
                                                        <span>{Math.round(parcel.humidityLevel)}%</span>
                                                        <span>Plants:</span>
                                                        <span>{parcel.plants.length} {parcel.plants.length === 1 ? 'plant' : 'plants'}</span>
                                                        <span>Insects:</span>
                                                        <span>{parcel.insects.length} {parcel.insects.length === 1 ? 'insect' : 'insects'}</span>
                                                    </div>
                                                </div>
                                            </>
                                        ) : (
                                            <span className="text-gray-300">Empty</span>
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
                    Error rendering grid. Check console for details.
                </div>
            );
        }
    };

    return (
        <div className="p-6 bg-white rounded-lg shadow-lg">
            <div className="flex justify-between items-center mb-6">
                <h2 className="text-2xl font-bold text-gray-800">Garden Parcel Grid</h2>
                <div className="text-sm text-gray-500 bg-gray-100 px-3 py-1 rounded-full">
                    <span>Grid: {cols} × {rows} | </span>
                    <span>Parcels: {parcels.length}</span>
                </div>
            </div>

            {loading ? (
                <div className="flex flex-col items-center justify-center py-12">
                    <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-green-500 mb-4"></div>
                    <p className="text-gray-500">Loading garden parcels...</p>
                </div>
            ) : (
                renderGrid()
            )}
        </div>
    );
};

export default ParcelGrid;