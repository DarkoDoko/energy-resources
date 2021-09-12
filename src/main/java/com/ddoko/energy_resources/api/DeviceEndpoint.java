package com.ddoko.energy_resources.api;

import io.dropwizard.util.ByteStreams;
import org.apache.kafka.clients.producer.KafkaProducer;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.nio.ByteBuffer;

import static io.dropwizard.util.ByteStreams.toByteArray;

@Path("/")
public class DeviceEndpoint {

    private final KafkaProducer producer;
    private final String topic;
    private final String table;

    private final DeviceDAO db;

    public DeviceEndpoint(KafkaProducer producer, String topic, String table, DeviceDAO db) {
        this.producer = producer;
        this.topic = topic;
        this.table = table;
        this.db = db;
    }

    @GET
    @Path("/state")
    public Response getStatus() {

        return Response.ok().build();
    }

    @POST
    @Path("/send/{uuid}")
    @Consumes({MediaType.APPLICATION_OCTET_STREAM, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response send(@PathParam("uuid") String uuid, @Context HttpServletRequest request) throws IOException {

        ByteBuffer body = ByteBuffer.wrap(toByteArray(request.getInputStream()));


        return Response.ok().build();
    }
}