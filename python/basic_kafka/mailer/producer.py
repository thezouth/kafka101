from confluent_kafka import Producer


producer = Producer()


async def produce(email: str, subject: str, text: str):
    pass
