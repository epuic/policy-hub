package com.endava.insurance.insurance_service.domain.model;

import com.endava.insurance.insurance_service.domain.enums.PolicyStatus;
import com.endava.insurance.insurance_service.domain.exception.ValidationException;
import com.endava.insurance.insurance_service.domain.model.metadata.Currency;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("Policy domain")
class PolicyTest {

    private static final String POLICY_NUMBER = "POL-ABC123";
    private static final LocalDate TOMORROW = LocalDate.now(ZoneOffset.UTC).plusDays(1);
    private static final LocalDate NEXT_WEEK = LocalDate.now(ZoneOffset.UTC).plusDays(7);
    private static final BigDecimal BASE_PREMIUM = new BigDecimal("100.00");
    private static final BigDecimal FINAL_PREMIUM = new BigDecimal("105.00");

    private static Client mockClient() {
        Client c = mock(Client.class);
        when(c.getId()).thenReturn(1L);
        return c;
    }

    private static Building mockBuilding() {
        return mock(Building.class);
    }

    private static Broker mockBroker() {
        return mock(Broker.class);
    }

    private static Currency mockCurrency() {
        return mock(Currency.class);
    }

    private static Policy.PolicyParties mockParties() {
        return new Policy.PolicyParties(mockClient(), mockBuilding(), mockBroker());
    }

    private static Policy createValidPolicy() throws ValidationException {
        return new Policy(
                POLICY_NUMBER,
                mockParties(),
                TOMORROW,
                NEXT_WEEK,
                BASE_PREMIUM,
                mockCurrency(),
                FINAL_PREMIUM
        );
    }

    @Nested
    @DisplayName("activate()")
    class Activate {

        @Test
        @DisplayName("when DRAFT and startDate in future – sets status ACTIVE")
        void draft_withFutureStartDate_setsActive() throws Exception {
            Policy policy = createValidPolicy();
            policy.activate();
            assertThat(policy.getStatus()).isEqualTo(PolicyStatus.ACTIVE);
        }

        @Test
        @DisplayName("when not DRAFT – throws")
        void notDraft_throws() throws Exception {
            Policy policy = createValidPolicy();
            policy.activate();
            assertThatThrownBy(policy::activate)
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("Only draft policies can be activated");
        }

        @Test
        @DisplayName("when startDate in past – throws")
        void startDateInPast_throws() throws Exception {
            Policy policy = new Policy(
                    POLICY_NUMBER,
                    mockParties(),
                    LocalDate.now(ZoneOffset.UTC).minusDays(1),
                    NEXT_WEEK,
                    BASE_PREMIUM,
                    mockCurrency(),
                    FINAL_PREMIUM
            );
            assertThatThrownBy(policy::activate)
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("start date in the past");
        }
    }

    @Nested
    @DisplayName("cancel()")
    class Cancel {

        @Test
        @DisplayName("when ACTIVE with valid reason – sets CANCELLED and reason")
        void active_withReason_setsCancelled() throws Exception {
            Policy policy = createValidPolicy();
            policy.activate();
            policy.cancel("Client request");
            assertThat(policy.getStatus()).isEqualTo(PolicyStatus.CANCELLED);
            assertThat(policy.getCancellationReason()).isEqualTo("Client request");
            assertThat(policy.getCancellationDate()).isEqualTo(LocalDate.now(ZoneOffset.UTC));
        }

        @Test
        @DisplayName("when not ACTIVE – throws")
        void notActive_throws() throws Exception {
            Policy policy = createValidPolicy();
            assertThatThrownBy(() -> policy.cancel("Reason"))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("Only active policies can be cancelled");
        }

        @Test
        @DisplayName("when reason blank – throws")
        void blankReason_throws() throws Exception {
            Policy policy = createValidPolicy();
            policy.activate();
            assertThatThrownBy(() -> policy.cancel("   "))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("Cancellation reason is required");
        }

        @Test
        @DisplayName("when reason over 1000 chars – throws")
        void reasonTooLong_throws() throws Exception {
            Policy policy = createValidPolicy();
            policy.activate();
            String longReason = "x".repeat(1001);
            assertThatThrownBy(() -> policy.cancel(longReason))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("at most 1000 characters");
        }

        @Test
        @DisplayName("when reason empty string – throws")
        void emptyReason_throws() throws Exception {
            Policy policy = createValidPolicy();
            policy.activate();
            assertThatThrownBy(() -> policy.cancel(""))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("Cancellation reason is required");
        }
    }

    @Nested
    @DisplayName("expire()")
    class Expire {

        @Test
        @DisplayName("when ACTIVE and endDate in past – sets EXPIRED")
        void active_endDateInPast_setsExpired() throws Exception {
            Policy policy = createValidPolicy();
            policy.activate();
            setEndDate(policy, LocalDate.now(ZoneOffset.UTC).minusDays(1));
            policy.expire();
            assertThat(policy.getStatus()).isEqualTo(PolicyStatus.EXPIRED);
        }

        @Test
        @DisplayName("when DRAFT – no change")
        void draft_noChange() throws Exception {
            Policy policy = createValidPolicy();
            policy.expire();
            assertThat(policy.getStatus()).isEqualTo(PolicyStatus.DRAFT);
        }

        private void setEndDate(Policy policy, LocalDate endDate) throws Exception {
            Field f = Policy.class.getDeclaredField("endDate");
            f.setAccessible(true);
            f.set(policy, endDate);
        }
    }

    @Nested
    @DisplayName("Constructor validation")
    class ConstructorValidation {

