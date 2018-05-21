from confluent_kafka import Producer

def delivery_callback(err, msg):
    if err:
        print('delivery fail {err}')


def main():
    bootstrap_servers = '192.168.0.17:9092'
    topic = 'test'

    conf = {
        'bootstrap.servers': bootstrap_servers
    }

    producer = Producer(conf)

    producer.produce(topic, 'hello')
    producer.flush()

if __name__ == '__main__':
    main()
