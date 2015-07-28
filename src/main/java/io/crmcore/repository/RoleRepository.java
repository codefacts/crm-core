package io.crmcore.repository;

import io.crmcore.model.Role;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by someone on 15-Jul-2015.
 */
public interface RoleRepository extends PagingAndSortingRepository<Role, Long> {
}
