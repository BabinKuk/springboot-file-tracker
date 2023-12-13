package org.babinkuk.validator;

import java.util.Arrays;

/**
 * enum representing different user roles
 * 
 * @author BabinKuk
 *
 */
public enum ValidatorRole {
	
	ROLE_ADMIN,
	ROLE_USER;
	
	public static ValidatorRole valueOfIgnoreCase(String str) {
		return Arrays.stream(ValidatorRole.values())
				.filter(e -> e.name().equalsIgnoreCase(str))
				.findAny()
				.orElseThrow(() -> new IllegalArgumentException("Cannot find enum constant for " + str));
	}
}
