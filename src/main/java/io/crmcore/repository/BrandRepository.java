package io.crmcore.repository;

import io.crmcore.model.Brand;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by someone on 07-Jun-2015.
 */
public interface BrandRepository extends PagingAndSortingRepository<Brand, Long> {
    Brand findByName(String brand);
}
