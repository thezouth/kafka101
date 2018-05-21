import atexit
from confluent_kafka import Consumer
from mailer import Mailer

def main():
    conf = {
            'bootstrap.servers': '192.168.0.17:9092',
            'group.id': 'consumer003',
            'enable.auto.commit': 'false',
            'default.topic.config': {'auto.offset.reset': 'smallest'}
            }
    consumer = Consumer(conf)
    consumer.subscribe(['test'])
    def close():
        consumer.close()

    atexit.register(close)

    while True:
        msg = consumer.poll(timeout=1.0)
        if msg.error():
            print('End of Message')
            raise Exception(msg.error())
        else:
            print(msg.headers())
            print(msg.key(), msg.value())

            m = Mailer()
            m.send('kafka message', 'elastalert.zouth@gmail.com', 'spitsanu@gmail.com', msg.value().decode('utf-8'))

if __name__ == '__main__':
    main()


