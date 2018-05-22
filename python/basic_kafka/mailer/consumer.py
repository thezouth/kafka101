import json

from confluent_kafka import Consumer
from .send_mail import send_mail

consumer = Consumer({
    'bootstrap.servers': 'kafka:9092',
    'group.id': 'mailer-2',
    'enable.auto.commit': False,
    'default.topic.config': {
        'auto.offset.reset': 'largest'
    }
})

consumer.subscribe(['send-mail'])

try:
    while True:
        kafka_msg = consumer.poll(1)

        if kafka_msg:
            msg_value = json.loads(kafka_msg.value().decode('UTF-8'))
            send_mail(msg_value['email'], msg_value['subject'], msg_value['text'])

            consumer.commit()
finally:
    consumer.close()
