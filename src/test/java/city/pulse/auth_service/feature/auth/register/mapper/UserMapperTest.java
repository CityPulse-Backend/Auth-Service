package city.pulse.auth_service.feature.auth.register.mapper;

import org.junit.jupiter.api.Test;
import city.pulse.auth_service.feature.auth.register.model.User;

import static org.junit.jupiter.api.Assertions.*;
import static city.pulse.auth_service.feature.auth.common.data.UserTestData.*;

class UserMapperTest {
    private final UserMapper mapper = new UserMapper();

    @Test
    void toDTO_mapsAllFields() {
        var user = aliceSaved();
        var dto = mapper.toDTO(user);

        assertNotNull(dto);
        assertEquals(ALICE_ID, dto.getId());
        assertEquals(ALICE, dto.getUsername());
        assertEquals(ALICE_EMAIL, dto.getEmail());
    }

    @Test
    void toDTO_returnsNull_whenUserIsNull() {
        User user = null;

        var dto = mapper.toDTO(user);

        assertNull(dto);
    }
}
