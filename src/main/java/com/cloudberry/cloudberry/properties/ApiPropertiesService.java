package com.cloudberry.cloudberry.properties;

import com.cloudberry.cloudberry.properties.model.ApiProperty;
import com.cloudberry.cloudberry.properties.model.PropertyId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ApiPropertiesService {

    private final ApiPropertyRepository apiPropertyRepository;

    public String getOrDefault(PropertyId id, String defaultValue) {
        return apiPropertyRepository.findById(id.getId())
                .map(ApiProperty::getValue)
                .orElse(defaultValue);
    }

    public void set(PropertyId id, String value) {
        apiPropertyRepository.save(new ApiProperty(id.getId(), value));
    }

    public void reset(PropertyId id) {
        apiPropertyRepository.deleteById(id.getId());
    }

    public Optional<String> get(PropertyId id) {
        return apiPropertyRepository.findById(id.getId()).map(ApiProperty::getValue);
    }

    public List<ApiProperty> getAll() {
        return apiPropertyRepository.findAll();
    }
}
