package io.crmcore.repository;

import io.crmcore.model.Area;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by someone on 07-Jun-2015.
 */
public interface AreaRepository extends PagingAndSortingRepository<Area, Long> {
    Area findByName(String s);
}
