package org.babinkuk.validator;

import org.babinkuk.exception.ObjectNotFoundException;
import org.babinkuk.exception.ObjectValidationException;
import org.babinkuk.vo.FileVO;

/**
 * validator interface
 * 
 * @author BabinKuk
 *
 */
public interface Validator {
	
	/** 
	 * @param file
	 * @param action
	 * @return
	 * @throws ObjectValidationException
	 */
	public void validate(FileVO vo, ActionType action) throws ObjectValidationException;
	
	/**
	 * @param id
	 * @param validatorType
	 * @return
	 * @throws ObjectNotFoundException
	 */
	public void validate(int id, ActionType action) throws ObjectNotFoundException;
}
