package com.epam.server.repo;

import com.epam.server.model.Customer;
import org.springframework.data.repository.RepositoryDefinition;

/**
 * Created by Ahmed_Khan on 5/2/2016.
 */
@RepositoryDefinition(domainClass = Customer.class, idClass = Long.class)
public interface CustomerRepository extends BaseRepository<Customer, Long> {
}
