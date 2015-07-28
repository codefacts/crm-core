package io.crmcore.repository;

import io.crmcore.model.UserBasic;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by sohan on 7/15/2015.
 */
public interface UserBasicRepository extends PagingAndSortingRepository<UserBasic, Long> {
}
