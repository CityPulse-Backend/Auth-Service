package city.pulse.auth_service.feature.auth.register.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import city.pulse.auth_service.feature.auth.common.exception.UserAlreadyExistsException;
import city.pulse.auth_service.feature.auth.register.mapper.UserMapper;
import city.pulse.auth_service.feature.auth.register.model.User;
import city.pulse.auth_service.feature.auth.register.repository.UserRepository;
import city.pulse.auth_service.feature.auth.register.service.impl.RegisterServiceImpl;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static city.pulse.auth_service.feature.auth.common.data.UserTestData.*;

@ExtendWith(MockitoExtension.class)
class RegisterServiceImplTest {
    @Mock private UserRepository repository;
    @Mock private PasswordEncoder encoder;
    @Mock private UserMapper mapper;

    @InjectMocks
    private RegisterServiceImpl authService;

    @Test
    void createUser_returnsMapperResult_andSavesUserWithEncodedPassword() {
        userDoesNotExist(ALICE, ALICE_EMAIL);
        encoderReturns(ALICE_RAW, ALICE_ENC);

        var saved = aliceSaved();
        var mapped = aliceDto();

        when(repository.save(any(User.class))).thenReturn(saved);
        when(mapper.toDTO(saved)).thenReturn(mapped);

        var result = authService.createUser(ALICE, ALICE_RAW, ALICE_EMAIL);

        assertSame(mapped, result);
        verify(encoder).encode(ALICE_RAW);

        var captor = ArgumentCaptor.forClass(User.class);
        verify(repository).save(captor.capture());
        var toSave = captor.getValue();

        assertEquals(ALICE, toSave.getUsername());
        assertEquals(ALICE_ENC, toSave.getPassword());
        assertEquals(ALICE_EMAIL, toSave.getEmail());

        verify(mapper).toDTO(saved);
    }

    @Test
    void createUser_throws_whenUsernameExists() {
        userExistsByUsername(ALICE);

        assertThrows(UserAlreadyExistsException.class,
                () -> authService.createUser(ALICE, ALICE_RAW, ALICE_EMAIL));

        verify(repository, never()).existsByEmail(any());
        assertNoSideEffects();
    }

    @Test
    void createUser_throws_whenEmailExists() {
        when(repository.existsByUsername(BOB)).thenReturn(false);
        userExistsByEmail(BOB_EMAIL);

        assertThrows(UserAlreadyExistsException.class,
                () -> authService.createUser(BOB, BOB_RAW, BOB_EMAIL));

        assertNoSideEffects();
    }

    @Test
    void createUser_wrapsDataIntegrityViolation() {
        userDoesNotExist(ALICE, ALICE_EMAIL);
        encoderReturns(ALICE_RAW, ALICE_ENC);

        when(repository.save(any(User.class)))
                .thenThrow(new DataIntegrityViolationException("unique constraint violated"));

        assertThrows(UserAlreadyExistsException.class,
                () -> authService.createUser(ALICE, ALICE_RAW, ALICE_EMAIL));

        verify(encoder).encode(ALICE_RAW);
        verify(repository).save(any(User.class));
        verify(mapper, never()).toDTO(any());
    }

    // -------------------------------HELPERS------------------------------------- //

    private void userDoesNotExist(String username, String email) {
        when(repository.existsByUsername(username)).thenReturn(false);
        when(repository.existsByEmail(email)).thenReturn(false);
    }

    private void userExistsByUsername(String username) {
        when(repository.existsByUsername(username)).thenReturn(true);
    }

    private void userExistsByEmail(String email) {
        when(repository.existsByEmail(email)).thenReturn(true);
    }

    private void encoderReturns(String raw, String encoded) {
        when(encoder.encode(raw)).thenReturn(encoded);
    }

    private void assertNoSideEffects() {
        verify(repository, never()).save(any());
        verify(encoder, never()).encode(any());
        verify(mapper, never()).toDTO(any());
    }
}
