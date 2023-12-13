package org.babinkuk.validator;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.babinkuk.exception.ObjectNotFoundException;
import org.babinkuk.exception.ValidatorException;
import org.babinkuk.service.FileService;
import org.babinkuk.vo.FileVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * implementation class for different field validations
 *  
 * @author BabinKuk
 *
 */
@Component
public class BusinessValidator {
	
	private final Logger log = LogManager.getLogger(getClass());
	
	@Autowired
	private FileService fileService;
	
	/**
	 * @param title
	 * @throws ValidationException
	 */
	public void validateFile(FileVO vo) throws ValidatorException {
		log.info("validateFile {}", vo);
		validateStringIsBlank(vo.getFileName(), ValidatorCodes.ERROR_CODE_FILE_NAME_EMPTY);
		validateStringIsBlank(vo.getFileDesc(), ValidatorCodes.ERROR_CODE_IMAGE_NAME_EMPTY);
		titleExists(vo);
	}
	
	/**
	 * validate if image name already exist 
	 * must be unique (call repository findByImageName)
	 * 
	 * @param vo
	 * @return
	 * @throws ValidatorException
	 */
	public void titleExists(FileVO vo) throws ValidatorException {
		FileVO dbVO = null;
		
		dbVO = fileService.findByFileDesc(vo.getFileDesc());
		 
		if (dbVO == null) {
			// image name not found
			// that's ok
			//log.info("image name not found");
		} else {
			//log.info("image name found");
			if (dbVO.getId() == vo.getId()) {
				// same course, title has not changed
				log.warn("belongs to same file, nothing has not changed");
			} else {
				// another course with same title already exists in db
				log.error(ValidatorCodes.ERROR_CODE_IMAGE_NAME_ALREADY_EXIST.getMessage());
				throw new ValidatorException(ValidatorCodes.ERROR_CODE_IMAGE_NAME_ALREADY_EXIST);
			}
		}
	}
	
	/**
	 * validate if object already exist
	 * @param vo
	 * @return
	 * @throws ValidatorException
	 */
	public void objectExists(FileVO vo) throws ValidatorException {
		
		FileVO result = objectExists(vo.getId());
			
		if (result != null) {
			// id found
			log.warn("file id found");
		} else {
			// id not found
			//log.error("result.notPresent");
			throw new ValidatorException(ValidatorCodes.ERROR_CODE_FILE_INVALID);
		}
	}

	/**
	 * @param str
	 * @param errorCode
	 * @throws ValidatorException
	 */
	private void validateStringIsBlank(String str, ValidatorCodes errorCode) throws ValidatorException {
		log.info("validateStringIsBlank {}", str);
		if (StringUtils.isBlank(str)) {
			throw new ValidatorException(errorCode);
		}
	}

	/**
	 * @param id
	 * @return
	 * @throws ObjectNotFoundException
	 */
	public FileVO objectExists(int id) throws ObjectNotFoundException {
		
		return fileService.findById(id);
	}

}
