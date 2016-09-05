package com.epam.server.repo;

import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;

/**
 * Created by Ahmed_Khan on 5/5/2016.
 */
@NoRepositoryBean
public interface BaseRepository<T, ID extends Serializable> {
    Iterable<T> findAll();

    T findOne(ID id);

    T save(T t);

    Iterable<T> save(Iterable<T> iterable);
}
