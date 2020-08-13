package com.cloudberry.cloudberry.serialization;

import com.fasterxml.jackson.databind.module.SimpleModule;
import org.bson.types.ObjectId;

public class CloudberryJacksonModule extends SimpleModule {
    public CloudberryJacksonModule() {
        super(CloudberryJacksonModule.class.getSimpleName());
        addCustomSerializers();
    }

    private void addCustomSerializers() {
        this.addSerializer(ObjectId.class, new ObjectIdSerializer());
    }
}
