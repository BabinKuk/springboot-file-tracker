package org.babinkuk.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.babinkuk.common.ApiResponse;
import org.babinkuk.service.FileService;
import org.babinkuk.validator.ActionType;
import org.babinkuk.validator.ValidatorFactory;
import org.babinkuk.validator.ValidatorRole;
import org.babinkuk.vo.FileVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.babinkuk.config.Api.*;

import java.io.IOException;
import java.util.Optional;

/**
 * Controller that uses FileService to export Rest APIs: 
 * POST a file, GET all files' information, download a File
 * 
 * @author BabinKuk
 *
 */
@RestController
@RequestMapping(ROOT + FILES)
public class FileController {
	
	private final Logger log = LogManager.getLogger(getClass());
	
	// services
	private FileService fileService;
	
	@Autowired
	private ValidatorFactory validatorFactory;
	
	@Autowired
	private ObjectMapper mapper;
	
	public FileController() {
		// TODO Auto-generated constructor stub
	}

	@Autowired
	public FileController(FileService fileService) {
		this.fileService = fileService;
	}

	/**
	 * expose GET "/files"
	 * get image list
	 *
	 * @param 
	 * @return ResponseEntity
	 */
	@GetMapping("")
	public ResponseEntity<Iterable<FileVO>> getAllFiles() {

		return ResponseEntity.of(Optional.ofNullable(fileService.getAllFiles()));
	}
	
	/**
	 * expose GET "/files/{fileId}"
	 * get specific file details
	 * 
	 * @param 
	 * @return ResponseEntity
	 */
	@GetMapping("/{fileId}")
	public ResponseEntity<FileVO> getFile(@PathVariable int fileId) {
		
		return ResponseEntity.of(Optional.ofNullable(fileService.findById(fileId)));
	}
	
	/**
	 * expose POST "/files/upload"
	 * add new image
	 * 
	 * @param file
	 * @param validationRole
	 * @return
	 * @throws IOException 
	 * @throws JsonProcessingException
	 */
	@PostMapping(UPLOAD)
	public ResponseEntity<ApiResponse> uploadFile(
			@RequestParam(FILE) MultipartFile file,
			@RequestParam(name=VALIDATION_ROLE, required = false) ValidatorRole validationRole) throws IOException {
		
		String fileName = StringUtils.isEmpty(StringUtils.cleanPath(file.getOriginalFilename())) ? "DEFAULT": StringUtils.cleanPath(file.getOriginalFilename());
		String imageName = StringUtils.isEmpty(StringUtils.cleanPath(file.getName())) ? "DEFAULT" : StringUtils.cleanPath(file.getName());
		
		// in case id is passed in json, set to 0
		// this is to force a save of new item ... instead of update
		FileVO fileVO = new FileVO();
		fileVO.setId(0);
		fileVO.setFileName(fileName);
		fileVO.setFileDesc(imageName);
		fileVO.setData(file.getBytes());
		//log.info(fileVO);
		
		validatorFactory.getValidator(validationRole).validate(fileVO, ActionType.CREATE);
		
		return ResponseEntity.of(Optional.ofNullable(fileService.saveFile(fileVO)));
	}
	
	/**
	 * expose PUT "/files/{fileId}"
	 * update file and image names 
	 * 
	 * @param fileId
	 * @param fileName
	 * @param imageName
	 * @param validationRole
	 * @return
	 * @throws JsonProcessingException
	 */
	@PutMapping("/{fileId}")
	public ResponseEntity<ApiResponse> updateFile(
			@PathVariable int fileId,
			@RequestParam(name=FILE_NAME, required = true) String fileName,
			@RequestParam(name=IMAGE_NAME, required = true) String imageName,
			@RequestParam(name=VALIDATION_ROLE, required = false) ValidatorRole validationRole) throws JsonProcessingException {

		// first find file
		FileVO fileVO = fileService.findById(fileId);
		
		// set new names
		fileVO.setFileName(fileName);
		fileVO.setFileDesc(imageName);
		
		validatorFactory.getValidator(validationRole).validate(fileVO, ActionType.UPDATE);
		
		return ResponseEntity.of(Optional.ofNullable(fileService.saveFile(fileVO)));
	}
	
	/**
	 * expose DELETE "/{fileId}"
	 * 
	 * @param fileId
	 * @param validationRole
	 * @return
	 */
	@DeleteMapping("/{fileId}")
	public ResponseEntity<ApiResponse> deleteFile(
			@PathVariable int fileId, 
			@RequestParam(name=VALIDATION_ROLE, required = false) ValidatorRole validationRole) {
		
		validatorFactory.getValidator(validationRole).validate(fileId, ActionType.DELETE);
		
		return ResponseEntity.of(Optional.ofNullable(fileService.deleteFile(fileId)));
	}
}
