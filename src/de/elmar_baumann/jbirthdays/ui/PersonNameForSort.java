package de.elmar_baumann.jbirthdays.ui;

import de.elmar_baumann.jbirthdays.api.Person;
import de.elmar_baumann.jbirthdays.util.StringUtil;
import java.text.Collator;

/**
 * @author Elmar Baumann
 */
public final class PersonNameForSort implements Comparable<PersonNameForSort> {

    private final Collator collator = Collator.getInstance();
    private final Person person;

    public PersonNameForSort(Person person) {
        if (person == null) {
            throw new NullPointerException("person == null");
        }
        this.person = person;
    }

    @Override
    public int compareTo(PersonNameForSort o) {
        String thisLastName = StringUtil.nullToEmptyString(person.getLastName());
        String otherLastName = StringUtil.nullToEmptyString(o.person.getLastName());
        return collator.compare(thisLastName, otherLastName);
    }

    @Override
    public String toString() {
        String firstName = StringUtil.nullToEmptyString(person.getFirstName());
        String lastName = StringUtil.nullToEmptyString(person.getLastName());
        return firstName + " " + lastName;
    }
}
