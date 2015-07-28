package io.crmcore.repository;

import io.crmcore.model.Town;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by sohan on 7/27/2015.
 */
public interface TownRepository extends PagingAndSortingRepository<Town, Long> {
}
