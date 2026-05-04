package com.endava.insurance.insurance_service.security;

import com.endava.insurance.insurance_service.domain.enums.BrokerStatus;
import com.endava.insurance.insurance_service.domain.exception.AccountInactiveException;
import com.endava.insurance.insurance_service.domain.model.auth.AdministratorAuth;
import com.endava.insurance.insurance_service.domain.model.auth.BrokerAuth;
import com.endava.insurance.insurance_service.persistence.repository.AdministratorAuthRepository;
import com.endava.insurance.insurance_service.persistence.repository.BrokerAuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthUserDetailsService implements UserDetailsService {

    private final AdministratorAuthRepository administratorAuthRepository;
    private final BrokerAuthRepository brokerAuthRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return administratorAuthRepository.findByEmail(username)
                .map(this::toUserDetails)
                .or(() -> brokerAuthRepository.findByEmail(username).map(this::toUserDetails))
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));
    }

    private UserDetails toUserDetails(AdministratorAuth auth) {
        var admin = auth.getAdministrator();
        return AuthUserDetails.forAdministrator(
                auth.getEmail(),
                auth.getPasswordHash(),
                admin.getRole(),
                admin.getId()
        );
    }

    private UserDetails toUserDetails(BrokerAuth auth) {
        var broker = auth.getBroker();
        if (broker.getStatus() == BrokerStatus.INACTIVE) {
            throw new AccountInactiveException("Broker account is inactive");
        }
        return AuthUserDetails.forBroker(
                auth.getEmail(),
                auth.getPasswordHash(),
                broker.getId()
        );
    }
}
