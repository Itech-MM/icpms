package org.flexitech.projects.icpms.common.exceptions;

public class CustomerNotFoundException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6636530154402333068L;

	public CustomerNotFoundException(String message) {
		super(message);
	}
}
