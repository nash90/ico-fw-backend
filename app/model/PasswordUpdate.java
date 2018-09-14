package model;

import org.hibernate.validator.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordUpdate
{
	private String oldPassword;

	@NotBlank(message = "VALIDATION_NOT_BLANK")
	private String newPassword;
}
