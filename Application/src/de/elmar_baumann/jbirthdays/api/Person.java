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
@XmlType(propOrder = {"uuid", "firstName", "lastName", "email", "birthdayYear", "birthdayMonth", "birthdayDay", "notes"})
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
    @XmlElement(name = "email")
    private String email;

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean hasEmail() {
        return StringUtil.hasContent(email);
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

    /**
     * @param obj
     * @return true if UUIDs equals
     * @see #isSamePerson(de.elmar_baumann.jbirthdays.api.Person)
     */
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

    /**
     * @param other
     * @return true if names and birthdays equals (not UUID comparison)
     */
    public boolean isSamePerson(Person other) {
        if (other == null) {
            throw new NullPointerException("other == null");
        }
        String thisFirstName = StringUtil.nullToEmptyString(firstName).trim();
        String otherFirstName = StringUtil.nullToEmptyString(other.getFirstName()).trim();
        String thisLastName = StringUtil.nullToEmptyString(lastName).trim();
        String otherLastName = StringUtil.nullToEmptyString(other.getLastName()).trim();
        boolean namesEquals = thisFirstName.equalsIgnoreCase(otherFirstName)
                && thisLastName.equalsIgnoreCase(otherLastName);
        boolean birthdaysEquals = this.birthdayYear == other.birthdayYear
                && this.birthdayMonth == other.birthdayMonth
                && this.birthdayDay == other.birthdayDay;
        return namesEquals && birthdaysEquals;
    }

    public static final Comparator<Person> CMP_ASC_BY_LAST_NAME = new Comparator<Person>() {

        private final Collator collator = Collator.getInstance();

        @Override
        public int compare(Person person1, Person person2) {
            String person1LastName = StringUtil.nullToEmptyString(person1.getLastName());
            String person2LastName = StringUtil.nullToEmptyString(person2.getLastName());
            int result = collator.compare(person1LastName, person2LastName);
            if (result == 0) {
                String person1FirstName = StringUtil.nullToEmptyString(person1.getFirstName());
                String person2FirstName = StringUtil.nullToEmptyString(person2.getFirstName());
                return collator.compare(person1FirstName, person2FirstName);
            }
            return result;
        }
    };
}
