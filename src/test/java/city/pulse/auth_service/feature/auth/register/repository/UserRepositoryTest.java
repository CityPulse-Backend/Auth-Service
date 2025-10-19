package city.pulse.auth_service.feature.auth.register.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import city.pulse.auth_service.feature.auth.register.model.Role;

import static org.junit.jupiter.api.Assertions.*;
import static city.pulse.auth_service.feature.auth.common.data.UserTestData.*;

@DataJpaTest
class UserRepositoryTest {
    @Autowired
    private UserRepository repo;

    @Test
    void existsByUsername_true_false() {
        var u = aliceBeforeSaving();

        repo.save(u);

        assertTrue(repo.existsByUsername(ALICE));
        assertFalse(repo.existsByUsername("nobody"));
    }

    @Test
    void existsByEmail_true_false() {
        var u = bobBeforeSaving();

        repo.save(u);

        assertTrue(repo.existsByEmail(BOB_EMAIL));
        assertFalse(repo.existsByEmail("nobody"));
    }

    @Test
    void existsById_true_false() {
        var u = bobBeforeSaving();

        repo.save(u);

        assertTrue(repo.existsById(BOB_ID));
        assertFalse(repo.existsById(-1L));
    }

    @Test
    void addUser_successful() {
        var u = aliceBeforeSaving();

        var result = repo.save(u);

        assertNotNull(result.getId());
        assertEquals(ALICE, result.getUsername());
        assertEquals(ALICE_ENC, result.getPassword());
        assertEquals(ALICE_EMAIL, result.getEmail());
        assertEquals(Role.USER, result.getRole());
    }
}