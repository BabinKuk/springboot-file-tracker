package org.babinkuk.validator;

/**
 * enum representing different validator messages
 * 
 * @author BabinKuk
 *
 */
public enum ValidatorCodes {
	
	VALIDATION_FAILED("validation_failed"),
	ERROR_CODE_FILE_NAME_EMPTY("error_code_file_name_empty"),
	ERROR_CODE_IMAGE_NAME_EMPTY("error_code_image_name_empty"),
	ERROR_CODE_FILE_INVALID("error_code_file_invalid"),
	ERROR_CODE_ACTION_INVALID("error_code_action_invalid"),
	ERROR_CODE_IMAGE_NAME_ALREADY_EXIST("error_code_image_name_already_exist"),
	ERROR_CODE_FILE_ID_NOT_FOUND("error_code_file_id_not_found"),
	ERROR_CODE_IMAGE_NAME_NOT_FOUND("error_code_image_name_not_found"),
	ERROR_CODE_FILE_SIZE_EXCEPTION("File size too large");
	
	private String message;
	
	ValidatorCodes(String message) {
		this.message = message;
	}
	
	public String getMessage() {
		return message;
	}
	
	public static ValidatorCodes fromMessage(String message) {
		
		for (ValidatorCodes code : ValidatorCodes.values()) {
			if(code.message.equalsIgnoreCase(message)) return code;
		}
		
		return null;
	}

}
