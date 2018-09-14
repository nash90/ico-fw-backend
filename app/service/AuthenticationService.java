package service;

import com.google.inject.ImplementedBy;
import model.Login;
import model.PasswordReset;
import model.User;
import common.Translator;
import serviceImpl.AuthenticationServiceImpl;

@ImplementedBy(AuthenticationServiceImpl.class)
public interface AuthenticationService
{
	public String login(
	    Login login);

	public User activateUser(
	    String activateToken);

	public String resetPasswordRequest(
	    String email,
	    Translator tran);

	public User resetPassword(
	    String token,
	    String email,
	    PasswordReset passwordreset);
}
