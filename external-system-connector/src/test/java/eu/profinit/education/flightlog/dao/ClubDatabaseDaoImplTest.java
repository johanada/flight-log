package eu.profinit.education.flightlog.dao;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@TestPropertySource(properties = {"integration.clubDb.baseUrl = http://vyuka.profinit.eu:8080"})
@ContextConfiguration
public class ClubDatabaseDaoImplTest {

    @Autowired
    private ClubDatabaseDao testSubject;

    @Ignore("Test is ignored because it requires clubDB server to run.")
    @Test
    public void getUsers(){
        List<User> users = testSubject.getUsers();

        assertNotNull(users);
        assertTrue("Should contains at least 5 items.", users.size() > 5);
        assertNotNull(users.get(0).getFirstName());
        assertNotNull(users.get(0).getLastName());
        assertNotNull(users.get(0).getMemberId());
        assertNotNull(users.get(0).getRoles());
    }

    @Configuration
    @ComponentScan
    public static class IntegrationTestConfig {

    }
}