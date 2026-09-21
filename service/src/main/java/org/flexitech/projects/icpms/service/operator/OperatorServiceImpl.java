package org.flexitech.projects.icpms.service.operator;

import java.io.File;
import java.nio.file.Files;
import java.util.Date;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.flexitech.projects.icpms.common.CommonValidators;
import org.flexitech.projects.icpms.common.enums.ActiveStatus;
import org.flexitech.projects.icpms.common.utils.ImageUtils;
import org.flexitech.projects.icpms.common.utils.QRCodeGenerator;
import org.flexitech.projects.icpms.dto.SearchResultDTO;
import org.flexitech.projects.icpms.dto.operator.OperatorDTO;
import org.flexitech.projects.icpms.dto.operator.OperatorSearchDTO;
import org.flexitech.projects.icpms.persistence.entities.operator.Operator;
import org.flexitech.projects.icpms.persistence.entities.site.Site;
import org.flexitech.projects.icpms.persistence.entities.user.User;
import org.flexitech.projects.icpms.persistence.repositories.operator.OperatorRepository;
import org.flexitech.projects.icpms.persistence.repositories.site.SiteRepository;
import org.flexitech.projects.icpms.service.auth.AuthenticationService;
import org.flexitech.projects.icpms.service.specifications.operator.OperatorSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OperatorServiceImpl implements OperatorService {

	private final OperatorRepository operatorRepository;
	private final SiteRepository siteRepository;
	private final PasswordEncoder passwordEncoder;
	private final AuthenticationService authenticationService;
	private final QRCodeGenerator qrCodeGenerator;
	private final ImageUtils imageUtils;

	private static final int MAX_TOKEN_GENERATION_ATTEMPTS = 5;

	@Override
	@Transactional
	public OperatorDTO manageOperator(OperatorDTO dto) throws Exception {
	    Operator operator;
	    User user = this.authenticationService.getLoggedInUser();
	    boolean isNew = !CommonValidators.validLong(dto.getId());
	    boolean needsQrBackfill = false;

	    if (!isNew) {
	        operator = this.operatorRepository.findById(dto.getId())
	                .orElseThrow(() -> new EntityNotFoundException("Operator doesn't exist!"));
	        operator.setUpdatedTime(new Date());
	        operator.setUpdatedBy(user);

	        if (CommonValidators.validString(dto.getPassword())) {
	            operator.setPassword(passwordEncoder.encode(dto.getPassword()));
	        } else if (!CommonValidators.validString(operator.getPassword())) {
	            operator.setPassword(passwordEncoder.encode("changeme123"));
	        }

	        if (!CommonValidators.validString(operator.getPinPassword())) {
	            operator.setPinPassword(generateUniqueToken(this::generatePinCandidate, operatorRepository::existsByPinPassword));
	        }

	        if (!CommonValidators.validString(operator.getRfidToken())) {
	            operator.setRfidToken(generateUniqueToken(this::generateRfidCandidate, operatorRepository::existsByRfidToken));
	        }

	        if (!CommonValidators.validString(operator.getStripeToken())) {
	            operator.setStripeToken(generateUniqueToken(this::generateStripeCandidate, operatorRepository::existsByStripeToken));
	        }

	        needsQrBackfill = !CommonValidators.validString(operator.getQrCodeToken());
	    } else {
	        operator = new Operator();
	        operator.setCreatedTime(new Date());
	        operator.setCreatedBy(user);
	        operator.setPassword(passwordEncoder.encode(
	                CommonValidators.validString(dto.getPassword()) ? dto.getPassword() : "changeme123"));
	    }

	    operator.setName(dto.getName());
	    operator.setUsername(dto.getUsername());
	    operator.setPhoneNumber(dto.getPhoneNumber());
	    operator.setRole(dto.getRole());
	    operator.setStatus(CommonValidators.isValidObject(dto.getStatus()) ? dto.getStatus() : ActiveStatus.ACTIVE.getCode());

	    if (CommonValidators.validLong(dto.getSiteId())) {
	        Site site = this.siteRepository.findById(dto.getSiteId())
	                .orElseThrow(() -> new EntityNotFoundException("Site doesn't exist!"));
	        operator.setSite(site);
	    } else {
	        operator.setSite(null);
	    }

	    if (isNew) {
	        operator.setPinPassword(assignPinPassword(dto.getPinPassword()));
	        operator.setRfidToken(generateUniqueToken(this::generateRfidCandidate, operatorRepository::existsByRfidToken));
	        operator.setStripeToken(generateUniqueToken(this::generateStripeCandidate, operatorRepository::existsByStripeToken));
	    }

	    Operator saved = this.operatorRepository.save(operator);

	    if (isNew || needsQrBackfill) {
	        String qrToken = generateUniqueToken(this::generateQrCandidate, operatorRepository::existsByQrCodeToken);
	        String qrImagePath = generateAndUploadQrImage(qrToken, saved.getId());
	        saved.setQrCodeToken(qrToken);
	        saved.setQrImagePath(qrImagePath);
	        saved = this.operatorRepository.save(saved);
	    }

	    return resolveImagePath(saved);
	}

	private String generatePinCandidate() {
	    return String.format("%06d", (int) (Math.random() * 1_000_000));
	}

	private String assignPinPassword(String requestedPin) {
		if (!CommonValidators.validString(requestedPin)) {
			throw new IllegalArgumentException("PIN is required for a new operator.");
		}
		if (this.operatorRepository.existsByPinPassword(requestedPin)) {
			throw new IllegalArgumentException("This PIN is already in use, please choose another.");
		}
		return requestedPin;
	}

	private String generateRfidCandidate() {
		return "RFID-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
	}

	private String generateStripeCandidate() {
		return "SWP-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
	}

	private String generateQrCandidate() {
		return "QR-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
	}

	private String generateUniqueToken(Supplier<String> candidateGenerator, Predicate<String> existsCheck) {
		int attempts = 0;
		while (attempts < MAX_TOKEN_GENERATION_ATTEMPTS) {
			String candidate = candidateGenerator.get();
			if (!existsCheck.test(candidate)) {
				return candidate;
			}
			attempts++;
		}
		throw new IllegalStateException(
				"Could not generate a unique token after " + MAX_TOKEN_GENERATION_ATTEMPTS + " attempts.");
	}

	private String generateAndUploadQrImage(String qrToken, Long operatorId) throws Exception {
		File qrFile = this.qrCodeGenerator.generateQRImage(qrToken);
		try {
			return "/" + this.imageUtils.uploadImage(qrFile, "operator", operatorId);
		} finally {
			Files.deleteIfExists(qrFile.toPath());
		}
	}

	@Override
	public OperatorDTO getOperatorById(Long id) throws Exception {
		Operator operator = this.operatorRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Operator doesn't exist!"));
		return resolveImagePath(operator);
	}

	@Override
	public SearchResultDTO<OperatorDTO> searchOperators(OperatorSearchDTO searchDTO, Pageable pageable)
			throws Exception {
		Specification<Operator> spec = OperatorSpecification.withSearchCriteria(searchDTO);
		Page<Operator> page = operatorRepository.findAll(spec, pageable);

		SearchResultDTO<OperatorDTO> result = new SearchResultDTO<>();
		result.setPageNo(page.getNumber());
		result.setLimit(page.getSize());
		result.setTotalPage(page.getTotalPages());
		result.setTotalRecords((int) page.getTotalElements());
		result.setPageCount(page.getNumberOfElements());
		result.setHasNextPage(page.hasNext());
		result.setResults(page.getContent().stream().map(this::resolveImagePath).collect(Collectors.toList()));
		return result;
	}

	@Override
	@Transactional
	public boolean deleteOperator(Long id) throws Exception {
		Operator operator = this.operatorRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Operator doesn't exist!"));
		this.operatorRepository.delete(operator);
		return true;
	}

	@Override
	@Transactional
	public void changePassword(Long operatorId, String newPassword, String confirmPassword) throws Exception {
		if (!CommonValidators.validString(newPassword) || !CommonValidators.validString(confirmPassword)) {
			throw new IllegalArgumentException("Password and confirmation are required.");
		}
		if (!newPassword.equals(confirmPassword)) {
			throw new IllegalArgumentException("Password and confirmation do not match.");
		}
		Operator operator = this.operatorRepository.findById(operatorId)
				.orElseThrow(() -> new EntityNotFoundException("Operator doesn't exist!"));
		operator.setPassword(this.passwordEncoder.encode(newPassword));
		operator.setUpdatedTime(new Date());
		operator.setUpdatedBy(this.authenticationService.getLoggedInUser());
		this.operatorRepository.save(operator);
	}

	@Override
	@Transactional
	public void changePin(Long operatorId, String newPin, String confirmPin) throws Exception {
		if (!CommonValidators.validString(newPin) || !CommonValidators.validString(confirmPin)) {
			throw new IllegalArgumentException("PIN and confirmation are required.");
		}
		if (!newPin.equals(confirmPin)) {
			throw new IllegalArgumentException("PIN and confirmation do not match.");
		}
		Operator operator = this.operatorRepository.findById(operatorId)
				.orElseThrow(() -> new EntityNotFoundException("Operator doesn't exist!"));
		operator.setPinPassword(updatePinPassword(newPin, operatorId));
		operator.setUpdatedTime(new Date());
		operator.setUpdatedBy(this.authenticationService.getLoggedInUser());
		this.operatorRepository.save(operator);
	}

	private String updatePinPassword(String requestedPin, Long operatorId) {
		if (this.operatorRepository.existsByPinPasswordAndIdNot(requestedPin, operatorId)) {
			throw new IllegalArgumentException("This PIN is already in use, please choose another.");
		}
		return requestedPin;
	}

	private OperatorDTO resolveImagePath(Operator operator) {
		OperatorDTO dto = new OperatorDTO(operator);
		if (CommonValidators.validString(dto.getQrImagePath())) {
			dto.setQrImagePath(imageUtils.getImageUrl(dto.getQrImagePath()));
		}
		return dto;
	}

	@Override
	public boolean existsByUsername(String username) {
		return operatorRepository.existsByUsername(username);
	}

	@Override
	public boolean existsByUsernameAndIdNot(String username, Long id) {
		return operatorRepository.existsByUsernameAndIdNot(username, id);
	}

	@Override
	public boolean existsByPinPassword(String pinPassword) {
		return operatorRepository.existsByPinPassword(pinPassword);
	}
}
