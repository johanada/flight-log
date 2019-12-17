package eu.profinit.education.flightlog.domain.repositories;

import eu.profinit.education.flightlog.domain.entities.Flight;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FlightRepository extends JpaRepository<Flight, Long> {

    List<Flight> findAll();

    List<Flight> findAllByFlightTypeOrderByTakeoffTimeDesc(Flight.Type flightType);

    // Lety by se měly řadit od nejstarších a v případě shody podle ID tak, aby vlečná byla před kluzákem, který táhne
    // Výsledek si můžete ověřit v testu k této tříde v modulu services
    List<Flight> findAllByLandingTimeIsNullOrderByTakeoffTimeDescFlightTypeDesc();

    List<Flight> findAllByLandingTimeIsNotNullOrderByTakeoffTimeDescFlightTypeDesc();

}

