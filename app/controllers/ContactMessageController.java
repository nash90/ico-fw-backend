package controllers;

import javax.inject.Inject;

import play.mvc.Result;
import serviceImpl.EmailService;
import play.data.DynamicForm;
import play.data.FormFactory;

/**
 * This controller contains an action to receive information from
 * user contact form and generate email to company email
 */
public class ContactMessageController
    extends
        BaseController
{

	private final EmailService	emailService;
	private final FormFactory	formFactory;

	@Inject
	public ContactMessageController(
	                                EmailService emailService,
	                                FormFactory formFactory)
	{
		this.emailService = emailService;
		this.formFactory = formFactory;
	}

	public Result contactEmail()
	{

		DynamicForm requestData = formFactory.form()
		                                     .bindFromRequest();
		String senderName = requestData.get("Name");
		String senderEmail = requestData.get("Email");
		String body = requestData.get("Body");

		emailService.sendContactEmail(senderName, senderEmail, body);

		return ok();
	}

}
