package de.elmar_baumann.jbirthdays.imexport;

import de.elmar_baumann.jbirthdays.api.BirthdaysUtil;
import de.elmar_baumann.jbirthdays.api.Person;
import de.elmar_baumann.jbirthdays.api.PersonRepository;
import de.elmar_baumann.jbirthdays.util.Bundle;
import de.elmar_baumann.jbirthdays.util.DateUtil;
import de.elmar_baumann.jbirthdays.util.FileUtil;
import de.elmar_baumann.jbirthdays.util.StringUtil;
import java.awt.Component;
import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 * @author Elmar Baumann
 */
public final class QtBirthdaysDbImport {

    /**
     * @param parent
     * @param charset
     * @return count of imported persons
     */
    public static int importPersons(Component parent, Charset charset) {
        if (charset == null) {
            throw new NullPointerException("charset == null");
        }
        File db = null;
        try {
            PersonRepository repo = BirthdaysUtil.findPreferredRepository();
            Collection<Person> existingPersons = new ArrayList<>(repo.findAll());
            db = chooseQtDb(parent);
            if (db != null) {
                Logger.getLogger(QtBirthdaysDbImport.class.getName()).log(Level.INFO, "Importing QT Birtday''s database file {0}", db);
                Collection<? extends Person> importedPersons = importPersons(db, charset, existingPersons);
                if (!importedPersons.isEmpty()) {
                    existingPersons.addAll(importedPersons);
                    repo.save(existingPersons);
                }
                return importedPersons.size();
            }
        } catch (Throwable t) {
            Logger.getLogger(QtBirthdaysDbImport.class.getName()).log(Level.SEVERE, null, t);
            JOptionPane.showMessageDialog(parent,
                    Bundle.getString(QtBirthdaysDbImport.class, "QtBirthdaysDbImport.Error.Message", t.getLocalizedMessage()),
                    Bundle.getString(QtBirthdaysDbImport.class, "QtBirthdaysDbImport.Error.Title", db),
                    JOptionPane.ERROR_MESSAGE);
        }
        return 0;
    }

    private static File chooseQtDb(Component parent) {
        JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fc.setMultiSelectionEnabled(false);
        if (fc.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
            return fc.getSelectedFile();
        }
        return null;
    }

    private static Collection<? extends Person> importPersons(File qtBirthddaysDb, Charset charset, Collection<? extends Person> existingPersons) throws Throwable {
        List<String> lines = FileUtil.getLines(qtBirthddaysDb, charset);
        Collection<Person> persons = new ArrayList<>(lines.size());
        for (String line : lines) {
            Logger.getLogger(QtBirthdaysDbImport.class.getName()).log(Level.INFO, "Reading line: {0}", line);
            Person person = lineToPeron(line);
            boolean exists = person != null && containsPerson(existingPersons, person);
            if (exists) {
                Logger.getLogger(QtBirthdaysDbImport.class.getName()).log(Level.INFO, "Person ''{0}'' already exists and will not be imported", person);
            }
            if (person == null) {
                Logger.getLogger(QtBirthdaysDbImport.class.getName()).log(Level.WARNING, "Line is not valid: {0}", line);
            }
            if (person != null && !exists) {
                Logger.getLogger(QtBirthdaysDbImport.class.getName()).log(Level.INFO, "Importing person: {0}", person);
                persons.add(person);
            }
        }
        return persons;
    }

    private static Person lineToPeron(String line) {
        if (!StringUtil.hasContent(line)) {
            return null;
        }
        String[] token = line.split("\t");
        if (token.length < 3) {
            Logger.getLogger(QtBirthdaysDbImport.class.getName()).log(Level.WARNING, "Line is not valid: {0}", line);
            return null;
        }
        String firstName = token[0];
        String lastName = token[1];
        String dateString = token[2];
        String notes = token.length > 3 ? token[3] : null;
        int birthdayYear = getYear(dateString);
        int birthdayMonth = getMonth(dateString);
        int birthdayDay = getDay(dateString);
        if (!DateUtil.maybeDate(birthdayMonth, birthdayDay)) {
            return null;
        }
        Person person = new Person();
        person.setFirstName(firstName);
        person.setLastName(lastName);
        if (notes != null) {
            person.setNotes(notes.replace("\\n", "\n"));
        }
        person.setBirthdayYear(birthdayYear);
        person.setBirthdayMonth(birthdayMonth);
        person.setBirthdayDay(birthdayDay);
        return person;
    }

    private static int toDateInt(String dateString, int index) {
        String[] token = dateString.split("\\.");
        if (token.length < index + 1) {
            return 0;
        }
        try {
            return Integer.parseInt(token[index]);
        } catch (NumberFormatException t) {
            Logger.getLogger(QtBirthdaysDbImport.class.getName()).log(Level.SEVERE, null, t);
            return 0;
        }
    }

    private static int getYear(String dateString) {
        return toDateInt(dateString, 2);
    }

    private static int getMonth(String dateString) {
        return toDateInt(dateString, 1);
    }

    private static int getDay(String dateString) {
        return toDateInt(dateString, 0);
    }

    private static boolean containsPerson(Collection<? extends Person> persons, Person person) {
        for (Person p : persons) {
            if (person.isSamePerson(p)) {
                return true;
            }
        }
        return false;
    }

    private QtBirthdaysDbImport() {
    }
}
