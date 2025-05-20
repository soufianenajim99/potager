import React, { useState } from 'react';
import { Button, Card, Form, Spinner, Alert, ListGroup } from 'react-bootstrap';
import { uploadGardenXmlFile, loadPresetGarden, getAvailablePresets } from '../api/gardenApi';

interface GardenPreset {
    name: string;
    description: string;
}

const GardenSetupPanel: React.FC<{ onSuccess?: () => void }> = ({ onSuccess }) => {
    const [selectedFile, setSelectedFile] = useState<File | null>(null);
    const [isLoading, setIsLoading] = useState(false);
    const [message, setMessage] = useState<{ text: string; variant: string }>({ text: '', variant: 'success' });
    const [presets, setPresets] = useState<GardenPreset[]>([]);
    const [showPresets, setShowPresets] = useState(false);

    const handleFileChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        if (event.target.files && event.target.files[0]) {
            setSelectedFile(event.target.files[0]);
        }
    };

    const handleUpload = async () => {
        if (!selectedFile) {
            setMessage({ text: 'Please select a file first', variant: 'danger' });
            return;
        }

        setIsLoading(true);
        setMessage({ text: '', variant: 'success' });

        try {
            await uploadGardenXmlFile(selectedFile);
            setMessage({ text: 'Garden configuration loaded successfully', variant: 'success' });
            if (onSuccess) onSuccess();
        } catch (error) {
            setMessage({
                text: error instanceof Error ? error.message : 'Failed to upload file',
                variant: 'danger',
            });
        } finally {
            setIsLoading(false);
        }
    };

    const fetchPresets = async () => {
        if (presets.length > 0) {
            setShowPresets(!showPresets);
            return;
        }

        setIsLoading(true);
        try {
            const data = await getAvailablePresets();
            setPresets(data);
            setShowPresets(true);
        } catch (error) {
            setMessage({
                text: 'Failed to fetch presets',
                variant: 'danger',
            });
        } finally {
            setIsLoading(false);
        }
    };

    const loadPreset = async (presetName: string) => {
        setIsLoading(true);
        try {
            await loadPresetGarden(presetName);
            setMessage({ text: `Preset "${presetName}" loaded successfully`, variant: 'success' });
            setShowPresets(false);
            if (onSuccess) onSuccess();
        } catch (error) {
            setMessage({
                text: error instanceof Error ? error.message : 'Failed to load preset',
                variant: 'danger',
            });
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <Card className="mb-4">
            <Card.Header>Garden Setup</Card.Header>
            <Card.Body>
                {message.text && (
                    <Alert variant={message.variant} onClose={() => setMessage({ text: '', variant: 'success' })} dismissible>
                        {message.text}
                    </Alert>
                )}

                <Form>
                    <Form.Group controlId="formFile" className="mb-3">
                        <Form.Label>Upload Garden XML Configuration</Form.Label>
                        <Form.Control type="file" accept=".xml" onChange={handleFileChange} />
                    </Form.Group>

                    <div className="d-flex gap-2 mb-3">
                        <Button
                            variant="primary"
                            onClick={handleUpload}
                            disabled={isLoading || !selectedFile}
                        >
                            {isLoading ? (
                                <>
                                    <Spinner as="span" animation="border" size="sm" role="status" aria-hidden="true" />
                                    <span className="ms-2">Loading...</span>
                                </>
                            ) : (
                                'Upload and Load'
                            )}
                        </Button>

                        <Button variant="secondary" onClick={fetchPresets} disabled={isLoading}>
                            {isLoading ? (
                                <>
                                    <Spinner as="span" animation="border" size="sm" role="status" aria-hidden="true" />
                                    <span className="ms-2">Loading...</span>
                                </>
                            ) : (
                                showPresets ? 'Hide Presets' : 'Show Presets'
                            )}
                        </Button>
                    </div>

                    {showPresets && (
                        <Card className="mb-3">
                            <Card.Header>Available Presets</Card.Header>
                            <Card.Body>
                                <ListGroup variant="flush">
                                    {presets.map((preset, index) => (
                                        <ListGroup.Item key={index} action onClick={() => loadPreset(preset.name)}>
                                            <strong>{preset.name}</strong>
                                            <div className="text-muted small">{preset.description}</div>
                                        </ListGroup.Item>
                                    ))}
                                </ListGroup>
                            </Card.Body>
                        </Card>
                    )}
                </Form>
            </Card.Body>
        </Card>
    );
};

export default GardenSetupPanel;