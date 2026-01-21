public interface MyList<T> {
    void add(T element);
    boolean remove(T element);
    int size();
    boolean isEmpty();
    T get(int index);
}