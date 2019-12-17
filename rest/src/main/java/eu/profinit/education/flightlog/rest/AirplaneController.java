package eu.profinit.education.flightlog.rest;

import eu.profinit.education.flightlog.service.AirplaneService;
import eu.profinit.education.flightlog.to.AirplaneTo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
public class AirplaneController {

    // letadla získáte voláním AirplaneService
    // bude se volat metoda GET na /airplane
    // struktura odpovědi je dána objektem AirplaneTo

    private final AirplaneService airplaneService;

    public AirplaneController(AirplaneService airplaneService) {
        this.airplaneService = airplaneService;
    }

    @RequestMapping("/airplane")
    public List<AirplaneTo> getClubAirplanes() {
        List<AirplaneTo> airplanes = airplaneService.getClubAirplanes();
        log.debug("Airplanes:\n{}", airplanes);
        return airplanes;
    }
}
