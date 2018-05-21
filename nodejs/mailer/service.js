const express = require("express");
const bodyParser = require("body-parser");
const producerHandler = require("./producer-handler");

const bunyan = require("bunyan");

const app = new express();
const logger = bunyan.createLogger({ name: "producer-service"});

app.use(bodyParser.json());
app.post("/mail-to/:who", async (req, res) => producerHandler.handle(req, res));

let server = null;

(async () => {
    try {
        await producerHandler.init();
        server = app.listen(3000, "0.0.0.0", 
                    () => logger.info("Service started."));
    } catch (err) {
        logger.error(err);
    }
})();

process.on("SIGINT", async () => {
    try {
        if (server)
            server.shutdown();
        await producerHandler.shutdown();
    } catch (err) {
        logger.error(err);
    }
})
