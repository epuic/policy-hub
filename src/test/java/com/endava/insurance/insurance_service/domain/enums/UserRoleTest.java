package com.endava.insurance.insurance_service.domain.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("UserRole")
class UserRoleTest {

    @Test
    @DisplayName("enum has BROKER and ADMINISTRATOR")
    void enumValues() {
        UserRole[] values = UserRole.values();
        assertThat(values).hasSize(2);
        assertThat(UserRole.BROKER).isNotNull();
        assertThat(UserRole.ADMINISTRATOR).isNotNull();
        assertThat(UserRole.valueOf("BROKER")).isEqualTo(UserRole.BROKER);
        assertThat(UserRole.valueOf("ADMINISTRATOR")).isEqualTo(UserRole.ADMINISTRATOR);
    }
}
