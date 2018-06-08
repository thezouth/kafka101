const kafkaClient = require("promise-node-rdkafka");
const bunyan = require("bunyan");

const logger = bunyan.createLogger({ name: "producer-handler" });

const producer = new kafkaClient.PromiseProducer();

module.exports = {
    init: async function () {
    },

    handle: async function (req, res) {
        
    },

    shutdown: async function () {
        
    }
}
