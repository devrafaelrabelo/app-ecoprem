package com.controlcenter.enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class CarrierTypeConverter implements AttributeConverter<CarrierType, String> {

    @Override
    public String convertToDatabaseColumn(CarrierType attribute) {
        return attribute == null ? null : attribute.name();
    }

    @Override
    public CarrierType convertToEntityAttribute(String dbData) {
        return dbData == null ? null : CarrierType.valueOf(dbData);
    }
}
