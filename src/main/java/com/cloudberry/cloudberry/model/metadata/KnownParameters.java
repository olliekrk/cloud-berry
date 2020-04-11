package com.cloudberry.cloudberry.model.metadata;

public interface KnownParameters {

    enum ConfigurationParameters {
        ; // todo

        public final String key;

        ConfigurationParameters(String key) {
            this.key = key;
        }
    }
}
