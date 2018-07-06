const kafkaClient = require("../dist/index.js");
const PromiseProducer = kafkaClient.PromiseProducer;

const bootstrapServer = "localhost:9092";

const producer = new PromiseProducer({"bootstrap.servers": bootstrapServer}, {});

(async () => {
    try {
        await producer.connect();
        console.log("Producer connected.");
        producer.produce("test-topic", Buffer.from("Hello"));
        await producer.disconnect(2000);
        console.log("Producing done.");
    } catch (err) {
        console.log("Error", err);
        process.exit(1);
    }
    
})();
