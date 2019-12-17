package eu.profinit.education.flightlog.domain.entities;

import eu.profinit.education.flightlog.domain.JpaConstants;
import lombok.*;
import org.apache.commons.lang3.ObjectUtils;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = JpaConstants.Tables.PERSON)
@SequenceGenerator(name = JpaConstants.Sequences.PERSON, sequenceName = JpaConstants.Sequences.PERSON, initialValue = JpaConstants.Sequences.INITIAL_VALUE)
@Getter
@Setter
@ToString
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@EqualsAndHashCode
public class Person {

    @Id
    @GeneratedValue(generator = JpaConstants.Sequences.PERSON)
    private Long id;
    @NotNull
    @Enumerated(EnumType.STRING)
    private Type personType;

    protected String firstName;
    protected String lastName;
    private Address address;
    private Long memberId;

    public Person(Type personType, String firstName, String lastName, Address address) {
        this.personType = personType;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
    }

    public Person(@NotNull Type personType, Long memberId, String firstName, String lastName) {
        this.personType = personType;
        this.memberId = memberId;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public enum Type {
        CLUB_MEMBER,
        GUEST
    }

    public String getFullName() {
        return (ObjectUtils.defaultIfNull(firstName, "") + " " + ObjectUtils.defaultIfNull(lastName, "")).trim();
    }
}
