package de.elmar_baumann.jbirthdays.util;

import java.util.Comparator;

/**
 * @author Elmar Baumann
 * @param <T>
 */
public final class ReverseComparator<T> implements Comparator<T> {

    private final Comparator<T> comparator;

    public ReverseComparator(Comparator<T> comparator) {
        if (comparator == null) {
            throw new NullPointerException("comparator == null");
        }
        this.comparator = comparator;
    }

    @Override
    public int compare(T o1, T o2) {
        return comparator.compare(o1, o2) * -1;
    }
}
