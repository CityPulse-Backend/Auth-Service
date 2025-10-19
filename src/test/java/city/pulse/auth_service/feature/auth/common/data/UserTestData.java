package city.pulse.auth_service.feature.auth.common.data;

import city.pulse.auth_service.feature.auth.register.dto.UserResponseDTO;
import city.pulse.auth_service.feature.auth.register.model.Role;
import city.pulse.auth_service.feature.auth.register.model.User;

public final class UserTestData {
    private UserTestData() {}

    public static final long ALICE_ID = 1L;
    public static final String ALICE = "alice";
    public static final String ALICE_RAW = "secret";
    public static final String ALICE_ENC = "ENC(secret)";
    public static final String ALICE_EMAIL = "alice@example.com";

    public static final long BOB_ID = 2L;
    public static final String BOB = "bob";
    public static final String BOB_RAW = "pwd";
    public static final String BOB_ENC = "ENC(pwd)";
    public static final String BOB_EMAIL = "bob@example.com";

    public static User newUser(String username, String password, String email) {
        return User.builder()
                .username(username)
                .password(password)
                .email(email)
                .role(Role.USER)
                .build();
    }

    public static User userBeforeSaving(String username, String encodedPassword, String email) {
        return User.builder()
                .username(username)
                .password(encodedPassword)
                .email(email)
                .role(Role.USER)
                .build();
    }

    public static User savedUser(long id, String username, String encodedPassword, String email) {
        return User.builder()
                .id(id)
                .username(username)
                .password(encodedPassword)
                .email(email)
                .role(Role.USER)
                .build();
    }

    public static UserResponseDTO dto(long id, String username, String email) {
        return UserResponseDTO.builder()
                .id(id)
                .username(username)
                .email(email)
                .build();
    }

    public static User aliceNew()            { return newUser(          ALICE,    ALICE_RAW, ALICE_EMAIL              );}
    public static User bobNew()              { return newUser(          BOB,      BOB_RAW,   BOB_EMAIL                );}
    public static User aliceSaved()          { return savedUser(        ALICE_ID, ALICE,     ALICE_ENC,   ALICE_EMAIL );}
    public static User bobSaved()            { return savedUser(        BOB_ID,   BOB,       BOB_ENC,     BOB_EMAIL   );}
    public static User aliceBeforeSaving()   { return userBeforeSaving( ALICE,    ALICE_ENC, ALICE_EMAIL              );}
    public static User bobBeforeSaving()     { return userBeforeSaving( BOB,      BOB_ENC,   BOB_EMAIL                );}
    public static UserResponseDTO aliceDto() { return dto(              ALICE_ID, ALICE,     ALICE_EMAIL              );}
    public static UserResponseDTO bobDto()   { return dto(              BOB_ID,   BOB,       BOB_EMAIL                );}
}
