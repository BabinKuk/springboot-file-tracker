package org.babinkuk.validator;

import java.util.Arrays;

/**
 * enum representing different CRUD action types
 * 
 * @author BabinKuk
 *
 */
public enum ActionType {
	
	CREATE,
	READ,
	UPDATE,
	DELETE;
	
	public static ActionType valueOfIgnoreCase(String str) {
		return Arrays.stream(ActionType.values())
				.filter(e -> e.name().equalsIgnoreCase(str))
				.findAny()
				.orElseThrow(() -> new IllegalArgumentException("Cannot find enum constant for " + str));
	}
}
