package org.flexitech.projects.icpms.common.exceptions.coupon;

public class InvalidCouponException extends RuntimeException{

	

	/**
	 * 
	 */
	private static final long serialVersionUID = -7672887048808839608L;
	
	public InvalidCouponException(String error) {
		super(error);
	}

}
