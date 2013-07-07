/*******************************************************************************
 * Copyright (c) 2013, Christos Sotiriou
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted 
 * provided that the following conditions are met:
 * 
 * -- Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * -- Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, 
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, 
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/
package com.oramind.messengers;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.oramind.dao.PreferenceDao;
import com.oramind.db.DBProperties;
import com.oramind.util.Util;


/**
 * 
 * @author Christos Sotiriou
 *
 */
public class MailMessenger extends AbstractMessenger{

	String mailContent = null;
	String toMail = null;


	public MailMessenger(String content, String targetEmail){
		mailContent = content;
		toMail = targetEmail;
	}



	@Override
	public MessageOperationResult sendMessage() throws Exception {
//		return sendOramindMessage();
		return sendMessageSSL();
	}

	public MessageOperationResult sendMessageUsingSSL() throws MessagingException{
		return sendMessageSSL();
	}

	
	/**
	 * Send a mail message using Google's SSL service
	 * @return
	 * @throws MessagingException
	 */
	private MessageOperationResult sendMessageSSL() throws MessagingException{
		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.socketFactory.port", "465");
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", "465");


		final String username = DBProperties.getInstance().getProperty("mailusername");
		final String password = DBProperties.getInstance().getProperty("mailpassword");

		Session session = Session.getInstance(props, new javax.mail.Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});
		
		MessageOperationResult result = null;

		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(username));
			message.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse(toMail));
			message.setSubject("InfoWeb Registration e-mail");
			message.setText(mailContent);

			Transport.send(message);

			Util.println("Done");
			result = new MessageOperationResult(true);
		} catch (MessagingException e) {
			e.printStackTrace();
			throw e;
		}
		return result;
	}

	
	/**
	 * Send an e-mail using Oramind's mail server. The username is provided into the DB.properties file
	 * and the password is stored in the database
	 * @return
	 * @throws MessagingException
	 */

	public MessageOperationResult sendOramindMessage() throws MessagingException{
		throw new MessagingException("Oramind server information is not provided in the public release");
	}
	


}
