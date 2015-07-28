package io.crmcore.repository;

import io.crmcore.model.Admin;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by someone on 15-Jul-2015.
 */
public interface AdminRepository extends PagingAndSortingRepository<Admin, Long> {
}
