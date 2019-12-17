package eu.profinit.education.flightlog;

import lombok.extern.slf4j.Slf4j;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.util.AssertionErrors.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = IntegrationTestConfig.class)
@Transactional
@Slf4j
@ActiveProfiles("stub")
public class FlightLogApplicationTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @Ignore
	@Test
    public void flightsInAirAndLanding() throws Exception {
        ResponseEntity<List<Map>> flightsResponse = restTemplate.exchange("/flight/inAir", HttpMethod.GET, null, new ParameterizedTypeReference<List<Map>>(){});
        List<Map> flightsBefore = flightsResponse.getBody();

        int initialFlightsCount = flightsBefore.size();
        assertTrue("There should be at least one flight", initialFlightsCount >= 1);
        assertEquals(5, flightsBefore.get(0).get("id"));

        String inputJson = readFileToString("landInput.json");
        HttpEntity<String> request = createRequestEntityWithHeaders(inputJson);
        ResponseEntity<Object> response = restTemplate.exchange("/flight/land", HttpMethod.POST, request, Object.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        ResponseEntity<List<Map>> flightsResponse2 = restTemplate.exchange("/flight/inAir", HttpMethod.GET, null, new ParameterizedTypeReference<List<Map>>(){});
        List<Map> flightsAfter = flightsResponse2.getBody();

        assertEquals("There should one flight less than at the beginning", initialFlightsCount - 1, flightsAfter.size());
    }

	@Test
	public void takeoff() throws Exception {
        String inputJson = readFileToString("takeoffInput.json");

        HttpEntity<String> request = createRequestEntityWithHeaders(inputJson);
        ResponseEntity<Object> response = restTemplate.exchange(  "/flight/takeoff", HttpMethod.POST, request, Object.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    private HttpEntity<String> createRequestEntityWithHeaders(String inputJson) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(inputJson, headers);
    }

    private String readFileToString(String fileName) throws IOException, URISyntaxException {
        return new String(Files.readAllBytes(Paths.get(getClass().getClassLoader().getResource(fileName).toURI())));
    }


}
