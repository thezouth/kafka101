const nodemailer = require("nodemailer");
const bunyan = require("bunyan");

const logger = bunyan.createLogger({ name: "mail-sender" });

const transport = nodemailer.createTransport({
    host: "smtp.gmail.com",
    port: 465,
    secure: true,
    auth: {
        user: "elastalert.zouth@gmail.com",
        pass: process.env.PASSWORD
    },
    logger: true
});

module.exports = function (who, subject, text) {
    return new Promise((resolve, reject) => {
        transport.sendMail({
            to: who,
            subject: subject,
            text: text
        }, (err, info) => {
            if (err) {
                logger.error(err);
                reject(err);
            } else {
                logger.info("Message to " + who + " sent.", info.response);
                resolve(info);
            }
        });
    });
}