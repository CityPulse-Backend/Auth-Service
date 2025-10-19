package city.pulse.auth_service.feature.auth.register.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import city.pulse.auth_service.feature.auth.config.user.CustomUserDetailsService;
import city.pulse.auth_service.feature.auth.register.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static city.pulse.auth_service.feature.auth.common.data.UserTestData.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {
    @Mock
    private UserRepository repo;
    @InjectMocks
    private CustomUserDetailsService uds;

    @Test
    void found() {
        when(repo.findByUsername(ALICE)).thenReturn(
                Optional.of(aliceBeforeSaving())
        );
        var ud = uds.loadUserByUsername(ALICE);
        assertEquals(ALICE, ud.getUsername());
        assertEquals(ALICE_ENC, ud.getPassword());
    }

    @Test
    void notFound() {
        when(repo.findByUsername(ALICE)).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> uds.loadUserByUsername(ALICE));
    }
}
