package com.cloudberry.cloudberry.properties;

import com.cloudberry.cloudberry.properties.model.ApiProperty;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ApiPropertyRepository extends CrudRepository<ApiProperty, String> {
    @NotNull
    List<ApiProperty> findAll();
}
