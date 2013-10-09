package com.jccm.edu.maven.mail;

import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.net.ssl.SSLContext;

import org.codehaus.plexus.mailsender.MailSenderException;
import org.codehaus.plexus.mailsender.util.DateFormatUtils;
import org.codehaus.plexus.util.StringUtils;

public class MailHelper {
	
	private String smtpHost;
	private Integer smtpPort;
	private String username;
	private String password;
	private Boolean sslMode;

	

    public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Boolean isSslMode() {
		return sslMode;
	}

	public void setSslMode(Boolean sslMode) {
		this.sslMode = sslMode;
	}

	public String getSmtpHost() {
		return smtpHost;
	}

	public void setSmtpHost(String smtpHost) {
		this.smtpHost = smtpHost;
	}

	public Integer getSmtpPort() {
		return smtpPort;
	}

	public void setSmtpPort(Integer smtpPort) {
		this.smtpPort = smtpPort;
	}

	public MailHelper()
    {
    }

	public void initialize() throws NoSuchAlgorithmException
    {
        if(StringUtils.isEmpty(getSmtpHost()))
            System.out.println("Error in configuration: Missing smtpHost.");
        if(getSmtpPort() == 0)
            setSmtpPort(25);
        props = new Properties();
        props.put("mail.smtp.host", getSmtpHost());
        props.put("mail.smtp.port", String.valueOf(getSmtpPort()));
        if(getUsername() != null)
            props.put("mail.smtp.auth", "true");
        props.put("mail.debug", "false");
        if(isSslMode())
        {
            Security.addProvider(SSLContext.getInstance("SSLv3").getProvider());
            props.put("mail.smtp.socketFactory.port", String.valueOf(getSmtpPort()));
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            props.put("mail.smtp.socketFactory.fallback", "false");
        }

    }

    public void send(String subject, String msgContent, List<String> toAddresses, String from, String encoding) throws MailSenderException {
        
        try
        {
            Authenticator auth = null;
            if(getUsername() != null)
                auth = new Authenticator() {

                    protected PasswordAuthentication getPasswordAuthentication()
                    {
                        return new PasswordAuthentication(getUsername(), getPassword());
                    }

               };
            Session session = Session.getDefaultInstance(props, auth);
            Message msg = new MimeMessage(session);
            
            InternetAddress addressFrom = new InternetAddress(from);
            msg.setFrom(addressFrom);
            if(toAddresses.size() > 0)
            {
                InternetAddress addressTo[] = new InternetAddress[toAddresses.size()];
                int count = 0;
                for(String address :toAddresses)
                {
                    addressTo[count++] = new InternetAddress(address);
                }

                msg.setRecipients(javax.mail.Message.RecipientType.TO, addressTo);
            }
            msg.setSubject(subject);
            
            MimeMultipart multipart = new MimeMultipart();
            addPart(multipart, msgContent, encoding);
            msg.setContent(multipart);
            
            System.out.println("Mensaje: "+msgContent);
            
            msg.setHeader("Date", DateFormatUtils.getDateHeader(new Date()));
            
            Transport.send(msg);
        }
        catch(Exception e)
        {
        	e.printStackTrace();
            throw new MailSenderException("Error while sending mail.", e);
        }
    }
    
    public void addPart(MimeMultipart multipart, String content,String encoding) throws Exception {
    	 MimeBodyPart body = new MimeBodyPart();
         body = new MimeBodyPart();
         body.setDataHandler(new DataHandler(content,"text/html; charset="+encoding));

         multipart.addBodyPart(body);
    	
    }
    
    

    private Properties props;
}
