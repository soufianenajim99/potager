package com.potager.services;

import com.potager.Utils.enums.TreatmentType;
import com.potager.dtos.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;

@Service
@RequiredArgsConstructor
public class XmlGardenLoaderService {

    private final ParcelService parcelService;
    private final PlantService plantService;
    private final InsectService insectService;
    private final TreatmentService treatmentService;

    public void loadGardenFromXml(String xmlContent) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new InputSource(new StringReader(xmlContent)));

            Element root = document.getDocumentElement();
            processPootagerElement(root);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse XML", e);
        }
    }

    private void processPootagerElement(Element pootager) {
        NodeList parcels = pootager.getElementsByTagName("Parcelle");
        for (int i = 0; i < parcels.getLength(); i++) {
            Element parcelElement = (Element) parcels.item(i);
            processParcelElement(parcelElement);
        }
    }

    private void processParcelElement(Element parcelElement) {
        int posX = Integer.parseInt(parcelElement.getAttribute("Pos_x"));
        int posY = Integer.parseInt(parcelElement.getAttribute("Pos_y"));

        // Create or get parcel
        ParcelDTO parcelDTO = parcelService.createParcel(ParcelDTO.builder()
                .xCoordinate(posX)
                .yCoordinate(posY)
                .humidityLevel(50.0) // Default humidity
                .build());

        // Process plants
        NodeList plants = parcelElement.getElementsByTagName("Plante");
        NodeList drageonnantePlants = parcelElement.getElementsByTagName("Plante_Drageonnante");
        processPlants(plants, parcelDTO.getId(), false);
        processPlants(drageonnantePlants, parcelDTO.getId(), true);

        // Process insects
        NodeList insects = parcelElement.getElementsByTagName("Insecte");
        processInsects(insects, parcelDTO.getId());

        // Process treatment devices
        NodeList devices = parcelElement.getElementsByTagName("Dispositif");
        processTreatmentDevices(devices, parcelDTO.getId());
    }

    private void processPlants(NodeList plantElements, Long parcelId, boolean isRunner) {
        for (int i = 0; i < plantElements.getLength(); i++) {
            Element plantElement = (Element) plantElements.item(i);

            PlantDTO plantDTO = PlantDTO.builder()
                    .species(plantElement.getAttribute("Espece"))
                    .maturityAge(Integer.parseInt(plantElement.getAttribute("Maturite_pied")))
                    .isRunner(isRunner)
                    .colonizationProbability(isRunner ?
                            Double.parseDouble(plantElement.getAttribute("Proba_Colonisation")) : null)
                    .parcelId(parcelId)
                    .build();

            plantService.createPlant(plantDTO);
        }
    }

    private void processInsects(NodeList insectElements, Long parcelId) {
        for (int i = 0; i < insectElements.getLength(); i++) {
            Element insectElement = (Element) insectElements.item(i);

            InsectDTO insectDTO = InsectDTO.builder()
                    .species(insectElement.getAttribute("Espece"))
                    .sex(insectElement.getAttribute("Sexe"))
                    .healthIndex(10) // Starting health
                    .mobility(Double.parseDouble(insectElement.getAttribute("Proba_mobilite")))
                    .insecticideResistance(Double.parseDouble(
                            insectElement.getAttribute("Resistance_insecticide")))
                    .stepsWithoutFood(0)
                    .parcelId(parcelId)
                    .build();

            insectService.createInsect(insectDTO);
        }
    }

    private void processTreatmentDevices(NodeList deviceElements, Long parcelId) {
        for (int i = 0; i < deviceElements.getLength(); i++) {
            Element deviceElement = (Element) deviceElements.item(i);
            int radius = Integer.parseInt(deviceElement.getAttribute("Rayon"));

            TreatmentDeviceDTO deviceDTO = TreatmentDeviceDTO.builder()
                    .radius(radius)
                    .parcelId(parcelId)
                    .build();

            TreatmentDeviceDTO createdDevice = treatmentService.createTreatmentDevice(deviceDTO);

            // Process programs
            NodeList programs = deviceElement.getElementsByTagName("Programme");
            for (int j = 0; j < programs.getLength(); j++) {
                Element programElement = (Element) programs.item(j);

                TreatmentProgramDTO programDTO = TreatmentProgramDTO.builder()
                        .startTime(Integer.parseInt(programElement.getAttribute("Debut")))
                        .duration(Integer.parseInt(programElement.getAttribute("Duree")))
                        .type(mapProductToTreatmentType(programElement.getAttribute("Produit")))
                        .deviceId(createdDevice.getId())
                        .build();

                treatmentService.createTreatmentProgram(programDTO);
            }
        }
    }

    private TreatmentType mapProductToTreatmentType(String product) {
        return switch (product) {
            case "Eau" -> TreatmentType.WATER;
            case "Engrais" -> TreatmentType.FERTILIZER;
            case "Insecticide" -> TreatmentType.INSECTICIDE;
            default -> throw new IllegalArgumentException("Unknown product type: " + product);
        };
    }
}
