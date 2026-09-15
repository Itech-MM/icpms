package org.flexitech.projects.icpms.api.controllers;

import java.util.List;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.flexitech.projects.icpms.api.security.OperatorPrincipal;
import org.flexitech.projects.icpms.common.CommonConstants;
import org.flexitech.projects.icpms.common.enums.OperatorRole;
import org.flexitech.projects.icpms.common.exceptions.NoActiveSubscriptionException;
import org.flexitech.projects.icpms.dto.SearchResultDTO;
import org.flexitech.projects.icpms.dto.api.request.member.RegisterMemberRequest;
import org.flexitech.projects.icpms.dto.api.request.member.SubscribeMemberPlanRequest;
import org.flexitech.projects.icpms.dto.api.request.member.TopUpBalanceRequest;
import org.flexitech.projects.icpms.dto.api.response.ApiResponse;
import org.flexitech.projects.icpms.dto.member.MemberBalanceTransactionDTO;
import org.flexitech.projects.icpms.dto.member.MemberDTO;
import org.flexitech.projects.icpms.dto.member.MemberPlanDTO;
import org.flexitech.projects.icpms.dto.member.MemberSearchDTO;
import org.flexitech.projects.icpms.dto.member.MemberSubscriptionDTO;
import org.flexitech.projects.icpms.dto.member.MembersSummaryDTO;
import org.flexitech.projects.icpms.dto.operator.OperatorDTO;
import org.flexitech.projects.icpms.service.member.MemberBalanceTransactionService;
import org.flexitech.projects.icpms.service.member.MemberPlanService;
import org.flexitech.projects.icpms.service.member.MemberService;
import org.flexitech.projects.icpms.service.member.MemberSubscriptionService;
import org.flexitech.projects.icpms.service.operator.OperatorService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
@Slf4j
public class MemberApiController {

	private final MemberService memberService;
	private final MemberSubscriptionService memberSubscriptionService;
	private final MemberPlanService memberPlanService;
	private final MemberBalanceTransactionService memberBalanceTransactionService;
	private final OperatorService operatorService;

	@GetMapping("/search")
	public ResponseEntity<ApiResponse<SearchResultDTO<MemberDTO>>> search(MemberSearchDTO searchDTO,
			@RequestParam(defaultValue = "1") int page, Authentication authentication) {
		try {
			if (currentOperator(authentication) == null) {
				return ApiResponse.error(HttpStatus.UNAUTHORIZED, "Operator context is required.");
			}
			Pageable pageable = PageRequest.of(Math.max(0, page - 1), CommonConstants.ROW_PER_PAGE, Sort.by("id").descending());
			SearchResultDTO<MemberDTO> result = memberService.searchMembers(searchDTO, pageable);
			return ApiResponse.ok(result);
		} catch (Exception e) {
			log.error("Error searching members:: {}", ExceptionUtils.getStackTrace(e));
			return ApiResponse.internalError(e.getMessage());
		}
	}

	@GetMapping("/summary")
	public ResponseEntity<ApiResponse<MembersSummaryDTO>> summary(Authentication authentication) {
		try {
			if (currentOperator(authentication) == null) {
				return ApiResponse.error(HttpStatus.UNAUTHORIZED, "Operator context is required.");
			}
			return ApiResponse.ok(memberService.getMembersSummary());
		} catch (Exception e) {
			log.error("Error fetching members summary:: {}", ExceptionUtils.getStackTrace(e));
			return ApiResponse.internalError(e.getMessage());
		}
	}

	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse<MemberDTO>> getById(@PathVariable Long id, Authentication authentication) {
		try {
			if (currentOperator(authentication) == null) {
				return ApiResponse.error(HttpStatus.UNAUTHORIZED, "Operator context is required.");
			}
			MemberDTO member = memberService.getMemberById(id);
			return ApiResponse.ok(member);
		} catch (Exception e) {
			log.error("Error fetching member:: {}", ExceptionUtils.getStackTrace(e));
			return ApiResponse.internalError(e.getMessage());
		}
	}

