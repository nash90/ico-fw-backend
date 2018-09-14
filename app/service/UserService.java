package service;

import com.google.inject.ImplementedBy;
import model.PasswordUpdate;
import model.User;
import model.UserApiModel;
import common.Translator;
import serviceImpl.UserServiceImpl;

@ImplementedBy(UserServiceImpl.class)
public interface UserService
{
	public User create(
	    UserApiModel user);

	public User getById(
	    long id);

	public User getUserByEmail(
	    String email);

	public User getUserByToken(
	    String token);

	public User getUserByActivateToken(
	    String token);

	public User update(
	    long id,
	    UserApiModel user);

	public void updatePassword(
	    long id,
	    PasswordUpdate user);

	public void sendUserCreateEmail(
	    User user,
	    Translator tran);

	public String generateRandomString();

	public String createActivationToken(
	    int timeInMicroSec);

	public void validatePassword(
	    String password,
	    Translator t);
}
