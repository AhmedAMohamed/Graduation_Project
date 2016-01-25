import pika
from WSD import getSense


QUEUE_NAME = 'WSD_RPC'

connection = pika.BlockingConnection(pika.ConnectionParameters(host="localhost"))

channel = connection.channel()

channel.queue_declare(queue=QUEUE_NAME)


def on_request(ch, method, props, body):
    print ' [.] received: ', body
    response = getSense(body)

    ch.basic_publish(
        exchange='',
        routing_key=props.reply_to,
        properties=pika.BasicProperties(
            correlation_id=props.correlation_id
        ),
        body=response
    )

    ch.basic_ack(delivery_tag=method.delivery_tag)

channel.basic_qos(prefetch_count=1)
channel.basic_consume(on_request, queue=QUEUE_NAME)

print " [x] Awaiting RPC requests"
channel.start_consuming()
