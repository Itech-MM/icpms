package org.flexitech.projects.icpms.persistence.entities.operator;

import org.flexitech.projects.icpms.common.TableNames;
import org.flexitech.projects.icpms.persistence.BasedEntity;
import org.flexitech.projects.icpms.persistence.entities.site.Site;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = TableNames.OPERATOR_TBL)
@Getter
@Setter
public class Operator extends BasedEntity {

	private String name;
	private String username;
	private String password;

	@Column(name = "phone_number")
	private String phoneNumber;

	@ManyToOne
	@JoinColumn(name = "site_id")
	private Site site;

	private Integer role;

	private Integer status = 1;
	
	@Column(name = "pin_password", unique = true)
	private String pinPassword;
	
	@Column(name = "rfid_token", unique = true)
	private String rfidToken;
	
	@Column(name = "qr_code_token", unique = true)
	private String qrCodeToken;
	
	@Column(name = "qr_image_path", unique = true)
	private String qrImagePath;
	
	@Column(name = "stripe_token", unique = true)
	private String stripeToken;
}
