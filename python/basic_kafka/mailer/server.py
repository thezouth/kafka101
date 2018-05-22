from sanic import Sanic, response

from . import auto_poll, producer

app = Sanic()

@app.listener('after_server_start')
def start_poll_loop(app, loop):
    auto_poll.start(producer.producer, 1, loop)

@app.post('/mailto/<email>')
async def mailto(request, email):
    data = request.json
    subject = data.get('subject')
    text = data.get('text')

    if text:
        await producer.produce(email, subject, text)
        return response.json({'email': email, 'text': text})
    else:
        return response.json(
            {'error': 'Blank Message'},
            status=400
        )


if __name__ == '__main__':
    app.run(host='0.0.0.0', port=8080)
