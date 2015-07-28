package io.crmcore.repository;

import io.crmcore.model.Address;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by someone on 15-Jul-2015.
 */
public interface AddressRepository extends PagingAndSortingRepository<Address, Long> {
}
