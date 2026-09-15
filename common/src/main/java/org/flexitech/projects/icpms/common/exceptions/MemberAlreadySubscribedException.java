package org.flexitech.projects.icpms.common.exceptions;

public class MemberAlreadySubscribedException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5406239278736722255L;

	public MemberAlreadySubscribedException(String message) {
		super(message);
	}
}