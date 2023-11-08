package io.parsingdata.metal;

/**
 * When objects are immutable, that means their hashcode will stay the same the moment it is created.
 * It will improve performance if these objects caches their hash.
 * <p>
 * This is a lazy implementation, instead of calculating the hash once within the constructor, to avoid
 * performance decrease during parsing. In most implementations the hash is not actually used/needed.
 * <p>
 * In addition, we may consider adding the equality methods here aas well, to make it mandatory to implement them.
 */
public abstract class ImmutableObject {

    private Integer hash;

    abstract public int cachingHashCode();

    @Override
    public int hashCode() {
        if (hash == null) {
            hash = cachingHashCode();
        }
        return hash;
    }
}
