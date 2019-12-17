package eu.profinit.education.flightlog.service;

import eu.profinit.education.flightlog.dao.ClubDatabaseDao;
import eu.profinit.education.flightlog.dao.User;
import eu.profinit.education.flightlog.domain.entities.Person;
import eu.profinit.education.flightlog.domain.repositories.PersonRepository;
import eu.profinit.education.flightlog.to.AddressTo;
import eu.profinit.education.flightlog.to.PersonTo;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.AdditionalAnswers;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PersonServiceTest {

    @Mock
    private PersonRepository personRepository;

    @Mock
    private ClubDatabaseDao clubDatabaseDao;

    private PersonServiceImpl testSubject;

    @Before
    public void setUp(){
        testSubject = new PersonServiceImpl(personRepository, clubDatabaseDao);
    }

    @Test
    public void shouldCreateGuest() {
        // prepare data
        PersonTo guestToCreate = PersonTo.builder()
            .firstName("Jan")
            .lastName("Novák")
            .address(AddressTo.builder()
                .street("Tychonova 2")
                .city("Praha 6")
                .postalCode("16000")
                .build())
            .build();

        // mock behaviour
        when(personRepository.save(any())).thenAnswer(AdditionalAnswers.returnsFirstArg());

        // call tested method
        Person person = testSubject.getExistingOrCreatePerson(guestToCreate);

        // verify results
        assertEquals("Person type does not match", Person.Type.GUEST, person.getPersonType());
        assertEquals("First name does not match", guestToCreate.getFirstName(), person.getFirstName());
        assertEquals("Last name does not match", guestToCreate.getLastName(), person.getLastName());

        assertEquals("Strear does not match", guestToCreate.getAddress().getStreet(), person.getAddress().getStreet());

    }

    @Test
    public void shouldReturnExistingClubMember() {
        // prepare data
        PersonTo existingClubMember = PersonTo.builder()
            .memberId(2L)
            .build();

        User testUser = new User(2L, "Kamila", "Spoustová", Arrays.asList("PILOT"));
        Person clubMemberFromDd = Person.builder().personType(Person.Type.CLUB_MEMBER).memberId(2L).build();

        // mock behaviour
        when(personRepository.findByMemberId(2L)).thenReturn(Optional.of(clubMemberFromDd));
        when(clubDatabaseDao.getUsers()).thenReturn(Arrays.asList(testUser));


        // call tested method
        Person person = testSubject.getExistingOrCreatePerson(existingClubMember);

        // verify results
        assertTrue("Should return prepared instance", clubMemberFromDd == person);

    }

    @Test
    public void shouldCreateNewClubMember() {
        PersonTo newMember = PersonTo.builder()
            .memberId(42L)
            .build();

        Person expected = Person.builder().personType(Person.Type.CLUB_MEMBER).memberId(42L).build();
        User testUser = new User(42L, "Adam", "Jolly", Arrays.asList("PILOT"));

        when(clubDatabaseDao.getUsers()).thenReturn(Arrays.asList(testUser));
        when(personRepository.findByMemberId(42L)).thenReturn(Optional.empty());
        when(personRepository.save(any())).thenAnswer(AdditionalAnswers.returnsFirstArg());

        // call tested method
        Person person = testSubject.getExistingOrCreatePerson(newMember);

        // verify results
        assertEquals(expected.getMemberId(), person.getMemberId());
    }


}