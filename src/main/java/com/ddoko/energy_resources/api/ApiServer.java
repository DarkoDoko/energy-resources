package com.ddoko.energy_resources.api;

import io.confluent.kafka.serializers.KafkaAvroSerializer;
import io.dropwizard.Application;
import io.dropwizard.jdbi3.JdbiFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.jdbi.v3.core.Jdbi;

import java.util.Properties;

public class ApiServer extends Application<ApiConfiguration> {
    @Override
    public void run(ApiConfiguration configuration, Environment environment) throws Exception {

        final JdbiFactory factory = new JdbiFactory();
        Jdbi jdbi = factory.build(environment, configuration.getDatabase(), "device-db");

        KafkaProducer producer = createProducer(configuration);
        environment.lifecycle().manage(new CloseableManaged(producer));

        environment.jersey().register(
                new DeviceEndpoint(
                        producer,
                        configuration.getTopic(), configuration.getDeviceTable(),
                        jdbi.onDemand(DeviceDAO.class))
        );
    }

    private KafkaProducer createProducer(ApiConfiguration configuration) {
        Properties properties = new Properties();

        properties.put(ProducerConfig.ACKS_CONFIG, "1");
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class.getName());

        properties.putAll(configuration.getKafka());
        return new KafkaProducer(properties);
    }

    @Override
    public void initialize(Bootstrap<ApiConfiguration> bootstrap) {
        super.initialize(bootstrap);
    }

    public static void main(String[] args) throws Exception {
        new ApiServer().run(args);
    }

}