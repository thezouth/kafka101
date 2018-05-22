from asyncio import Future
from confluent_kafka import Producer
import logging
import json

TOPIC = 'send-mail'
BOOTSTRAP_SERVERS = 'kafka:9092'

producer = Producer({
    'bootstrap.servers': BOOTSTRAP_SERVERS
})
logger = logging.getLogger(__name__)

def callback(future: Future):
    def __callback(err, msg):
        if err:
            future.set_exception(err)
        else:
            future.set_result(msg)
    
    return __callback


async def produce(email: str, subject: str, text: str):
    future = Future()
    message = json.dumps(dict(
        email=email,
        subject=subject,
        text=text
    ))

    producer.produce(TOPIC, message.encode(), 
        key=email.encode(), callback=callback(future))

    result = await future
    logger.info('Produce result %s', result)
