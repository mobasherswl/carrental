package com.epam.server.repo;

import com.epam.server.model.Car;
import org.springframework.data.repository.RepositoryDefinition;

/**
 * Created by Ahmed_Khan on 5/2/2016.
 */
@RepositoryDefinition(domainClass = Car.class, idClass = Long.class)
public interface CarRepository extends BaseRepository<Car, Long> {
}
