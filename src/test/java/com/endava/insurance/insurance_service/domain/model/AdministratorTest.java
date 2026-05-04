package com.endava.insurance.insurance_service.domain.model;

import com.endava.insurance.insurance_service.domain.enums.AdministratorRole;
import com.endava.insurance.insurance_service.domain.exception.ValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Administrator domain")
class AdministratorTest {

    private static final String VALID_NAME = "Admin User";
    private static final String VALID_EMAIL = "admin@example.com";
    @Nested
    @DisplayName("Constructor - validation")
    class ConstructorValidation {

        @Test
        @DisplayName("valid args creates administrator and trims")
        void validArgs_trimsFields() throws ValidationException {
            Administrator admin = new Administrator("  Name  ", "  a@a.com  ", AdministratorRole.ADMIN);
            assertThat(admin.getName()).isEqualTo("Name");
            assertThat(admin.getEmail()).isEqualTo("a@a.com");
            assertThat(admin.getRole()).isEqualTo(AdministratorRole.ADMIN);
        }

        @Test
        @DisplayName("null name – throws")
        void nullName_throws() {
            assertThatThrownBy(() -> new Administrator(null, VALID_EMAIL, AdministratorRole.ADMIN))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("Name is required");
        }

        @Test
        @DisplayName("name too short – throws")
        void nameTooShort_throws() {
            assertThatThrownBy(() -> new Administrator("A", VALID_EMAIL, AdministratorRole.ADMIN))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("Name must be between 2 and 20 characters");
        }

        @Test
        @DisplayName("null email – throws")
        void nullEmail_throws() {
            assertThatThrownBy(() -> new Administrator(VALID_NAME, null, AdministratorRole.ADMIN))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("Email is required");
        }

        @Test
        @DisplayName("invalid email format – throws")
        void invalidEmail_throws() {
            assertThatThrownBy(() -> new Administrator(VALID_NAME, "invalid", AdministratorRole.ADMIN))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("Invalid email format");
        }

        @Test
        @DisplayName("null role – throws")
        void nullRole_throws() {
            assertThatThrownBy(() -> new Administrator(VALID_NAME, VALID_EMAIL, null))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("Role is required");
        }
    }

    @Nested
    @DisplayName("updateDetails")
    class UpdateDetails {

        @Test
        @DisplayName("valid update sets name email and role")
        void validUpdate_setsFields() throws ValidationException {
            Administrator admin = new Administrator(VALID_NAME, VALID_EMAIL, AdministratorRole.ADMIN);
            admin.updateDetails("  New Name  ", "  new@a.com  ", AdministratorRole.MANAGER);
            assertThat(admin.getName()).isEqualTo("New Name");
            assertThat(admin.getEmail()).isEqualTo("new@a.com");
            assertThat(admin.getRole()).isEqualTo(AdministratorRole.MANAGER);
        }
    }
}
