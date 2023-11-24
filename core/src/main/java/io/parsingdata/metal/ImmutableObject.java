package io.parsingdata.metal;

/**
 * When objects are immutable, that means their hashcode will stay the same the moment it is created.
 * It will improve performance if these objects cache their hash.
 * <p>
 * This is a lazy implementation, instead of calculating the hash once within the constructor, to avoid
 * performance decrease during parsing. In most implementations the hash is not actually used/needed.
 */
public abstract class ImmutableObject {

    private Integer hash;

    public abstract int cachingHashCode();

    @Override
    public abstract boolean equals(final Object obj);

    @Override
    public int hashCode() {
        if (hash == null) {
            hash = cachingHashCode();
        }
        return hash;
    }
}