        @Test
        @DisplayName("null policy number – throws")
        void nullPolicyNumber_throws() {
            assertThatThrownBy(() -> new Policy(
                    null, mockParties(),
                    TOMORROW, NEXT_WEEK, BASE_PREMIUM, mockCurrency(), FINAL_PREMIUM))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("Policy number is required");
        }

        @Test
        @DisplayName("end date before start date – throws")
        void endBeforeStart_throws() {
            assertThatThrownBy(() -> new Policy(
                    POLICY_NUMBER, mockParties(),
                    NEXT_WEEK, TOMORROW, BASE_PREMIUM, mockCurrency(), FINAL_PREMIUM))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("End date must be on or after start date");
        }

        @Test
        @DisplayName("base premium zero – throws")
        void basePremiumZero_throws() {
            assertThatThrownBy(() -> new Policy(
                    POLICY_NUMBER, mockParties(),
                    TOMORROW, NEXT_WEEK, BigDecimal.ZERO, mockCurrency(), FINAL_PREMIUM))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("Base premium amount must be greater than 0");
        }

        @Test
        @DisplayName("final premium negative – throws")
        void finalPremiumNegative_throws() {
            assertThatThrownBy(() -> new Policy(
                    POLICY_NUMBER, mockParties(),
                    TOMORROW, NEXT_WEEK, BASE_PREMIUM, mockCurrency(), new BigDecimal("-1")))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("Final premium must be non-negative");
        }

        @Test
        @DisplayName("blank policy number – throws")
        void blankPolicyNumber_throws() {
            assertThatThrownBy(() -> new Policy(
                    "   ", mockParties(),
                    TOMORROW, NEXT_WEEK, BASE_PREMIUM, mockCurrency(), FINAL_PREMIUM))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("Policy number is required");
        }

        @Test
        @DisplayName("policy number over 50 chars – throws")
        void policyNumberTooLong_throws() {
            String longNumber = "POL-" + "x".repeat(50);
            assertThatThrownBy(() -> new Policy(
                    longNumber, mockParties(),
                    TOMORROW, NEXT_WEEK, BASE_PREMIUM, mockCurrency(), FINAL_PREMIUM))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("at most 50 characters");
        }

        @Test
        @DisplayName("null client – throws")
        void nullClient_throws() {
            Policy.PolicyParties parties = new Policy.PolicyParties(null, mockBuilding(), mockBroker());
            assertThatThrownBy(() -> new Policy(
                    POLICY_NUMBER, parties, TOMORROW, NEXT_WEEK, BASE_PREMIUM, mockCurrency(), FINAL_PREMIUM))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("Client is required");
        }

        @Test
        @DisplayName("null building – throws")
        void nullBuilding_throws() {
            Policy.PolicyParties parties = new Policy.PolicyParties(mockClient(), null, mockBroker());
            assertThatThrownBy(() -> new Policy(
                    POLICY_NUMBER, parties, TOMORROW, NEXT_WEEK, BASE_PREMIUM, mockCurrency(), FINAL_PREMIUM))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("Building is required");
        }

        @Test
        @DisplayName("null broker – throws")
        void nullBroker_throws() {
            Policy.PolicyParties parties = new Policy.PolicyParties(mockClient(), mockBuilding(), null);
            assertThatThrownBy(() -> new Policy(
                    POLICY_NUMBER, parties, TOMORROW, NEXT_WEEK, BASE_PREMIUM, mockCurrency(), FINAL_PREMIUM))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("Broker is required");
        }

        @Test
        @DisplayName("null start date – throws")
        void nullStartDate_throws() {
            assertThatThrownBy(() -> new Policy(
                    POLICY_NUMBER, mockParties(), null, NEXT_WEEK, BASE_PREMIUM, mockCurrency(), FINAL_PREMIUM))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("Start date is required");
        }

        @Test
        @DisplayName("null end date – throws")
        void nullEndDate_throws() {
            assertThatThrownBy(() -> new Policy(
                    POLICY_NUMBER, mockParties(), TOMORROW, null, BASE_PREMIUM, mockCurrency(), FINAL_PREMIUM))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("End date is required");
        }

        @Test
        @DisplayName("null base premium – throws")
        void nullBasePremium_throws() {
            assertThatThrownBy(() -> new Policy(
                    POLICY_NUMBER, mockParties(), TOMORROW, NEXT_WEEK, null, mockCurrency(), FINAL_PREMIUM))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("Base premium amount is required");
        }

        @Test
        @DisplayName("null currency – throws")
        void nullCurrency_throws() {
            assertThatThrownBy(() -> new Policy(
                    POLICY_NUMBER, mockParties(), TOMORROW, NEXT_WEEK, BASE_PREMIUM, null, FINAL_PREMIUM))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("Currency is required");
        }

        @Test
        @DisplayName("null final premium – throws")
        void nullFinalPremium_throws() {
            assertThatThrownBy(() -> new Policy(
                    POLICY_NUMBER, mockParties(), TOMORROW, NEXT_WEEK, BASE_PREMIUM, mockCurrency(), null))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("Final premium is required");
        }
    }

    @Nested
    @DisplayName("cancel() – reason validation")
    class CancelReasonValidation {

        @Test
        @DisplayName("when reason null – throws")
        void nullReason_throws() throws Exception {
            Policy policy = createValidPolicy();
            policy.activate();
            assertThatThrownBy(() -> policy.cancel(null))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("Cancellation reason is required");
        }
    }
}
