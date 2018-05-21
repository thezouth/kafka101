import smtplib

from email.mime.text import MIMEText
from email.mime.multipart import MIMEMultipart

class Mailer:
  #def __init__(host, port, username, password):
  def __init__(self):
    self.host = "smtp.gmail.com" #host
    self.port = 465 #port
    self.username = "" #username
    self.password = "" #password

  def send(self, subject, mail_from, mail_to, message):
    smtp = smtplib.SMTP_SSL(self.host, self.port)
    smtp.login(self.username, self.password)
    
    mm = MIMEMultipart()
    mm['Subject'] = subject
    mm['From'] = mail_from
    mm['To'] = mail_to
    msg = MIMEText('<h1>' + message + '</h1>', 'html')
    mm.attach(msg)
    smtp.send_message(mm)
    smtp.close()
