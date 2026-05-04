package com.endava.insurance.insurance_service.domain.model;

import com.endava.insurance.insurance_service.domain.enums.BrokerStatus;
import com.endava.insurance.insurance_service.domain.exception.ValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Broker domain")
class BrokerTest {

    private static final String VALID_CODE = "BRK1";
    private static final String VALID_NAME = "Broker One";
    private static final String VALID_EMAIL = "broker@example.com";
    private static final String VALID_PHONE = "+40123456789";
    private static final BigDecimal COMMISSION_TEN = new BigDecimal("10.00");

    @Nested
    @DisplayName("Constructor - validation")
    class ConstructorValidation {

        @Test
        @DisplayName("valid args creates broker and trims fields")
        void validArgs_trimsFields() throws ValidationException {
            Broker broker = new Broker(VALID_CODE, "  Name  ", "  e@e.com  ", "  +40123456789  ",
                    BrokerStatus.ACTIVE, COMMISSION_TEN);
            assertThat(broker.getBrokerCode()).isEqualTo(VALID_CODE);
            assertThat(broker.getName()).isEqualTo("Name");
            assertThat(broker.getEmail()).isEqualTo("e@e.com");
            assertThat(broker.getPhone()).isEqualTo("+40123456789");
            assertThat(broker.getStatus()).isEqualTo(BrokerStatus.ACTIVE);
            assertThat(broker.getCommissionPercentage()).isEqualByComparingTo(COMMISSION_TEN);
        }

        @Test
        @DisplayName("null broker code – throws")
        void nullBrokerCode_throws() {
            assertThatThrownBy(() -> new Broker(null, VALID_NAME, VALID_EMAIL, VALID_PHONE, BrokerStatus.ACTIVE, null))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("Broker code is required");
        }

        @Test
        @DisplayName("blank name – throws")
        void blankName_throws() {
            assertThatThrownBy(() -> new Broker(VALID_CODE, "   ", VALID_EMAIL, VALID_PHONE, BrokerStatus.ACTIVE, null))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("Name is required");
        }

        @Test
        @DisplayName("name too short – throws")
        void nameTooShort_throws() {
            assertThatThrownBy(() -> new Broker(VALID_CODE, "A", VALID_EMAIL, VALID_PHONE, BrokerStatus.ACTIVE, null))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("Name must be between 2 and 20 characters");
        }

        @Test
        @DisplayName("invalid email format – throws")
        void invalidEmail_throws() {
            assertThatThrownBy(() -> new Broker(VALID_CODE, VALID_NAME, "notanemail", VALID_PHONE, BrokerStatus.ACTIVE, null))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("Invalid email format");
        }

        @Test
        @DisplayName("invalid phone format – throws")
        void invalidPhone_throws() {
            assertThatThrownBy(() -> new Broker(VALID_CODE, VALID_NAME, VALID_EMAIL, "123", BrokerStatus.ACTIVE, null))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("Invalid phone number format");
        }

        @Test
        @DisplayName("null status – throws")
        void nullStatus_throws() {
            assertThatThrownBy(() -> new Broker(VALID_CODE, VALID_NAME, VALID_EMAIL, VALID_PHONE, null, null))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("Status is required");
        }

        @Test
        @DisplayName("negative commission – throws")
        void negativeCommission_throws() {
            assertThatThrownBy(() -> new Broker(VALID_CODE, VALID_NAME, VALID_EMAIL, VALID_PHONE, BrokerStatus.ACTIVE, new BigDecimal("-1")))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("Commission percentage must be non-negative");
        }

        @Test
        @DisplayName("commission over 100 – throws")
        void commissionOver100_throws() {
            assertThatThrownBy(() -> new Broker(VALID_CODE, VALID_NAME, VALID_EMAIL, VALID_PHONE, BrokerStatus.ACTIVE, new BigDecimal("101")))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("must not exceed 100");
        }
    }

    @Nested
    @DisplayName("updateDetails")
    class UpdateDetails {

        @Test
        @DisplayName("valid update trims and sets fields")
        void validUpdate_setsFields() throws ValidationException {
            Broker broker = new Broker(VALID_CODE, VALID_NAME, VALID_EMAIL, VALID_PHONE, BrokerStatus.ACTIVE, COMMISSION_TEN);
            broker.updateDetails("  New Name  ", "  new@b.com  ", "  +40999887766  ", new BigDecimal("5"));
            assertThat(broker.getName()).isEqualTo("New Name");
            assertThat(broker.getEmail()).isEqualTo("new@b.com");
            assertThat(broker.getPhone()).isEqualTo("+40999887766");
            assertThat(broker.getCommissionPercentage()).isEqualByComparingTo("5");
        }
    }

    @Nested
    @DisplayName("activate")
    class Activate {

        @Test
        @DisplayName("when INACTIVE – sets ACTIVE")
        void whenInactive_setsActive() throws ValidationException {
            Broker broker = new Broker(VALID_CODE, VALID_NAME, VALID_EMAIL, VALID_PHONE, BrokerStatus.INACTIVE, null);
            broker.activate();
            assertThat(broker.getStatus()).isEqualTo(BrokerStatus.ACTIVE);
        }

        @Test
        @DisplayName("when already ACTIVE – throws")
        void whenAlreadyActive_throws() throws ValidationException {
            Broker broker = new Broker(VALID_CODE, VALID_NAME, VALID_EMAIL, VALID_PHONE, BrokerStatus.ACTIVE, null);
            assertThatThrownBy(broker::activate)
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("Broker is already active");
        }
    }

    @Nested
    @DisplayName("deactivate")
    class Deactivate {

        @Test
        @DisplayName("when ACTIVE – sets INACTIVE")
        void whenActive_setsInactive() throws ValidationException {
            Broker broker = new Broker(VALID_CODE, VALID_NAME, VALID_EMAIL, VALID_PHONE, BrokerStatus.ACTIVE, null);
            broker.deactivate();
            assertThat(broker.getStatus()).isEqualTo(BrokerStatus.INACTIVE);
        }

        @Test
        @DisplayName("when already INACTIVE – throws")
        void whenAlreadyInactive_throws() throws ValidationException {
            Broker broker = new Broker(VALID_CODE, VALID_NAME, VALID_EMAIL, VALID_PHONE, BrokerStatus.INACTIVE, null);
            assertThatThrownBy(broker::deactivate)
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("Broker is already inactive");
        }
    }
}
