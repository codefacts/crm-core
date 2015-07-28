package io.crmcore.repository;

import io.crmcore.model.Employee;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by someone on 15-Jul-2015.
 */
public interface EmployeeRepository extends PagingAndSortingRepository<Employee, Long> {
}
