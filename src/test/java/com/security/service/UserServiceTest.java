package com.security.service;

import com.security.entity.Role;
import com.security.entity.User;
import com.security.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

/**
 * Tests unitaires de {@link UserService} avec Mockito : aucune base de donnees,
 * le {@link UserRepository} et l'encodeur sont mockes.
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private static Role role(String name) {
        Role r = new Role();
        r.setName(name);
        return r;
    }

    private static User userWithRoles(String username, String... roleNames) {
        User user = new User();
        user.setUsername(username);
        user.setRoles(List.of(java.util.Arrays.stream(roleNames)
                .map(UserServiceTest::role)
                .toArray(Role[]::new)));
        return user;
    }

    @Test
    void getUserRoles_returnsRoleNames() {
        User user = userWithRoles("alice", "ROLE_ADMIN", "ROLE_USER");

        List<String> roles = userService.getUserRoles(user);

        assertThat(roles).containsExactly("ROLE_ADMIN", "ROLE_USER");
    }

    @Test
    void getUserRoles_throws_whenUserIsNull() {
        assertThatThrownBy(() -> userService.getUserRoles((User) null))
                .isInstanceOf(UsernameNotFoundException.class);
    }

    @Test
    void getUserRoles_byUsername_delegatesToRepository() {
        when(userRepository.findByUsername("bob"))
                .thenReturn(userWithRoles("bob", "ROLE_USER"));

        assertThat(userService.getUserRoles("bob")).containsExactly("ROLE_USER");
    }

    @Test
    void registerNewUserAccount_encodesPasswordBeforeSaving() {
        User user = new User();
        user.setUsername("charlie");
        user.setPassword("plaintext");

        when(passwordEncoder.encode("plaintext")).thenReturn("ENCODED");
        when(userRepository.save(user)).thenReturn(user);

        User saved = userService.registerNewUserAccount(user);

        assertThat(saved.getPassword()).isEqualTo("ENCODED");
    }

    @Test
    void getUserByToken_throws_whenTokenUnknown() {
        when(userRepository.findByClaimToken("missing")).thenReturn(null);

        assertThatThrownBy(() -> userService.getUserByToken("missing"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    void getUserByToken_throws_whenTokenExpired() {
        User user = new User();
        user.setTokenExpiration(LocalDateTime.now().minusHours(1));
        user.setEnabled(false);
        when(userRepository.findByClaimToken("expired")).thenReturn(user);

        assertThatThrownBy(() -> userService.getUserByToken("expired"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("Token expired");
    }

    @Test
    void getUserByToken_throws_whenUserAlreadyEnabled() {
        User user = new User();
        user.setTokenExpiration(LocalDateTime.now().plusHours(1));
        user.setEnabled(true);
        when(userRepository.findByClaimToken("used")).thenReturn(user);

        assertThatThrownBy(() -> userService.getUserByToken("used"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("already enabled");
    }

    @Test
    void getUserByToken_returnsUser_whenTokenValidAndUserDisabled() {
        User user = new User();
        user.setUsername("dave");
        user.setTokenExpiration(LocalDateTime.now().plusHours(1));
        user.setEnabled(false);
        when(userRepository.findByClaimToken("valid")).thenReturn(user);

        assertThat(userService.getUserByToken("valid")).isSameAs(user);
    }
}