	@GetMapping("/{id}/subscription")
	public ResponseEntity<ApiResponse<MemberSubscriptionDTO>> getSubscription(@PathVariable Long id, Authentication authentication) {
		try {
			if (currentOperator(authentication) == null) {
				return ApiResponse.error(HttpStatus.UNAUTHORIZED, "Operator context is required.");
			}
			try {
				MemberSubscriptionDTO subscription = memberSubscriptionService.getActiveSubscription(id);
				return ApiResponse.ok(subscription);
			} catch (NoActiveSubscriptionException e) {
				return ApiResponse.notFound("No active subscription for this member.");
			}
		} catch (Exception e) {
			log.error("Error fetching member subscription:: {}", ExceptionUtils.getStackTrace(e));
			return ApiResponse.internalError(e.getMessage());
		}
	}

	@GetMapping("/{id}/subscriptions/{subscriptionId}/transactions")
	public ResponseEntity<ApiResponse<List<MemberBalanceTransactionDTO>>> transactions(@PathVariable Long id,
			@PathVariable Long subscriptionId, Authentication authentication) {
		try {
			if (currentOperator(authentication) == null) {
				return ApiResponse.error(HttpStatus.UNAUTHORIZED, "Operator context is required.");
			}
			List<MemberBalanceTransactionDTO> transactions = memberBalanceTransactionService.getTransactionsBySubscription(subscriptionId);
			return ApiResponse.ok(transactions);
		} catch (Exception e) {
			log.error("Error fetching member balance transactions:: {}", ExceptionUtils.getStackTrace(e));
			return ApiResponse.internalError(e.getMessage());
		}
	}

	@GetMapping("/plans/active")
	public ResponseEntity<ApiResponse<List<MemberPlanDTO>>> activePlans(Authentication authentication) {
		try {
			if (currentOperator(authentication) == null) {
				return ApiResponse.error(HttpStatus.UNAUTHORIZED, "Operator context is required.");
			}
			return ApiResponse.ok(memberPlanService.getActivePlans());
		} catch (Exception e) {
			log.error("Error fetching active plans:: {}", ExceptionUtils.getStackTrace(e));
			return ApiResponse.internalError(e.getMessage());
		}
	}

	@PostMapping
	public ResponseEntity<ApiResponse<MemberDTO>> register(@Valid @RequestBody RegisterMemberRequest request, Authentication authentication, @RequestParam Long approvedBySupervisorId) {
		try {
			if (currentOperator(authentication) == null) {
				return ApiResponse.error(HttpStatus.UNAUTHORIZED, "Operator context is required.");
			}
			ResponseEntity<ApiResponse<MemberDTO>> approvalError = validateSupervisorApproval(approvedBySupervisorId);
			if (approvalError != null) {
				return approvalError;
			}
			
			MemberDTO saved = memberService.manageMember(request.getMember(), request.getVehicles());
			return ApiResponse.ok(saved, "Member registered successfully.");
		} catch (Exception e) {
			log.error("Error registering member:: {}", ExceptionUtils.getStackTrace(e));
			return ApiResponse.internalError(e.getMessage());
		}
	}

	@PostMapping("/{id}/subscribe")
	public ResponseEntity<ApiResponse<MemberSubscriptionDTO>> subscribe(@PathVariable Long id, @RequestParam Long planId,
			Authentication authentication, @RequestParam Long approvedBySupervisorId) {
		try {
			if (currentOperator(authentication) == null) {
				return ApiResponse.error(HttpStatus.UNAUTHORIZED, "Operator context is required.");
			}
			ResponseEntity<ApiResponse<MemberSubscriptionDTO>> approvalError = validateSupervisorApproval(approvedBySupervisorId);
			if (approvalError != null) {
				return approvalError;
			}
			MemberSubscriptionDTO subscription = memberSubscriptionService.subscribe(new SubscribeMemberPlanRequest(id, planId, approvedBySupervisorId));
			return ApiResponse.ok(subscription, "Member subscribed successfully.");
		} catch (Exception e) {
			log.error("Error subscribing member:: {}", ExceptionUtils.getStackTrace(e));
			return ApiResponse.internalError(e.getMessage());
		}
	}

