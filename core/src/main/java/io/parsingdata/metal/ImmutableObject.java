package io.parsingdata.metal;

/**
 * When objects are immutable, that means their hash code will stay the same the moment it is created.
 * It will improve performance if these objects cache their hash code.
 * <p>
 * This is a lazy implementation, instead of calculating the hash once within the constructor, to avoid
 * performance decrease during parsing. In most implementations the hash code is not actually used/needed.
 */
public abstract class ImmutableObject {

    private Integer hashCode;

    public abstract int immutableHashCode();

    @Override
    public abstract boolean equals(final Object obj);

    @Override
    public int hashCode() {
        if (hashCode == null) {
            hashCode = immutableHashCode();
        }
        return hashCode;
    }
}
