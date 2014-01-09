package de.elmar_baumann.jbirthdays.api;

import de.elmar_baumann.jbirthdays.util.DateUtil;
import de.elmar_baumann.jbirthdays.util.StringUtil;
import java.text.Collator;
import java.util.Comparator;
import java.util.Objects;
import java.util.UUID;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * @author Elmar Baumann
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = {"uuid", "firstName", "lastName", "birthdayYear", "birthdayMonth", "birthdayDay", "notes"})
public final class Person {

    @XmlElement(name = "uuid")
    private final String uuid = UUID.randomUUID().toString();
    @XmlElement(name = "firstName")
    private String firstName;
    @XmlElement(name = "lastName")
    private String lastName;
    @XmlElement(name = "birthdayYear")
    private int birthdayYear;
    @XmlElement(name = "birthdayMonth")
    private int birthdayMonth;
    @XmlElement(name = "birthdayDay")
    private int birthdayDay;
    @XmlElement(name = "notes")
    private String notes;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getBirthdayYear() {
        return birthdayYear;
    }

    public void setBirthdayYear(int birthdayYear) {
        this.birthdayYear = birthdayYear;
    }

    public int getBirthdayMonth() {
        return birthdayMonth;
    }

    public void setBirthdayMonth(int birthdayMonth) {
        this.birthdayMonth = birthdayMonth;
    }

    public int getBirthdayDay() {
        return birthdayDay;
    }

    public void setBirthdayDay(int birthdayDay) {
        this.birthdayDay = birthdayDay;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    /**
     * @return true if month and day may be a date, if year is specified,
     *         leap years are an issue (February 29 on non leap years is invalid)
     */
    public boolean isBirthdayDateValid() {
        return birthdayDay == 0 || birthdayMonth == 0
                ? false
                : birthdayYear > 0
                ? DateUtil.isValidDate(birthdayYear, birthdayMonth, birthdayDay)
                : DateUtil.maybeDate(birthdayMonth, birthdayDay);
    }

    @Override
    public String toString() {
        return firstName + " " + lastName + " " + birthdayYear + "-" + birthdayMonth + "-" + birthdayDay;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Person)) {
            return false;
        }
        Person other = (Person) obj;
        return uuid.equals(other.uuid);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 23 * hash + Objects.hashCode(this.uuid);
        return hash;
    }

    public static final Comparator<Person> CMP_ASC_BY_FIRST_NAME = new Comparator<Person>() {

        private final Collator collator = Collator.getInstance();

        @Override
        public int compare(Person person1, Person person2) {
            String lastName1 = StringUtil.nullToEmptyString(person1.firstName);
            String lastName2 = StringUtil.nullToEmptyString(person2.firstName);
            return collator.compare(lastName1, lastName2);
        }
    };

    public static final Comparator<Person> CMP_ASC_BY_LAST_NAME = new Comparator<Person>() {

        private final Collator collator = Collator.getInstance();

        @Override
        public int compare(Person person1, Person person2) {
            String lastName1 = StringUtil.nullToEmptyString(person1.lastName);
            String lastName2 = StringUtil.nullToEmptyString(person2.lastName);
            return collator.compare(lastName1, lastName2);
        }
    };

    public static final Comparator<Person> CMP_ASC_BY_BIRTHDAY_MONTH_AND_DAY = new Comparator<Person>() {

        @Override
        public int compare(Person o1, Person o2) {
            return o1.birthdayDay == o2.birthdayDay && o1.birthdayMonth == o2.birthdayMonth
                    ? 0
                    : DateUtil.isBefore(o1.birthdayMonth, o1.birthdayDay, o2.birthdayMonth, o2.birthdayDay)
                    ? 1
                    : -1;
        }
    };
}
