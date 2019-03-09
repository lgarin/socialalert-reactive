package com.bravson.socialalert.infrastructure.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;

@NoArgsConstructor(access=AccessLevel.PROTECTED)
@ToString(of="id")
@EqualsAndHashCode(of="id")
public abstract class VersionedEntity {

	@Id
	@Getter
	@NonNull
	protected String id;
	
	@Version
	private Integer version;
	
	@NonNull
	protected VersionInfo versionInfo;
}
