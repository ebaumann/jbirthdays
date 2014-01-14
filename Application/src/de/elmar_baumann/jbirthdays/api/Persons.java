package de.elmar_baumann.jbirthdays.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Elmar Baumann
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public final class Persons {

    @XmlElement(name = "person")
    private List<Person> persons = new ArrayList<>();

    public List<Person> getPersons() {
        return persons; // JAXB, no copy
    }

    public void setPersons(List<Person> persons) {
        if (persons == null) {
            throw new NullPointerException("persons == null");
        }
        this.persons = persons; // JAXB, no copy
    }

    public void sort(Comparator<Person> cmp) {
        if (this == null) {
            throw new NullPointerException("this == null");
        }
        Collections.sort(persons, cmp);
    }

    public boolean addToPersons(Person person) {
        if (person == null) {
            throw new NullPointerException("person == null");
        }
        if (!persons.contains(person)) {
            return persons.add(person);
        }
        return false;
    }
}
