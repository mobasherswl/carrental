package com.epam.server.repo;

import com.epam.server.model.RentalClass;
import org.springframework.data.repository.RepositoryDefinition;

@RepositoryDefinition(domainClass = RentalClass.class, idClass = Long.class)
public interface RentalClassRepository extends BaseRepository<RentalClass, Long> {
}
