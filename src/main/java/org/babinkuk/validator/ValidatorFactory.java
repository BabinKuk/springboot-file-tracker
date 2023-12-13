package org.babinkuk.validator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * factory for instantiation of different validator classes
 * 
 * @author BabinKuk
 *
 */
@Component
public class ValidatorFactory {
	
	private final Logger log = LogManager.getLogger(getClass());
	
	@Autowired
	private ApplicationContext applicationContext;
	
	public Validator getValidator(ValidatorRole type) {
		
		String beanName = "validator." + (type != null ? type : ValidatorRole.ROLE_USER);
		
		Validator validator = applicationContext.getBean(beanName, Validator.class);
		
		if (validator == null) {
			throw new IllegalStateException("Cannot acquire validator instance for type : " + type);
		}
		
		return validator;
	}
	
}
