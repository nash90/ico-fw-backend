package model;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;
import json.BaseEntity;
import json.Hidden;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserApiModel
    extends
        BaseEntity
{
	// --- FIELDS --- //

	@NotBlank(message = "VALIDATION_NAME_BLANK")
	private String name;

	@NotBlank(message = "VALIDATION_EMAIL_BLANK")
	@Email(message = "VALIDATION_EMAIL_FORMAT")
	private String email;

	@NotBlank(message = "VALIDATION_PASSWORD_BLANK")
	@Hidden
	private String password;

	private String walletAddress;

}
