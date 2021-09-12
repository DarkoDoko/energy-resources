package com.ddoko.energy_resources.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

public class ApiConfiguration extends Configuration {

    private String deviceTable;

    @NotNull
    private Map<String, String> kafka = new HashMap<>();

    @NotNull
    private String topic;

    @Valid
    @NotNull
    private DataSourceFactory database = new DataSourceFactory();

    @JsonProperty("deviceTable")
    public String getDeviceTable() {
        return deviceTable;
    }

    @JsonProperty("deviceTable")
    public void setDeviceTable(String deviceTable) {
        this.deviceTable = deviceTable;
    }

    @JsonProperty("kafka")
    public Map<String, String> getKafka() {
        return kafka;
    }

    @JsonProperty("kafka")
    public void setKafka(Map<String, String> kafka) {
        this.kafka = kafka;
    }

    @JsonProperty("topic")
    public String getTopic() {
        return topic;
    }

    @JsonProperty("topic")
    public void setTopic(String topic) {
        this.topic = topic;
    }

    @JsonProperty("database")
    public DataSourceFactory getDatabase() {
        return database;
    }

    @JsonProperty("database")
    public void setDatabase(DataSourceFactory database) {
        this.database = database;
    }
}
