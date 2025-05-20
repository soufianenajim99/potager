import React, { useState, useEffect } from 'react';
import ParcelGrid from '../components/ParcelGrid';
import { getSimulationStatus } from '../api/gardenApi';
import SimulationControl from "./SimulationControl";
import GardenSetupPanel from "../components/GardenSetupPanel";

const Dashboard: React.FC = () => {
    const [simulationState, setSimulationState] = useState<{
        currentStep: number;
        isRunning: boolean;
        speedMultiplier: number;
    } | null>(null);
    const [refreshTrigger, setRefreshTrigger] = useState(0);

    const refreshStatus = async () => {
        try {
            const status = await getSimulationStatus();
            setSimulationState(status);
        } catch (error) {
            console.error('Error fetching simulation status:', error);
        }
    };

    const handleSetupSuccess = () => {
        setRefreshTrigger(prev => prev + 1); // Trigger refresh of other components
    };

    useEffect(() => {
        refreshStatus();
        const interval = setInterval(refreshStatus, 1000);
        return () => clearInterval(interval);
    }, [refreshTrigger]); // Add refreshTrigger as dependency

    return (
        <div className="container mx-auto p-4">
            <h1 className="text-2xl font-bold mb-6">Automated Garden Simulation</h1>

            <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
                <div className="lg:col-span-2 space-y-4">
                    {/*<GardenSetupPanel onSuccess={handleSetupSuccess} />*/}
                    <ParcelGrid key={refreshTrigger} />
                </div>

                <div className="lg:col-span-1">
                    <SimulationControl
                        simulationState={simulationState}
                        refreshStatus={refreshStatus}
                    />
                </div>
            </div>
        </div>
    );
};

export default Dashboard;