package com.cloudberry.cloudberry;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@DirtiesContext
@ContextConfiguration(classes = {CloudberryApplication.class})
@DataMongoTest(properties = {"spring.data.mongodb.port=37017"})
@ExtendWith(SpringExtension.class)
public @interface EmbeddedMongoTest {
}
