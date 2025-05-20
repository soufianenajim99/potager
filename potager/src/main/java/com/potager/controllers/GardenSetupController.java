package com.potager.controllers;

import com.potager.customExceptions.EntityNotFoundException;
import com.potager.dtos.GardenPresetDTO;
import com.potager.dtos.InsectDTO;
import com.potager.services.InsectService;
import com.potager.services.SimulationService;
import com.potager.services.XmlGardenLoaderService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;


@RestController
@RequestMapping("/api/garden-setup")
@RequiredArgsConstructor
public class GardenSetupController extends GardenController {

    private final XmlGardenLoaderService xmlGardenLoaderService;
    private final SimulationService simulationService;

    @PostMapping("/load-xml")
    public ResponseEntity<?> loadGardenFromXml(@RequestBody String xmlContent) {
        logger.info("Loading garden from XML");
        try {
            simulationService.resetSimulation();

            xmlGardenLoaderService.loadGardenFromXml(xmlContent);
            return ok("Garden configuration loaded successfully");
        } catch (RuntimeException e) {
            logger.error("Failed to load garden from XML: {}", e.getMessage());
            return badRequest("Invalid XML format: " + e.getMessage());
        }
    }

    @PostMapping(value = "/upload-xml", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadGardenXmlFile(@RequestParam("file") MultipartFile file) {
        logger.info("Uploading garden XML file: {}", file.getOriginalFilename());
        try {
            if (file.isEmpty()) {
                return badRequest("Uploaded file is empty");
            }

            if (!file.getOriginalFilename().toLowerCase().endsWith(".xml")) {
                return badRequest("Only XML files are allowed");
            }

            String xmlContent = new String(file.getBytes(), StandardCharsets.UTF_8);
            return loadGardenFromXml(xmlContent);
        } catch (IOException e) {
            logger.error("File upload error: {}", e.getMessage());
            return badRequest("Failed to process uploaded file: " + e.getMessage());
        }
    }

    @GetMapping("/available-presets")
    public ResponseEntity<List<GardenPresetDTO>> getAvailablePresets() {
        logger.info("Fetching available garden presets");
        return ok(List.of(
                new GardenPresetDTO("Case 1", "Basic garden setup"),
                new GardenPresetDTO("Case 2", "Garden with runner plants"),
                new GardenPresetDTO("Case 3", "Insect evolution test"),
                new GardenPresetDTO("Case 4", "Treatment devices test")
        ));
    }

    @PostMapping("/load-preset/{presetName}")
    public ResponseEntity<?> loadPreset(@PathVariable String presetName) {
        logger.info("Loading garden preset: {}", presetName);
        try {
            String resourcePath = String.format("presets/Pootager_%s.xml", presetName.replace(" ", "_"));
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resourcePath);

            if (inputStream == null) {
                return notFound("Preset not found: " + presetName);
            }

            String xmlContent = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            return loadGardenFromXml(xmlContent);
        } catch (IOException e) {
            logger.error("Failed to load preset: {}", e.getMessage());
            return badRequest("Failed to load preset: " + e.getMessage());
        }
    }
}
