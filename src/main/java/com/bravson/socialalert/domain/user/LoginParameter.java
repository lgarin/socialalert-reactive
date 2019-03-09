package com.bravson.socialalert.domain.user;


import javax.validation.constraints.NotEmpty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginParameter {
	@NotEmpty
	private String username;
	
	@NotEmpty
	private String password;
}
