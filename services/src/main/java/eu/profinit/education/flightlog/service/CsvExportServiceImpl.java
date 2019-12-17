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
import java.util.List;

@Service
public class CsvExportServiceImpl implements CsvExportService {

    private static final String DATE_PATTERN = "dd.MM.yyyy HH:mm:ss";
    private static final String ENCODING = "UTF-8";

    private final FlightRepository flightRepository;

    private final String fileName;

    public CsvExportServiceImpl(FlightRepository flightRepository, @Value("${csv.export.flight.fileName}") String fileName) {
        this.flightRepository = flightRepository;
        this.fileName = fileName;
    }

    @Override
    public FileExportTo getAllFlightsAsCsv() {
        // TODO 4.3: Naimplementujte vytváření CSV.
        // Tip: můžete použít Apache Commons CSV - https://commons.apache.org/proper/commons-csv/ v příslušných pom.xml naleznete další komentáře s postupem
        final List<Flight> flights = flightRepository.findAll(Sort.by(Sort.Order.asc("takeoffTime"), Sort.Order.asc("id")));
        try (ByteArrayOutputStream stream = new ByteArrayOutputStream();
             Writer printWriter = new OutputStreamWriter(stream, (Charset.forName(ENCODING)));
             CSVPrinter csvExport = new CSVPrinter(printWriter, CSVFormat.DEFAULT)) {
            for (Flight flight : flights) {
                csvExport.printRecord(flight.getAirplane().getSafeImmatriculation());
            }
            csvExport.flush();
            return new FileExportTo(fileName, MediaType.TEXT_PLAIN, stream.toByteArray());
        } catch (IOException e) {
            throw new FlightLogException("Error during flights CSV export", e);
        }
    }

}
