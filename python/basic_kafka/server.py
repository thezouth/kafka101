import asyncio
import uvloop
import time

from sanic import Sanic
from sanic.response import json
from confluent_kafka import Producer

asyncio.set_event_loop_policy(uvloop.EventLoopPolicy())

app =Sanic()

bootstrap_servers = '192.168.0.17:9092'
topic = 'test'

conf = {
    'bootstrap.servers': bootstrap_servers
}

producer = Producer(conf)

loop = asyncio.new_event_loop()
asyncio.set_event_loop(loop)

def delivery_report(err, msg):
  if err is not None:
    print(f'Message dilivery failed: {err}')
  else:
    print(f'deliver: topic={msg.topic()} partition={msg.partition()} offset={msg.offset()}')
  

@app.post('/mailto/<email>')
async def mailto(request, email):
  data = request.json
  subject = data.get('subject')
  text = data.get('text')

  if text:
    producer.produce(topic, text, callback=delivery_report)
    producer.flush()

  print(f'send {text}')
  return json({'email': email, 'text': text})

if __name__ == '__main__':
  app.run(host='0.0.0.0', port=8080)
