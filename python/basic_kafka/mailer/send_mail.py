import smtplib
import os
import logging

HOST = 'smtp.gmail.com'
PORT = 465
USERNAME = os.environ['USERNAME']
PASSWORD = os.environ['PASSWORD']

logger = logging.getLogger(__name__)
logger.setLevel(logging.INFO)

logger.info('Connect to %s:%d with %s/%s', HOST, PORT, USERNAME, PASSWORD)
smtp_client = smtplib.SMTP_SSL(HOST, PORT)
smtp_client.login(USERNAME, PASSWORD)

def send_mail(mail_to: str, subject: str, message: str):
    body = f'From: {USERNAME}\r\n' + \
        f'To: {mail_to}\r\n' + \
        f'Subject: {subject}\r\n\r\n' + \
        message

    smtp_client.sendmail(USERNAME, mail_to, body)
