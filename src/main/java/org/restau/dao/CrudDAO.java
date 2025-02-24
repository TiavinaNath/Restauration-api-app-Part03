package org.restau.dao;

import java.util.List;
import java.util.Optional;

public interface CrudDAO <E> {
    List<E> getAllPaginated(int page, int size);
    Optional<E> findById(Long id);
    E save(E entity);
    List<E> saveAll(List<E> entities);
    boolean delete(Long id);
}
