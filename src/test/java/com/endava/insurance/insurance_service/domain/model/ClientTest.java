package com.endava.insurance.insurance_service.domain.model;

import com.endava.insurance.insurance_service.domain.enums.ClientType;
import com.endava.insurance.insurance_service.domain.exception.ValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Client domain")
class ClientTest {

    private static final String COUNTRY_RO = "RO";
    private static final String VALID_CNP = "1234567890123";
    private static final String VALID_EMAIL = "test@example.com";
    private static final String VALID_PHONE = "0712345678";

    @Nested
    @DisplayName("Constructor - validation")
    class ConstructorValidation {

        @Test
        @DisplayName("null country code should fail")
        void nullCountryCode_throws() {
            assertThatThrownBy(() -> new Client(
                    null, ClientType.INDIVIDUAL, "Valid Name", VALID_CNP, VALID_EMAIL, VALID_PHONE, null))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("Country code is required");
        }

        @Test
        @DisplayName("null type should fail")
        void nullType_throws() {
            assertThatThrownBy(() -> new Client(
                    COUNTRY_RO, null, "Valid Name", VALID_CNP, VALID_EMAIL, VALID_PHONE, null))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("Client type is required");
        }

        @Test
        @DisplayName("null name should fail")
        void nullName_throws() {
            assertThatThrownBy(() -> new Client(
                    COUNTRY_RO, ClientType.INDIVIDUAL, null, VALID_CNP, VALID_EMAIL, VALID_PHONE, null))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("Name is required");
        }

        @Test
        @DisplayName("blank name should fail")
        void blankName_throws() {
            assertThatThrownBy(() -> new Client(
                    COUNTRY_RO, ClientType.INDIVIDUAL, "   ", VALID_CNP, VALID_EMAIL, VALID_PHONE, null))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("Name is required");
        }

        @Test
        @DisplayName("empty name should fail")
        void emptyName_throws() {
            assertThatThrownBy(() -> new Client(
                    COUNTRY_RO, ClientType.INDIVIDUAL, "", VALID_CNP, VALID_EMAIL, VALID_PHONE, null))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("Name is required");
        }

        @Test
        @DisplayName("name shorter than 2 characters should fail")
        void nameTooShort_throws() {
            assertThatThrownBy(() -> new Client(
                    COUNTRY_RO, ClientType.INDIVIDUAL, "A", VALID_CNP, VALID_EMAIL, VALID_PHONE, null))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("Name must be between 2 and 20 characters");
        }

        @Test
        @DisplayName("name longer than 20 characters should fail")
        void nameTooLong_throws() {
            String longName = "x".repeat(21);
            assertThatThrownBy(() -> new Client(
                    COUNTRY_RO, ClientType.INDIVIDUAL, longName, VALID_CNP, VALID_EMAIL, VALID_PHONE, null))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("Name must be between 2 and 20 characters");
        }

        @Test
        @DisplayName("null identification number should fail")
        void nullIdentificationNumber_throws() {
            assertThatThrownBy(() -> new Client(
                    COUNTRY_RO, ClientType.INDIVIDUAL, "Valid Name", null, VALID_EMAIL, VALID_PHONE, null))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("Identification number is required");
        }

        @Test
        @DisplayName("blank identification number should fail")
        void blankIdentificationNumber_throws() {
            assertThatThrownBy(() -> new Client(
                    COUNTRY_RO, ClientType.INDIVIDUAL, "Valid Name", "  ", VALID_EMAIL, VALID_PHONE, null))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("Identification number is required");
        }

        @Test
        @DisplayName("null email should fail")
        void nullEmail_throws() {
            assertThatThrownBy(() -> new Client(
                    COUNTRY_RO, ClientType.INDIVIDUAL, "Valid Name", VALID_CNP, null, VALID_PHONE, null))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("Email is required");
        }

        @Test
        @DisplayName("blank email should fail")
        void blankEmail_throws() {
            assertThatThrownBy(() -> new Client(
                    COUNTRY_RO, ClientType.INDIVIDUAL, "Valid Name", VALID_CNP, "  ", VALID_PHONE, null))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("Email is required");
        }

        @Test
        @DisplayName("invalid email format should fail")
        void invalidEmailFormat_throws() {
            assertThatThrownBy(() -> new Client(
                    COUNTRY_RO, ClientType.INDIVIDUAL, "Valid Name", VALID_CNP, "not-an-email", VALID_PHONE, null))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("Invalid email format");
        }

