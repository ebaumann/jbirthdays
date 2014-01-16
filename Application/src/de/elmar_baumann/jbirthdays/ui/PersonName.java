package de.elmar_baumann.jbirthdays.ui;

import de.elmar_baumann.jbirthdays.api.Person;
import de.elmar_baumann.jbirthdays.util.StringUtil;

/**
 * @author Elmar Baumann
 */
public final class PersonName implements Comparable<PersonName> {

    private final Person person;

    public PersonName(Person person) {
        if (person == null) {
            throw new NullPointerException("person == null");
        }
        this.person = person;
    }

    @Override
    public int compareTo(PersonName o) {
        return Person.CMP_ASC_BY_LAST_NAME.compare(person, o.person);
    }

    @Override
    public String toString() {
        String firstName = StringUtil.nullToEmptyString(person.getFirstName());
        String lastName = StringUtil.nullToEmptyString(person.getLastName());
        return firstName + " " + lastName;
    }
}
