package org.flexitech.projects.icpms.api.security;

import org.flexitech.projects.icpms.common.enums.ActiveStatus;
import org.flexitech.projects.icpms.common.enums.OperatorAuthMethod;
import org.flexitech.projects.icpms.dto.api.request.auth.LoginRequestDTO;
import org.flexitech.projects.icpms.persistence.entities.operator.Operator;
import org.flexitech.projects.icpms.persistence.repositories.operator.OperatorRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OperatorLoginAuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final OperatorRepository operatorRepository;

    public OperatorPrincipal authenticate(LoginRequestDTO request) {

        OperatorAuthMethod authMethod =
                OperatorAuthMethod.getByCode(request.getAuthMethod());

        if (authMethod == null) {
            throw new BadCredentialsException(
                    "Invalid authentication method.");
        }

        return switch (authMethod) {

            case PASSWORD -> authenticatePassword(request);

            case RFID -> authenticateRfid(request);

            case QR_CODE -> authenticateQrCode(request);

            case MAG_STRIPE -> authenticateMagStripe(request);

            case PIN -> authenticatePin(request);
        };
    }

    private OperatorPrincipal authenticatePassword(
            LoginRequestDTO request) {

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.getUsername(),
                                request.getPassword()
                        )
                );

        return (OperatorPrincipal) authentication.getPrincipal();
    }

    private OperatorPrincipal authenticateRfid(
            LoginRequestDTO request) {

        Operator operator = operatorRepository.findByRfidToken(request.getCredential())
                .orElseThrow(() -> new BadCredentialsException("Invalid RFID credential."));

        return toPrincipal(operator);
    }

    private OperatorPrincipal authenticateQrCode(
            LoginRequestDTO request) {

        Operator operator = operatorRepository.findByQrCodeToken(request.getCredential())
                .orElseThrow(() -> new BadCredentialsException("Invalid QR credential."));

        return toPrincipal(operator);
    }

    private OperatorPrincipal authenticateMagStripe(
            LoginRequestDTO request) {

        Operator operator = operatorRepository.findByStripeToken(request.getCredential())
                .orElseThrow(() -> new BadCredentialsException("Invalid swipe card credential."));

        return toPrincipal(operator);
    }

    private OperatorPrincipal authenticatePin(
            LoginRequestDTO request) {

        Operator operator = operatorRepository.findByPinPassword(request.getCredential())
                .orElseThrow(() -> new BadCredentialsException("Invalid PIN."));

        return toPrincipal(operator);
    }

    private OperatorPrincipal toPrincipal(Operator operator) {
        if (operator.getStatus() == null || operator.getStatus() != ActiveStatus.ACTIVE.getCode()) {
            throw new DisabledException("Operator account is inactive.");
        }
        return new OperatorPrincipal(operator);
    }
}