        @Test
        @DisplayName("null phone should fail")
        void nullPhone_throws() {
            assertThatThrownBy(() -> new Client(
                    COUNTRY_RO, ClientType.INDIVIDUAL, "Valid Name", VALID_CNP, VALID_EMAIL, null, null))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("Phone number is required");
        }

        @Test
        @DisplayName("blank phone should fail")
        void blankPhone_throws() {
            assertThatThrownBy(() -> new Client(
                    COUNTRY_RO, ClientType.INDIVIDUAL, "Valid Name", VALID_CNP, VALID_EMAIL, "  ", null))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("Phone number is required");
        }

        @Test
        @DisplayName("invalid phone format (too short) should fail")
        void invalidPhoneFormat_throws() {
            assertThatThrownBy(() -> new Client(
                    COUNTRY_RO, ClientType.INDIVIDUAL, "Valid Name", VALID_CNP, VALID_EMAIL, "123", null))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("Invalid phone number format");
        }
    }

    @Nested
    @DisplayName("Constructor - success and trimming")
    class ConstructorSuccess {

        @Test
        @DisplayName("address can be null")
        void addressCanBeNull() throws ValidationException {
            Client client = new Client(
                    COUNTRY_RO, ClientType.INDIVIDUAL, "Ion Pop", VALID_CNP, VALID_EMAIL, VALID_PHONE, null
            );
            assertThat(client.getAddress()).isNull();
        }
    }

    @Nested
    @DisplayName("updateDetails - validation")
    class UpdateDetailsValidation {

        private Client createValidClient() throws ValidationException {
            return new Client(
                    COUNTRY_RO, ClientType.INDIVIDUAL, "Ion Pop", VALID_CNP, VALID_EMAIL, VALID_PHONE, null
            );
        }

        @Test
        @DisplayName("null name should fail")
        void nullName_throws() throws ValidationException {
            Client client = createValidClient();
            assertThatThrownBy(() -> client.updateDetails(null, VALID_EMAIL, VALID_PHONE, null))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("Name is required");
        }

        @Test
        @DisplayName("blank name should fail")
        void blankName_throws() throws ValidationException {
            Client client = createValidClient();
            assertThatThrownBy(() -> client.updateDetails("  ", VALID_EMAIL, VALID_PHONE, null))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("Name is required");
        }

        @Test
        @DisplayName("invalid email format should fail")
        void invalidEmail_throws() throws ValidationException {
            Client client = createValidClient();
            assertThatThrownBy(() -> client.updateDetails("Ion Pop", "bad-email", VALID_PHONE, null))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("Invalid email format");
        }

        @Test
        @DisplayName("invalid phone format should fail")
        void invalidPhone_throws() throws ValidationException {
            Client client = createValidClient();
            assertThatThrownBy(() -> client.updateDetails("Ion Pop", VALID_EMAIL, "short", null))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("Invalid phone number format");
        }
    }

    @Nested
    @DisplayName("updateDetails - success")
    class UpdateDetailsSuccess {
        @Test
        @DisplayName("address can be updated")
        void addressUpdated() throws ValidationException {
            Client client = new Client(
                    COUNTRY_RO, ClientType.INDIVIDUAL, "Ion Pop", VALID_CNP, VALID_EMAIL, VALID_PHONE, "Old address"
            );
            client.updateDetails("Ion Pop", VALID_EMAIL, VALID_PHONE, "New address");
            assertThat(client.getAddress()).isEqualTo("New address");
        }
    }

    @Nested
    @DisplayName("getBuildings")
    class GetBuildings {

        @Test
        @DisplayName("returns unmodifiable list")
        void returnsUnmodifiableList() throws ValidationException {
            Client client = new Client(
                    COUNTRY_RO, ClientType.INDIVIDUAL, "Ion Pop", VALID_CNP, VALID_EMAIL, VALID_PHONE, null
            );
            List<?> list = client.getBuildings();
            assertThat(list).isNotNull();
            assertThatThrownBy(() -> list.add(null))
                    .isInstanceOf(UnsupportedOperationException.class);
        }

        @Test
        @DisplayName("returns empty list when buildings not set (e.g. new entity)")
        void returnsEmptyWhenBuildingsNull() throws ValidationException {
            Client client = new Client(
                    COUNTRY_RO, ClientType.INDIVIDUAL, "Ion Pop", VALID_CNP, VALID_EMAIL, VALID_PHONE, null
            );
            assertThat(client.getBuildings()).isEmpty();
        }
    }
}
