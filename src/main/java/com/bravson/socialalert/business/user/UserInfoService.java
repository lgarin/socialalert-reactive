package com.bravson.socialalert.business.user;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.bravson.socialalert.business.user.profile.UserProfileEntity;
import com.bravson.socialalert.business.user.profile.UserProfileRepository;
import com.bravson.socialalert.domain.media.UserContent;
import com.bravson.socialalert.domain.user.UserInfo;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Service
@Transactional(propagation=Propagation.SUPPORTS)
@NoArgsConstructor(access=AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserInfoService {
	/*
	@Autowired
	@NonNull
	OnlineUserRepository onlineUserRepository;
	*/
	@Autowired
	@NonNull
	UserProfileRepository profileRepository;
	
	private Function<UserProfileEntity, UserInfo> getUserInfoMapper(String userId) {
		return UserProfileEntity::toOfflineUserInfo;
		/*
		if (onlineUserRepository.isUserActive(userId)) {
			return UserProfileEntity::toOnlineUserInfo;
		} else {
			return UserProfileEntity::toOfflineUserInfo;
		}
		*/
	}
	
	public Optional<UserInfo> findUserInfo(@NonNull String userId) {
		return profileRepository.findByUserId(userId).map(getUserInfoMapper(userId));
	}
	
	public <T extends UserContent> T fillUserInfo(@NonNull T content) {
		findUserInfo(content.getCreatorId()).ifPresent(content::setCreator);
		return content;
	}
	
	public <T extends UserContent> Optional<T> fillUserInfo(@NonNull Optional<T> content) {
		if (content.isPresent()) {
			fillUserInfo(content.get());
		}
		return content;
	}
	
	public <T extends Collection<? extends UserContent>> T fillUserInfo(@NonNull T collection) {
		for (UserContent content : collection) {
			fillUserInfo(content);
		}
		return collection;
	}
}
