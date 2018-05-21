const kafkaClient = require("promise-node-rdkafka");
const bunyan = require("bunyan");

const mailSender = require("./mail-sender");

const logger = bunyan.createLogger({ name: "mailer-consumer" });

const consumer = new kafkaClient.PromiseConsumer({
    "bootstrap.servers": "kafka:9092",
    "group.id": "mailer",
    "enable.auto.commit": false    
}, {
    "auto.offset.reset": "earliest"
});

let running = true;

(async () => {
    await consumer.connect();
    consumer.subscribe(["send-mail"]);
    try {
        while(running) {
            const messages = await consumer.consume(1);
            if (messages.length > 0) {
                const to = messages[0].key.toString();
                const msg = JSON.parse(messages[0].value.toString());
                
                logger.info("Sending mail:", to, msg);
                
                await mailSender(to, msg["subject"], msg["text"]);
                consumer.commit();
            }
        }
    } finally {
        await consumer.disconnect(10000);
    }

})().catch((err) => {
    logger.error(err);
});

process.on("SIGINT", () => { running = false; })
