package org.flexitech.projects.icpms.api.security;

import org.flexitech.projects.icpms.common.enums.OperatorAuthMethod;
import org.flexitech.projects.icpms.dto.api.request.auth.LoginRequestDTO;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OperatorLoginAuthenticationService {

    private final AuthenticationManager authenticationManager;

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

        // RFID authentication
        throw new UnsupportedOperationException(
                "RFID authentication not implemented.");
    }

    private OperatorPrincipal authenticateQrCode(
            LoginRequestDTO request) {

        // QR authentication
        throw new UnsupportedOperationException(
                "QR authentication not implemented.");
    }

    private OperatorPrincipal authenticateMagStripe(
            LoginRequestDTO request) {

        // Magnetic stripe authentication
        throw new UnsupportedOperationException(
                "Magnetic stripe authentication not implemented.");
    }

    private OperatorPrincipal authenticatePin(
            LoginRequestDTO request) {

        // PIN authentication
        throw new UnsupportedOperationException(
                "PIN authentication not implemented.");
    }
}