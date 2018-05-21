const kafkaClient = require("promise-node-rdkafka");
const bunyan = require("bunyan");

const logger = bunyan.createLogger({ name: "producer-handler" });

const producer = new kafkaClient.PromiseProducer({
    "bootstrap.servers": "kafka:9092"
});

module.exports = {
    init: async function () {
        // Let's initial connection here.
        await producer.connect();
    },

    handle: async function (req, res) {
        const who = req.params.who;
        const subject = req.body.subject;
        const text = req.body.text;
    
        // Let's produce incomming request to kafka.
        logger.info("Sending mail to ", who);
        try {
            const message = JSON.stringify({
                "subject": subject,
                "text": text + "\nfrom Kafka."
            });
    
            await producer.produce("send-mail", Buffer.from(message), Buffer.from(who));
            
            logger.info("Message.send");
            res.send("OK!");

        } catch (err) {
            logger.error(err);
            res.status(500).send(err);
        }
    },

    shutdown: async function () {
        await producer.disconnect();
    }
}
