package org.babinkuk.validator;

import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.babinkuk.config.MessagePool;
import org.babinkuk.exception.ObjectNotFoundException;
import org.babinkuk.exception.ObjectValidationException;
import org.babinkuk.exception.ValidatorException;
import org.babinkuk.vo.FileVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import static org.babinkuk.config.Api.*;

/**
 * if special valdations are required depending on the role 
 * implementation class for User role
 * 
 * @author BabinKuk
 *
 */
@Component("validator.ROLE_USER")
public class ValidatorImplRoleUser implements Validator {

private final Logger log = LogManager.getLogger(getClass());
	
	@Autowired
	private ValidatorHelper validatorHelper;
	
	@Override
	public void validate(FileVO vo, ActionType action) throws ObjectValidationException {
		log.info("ROLE_STUDENT Validating {} {} (vo={})", action, vo);
		
		List<ValidatorException> exceptionList = new LinkedList<ValidatorException>();
		
		// only READ action enabled
		if (ActionType.READ == action) {
			exceptionList.addAll(validatorHelper.validate(vo, action));
			
			String message = String.format(MessagePool.getMessage(ValidatorCodes.VALIDATION_FAILED.getMessage()), action);
			ObjectValidationException e = new ObjectValidationException(message);
			
			for (ValidatorException validationException : exceptionList) {
				e.addValidationError(MessagePool.getMessage(validationException.getErrorCode().getMessage()));
			}
			
			if (e.hasErrors()) {
				throw e;
			}
			
		} else {
			String message = String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_ACTION_INVALID.getMessage()), action);
			ObjectValidationException e = new ObjectValidationException(message);
			throw e;
		}
	}

	@Override
	public void validate(int id, ActionType action) throws ObjectNotFoundException {
		log.info("ROLE_STUDENT Validating {} {} (id={})", action, id);
		
		List<ValidatorException> exceptionList = new LinkedList<ValidatorException>();
		
		// only READ action enabled
		if (ActionType.READ == action) {
			//log.info("read action only");
			
			exceptionList.addAll(validatorHelper.validate(id));
			
			String message = String.format(MessagePool.getMessage(ValidatorCodes.VALIDATION_FAILED.getMessage()), action);
			ObjectValidationException e = new ObjectValidationException(message);
			
			for (ValidatorException validationException : exceptionList) {
				//log.error(validationException.getErrorCode().getMessage());
				e.addValidationError(MessagePool.getMessage(validationException.getErrorCode().getMessage()));
			}
			
			if (e.hasErrors()) {
				throw e;
			}
		
		} else {
			String message = String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_ACTION_INVALID.getMessage()), action);
			ObjectValidationException e = new ObjectValidationException(message);
			throw e;
		}
	}

}