package interfaces;

import java.util.List;

/**
 * Generic DAO interface – acts as an abstract contract for all DAO classes.
 * Demonstrates the use of interfaces in OOP.
 */
public interface IDao<T> {
    void add(T entity);
    T getById(String id);
    List<T> getAll();
    void update(T entity);
    boolean delete(String id);
}
