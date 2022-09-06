package me.cutehammond.pill.global.common;

import org.bson.types.ObjectId;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class ObjectIdConverter implements AttributeConverter<ObjectId, String> {

    @Override
    public String convertToDatabaseColumn(ObjectId attribute) {
        return attribute.toHexString();
    }

    @Override
    public ObjectId convertToEntityAttribute(String dbData) {
        return new ObjectId(dbData);
    }

}
