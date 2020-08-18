package com.cloudberry.cloudberry;

import com.github.fakemongo.Fongo;
import com.mongodb.MongoClient;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;

// todo: for now it is only experimental to create fake mongo fixture for tests

@TestConfiguration
public class FakeMongoConfiguration extends AbstractMongoConfiguration {
    public static final String FAKE_DB_NAME = "cloudberry_fake";

    @NotNull
    @Override
    public MongoClient mongoClient() {
        return new Fongo(FAKE_DB_NAME).getMongo();
    }

    @NotNull
    @Override
    protected String getDatabaseName() {
        return FAKE_DB_NAME;
    }
}
