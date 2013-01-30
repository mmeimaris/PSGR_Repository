package publicspending.java.daily;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.io.IOUtils;

public class MailSender {
	
	private static final String SMTP_HOST_NAME = "smtp.gmail.com";
    private static final int SMTP_HOST_PORT = 465;
    private static final String SMTP_AUTH_USER = "m.meimaris@gmail.com";
    private static final String SMTP_AUTH_PWD  = "ointzasetsi.1";
    String path, recipient;
        
    public MailSender(String path, String recipient){
    	this.path = path;
    	this.recipient = recipient;
    }
    
    public void test() throws Exception{
        Properties props = new Properties();
 
        props.put("mail.transport.protocol", "smtps");
        props.put("mail.smtps.host", SMTP_HOST_NAME);
        props.put("mail.smtps.auth", "true");
        // props.put("mail.smtps.quitwait", "false");
 
        Session mailSession = Session.getDefaultInstance(props);
        //mailSession.setDebug(true);
        Transport transport = mailSession.getTransport();
 
        MimeMessage message = new MimeMessage(mailSession);
        // message subject
        message.setSubject("Daily Data (auto message)");
        BodyPart messageBodyPart = new MimeBodyPart();

        // Fill the message
        messageBodyPart.setText("This is an automated message. Katalaves?");
        
        // Create a multipar message
        Multipart multipart = new MimeMultipart();

        // Set text message part
        multipart.addBodyPart(messageBodyPart);

        // Part two is attachment
        messageBodyPart = new MimeBodyPart();
        //String filename = path;
        if(path!=null){
	        DataSource source = new FileDataSource(path);
	        messageBodyPart.setDataHandler(new DataHandler(source));
	        messageBodyPart.setFileName(path);
	        multipart.addBodyPart(messageBodyPart);
        }
        // Send the complete message parts
        message.setContent(multipart );
 
        message.addRecipient(Message.RecipientType.TO,
             new InternetAddress(recipient));
 
        transport.connect
          (SMTP_HOST_NAME, SMTP_HOST_PORT, SMTP_AUTH_USER, SMTP_AUTH_PWD);
 
        transport.sendMessage(message,
            message.getRecipients(Message.RecipientType.TO));        
        transport.close();
    }
    
    
}
