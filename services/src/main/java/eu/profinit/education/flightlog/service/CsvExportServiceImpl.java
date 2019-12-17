package eu.profinit.education.flightlog.service;

import eu.profinit.education.flightlog.domain.entities.Flight;
import eu.profinit.education.flightlog.domain.repositories.FlightRepository;
import eu.profinit.education.flightlog.exceptions.FlightLogException;
import eu.profinit.education.flightlog.to.FileExportTo;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class CsvExportServiceImpl implements CsvExportService {

    private static final String DATE_PATTERN_TIME = "HH:mm:ss";
    private static final String DATE_PATTERN_DAY = "dd.MM.yyyy";
    private static final String ENCODING = "UTF-8";

    private final FlightRepository flightRepository;

    private final String fileName;

    public CsvExportServiceImpl(FlightRepository flightRepository, @Value("${csv.export.flight.fileName}") String fileName) {
        this.flightRepository = flightRepository;
        this.fileName = fileName;
    }

    @Override
    public FileExportTo getAllFlightsAsCsv() {
        final List<Flight> flights = flightRepository.findAllByLandingTimeIsNotNullOrderByTakeoffTimeAscIdAscFlightTypeDesc();
        try (ByteArrayOutputStream stream = new ByteArrayOutputStream();
             Writer printWriter = new OutputStreamWriter(stream, (Charset.forName(ENCODING)));
             CSVPrinter csvExport = new CSVPrinter(printWriter, CSVFormat.EXCEL.withHeader(
                 "Datum", "Typ", "Imatrikulace", "Osádka", "Úkol", "Start", "Přistání", "Doba letu", "Poznámka", "Ulice", "Město", "PSČ", "Země"
             ))) {
            for (Flight flight : flights) {
                String crew = getCrew(flight);
                csvExport.printRecord(
                    getDay(flight.getTakeoffTime()),
                    flight.getFlightType().toString(),
                    flight.getAirplane().getSafeImmatriculation(),
                    crew,
                    flight.getTask().getValue(),
                    getTime(flight.getTakeoffTime()),
                    getTime(flight.getLandingTime()),
                    getFlightLength(flight.getTakeoffTime(), flight.getLandingTime()),
                    flight.getNote(),
                    flight.getPilot().getAddress().getStreet(),
                    flight.getPilot().getAddress().getCity(),
                    flight.getPilot().getAddress().getPostalCode(),
                    flight.getPilot().getAddress().getCountry()
                    );
            }
            csvExport.flush();
            return new FileExportTo(fileName, MediaType.TEXT_PLAIN, stream.toByteArray());
        } catch (IOException e) {
            throw new FlightLogException("Error during flights CSV export", e);
        }
    }

    private String getDay(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_PATTERN_DAY);
        return formatter.format(dateTime);
    }

    private String getTime(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_PATTERN_TIME);
        return formatter.format(dateTime);
    }

    private String getFlightLength(LocalDateTime takeoff, LocalDateTime landing) {
        Duration duration = Duration.between(takeoff, landing);
        int hours = duration.toHoursPart();
        int minutes = duration.toMinutesPart();
        return hours + "°" + minutes + "'";
    }

    private String getCrew(Flight flight) {
        String crew = flight.getPilot().getLastName();
        if (flight.getCopilot() != null) {
            crew = crew + ", " + flight.getCopilot().getLastName();
        }
        return crew;
    }

}
