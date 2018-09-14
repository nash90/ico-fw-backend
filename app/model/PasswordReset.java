package model;

import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Email;
import json.BaseEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordReset
    extends
        BaseEntity
{
	@NotNull(message = "ERR_VALIDATION_NO_TOKEN")
	private String code = null;

	@NotNull(message = "ERR_VALIDATION_NO_EMAIL")
	@Email(message = "ERR_VALIDATION_INVALID_EMAIL")
	private String email = null;

	@NotNull(message = "ERR_VALIDATION_NO_NEW_PASSWORD")
	private String password = null;
}
