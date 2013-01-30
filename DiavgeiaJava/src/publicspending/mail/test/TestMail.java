package publicspending.mail.test;

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

public class TestMail {

	/**
	 * @param args
	 */
	 private static final String SMTP_HOST_NAME = "smtp.gmail.com";
	    private static final int SMTP_HOST_PORT = 465;
	    private static final String SMTP_AUTH_USER = "m.meimaris@gmail.com";
	    private static final String SMTP_AUTH_PWD  = "aliceson";
	 
	    public static void main(String[] args) throws Exception{
	       //new TestMail().test();
	    	new TestMail().zipFiles();
	    }
	 
	    public void test() throws Exception{
	        Properties props = new Properties();
	 
	        props.put("mail.transport.protocol", "smtps");
	        props.put("mail.smtps.host", SMTP_HOST_NAME);
	        props.put("mail.smtps.auth", "true");
	        // props.put("mail.smtps.quitwait", "false");
	 
	        Session mailSession = Session.getDefaultInstance(props);
	        mailSession.setDebug(true);
	        Transport transport = mailSession.getTransport();
	 
	        MimeMessage message = new MimeMessage(mailSession);
	        // message subject
	        message.setSubject("Java Programming Forums");
	        BodyPart messageBodyPart = new MimeBodyPart();

	        // Fill the message
	        messageBodyPart.setText("This is message body");
	        
	        // Create a multipar message
	        Multipart multipart = new MimeMultipart();

	        // Set text message part
	        multipart.addBodyPart(messageBodyPart);

	        // Part two is attachment
	        messageBodyPart = new MimeBodyPart();
	        String filename = "C:/Users/marios/Desktop/counter.txt";
	        DataSource source = new FileDataSource(filename);
	        messageBodyPart.setDataHandler(new DataHandler(source));
	        messageBodyPart.setFileName(filename);
	        multipart.addBodyPart(messageBodyPart);

	        // Send the complete message parts
	        message.setContent(multipart );
	 
	        message.addRecipient(Message.RecipientType.TO,
	             new InternetAddress("m.meimaris@yahoo.com"));
	 
	        transport.connect
	          (SMTP_HOST_NAME, SMTP_HOST_PORT, SMTP_AUTH_USER, SMTP_AUTH_PWD);
	 
	        transport.sendMessage(message,
	            message.getRecipients(Message.RecipientType.TO));
	        transport.close();
	    }
	    
	    public void zipFiles(){
	    	try
	    	 {
	    		File inFolder=new File("C:/Users/marios/Desktop/Diavgeia Root/JSONOutput/05-09-2012");
	    		File outFolder=new File("C:/Users/marios/Desktop/Diavgeia Root/JSONOutput/Out.zip");
	    	 	ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(outFolder)));
	    	 	int len = inFolder.getAbsolutePath().lastIndexOf(File.separator);
				String baseName = inFolder.getAbsolutePath().substring(0,len+1);
				
				addFolderToZip(inFolder, out, baseName);
				
				out.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
	    	
	    	 }
	    
	    private static void addFolderToZip(File folder, ZipOutputStream zip, String baseName) throws IOException {
			File[] files = folder.listFiles();
			for (File file : files) {
				if (file.isDirectory()) {
					addFolderToZip(file, zip, baseName);
				} else {
					String name = file.getAbsolutePath().substring(baseName.length());
					ZipEntry zipEntry = new ZipEntry(name);
					zip.putNextEntry(zipEntry);
					IOUtils.copy(new FileInputStream(file), zip);
					zip.closeEntry();
				}
			}
		}
		
		
	
}
