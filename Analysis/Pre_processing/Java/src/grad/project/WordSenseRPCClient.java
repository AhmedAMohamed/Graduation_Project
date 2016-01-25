package grad.project;

import com.rabbitmq.client.*;
import com.rabbitmq.client.AMQP.BasicProperties;
/**
 * Created by yousefhamza on 1/25/16.
 */
public class WordSenseRPCClient {
    private Connection connection;
    private Channel channel;
    private String requestQueueName = "WSD_RPC";
    private String replyQueueName;
    private QueueingConsumer consumer;

    public WordSenseRPCClient() throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        connection = factory.newConnection();
        channel = connection.createChannel();

        replyQueueName = channel.queueDeclare().getQueue();
        consumer = new QueueingConsumer(channel);
        channel.basicConsume(replyQueueName, true, consumer);
    }

    public String call(String message) throws Exception {
        String response = null;
        String corrId = java.util.UUID.randomUUID().toString();

        BasicProperties props = new BasicProperties
                .Builder()
                .correlationId(corrId)
                .replyTo(replyQueueName)
                .build();

        channel.basicPublish("", requestQueueName, props, message.getBytes());

        while (true) {
            QueueingConsumer.Delivery delivery = consumer.nextDelivery();
            if (delivery.getProperties().getCorrelationId().equals(corrId)) {
                response = new String(delivery.getBody());
                break;
            }
        }

        return response;
    }

    public void close() throws Exception {
        connection.close();
    }
}
