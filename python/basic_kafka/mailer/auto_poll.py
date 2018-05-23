import asyncio
from contextlib import suppress


async def __poll_loop(producer, interval_sec: float):
    with suppress(asyncio.CancelledError):
        while True:
            producer.poll(0)
            await asyncio.sleep(interval_sec)

def start(producer, interval_sec: float = 0.1, loop=None):
    if not loop:
        loop = asyncio.get_event_loop()

    return loop.create_task(__poll_loop(producer, interval_sec))
