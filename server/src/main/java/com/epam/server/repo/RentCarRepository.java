package com.epam.server.repo;

import com.epam.server.model.RentCar;
import org.springframework.data.repository.RepositoryDefinition;

/**
 * Created by Ahmed_Khan on 5/13/2016.
 */
@RepositoryDefinition(domainClass = RentCar.class, idClass = Long.class)
public interface RentCarRepository extends BaseRepository<RentCar, Long> {
}
