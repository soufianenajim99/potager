import React, { useState } from 'react';
import {
    startSimulation,
    pauseSimulation,
    resetSimulation,
    executeSingleStep,
    setSimulationSpeed
} from '../api/gardenApi';
import {
    PlayIcon,
    PauseIcon,
    ArrowPathIcon,
    ForwardIcon
} from '@heroicons/react/24/solid';

interface SimulationControlProps {
    simulationState: {
        currentStep: number;
        isRunning: boolean;
        speedMultiplier: number;
    } | null;
    refreshStatus: () => void;
}

const SimulationControl: React.FC<SimulationControlProps> = ({
                                                                 simulationState,
                                                                 refreshStatus
                                                             }) => {
    const [speed, setSpeed] = useState(1);

    const handleSpeedChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const newSpeed = parseFloat(e.target.value);
        setSpeed(newSpeed);
        setSimulationSpeed(newSpeed).then(refreshStatus);
    };

    const controlButtons = [
        {
            label: 'Start',
            icon: <PlayIcon className="h-4 w-4" />,
            action: () => startSimulation().then(refreshStatus),
            disabled: simulationState?.isRunning,
            style: 'bg-blue-600 hover:bg-blue-700 text-white'
        },
        {
            label: 'Pause',
            icon: <PauseIcon className="h-4 w-4" />,
            action: () => pauseSimulation().then(refreshStatus),
            disabled: !simulationState?.isRunning,
            style: 'bg-red-600 hover:bg-red-700 text-white'
        },
        {
            label: 'Reset',
            icon: <ArrowPathIcon className="h-4 w-4" />,
            action: () => resetSimulation().then(refreshStatus),
            style: 'border border-gray-300 hover:bg-gray-100'
        },
        {
            label: 'Step',
            icon: <ForwardIcon className="h-4 w-4" />,
            action: () => executeSingleStep().then(refreshStatus),
            disabled: simulationState?.isRunning,
            style: 'border border-gray-300 hover:bg-gray-100'
        }
    ];

    return (
        <div className="bg-white p-4 rounded-lg shadow">
            <h2 className="text-xl font-semibold mb-4">Simulation Control</h2>

            <div className="flex flex-wrap items-center gap-3 mb-4">
                {controlButtons.map((button, index) => (
                    <button
                        key={index}
                        className={`flex items-center px-3 py-1.5 rounded-md text-sm ${button.style} ${
                            button.disabled ? 'opacity-50 cursor-not-allowed' : ''
                        }`}
                        onClick={button.action}
                        disabled={button.disabled}
                    >
                        <span className="mr-1">{button.icon}</span>
                        {button.label}
                    </button>
                ))}

                <div className="flex-1 min-w-[200px]">
                    <label className="block text-sm font-medium text-gray-700 mb-1">Speed: {speed.toFixed(1)}x</label>
                    <input
                        type="range"
                        min="0.1"
                        max="10"
                        step="0.1"
                        value={speed}
                        onChange={handleSpeedChange}
                        className="w-full h-2 bg-gray-200 rounded-lg appearance-none cursor-pointer"
                    />
                </div>
            </div>

            {simulationState && (
                <div className="text-sm text-gray-700 space-x-4">
                    <span>Step: {simulationState.currentStep}</span>
                    <span>Status: {simulationState.isRunning ? 'Running' : 'Paused'}</span>
                    <span>Speed: {simulationState.speedMultiplier}x</span>
                </div>
            )}
        </div>
    );
};

export default SimulationControl;