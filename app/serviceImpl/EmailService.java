package serviceImpl;

import javax.inject.Inject;
import play.Logger;
import play.libs.mailer.Email;
import play.libs.mailer.MailerClient;
import serviceImpl.ApplicationConfigurationService.CONFIGURATION;

public class EmailService
{

	// --- STATIC FIELDS --- //
	private static final String	SENDING_EMAIL_SUCCESS	= "Success sending email to ";
	private static final String	ERROR_SENDING_EMAIL		= "Error sending email";
	private static final String	EMAIL_SUBJECT_PREFIX	= "Contact From ";

	private final MailerClient									mailer;
	private final serviceImpl.ApplicationConfigurationService	config;

	// --- CONSTRUCTORS --- //

	@Inject
	public EmailService(
	                    MailerClient mailer,
	                    serviceImpl.ApplicationConfigurationService config)
	{
		this.mailer = mailer;
		this.config = config;
	}

	// --- METHODS --- //

	public void sendEmail(
	    Email email)
	{
		new Thread()
		{
			public void run()
			{
				Thread.currentThread()
				      .setContextClassLoader(getClass().getClassLoader());
				try
				{
					mailer.send(email);
					Logger.info(SENDING_EMAIL_SUCCESS + email.getTo()
					                                         .get(0));
				} catch (Exception e)
				{
					Logger.error(ERROR_SENDING_EMAIL, e);
				}
			}
		}.start();
	}

	public void sendGeneralEmail(
	    String from,
	    String to,
	    String subject,
	    String body)
	{
		Email email = new Email();
		email.setSubject(subject)
		     .setFrom(from)
		     .addTo(to)
		     .setBodyText(body);
		sendEmail(email);
	}

	public void sendContactEmail(
	    String senderName,
	    String senderEmail,
	    String content)
	{
		String receiver = CONFIGURATION.CONTACT_EMAIL.getValue();
		sendGeneralEmail(senderEmail, receiver, EMAIL_SUBJECT_PREFIX + senderName, content);
	}
}
