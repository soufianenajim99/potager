
import type { Parcel, SimulationState } from '../models/types';
import apiClient from './axiosConfig';


const transformParcel = (data: any): Parcel => ({
    id: data.id,
    xCoordinate: data.xcoordinate || data.xCoordinate, // Handles both cases
    yCoordinate: data.ycoordinate || data.yCoordinate, // Handles both cases
    humidityLevel: data.humidityLevel,
    plants: data.plants || [],
    insects: data.insects || [],
    treatmentDevice: data.treatmentDevice
});

export const getParcels = async (): Promise<Parcel[]> => {
    try {
        const response = await apiClient.get('/parcels');
        return response.data.map(transformParcel);
    } catch (error) {
        console.error('Error fetching parcels:', error);
        throw error;
    }
};

export const startSimulation = async (): Promise<void> => {
    await apiClient.post('/simulation/start');
};

export const pauseSimulation = async (): Promise<void> => {
    await apiClient.post('/simulation/pause');
};

export const resetSimulation = async (): Promise<void> => {
    await apiClient.post('/simulation/reset');
};

export const executeSingleStep = async (): Promise<void> => {
    await apiClient.post('/simulation/step');
};

export const setSimulationSpeed = async (speed: number): Promise<void> => {
    await apiClient.put('/simulation/speed', null, {
        params: { speedMultiplier: speed }
    });
};

export const getSimulationStatus = async (): Promise<SimulationState> => {
    const response = await apiClient.get('/simulation/status');
    return response.data;
};