package model;

import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;
import json.BaseEntity;
import json.Hidden;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "user")
public class User
    extends
        BaseEntity
{
	// --- NESTED TYPES --- //

	public static enum Status
	{
			PENDING_VERIFICATION,
			ACTIVE,
			SUSPENDED
	}

	// --- FIELDS --- //

	@Column(name = "name", length = 255)
	@NotBlank(message = "VALIDATION_NAME_BLANK")
	private String name;

	@Column(name = "email", length = 255)
	@NotBlank(message = "VALIDATION_EMAIL_BLANK")
	@Email(message = "VALIDATION_EMAIL_FORMAT")
	private String email;

	@Column(name = "password", length = 255)
	@NotBlank(message = "VALIDATION_NOT_BLANK")
	@Hidden
	private String password;

	@Column(name = "login_token", length = 255)
	@Hidden
	private String loginToken;

	@Column(name = "activate_token", length = 255)
	@Hidden
	private String activateToken;

	@Column(name = "wallet_address", length = 255)
	private String walletAddress;

	@Column(name = "referral_email", length = 255)
	private String referralEmail;

	@Column(name = "token_purchased")
	private BigDecimal tokenPurchased;

	@Column(name = "unsent_token")
	private BigDecimal unsentToken;

	@Column(name = "presale_bonus")
	private BigDecimal presaleBonus;

	@Column(name = "referral_bonus")
	private BigDecimal referralBonus;

	@Column(name = "status")
	private Status status;

}
