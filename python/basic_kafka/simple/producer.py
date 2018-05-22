from confluent_kafka import Producer

def delivery_callback(err, msg):
    if err:
        print('delivery fail {err}')
    else:
        print('producer at {msg.offset}')


def main():
    bootstrap_servers = '192.168.0.17:9092'
    topic = 'test'

    conf = {
        'bootstrap.servers': bootstrap_servers
    }

    producer = Producer(conf)

    producer.produce(topic, 'hello', callback=delivery_callback)
    producer.flush()

if __name__ == '__main__':
    main()
