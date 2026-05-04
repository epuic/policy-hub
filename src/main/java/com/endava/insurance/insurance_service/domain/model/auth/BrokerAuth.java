package com.endava.insurance.insurance_service.domain.model.auth;

import com.endava.insurance.insurance_service.domain.model.Broker;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "broker_auth")
@Getter
@NoArgsConstructor
public class BrokerAuth {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "broker_id", nullable = false, unique = true)
    private Broker broker;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    public BrokerAuth(Broker broker, String email, String passwordHash) {
        this.broker = broker;
        this.email = email;
        this.passwordHash = passwordHash;
    }

    public void updateEmail(String newEmail) {
        this.email = newEmail;
    }
}
