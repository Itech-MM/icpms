package org.flexitech.projects.icpms.common.exceptions;

public class NoActiveSubscriptionException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7049433651070139562L;

	public NoActiveSubscriptionException(String message) {
		super(message);
	}
}