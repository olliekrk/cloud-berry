package com.cloudberry.cloudberry.serialization;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.bson.types.ObjectId;

import java.io.IOException;

public class ObjectIdDeserializer extends JsonDeserializer<ObjectId> {
    @Override
    public ObjectId deserialize(
            JsonParser p, DeserializationContext ctxt
    ) throws IOException, JsonProcessingException {
        try {
            return new ObjectId(p.getValueAsString());
        } catch (IllegalArgumentException e){
            throw new JsonParseException(p, "Could not parse ObjectId token", e);
        }
    }
}
