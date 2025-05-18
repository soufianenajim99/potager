export interface Insect {
    id: number;
    species: string;
    sex: string;
    healthIndex: number;
    mobility: number;
    insecticideResistance: number;
    stepsWithoutFood: number;
    parcelId: number;
}

export interface Plant {
    id: number;
    species: string;
    currentAge: number;
    maturityAge: number;
    isRunner: boolean;
    colonizationProbability: number;
    parcelId: number;
}

export interface TreatmentProgram {
    id: number;
    startTime: number;
    duration: number;
    type: 'WATER' | 'INSECTICIDE' | 'FERTILIZER';
    deviceId: number;
}

export interface TreatmentDevice {
    id: number;
    radius: number;
    programs: TreatmentProgram[];
    parcelId: number;
}

export interface Parcel {
    id: number;
    xCoordinate: number;
    yCoordinate: number;
    humidityLevel: number;
    plants: Plant[];
    insects: Insect[];
    treatmentDevice?: TreatmentDevice;
}

export interface SimulationState {
    currentStep: number;
    isRunning: boolean;
    speedMultiplier: number;
}

