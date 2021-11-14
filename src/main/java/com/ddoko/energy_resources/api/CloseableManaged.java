package com.ddoko.energy_resources.api;

import io.dropwizard.lifecycle.Managed;

import java.io.Closeable;

public record CloseableManaged(Closeable closeable) implements Managed {

    public void start() throws Exception {
    }

    public void stop() throws Exception {
        closeable.close();
    }
}
