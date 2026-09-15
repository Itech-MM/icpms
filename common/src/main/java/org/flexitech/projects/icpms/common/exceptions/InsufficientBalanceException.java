package org.flexitech.projects.icpms.common.exceptions;

public class InsufficientBalanceException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4430708863350106311L;

	public InsufficientBalanceException(String message) {
		super(message);
	}
}