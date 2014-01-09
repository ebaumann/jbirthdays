package de.elmar_baumann.jbirthdays.ui;

import de.elmar_baumann.jbirthdays.api.Person;
import de.elmar_baumann.jbirthdays.util.Bundle;
import de.elmar_baumann.jbirthdays.util.DateUtil;
import de.elmar_baumann.jbirthdays.util.StringUtil;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

/**
 * @author Elmar Baumann
 */
public class PersonTableModel implements TableModel {

    private final Collection<TableModelListener> listeners;
    private final List<Person> persons;
    private final boolean withBirthdayColumn;

    private static final String[] COLUMN_NAMES_WITH_BIRTHDAY = new String[]{
        Bundle.getString(PersonTableModel.class, "PersonTableModel.Column.Birthday"),
        Bundle.getString(PersonTableModel.class, "PersonTableModel.Column.Name"),
        Bundle.getString(PersonTableModel.class, "PersonTableModel.Column.Age"),
        Bundle.getString(PersonTableModel.class, "PersonTableModel.Column.Born"),
        Bundle.getString(PersonTableModel.class, "PersonTableModel.Column.Notes"),
    };

    private static final String[] COLUMN_NAMES_NO_BIRTHDAY = new String[]{
        Bundle.getString(PersonTableModel.class, "PersonTableModel.Column.Name"),
        Bundle.getString(PersonTableModel.class, "PersonTableModel.Column.Age"),
        Bundle.getString(PersonTableModel.class, "PersonTableModel.Column.Born"),
        Bundle.getString(PersonTableModel.class, "PersonTableModel.Column.Notes"),
    };

    private static final Class<?>[] COLUMN_CLASSES_WITH_BIRTHDAY = new Class<?>[] {
        String.class,
        PersonNameForSort.class,
        String.class,
        String.class,
        String.class,
    };

    private static final Class<?>[] COLUMN_CLASSES_NO_BIRTHDAY = new Class<?>[] {
        PersonNameForSort.class,
        String.class,
        String.class,
        String.class,
        String.class,
    };

    public PersonTableModel(boolean withBirthdayColumn) {
        this(Collections.<Person>emptyList(), withBirthdayColumn);
    }

    public PersonTableModel(Collection<? extends Person> persons, boolean withBirthdayColumn) {
        if (persons == null) {
            throw new NullPointerException("persons == null");
        }
        this.persons = new ArrayList<>(persons);
        this.withBirthdayColumn = withBirthdayColumn;
        listeners = new CopyOnWriteArrayList<>();
    }

    public void setPersons(Collection<? extends Person> persons) {
        if (persons == null) {
            throw new NullPointerException("persons == null");
        }
        this.persons.clear();
        this.persons.addAll(persons);
        fireTableChanged();
    }

    private void fireTableChanged() {
        for (TableModelListener listener : listeners) {
            listener.tableChanged(new TableModelEvent(this));
        }
    }

    @Override
    public int getRowCount() {
        return persons.size();
    }

    @Override
    public int getColumnCount() {
        return withBirthdayColumn
                ? COLUMN_NAMES_WITH_BIRTHDAY.length
                : COLUMN_NAMES_NO_BIRTHDAY.length;
    }

    @Override
    public String getColumnName(int columnIndex) {
        assertIsColumnIndex(columnIndex);
        return withBirthdayColumn
                ? COLUMN_NAMES_WITH_BIRTHDAY[columnIndex]
                : COLUMN_NAMES_NO_BIRTHDAY[columnIndex];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        assertIsColumnIndex(columnIndex);
        return withBirthdayColumn
                ? COLUMN_CLASSES_WITH_BIRTHDAY[columnIndex]
                : COLUMN_CLASSES_NO_BIRTHDAY[columnIndex];
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        assertIsIndex(rowIndex, columnIndex);
        Person person = getPerson(rowIndex);
        switch (columnIndex) {
            case 0:
                return withBirthdayColumn ? formatBirthday(person) : new PersonNameForSort(person);
            case 1:
                return withBirthdayColumn ? new PersonNameForSort(person) : formatAge(person);
            case 2:
                return withBirthdayColumn ? formatAge(person) : formatBorn(person);
            case 3:
                return withBirthdayColumn ? formatBorn(person) : StringUtil.nullToEmptyString(person.getNotes());
            case 4:
                if (withBirthdayColumn) {
                    return StringUtil.nullToEmptyString(person.getNotes());
                } else {
                    throw new IllegalArgumentException("Invalid column index " + columnIndex);
                }
            default:
                throw new IllegalArgumentException("Invalid column index " + columnIndex);
        }
    }

