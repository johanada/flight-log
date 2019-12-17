package eu.profinit.education.flightlog.service;

import eu.profinit.education.flightlog.IntegrationTestConfig;
import eu.profinit.education.flightlog.to.FileExportTo;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = IntegrationTestConfig.class)
@Transactional
@TestPropertySource(
    locations = "classpath:application-integrationtest.properties")
public class CsvExportServiceTest {

    @Autowired
    private CsvExportService testSubject;

    @Test
    public void testCSVExport() throws IOException, URISyntaxException {
        FileExportTo allFlightsAsCsv = testSubject.getAllFlightsAsCsv();
        Assert.assertEquals(readFileToString("expectedExport.csv"), new String(allFlightsAsCsv.getContent()));
    }

    private String readFileToString(String fileName) throws URISyntaxException, IOException {
        return new String(Files.readAllBytes(Paths.get(getClass().getClassLoader().getResource(fileName).toURI())));
    }
}