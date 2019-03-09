package com.bravson.socialalert.login;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document
public class UserCredential {

	@Id
	public ObjectId id;

	public String username;
	public String password;
}
