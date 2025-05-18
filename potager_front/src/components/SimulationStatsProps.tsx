import React from 'react';
import { Paper, Typography, Box } from '@mui/material';
import type { SimulationState } from '../models/types';

interface SimulationStatsProps {
    simulationState: SimulationState | null;
}

const SimulationStats: React.FC<SimulationStatsProps> = ({ simulationState }) => {
    return (
        <Paper sx={{ p: 2, height: '100%' }}>
            <Typography variant="h6" gutterBottom>Simulation Stats</Typography>
            <Box sx={{ display: 'flex', flexDirection: 'column', gap: 1 }}>
                <Typography>
                    Status: {simulationState?.isRunning ? 'Running' : 'Paused'}
                </Typography>
                <Typography>
                    Current Step: {simulationState?.currentStep || 0}
                </Typography>
                <Typography>
                    Speed: {simulationState?.speedMultiplier || 1}x
                </Typography>
            </Box>
        </Paper>
    );
};

export default SimulationStats;