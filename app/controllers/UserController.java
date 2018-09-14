package controllers;

import javax.inject.Inject;
import model.PasswordUpdate;
import model.User;
import model.UserApiModel;
import common.filter.WithModel;
import common.filter.WithValidation;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Result;
import service.AuthorizationService;
import service.UserService;

@Transactional
public class UserController
    extends
        BaseController
{
	// --- FIELDS --- //

	private final UserService userService;

	// --- CONSTRUCTORS --- //

	@Inject
	public UserController(
	                      UserService userService)
	{
		this.userService = userService;
	}

	@WithModel(UserApiModel.class)
	@WithValidation(selective = false)
	public Result createUser()
	{
		UserApiModel userApiModel = entity();
		userService.validatePassword(userApiModel.getPassword(), getTranslator());

		User user = userService.create(userApiModel);
		userService.sendUserCreateEmail(user, getTranslator());
		return ok(Json.toJson(user));
	}

	public Result getUserById(
	    long id)
	{
		AuthorizationService auth = getAuth();
		auth.isLoggedIn();

		id = auth.getValidOwnerId(id);
		User user = userService.getById(id);

		return ok(Json.toJson(user));
	}

	@WithModel(UserApiModel.class)
	@WithValidation(selective = true)
	public Result update(
	    long id)
	{
		AuthorizationService auth = getAuth();
		auth.isLoggedIn();

		id = auth.getValidOwnerId(id);
		User user = userService.update(id, entity());
		return ok(Json.toJson(user));
	}

	@WithModel(PasswordUpdate.class)
	@WithValidation(selective = true)
	public Result updatePassword(
	    long id)
	{
		AuthorizationService auth = getAuth();
		auth.isLoggedIn();

		PasswordUpdate updatePass = entity();
		userService.validatePassword(updatePass.getNewPassword(), getTranslator());

		id = auth.getValidOwnerId(id);
		userService.updatePassword(id, updatePass);

		return ok();
	}
}
