package org.babinkuk.exception;

import org.babinkuk.validator.ValidatorCodes;

public class ValidatorException extends Exception {

	private static final long serialVersionUID = 1L;
	
	private ValidatorCodes errorCode;

	public ValidatorException(ValidatorCodes errorCode) {
		super();
		this.errorCode = errorCode;
	}
	
	public ValidatorCodes getErrorCode() {
		return errorCode;
	}
}
