package com.ddoko.energy_resources;

import com.ddoko.energy_resources.api.CloseableManaged;
import com.ddoko.energy_resources.api.DeviceDAO;
import com.ddoko.energy_resources.api.DeviceEndpoint;
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

public class EnergyResourcesApplication extends Application<EnergyResourcesApplicationConfiguration> {
    @Override
    public void run(EnergyResourcesApplicationConfiguration configuration, Environment environment) throws Exception {

        final var factory = new JdbiFactory();
        Jdbi jdbi = factory.build(environment, configuration.getDatabase(), "device-db");

        KafkaProducer<String, Object> producer = createProducer(configuration);
        environment.lifecycle().manage(new CloseableManaged(producer));

        environment.jersey().register(
                new DeviceEndpoint(
                        producer,
                        configuration.getTopic(), configuration.getDeviceTable(),
                        jdbi.onDemand(DeviceDAO.class))
        );
    }

    private KafkaProducer<String, Object> createProducer(EnergyResourcesApplicationConfiguration configuration) {
        Properties properties = new Properties();

        properties.put(ProducerConfig.ACKS_CONFIG, "1");
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class.getName());

        properties.putAll(configuration.getKafka());
        return new KafkaProducer<>(properties);
    }

    @Override
    public void initialize(Bootstrap<EnergyResourcesApplicationConfiguration> bootstrap) {
        super.initialize(bootstrap);
    }

    public static void main(String[] args) throws Exception {
        new EnergyResourcesApplication().run(args);
    }

}