	@PostMapping("/{id}/subscriptions/{subscriptionId}/renew")
	public ResponseEntity<ApiResponse<MemberSubscriptionDTO>> renew(@PathVariable Long id, @PathVariable Long subscriptionId,
			Authentication authentication, @RequestParam Long approvedBySupervisorId) {
		try {
			if (currentOperator(authentication) == null) {
				return ApiResponse.error(HttpStatus.UNAUTHORIZED, "Operator context is required.");
			}
			ResponseEntity<ApiResponse<MemberSubscriptionDTO>> approvalError = validateSupervisorApproval(approvedBySupervisorId);
			if (approvalError != null) {
				return approvalError;
			}
			MemberSubscriptionDTO subscription = memberSubscriptionService.renew(subscriptionId, approvedBySupervisorId);
			return ApiResponse.ok(subscription, "Subscription renewed successfully.");
		} catch (Exception e) {
			log.error("Error renewing subscription:: {}", ExceptionUtils.getStackTrace(e));
			return ApiResponse.internalError(e.getMessage());
		}
	}

	@PostMapping("/{id}/subscriptions/{subscriptionId}/topup")
	public ResponseEntity<ApiResponse<MemberSubscriptionDTO>> topUp(@PathVariable Long id, @PathVariable Long subscriptionId,
			@Valid @RequestBody TopUpBalanceRequest request, @RequestParam Long approvedBySupervisorId, Authentication authentication) {
		try {
			if (currentOperator(authentication) == null) {
				return ApiResponse.error(HttpStatus.UNAUTHORIZED, "Operator context is required.");
			}
			ResponseEntity<ApiResponse<MemberSubscriptionDTO>> approvalError = validateSupervisorApproval(approvedBySupervisorId);
			if (approvalError != null) {
				return approvalError;
			}

			request.setSupervisorId(approvedBySupervisorId);

			MemberSubscriptionDTO subscription = memberSubscriptionService.topUp(subscriptionId, request);
			return ApiResponse.ok(subscription, "Balance topped up successfully.");
		} catch (Exception e) {
			log.error("Error topping up subscription:: {}", ExceptionUtils.getStackTrace(e));
			return ApiResponse.internalError(e.getMessage());
		}
	}

	@PostMapping("/{id}/subscriptions/{subscriptionId}/cancel")
	public ResponseEntity<ApiResponse<MemberSubscriptionDTO>> cancel(@PathVariable Long id, @PathVariable Long subscriptionId,
			@RequestParam Long approvedBySupervisorId, Authentication authentication) {
		try {
			if (currentOperator(authentication) == null) {
				return ApiResponse.error(HttpStatus.UNAUTHORIZED, "Operator context is required.");
			}
			ResponseEntity<ApiResponse<MemberSubscriptionDTO>> approvalError = validateSupervisorApproval(approvedBySupervisorId);
			if (approvalError != null) {
				return approvalError;
			}
			MemberSubscriptionDTO subscription = memberSubscriptionService.cancel(subscriptionId, approvedBySupervisorId);
			return ApiResponse.ok(subscription, "Subscription cancelled successfully.");
		} catch (Exception e) {
			log.error("Error cancelling subscription:: {}", ExceptionUtils.getStackTrace(e));
			return ApiResponse.internalError(e.getMessage());
		}
	}

	private OperatorPrincipal currentOperator(Authentication authentication) {
		if (authentication != null && authentication.getPrincipal() instanceof OperatorPrincipal principal) {
			return principal;
		}
		return null;
	}

	private <T> ResponseEntity<ApiResponse<T>> validateSupervisorApproval(Long approvedBySupervisorId) {
		if (approvedBySupervisorId == null) {
			return ApiResponse.error(HttpStatus.FORBIDDEN, "Supervisor approval is required.");
		}
		OperatorDTO supervisor;
		try {
			supervisor = operatorService.getOperatorById(approvedBySupervisorId);
			if (supervisor == null || !OperatorRole.SUPERVISOR.getCode().equals(supervisor.getRole())) {
				return ApiResponse.error(HttpStatus.FORBIDDEN, "Provided operator is not a valid supervisor.");
			}
		} catch (Exception e) {
			log.error("Error on getting supervisor operator:: {}", ExceptionUtils.getStackTrace(e));
			return ApiResponse.error(HttpStatus.FORBIDDEN, "Provided operator is not a valid supervisor.");
		}
		return null;
	}
}