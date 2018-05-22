from confluent_kafka import Consumer

def main():
    consumer = Consumer({
        'bootstrap.servers': 'kafka:9092',
        'group.id': 'logger',
        'enable.auto.commit': False,
        'default.topic.config': {'auto.offset.reset': 'smallest'}
    })
    consumer.subscribe(['test'])

    try:
        while True:
            msg = consumer.poll(timeout=1.0)
            if msg:
                print(msg.key() if msg.key() else 'No-key', msg.value().decode())
                consumer.commit()
    finally:
        consumer.close()


if __name__ == '__main__':
    main()


