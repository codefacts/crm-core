package io.crmcore.repository;

import io.crmcore.model.ConsumerContact;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Date;

public interface ConsumerContactRepository extends PagingAndSortingRepository<ConsumerContact, Long> {
    public long countAllByDateBetween(Date startDate, Date endDate);
}
