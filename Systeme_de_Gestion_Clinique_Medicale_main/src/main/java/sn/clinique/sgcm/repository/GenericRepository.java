package sn.clinique.sgcm.repository;

import java.util.List;
import java.util.Optional;

public interface GenericRepository<T, ID> {
    T save(T entity);
    T update(T entity);
    void delete(ID id);
    Optional<T> findById(ID id);
    List<T> findAll();
}