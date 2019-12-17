package eu.profinit.education.flightlog.dao;

import eu.profinit.education.flightlog.exceptions.ExternalSystemException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Component
@Profile("!stub")
public class ClubDatabaseDaoImpl implements ClubDatabaseDao {

    private final RestTemplate restTemplate;
    private final String clubDbBaseUrl;

    public ClubDatabaseDaoImpl(@Value("${integration.clubDb.baseUrl}") String url) {
        this.restTemplate = new RestTemplate();
        this.clubDbBaseUrl = url;
    }


    @Override
    public List<User> getUsers() {
        User[] userList;
        try {
            userList = restTemplate.getForObject(clubDbBaseUrl + "/club/user", User[].class);
        } catch (RuntimeException e) {
            throw new ExternalSystemException("Cannot get users from Club database. URL: {}. Call resulted in exception.", e, clubDbBaseUrl);
        }
        assert userList != null;
        return Arrays.asList(userList);
    }
}
