package de.elmar_baumann.jbirthdays.api;

/**
 * @author Elmar Baumann
 */
public final class RepositoryChangedEvent {

    public enum Type {
        TYPE,
        LOCATION
    }

    private final Object source;
    private final Type type;

    public RepositoryChangedEvent(Type type, Object source) {
        if (type == null) {
            throw new NullPointerException("type == null");
        }
        if (source == null) {
            throw new NullPointerException("source == null");
        }
        this.type = type;
        this.source = source;
    }

    public Type getType() {
        return type;
    }

    public Object getSource() {
        return source;
    }
}
