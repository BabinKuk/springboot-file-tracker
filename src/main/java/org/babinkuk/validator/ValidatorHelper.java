package org.babinkuk.validator;

import java.util.LinkedList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.babinkuk.exception.ObjectNotFoundException;
import org.babinkuk.exception.ObjectValidationException;
import org.babinkuk.exception.ValidatorException;
import org.babinkuk.vo.FileVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * validator helper methods
 * 
 * @author BabinKuk
 *
 */
@Component
public class ValidatorHelper {
	
	private final Logger log = LogManager.getLogger(getClass());
	
	@Autowired
	private BusinessValidator validator;
	
	public List<ValidatorException> validate(FileVO vo, ActionType action) throws ObjectValidationException {
		List<ValidatorException> exceptions = new LinkedList<ValidatorException>();
		
		try {
			validator.validateFile(vo);
		} catch (ValidatorException e) {
			exceptions.add(e);
		}
		
		if (action == ActionType.UPDATE || action == ActionType.DELETE || action == ActionType.READ) {
			try {
				validator.objectExists(vo);
			} catch (ValidatorException e) {
				exceptions.add(e);
			}
		}
		
		return exceptions;
	}
	
	public List<ValidatorException> validate(int id) throws ObjectNotFoundException {
		List<ValidatorException> exceptions = new LinkedList<ValidatorException>();
		
		try {
			validator.objectExists(id);
		} catch (ObjectNotFoundException e) {
			log.error(e.getMessage());
			throw e;
		}
		
		return exceptions;
	}
}
