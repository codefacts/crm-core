package io.crmcore.repository;

import io.crmcore.model.Client;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by someone on 15-Jul-2015.
 */
public interface ClientRepository extends PagingAndSortingRepository<Client, Long>{
}
