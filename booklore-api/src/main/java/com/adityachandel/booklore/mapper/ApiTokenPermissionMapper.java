package com.adityachandel.booklore.mapper;

import com.adityachandel.booklore.model.ApiTokenPermissions;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class ApiTokenPermissionMapper implements AttributeConverter<ApiTokenPermissions, String> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(ApiTokenPermissions attribute) {
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Error serializing permissions to JSON", e);
        }
    }

    @Override
    public ApiTokenPermissions convertToEntityAttribute(String dbData) {
        try {
            return objectMapper.readValue(dbData, ApiTokenPermissions.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error deserializing JSON to Permissions", e);
        }
    }
}
