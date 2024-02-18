package com.reset.password.demo.app;

import java.io.InputStream;
import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;


@Component
@PropertySource("classpath:email.properties")
public class EmailService {
	private static final Logger log = Logger.getLogger(EmailService.class);

	@Autowired
	private PersonService peesonStub;
	@Autowired
	private AccessTokenService  accessTokenService ;
	
	@Value("${emailUsername}")
	private String emailUsername;
	@Value("${emailPassword}")
	private String emailPassword;
	private static final String RESET_PASS_URL_REPLACEMENT = "inject-reset-password-url" ;

	public boolean sendMail(MailMessageRequest messageRequest) {
		if (!ResetPasswordDemoUtil.emailValidator(messageRequest.getSentTo())) {
			return false;
		}
     Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");
		props.setProperty("mail.transport.protocol", "smtp");

		Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(emailUsername,emailPassword);
			}
		});
		try {
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(emailUsername, "Reset-Password Application"));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(messageRequest.getSentTo()));
			message.setSubject(messageRequest.getSubject());
			message.setText("Reset-Password Application");
			message.setContent(messageRequest.getBody(), "text/html");
			message.setReplyTo(messageRequest.getReplayTo());
			Transport.send(message);
			log.info("An e-mail has been sent to " + messageRequest.getSentTo() + " at --> " + new Date());
		} catch (Exception e) {
			log.info("Reset password e-mail to ------> " + messageRequest.getSentTo() + " has faild.", e);
			return false;
		}
		return true;
	}

	public MailMessageRequest prepareResetPasswordMail(String sendConformationMailTo ,HttpServletRequest req) {
		Person temp = peesonStub.findByEmail(sendConformationMailTo);
		if (temp == null) {
			return null;
		}
		String token = ResetPasswordDemoUtil.getSaltString();
		String body = createResetPasswordTemplate(req,token,sendConformationMailTo) ;
		MailMessageRequest message = new MailMessageRequest(sendConformationMailTo,"Reset password for your Reset-Password-Demo account",body,"donotrelplay@reset.com");
		accessTokenService.addAccessToken(sendConformationMailTo, token);
		return message ;

	}
	
	public boolean sendResetPasswordMail(String sendConformationMailTo ,HttpServletRequest req) {
		MailMessageRequest message = prepareResetPasswordMail(sendConformationMailTo,req) ;
		if (message == null) {
			return false ;
		}
		return sendMail(message);
	}
	
private String createResetPasswordTemplate(HttpServletRequest req, String token ,String sendConformationMailTo)   {
		String host = "http"+ResetPasswordDemoUtil.getMachineHostName(req);
		String urlToInject =null;
		String content = null;
	try {
		 urlToInject = host+ "/reset.html?token=" + token+ "&email=" + sendConformationMailTo;
		 InputStream is = getClass().getResourceAsStream("/reset-password-mail.html");
		 content =ResetPasswordDemoUtil.getEmailTemplateFromClasspath(is) ;
	} catch (Exception e) {
		log.error("Faild to inject \"Reset Password URL\" in Rest password Email template" , e);
	}
	   content = content.replace(RESET_PASS_URL_REPLACEMENT , urlToInject) ;
	   return content;
}


}
