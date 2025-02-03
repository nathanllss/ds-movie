package com.devsuperior.dsmovie.services;

import com.devsuperior.dsmovie.entities.UserEntity;
import com.devsuperior.dsmovie.projections.UserDetailsProjection;
import com.devsuperior.dsmovie.repositories.UserRepository;
import com.devsuperior.dsmovie.tests.UserDetailsFactory;
import com.devsuperior.dsmovie.tests.UserFactory;
import com.devsuperior.dsmovie.utils.CustomUserUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration
public class UserServiceTests {

	@InjectMocks
	private UserService service;
	@Mock
	private UserRepository repository;
	@Mock
	private CustomUserUtil customUserUtil;

	private UserEntity user;
	private String userUsername, nonExistingUsername;
	private List<UserDetailsProjection> existingUserDetails, nonExistingUserDetails;

	@BeforeEach
	void setUp() {
		user = UserFactory.createUserEntity();
		userUsername = "maria@gmail.com";
		existingUserDetails = UserDetailsFactory.createCustomAdminClientUser(userUsername);
		nonExistingUserDetails = new ArrayList<>();
		nonExistingUsername = "test@gmail.com";

	}

	@Test
	public void authenticatedShouldReturnUserEntityWhenUserExists() {
		when(customUserUtil.getLoggedUsername()).thenReturn(userUsername);
		when(repository.findByUsername(anyString())).thenReturn(Optional.of(user));

		UserEntity result = service.authenticated();

		assertThat(result).isNotNull();
		assertThat(result.getUsername()).isEqualTo(userUsername);

	}

	@Test
	public void authenticatedShouldThrowUsernameNotFoundExceptionWhenUserDoesNotExists() {
		when(customUserUtil.getLoggedUsername()).thenReturn(null);
		when(repository.findByUsername(anyString())).thenReturn(Optional.empty());

		assertThrows(UsernameNotFoundException.class, () -> {
			service.authenticated();
		});
	}

	@Test
	public void loadUserByUsernameShouldReturnUserDetailsWhenUserExists() {
		when(repository.searchUserAndRolesByUsername(anyString())).thenReturn(existingUserDetails);

		UserEntity result = (UserEntity) service.loadUserByUsername(userUsername);

		assertThat(result).isNotNull();
		assertThat(result.getUsername()).isEqualTo(userUsername);

	}

	@Test
	public void loadUserByUsernameShouldThrowUsernameNotFoundExceptionWhenUserDoesNotExists() {
		when(repository.searchUserAndRolesByUsername(anyString())).thenReturn(nonExistingUserDetails);

		assertThrows(UsernameNotFoundException.class, () -> {
			service.loadUserByUsername(nonExistingUsername);
		});

	}
}
