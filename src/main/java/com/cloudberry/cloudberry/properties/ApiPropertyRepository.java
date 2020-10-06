package com.cloudberry.cloudberry.properties;

import com.cloudberry.cloudberry.properties.model.ApiProperty;
import org.springframework.data.repository.CrudRepository;

public interface ApiPropertyRepository extends CrudRepository<ApiProperty, String> {
}