    private String formatBirthdaysWeekday(Person person, boolean current) {
        if (!person.isBirthdayDateValid()) {
            return getInvalidDateString(person);
        }
        int birthdayMonth = person.getBirthdayMonth();
        int birthdayDay = person.getBirthdayDay();
        Calendar todayCal = Calendar.getInstance();
        Calendar birthdayCal = Calendar.getInstance();
        if (current) {
            boolean inThisYear = DateUtil.isBefore(
                    todayCal.get(Calendar.MONTH) + 1, todayCal.get(Calendar.DAY_OF_MONTH),
                    birthdayMonth, birthdayDay);
            int thisYear = todayCal.get(Calendar.YEAR);
            birthdayCal.set(Calendar.YEAR, inThisYear ? thisYear : thisYear + 1);
        } else {
            if (person.getBirthdayYear() > 0) {
                birthdayCal.set(Calendar.YEAR, person.getBirthdayYear());
            } else {
                return "?";
            }
        }
        birthdayCal.set(Calendar.MONTH, birthdayMonth - 1);
        birthdayCal.set(Calendar.DAY_OF_MONTH, birthdayDay);
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE");
        return dateFormat.format(birthdayCal.getTime());
    }

    private String getInvalidDateString(Person person) {
        return Bundle.getString(PersonTableModel.class, "PersonTableModel.InvalidDate", person.getBirthdayMonth(), person.getBirthdayDay());
    }

    private String formatName(Person person) {
        return StringUtil.nullToEmptyString(person.getFirstName())
                + " "
                + StringUtil.nullToEmptyString(person.getLastName());
    }

    private String formatAge(Person person) {
        if (!person.isBirthdayDateValid() || person.getBirthdayYear() <= 0) {
            return "?";
        }
        Calendar todayCal = Calendar.getInstance();
        int thisYear = todayCal.get(Calendar.YEAR);
        int age = thisYear - person.getBirthdayYear();
        return Bundle.getString(PersonTableModel.class, "PersonTableModel.Age", age);
    }

    private String formatBirthday(Person person) {
        if (!person.isBirthdayDateValid()) {
            return getInvalidDateString(person);
        }
        Calendar cal = Calendar.getInstance();
        if (DateUtil.isBefore(
                cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH),
                person.getBirthdayMonth(), person.getBirthdayDay())) {
            cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) - 1);
        }
        cal.set(Calendar.MONTH, person.getBirthdayMonth() - 1);
        cal.set(Calendar.DAY_OF_MONTH, person.getBirthdayDay());
        String weekday = formatBirthdaysWeekday(person, false);
        DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);
        return weekday + ", " + df.format(cal.getTime());
    }

    private String formatBorn(Person person) {
        if (!person.isBirthdayDateValid()) {
            return getInvalidDateString(person);
        }
        if (person.getBirthdayYear() <= 0) {
            return person.getBirthdayMonth() + " - " + person.getBirthdayDay();
        }
        Calendar birthdayCal = Calendar.getInstance();
        birthdayCal.set(Calendar.YEAR, person.getBirthdayYear());
        birthdayCal.set(Calendar.MONTH, person.getBirthdayMonth() - 1);
        birthdayCal.set(Calendar.DAY_OF_MONTH, person.getBirthdayDay());
        String weekday = formatBirthdaysWeekday(person, false);
        DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);
        return weekday + ", " + df.format(birthdayCal.getTime());
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void addTableModelListener(TableModelListener l) {
        listeners.add(l);
    }

    @Override
    public void removeTableModelListener(TableModelListener l) {
        listeners.remove(l);
    }

    public Person getPerson(int rowIndex) {
        assertIsRowIndex(rowIndex);
        return persons.get(rowIndex);
    }

    private void assertIsRowIndex(int rowIndex) {
        if (rowIndex < 0 || rowIndex >= getRowCount()) {
            throw new IllegalArgumentException("Invalid row index " + rowIndex + ". Row Count is " + getRowCount());
        }
    }

    private void assertIsColumnIndex(int columnIndex) {
        if (columnIndex < 0 || columnIndex >= getColumnCount()) {
            throw new IllegalArgumentException("Invalid column index " + columnIndex + ". Column Count is " + getColumnCount());
        }
    }

    private void assertIsIndex(int rowIndex, int columnIndex) {
        assertIsRowIndex(rowIndex);
        assertIsColumnIndex(columnIndex);
    }
}
