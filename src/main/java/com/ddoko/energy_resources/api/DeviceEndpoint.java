package com.ddoko.energy_resources.api;

import com.ddoko.energy_resources.devices.raw.RawRecord;
import com.google.common.collect.ImmutableMap;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Path("/")
public class DeviceEndpoint {

    private final KafkaProducer<String, Object> producer;
    private final String topic;
    private final String table;

    private final DeviceDAO db;

    public DeviceEndpoint(KafkaProducer<String, Object> producer, String topic, String table, DeviceDAO db) {
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
    public Response send(@PathParam("uuid") String uuid, @Context HttpServletRequest request) throws IOException, ExecutionException, InterruptedException {

        ByteBuffer body = ByteBuffer.wrap(request.getInputStream().readAllBytes());
        RawRecord payload = new RawRecord(uuid, Instant.now().toEpochMilli(), body);

        ProducerRecord<String, Object> kafkaRecord = new ProducerRecord<>(topic, uuid, payload);
        Future<RecordMetadata> metadata = producer.send(kafkaRecord);

        return Response.ok().entity(serialize(metadata.get())).build();
    }

    @GET
    @Path("/state")
    public Response getStatus(@QueryParam("uuid") String uuid){
        return Response.ok().entity(db.getDeviceState(table, uuid)).build();
    }

    private Map<String, Object> serialize(RecordMetadata metadata) {
        return ImmutableMap.<String, Object>builder()
                .put("offset", metadata.offset())
                .put("partition", metadata.partition())
                .put("topic", metadata.topic())
                .put("timestamp", metadata.timestamp())
                .build();
    }
}
