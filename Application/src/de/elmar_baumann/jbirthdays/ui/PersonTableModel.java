package de.elmar_baumann.jbirthdays.ui;

import de.elmar_baumann.jbirthdays.api.Person;
import de.elmar_baumann.jbirthdays.util.Bundle;
import de.elmar_baumann.jbirthdays.util.StringUtil;
import java.util.ArrayList;
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

    private static final int[] COL_WIDTHS_BIRTHDAY = new int[]{150,250,75,150,200};
    private static final int[] COL_WIDTHS_NO_BIRTHDAY = new int[]{250,75,150,200};
    private final Collection<TableModelListener> listeners;
    private final List<Person> persons;
    private final boolean withBirthdayColumn;
    private final boolean birthdayColumnForFutureBirthdays;
    private boolean showNextYearAge;

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
        BirthdayDate.class,
        PersonName.class,
        Age.class,
        Born.class,
        String.class,
    };

    private static final Class<?>[] COLUMN_CLASSES_NO_BIRTHDAY = new Class<?>[] {
        PersonName.class,
        Age.class,
        Born.class,
        String.class,
    };

    public PersonTableModel(boolean withBirthdayColumn, boolean birthdayColumnForFutureBirthdays) {
        this(Collections.<Person>emptyList(), withBirthdayColumn, birthdayColumnForFutureBirthdays);
    }

    public PersonTableModel(Collection<? extends Person> persons, boolean withBirthdayColumn, boolean birthdayColumnForFutureBirthdays) {
        if (persons == null) {
            throw new NullPointerException("persons == null");
        }
        this.persons = new ArrayList<Person>(persons);
        this.withBirthdayColumn = withBirthdayColumn;
        this.birthdayColumnForFutureBirthdays = birthdayColumnForFutureBirthdays;
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

    /**
     * @param showNextYearAge default: false
     */
    public void setShowNextYearAge(boolean showNextYearAge) {
        this.showNextYearAge = showNextYearAge;
    }

    private void fireTableChanged() {
        for (TableModelListener listener : listeners) {
            listener.tableChanged(new TableModelEvent(this));
        }
    }

    public int[] getDefaultColumnWidths() {
        return withBirthdayColumn
                ? COL_WIDTHS_BIRTHDAY
                : COL_WIDTHS_NO_BIRTHDAY;
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
                return withBirthdayColumn
                        ? new BirthdayDate(person, birthdayColumnForFutureBirthdays)
                        : new PersonName(person);
            case 1:
                return withBirthdayColumn
                        ? new PersonName(person)
                        : new Age(person, showNextYearAge);
            case 2:
                return withBirthdayColumn
                        ? new Age(person, showNextYearAge)
                        : new Born(person);
            case 3:
                return withBirthdayColumn
                        ? new Born(person)
                        : StringUtil.nullToEmptyString(person.getNotes());
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

    public List<Person> getPersons() {
        return Collections.unmodifiableList(persons);
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
