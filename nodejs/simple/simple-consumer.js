const kafkaClient = require("../dist/index.js");
const PromiseConsumer = kafkaClient.PromiseConsumer;

const topic = "test-topic";
const bootstrapServer = "localhost:9092";

const consumer = new PromiseConsumer(
    {
        "bootstrap.servers": bootstrapServer,
        "group.id": "test-group",
        "enable.auto.commit": false
    }, 
    {
        "auto.offset.reset": "earliest"
    }
);

(async () => {
    await consumer.connect();
    console.log("Consumer connected.");
    consumer.subscribe([topic]);
    const messages = await consumer.consume(10);
    for (msg of messages) {
        console.log(msg.value.toString());
        // Show more info
        // console.log(msg.partition + "@" + msg.topic + " " + msg.key.toString() + ":" + msg.value.toString());
    }
    if (messages.length > 0)
        consumer.commit();
    await consumer.disconnect(10000);
    console.log("Consumer disconnected.");
})();
