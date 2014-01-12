package de.elmar_baumann.jbirthdays.ui;

import de.elmar_baumann.jbirthdays.api.Person;
import de.elmar_baumann.jbirthdays.util.StringUtil;
import javax.swing.RowFilter;
import javax.swing.RowFilter.Entry;

/**
 * @author Elmar Baumann
 */
public final class PersonNameRowFilter extends RowFilter<PersonTableModel, Integer> {

    private final String filterTextLowerCase;

    public PersonNameRowFilter(String filterText) {
        if (filterText == null) {
            throw new NullPointerException("filterText == null");
        }
        this.filterTextLowerCase = filterText.toLowerCase();
    }

    @Override
    public boolean include(Entry<? extends PersonTableModel, ? extends Integer> entry) {
        if (filterTextLowerCase.isEmpty()) {
            return true;
        }
        int rowIndex = entry.getIdentifier();
        PersonTableModel model = entry.getModel();
        Person person = model.getPerson(rowIndex);
        String firstNameLowerCase = StringUtil.nullToEmptyString(person.getFirstName()).toLowerCase();
        String lastNameLowerCase = StringUtil.nullToEmptyString(person.getLastName()).toLowerCase();
        String bothNamesLowerCase = firstNameLowerCase + " " + lastNameLowerCase;
        return firstNameLowerCase.contains(filterTextLowerCase)
                || lastNameLowerCase.contains(filterTextLowerCase)
                || bothNamesLowerCase.contains(filterTextLowerCase);
    }
}
