package org.babinkuk.controller;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.babinkuk.config.MessagePool;
import org.babinkuk.service.FileService;
import org.babinkuk.utils.ApplicationTestUtils;
import org.babinkuk.validator.ActionType;
import org.babinkuk.validator.ValidatorCodes;
import org.babinkuk.vo.FileVO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.babinkuk.config.Api.*;
import static org.babinkuk.utils.ApplicationTestConstants.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@AutoConfigureMockMvc
public class FileControllerTest {
	
	public static final Logger log = LogManager.getLogger(FileControllerTest.class);
	
	private static MockHttpServletRequest request;
	
	@PersistenceContext
	private EntityManager entityManager;
	
//	@Autowired
//	private MockMvc mockMvc;
	
	@Autowired
	private WebApplicationContext webApplicationContext;
	
	@Autowired
	private JdbcTemplate jdbc;
	
	@Autowired
	ObjectMapper objectMApper;
	
	@Autowired
	private FileService fileService;
	
	@Value("${sql.script.file.insert-1}")
	private String sqlAddFile1;
	
	@Value("${sql.script.file.insert-2}")
	private String sqlAddFile2;
	
	@Value("${sql.script.file.delete}")
	private String sqlDeleteFile;
	
	public static final MediaType APPLICATION_JSON_UTF8 = MediaType.APPLICATION_JSON;
	
	@BeforeAll
	public static void setup() {
		
	}
	
	@BeforeEach
    public void setupDatabase() {
		
		jdbc.execute(sqlAddFile1);
		jdbc.execute(sqlAddFile2);
	}
	
	@AfterEach
	public void setupAfterTransaction() {
		
		jdbc.execute(sqlDeleteFile);
		
//		// check
//		List<Map<String,Object>> userList = new ArrayList<Map<String,Object>>();
//		userList = jdbc.queryForList("select * from user");
//		log.info("size() " + userList.size());
//		for (Map m : userList) {
//			m.forEach((key, value) -> log.info(key + " : " + value));
//		}
	}
	
	@Test
	void getAllFiles() throws Exception {
		
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		
		// get all files
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + FILES)
				.param(VALIDATION_ROLE, ROLE_ADMIN)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(2))) // verify that json root element $ is size 2
			;

		// get all files (different validationRole param)
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + FILES)
				.param(VALIDATION_ROLE, ROLE_USER)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(2))) // verify that json root element $ is size 2
			;
		
		// get all files (without validationRole param)
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + FILES)
			//	.param(VALIDATION_ROLE, "ROLE_USER")
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(2))) // verify that json root element $ is size 2
			;
		
		// get all files (not existing validationRole param)
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + FILES)
				.param(VALIDATION_ROLE, ROLE_NOT_EXIST)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(2))) // verify that json root element $ is size 2
			;
	}
	
	@Test
	void getFile() throws Exception {
		
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		
		// get file with id=1
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + FILES + "/{id}", 1)
				.param(VALIDATION_ROLE, ROLE_ADMIN)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.fileName", is(FILE_1))) // verify json element
			.andExpect(jsonPath("$.fileDesc", is(DESC_1))) // verify json element
			;

		// get file with id=22 (non existing)
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + FILES + "/{id}", 22)
				.param(VALIDATION_ROLE, ROLE_ADMIN)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.message", is(String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_FILE_ID_NOT_FOUND.getMessage()), 22)))) // verify json element
			;
		
		// get file with id=1 (validationRole ROLE_STUDENT)
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + FILES + "/{id}", 1)
				.param(VALIDATION_ROLE, ROLE_USER)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.id", is(1))) // verify json root element id is 1
			.andExpect(jsonPath("$.fileName", is(FILE_1))) // verify json element
			.andExpect(jsonPath("$.fileDesc", is(DESC_1))) // verify json element
			;

		// get file with id=22 (non existing) (validationRole ROLE_STUDENT)
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + FILES + "/{id}", 22)
				.param(VALIDATION_ROLE, ROLE_USER)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.message", is(String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_FILE_ID_NOT_FOUND.getMessage()), 22)))) // verify json element
			;
		
		// get file with id=1 (without validationRole param)
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + FILES + "/{id}", 1)
				//.param(VALIDATION_ROLE, "ROLE_USER")
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.id", is(1))) // verify json root element id is 1
			.andExpect(jsonPath("$.fileName", is(FILE_1))) // verify json element
			.andExpect(jsonPath("$.fileDesc", is(DESC_1))) // verify json element
			;
		
		// get file with id=22 (non existing) (without validationRole param)
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + FILES + "/{id}", 22)
				//.param(VALIDATION_ROLE, "ROLE_USER")
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.message", is(String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_FILE_ID_NOT_FOUND.getMessage()), 22)))) // verify json element
			;
		
		// get file with id=22 (non existing) (not existing validationRole param)
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + FILES + "/{id}", 22)
				.param(VALIDATION_ROLE, ROLE_NOT_EXIST)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.message", is(String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_FILE_ID_NOT_FOUND.getMessage()), 22)))) // verify json element
			;
	}
	
	@Test
	void addFileRoleAdmin() throws Exception {

		addFileSuccess(ROLE_ADMIN);
	}

	private void addFileSuccess(String validationRole) throws Exception {
		
		// create mock file
		MockMultipartFile file = ApplicationTestUtils.createMultipartFile();
		
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		
		mockMvc.perform(MockMvcRequestBuilders.multipart(ROOT + FILES + UPLOAD)
				.file(FILE, file.getBytes())
				.param(VALIDATION_ROLE, validationRole)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message", is(MessagePool.getMessage(FILE_SAVE_SUCCESS)))) // verify json element
			;
		
		// additional check
		// get all files
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + FILES)
				.param(VALIDATION_ROLE, validationRole)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(3))) // verify that json root element $ is now size 3
			.andExpect(jsonPath("$[0].fileName", is(FILE_1)))
			.andExpect(jsonPath("$[1].fileName", is(FILE_2)))
			;
	}
	
	@Test
	void addFileRoleStudent() throws Exception {

		addFileFail(ROLE_USER);
	}
	
	@Test
	void addFileNoRole() throws Exception {

		addFileFail(null);
	}
	
	private void addFileFail(String validationRole) throws Exception {
		
		// create mock file
		MockMultipartFile file = ApplicationTestUtils.createMultipartFile();
		
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		
		mockMvc.perform(MockMvcRequestBuilders.multipart(ROOT + FILES + UPLOAD)
				.file(FILE, file.getBytes())
				.param(VALIDATION_ROLE, validationRole)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			.andExpect(jsonPath("$.message", is(String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_ACTION_INVALID.getMessage()), ActionType.CREATE)))) // verify json root element message
			;
		
		// additional check
		// get all files
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + FILES)
				.param(VALIDATION_ROLE, validationRole)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(2))) // verify that json root element $ is still size 2
			;
	}
	
	@Test
	void addFileRoleNotExist() throws Exception {
		
		// create mock file
		MockMultipartFile file = ApplicationTestUtils.createMultipartFile();
		
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		
		mockMvc.perform(MockMvcRequestBuilders.multipart(ROOT + FILES + UPLOAD)
				.file(FILE, file.getBytes())
				.param(VALIDATION_ROLE, ROLE_NOT_EXIST)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			//.andExpect(jsonPath("$.message", is(String.format(messageSource.getMessage(ValidatorCodes.ERROR_CODE_ACTION_INVALID.getMessage(), new Object[] {}, LocaleContextHolder.getLocale()), ActionType.CREATE)))) // verify json root element message
			;
		
		// additional check
		// get all files
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + FILES)
				.param(VALIDATION_ROLE, ROLE_NOT_EXIST)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(2))) // verify that json root element $ is still size 2
			;
	}
	
	@Test
	void updateFileRoleAdmin() throws Exception {

		updateFileSuccess(ROLE_ADMIN);
	}
	
	private void updateFileSuccess(String validationRole) throws Exception {
		
		String fileName = FILE_UPDATED;
		String fileDesc = DESC_UPDATED;
		
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		
		// update image id=1 with new title
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + FILES + "/{id}", 1)
				.param(VALIDATION_ROLE, validationRole)
				.param(FILE_NAME, fileName)
				.param(IMAGE_NAME, fileDesc)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message", is(MessagePool.getMessage(FILE_SAVE_SUCCESS)))) // verify json element
			;
		
		// additional check
		// get image with id=1
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + FILES + "/{id}", 1)
				.param(VALIDATION_ROLE, validationRole)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.id", is(1))) // verify json root element id is 1
			.andExpect(jsonPath("$.fileName", is(fileName))) // verify json element
			.andExpect(jsonPath("$.fileDesc", is(fileDesc))) // verify json element
			;
		
		// update image with id=22 (non existing)
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + FILES + "/{id}", 22)
				.param(VALIDATION_ROLE, validationRole)
				.param(FILE_NAME, fileName)
				.param(IMAGE_NAME, fileDesc)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message", is(String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_FILE_ID_NOT_FOUND.getMessage()), 22)))) // verify json element
			;
		
		// update image id=1 without required parameter
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + FILES + "/{id}", 1)
				.param(VALIDATION_ROLE, validationRole)
				//.param(FILE_NAME, fileName)
				//.param(IMAGE_NAME, fileDesc)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			//.andExpect(jsonPath("$.message", is("Required String parameter 'title' is not present"))) // verify json element
			;
			
		// update image id=1 with empty parameters
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + FILES + "/{id}", 1)
				.param(VALIDATION_ROLE, validationRole)
				.param(FILE_NAME, "")
				.param(IMAGE_NAME, fileDesc)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			.andExpect(jsonPath("$.message", is(MessagePool.getMessage(ValidatorCodes.VALIDATION_FAILED.getMessage())))) // verify json element
			.andExpect(jsonPath("$.errors", hasSize(1))) // verify json element
			.andExpect(jsonPath("$.errors", contains(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_FILE_NAME_EMPTY.getMessage())))) // verify json element
			;
		
		// update image id=1 with empty parameters
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + FILES + "/{id}", 1)
				.param(VALIDATION_ROLE, validationRole)
				.param(FILE_NAME, fileName)
				.param(IMAGE_NAME, "")
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			.andExpect(jsonPath("$.message", is(MessagePool.getMessage(ValidatorCodes.VALIDATION_FAILED.getMessage())))) // verify json element
			.andExpect(jsonPath("$.errors", hasSize(1))) // verify json element
			.andExpect(jsonPath("$.errors", contains(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_IMAGE_NAME_EMPTY.getMessage())))) // verify json element
			;
	}
	
	@Test
	void updateFileRoleStudent() throws Exception {

		updateFileFail(ROLE_USER);
	}
	
	@Test
	void updateFileNoRole() throws Exception {

		updateFileFail(null);
	}
	
	private void updateFileFail(String validationRole) throws Exception {
		
		String fileName = FILE_UPDATED;
		String fileDesc = DESC_UPDATED;
		
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		
		// update image id=1 with new params
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + FILES + "/{id}", 1)
				.param(VALIDATION_ROLE, validationRole)
				.param(FILE_NAME, fileName)
				.param(IMAGE_NAME, fileDesc)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			.andExpect(jsonPath("$.message", is(String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_ACTION_INVALID.getMessage()), ActionType.UPDATE)))) // verify json root element message
			;
		
		// get file with id=1
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + FILES + "/{id}", 1)
				.param(VALIDATION_ROLE, validationRole)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.fileName", is(FILE_1))) // verify json element
			.andExpect(jsonPath("$.fileDesc", is(DESC_1))) // verify json element
			;
		
		// update image with id=22 (non existing)
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + FILES + "/{id}", 22)
				.param(VALIDATION_ROLE, validationRole)
				.param(FILE_NAME, fileName)
				.param(IMAGE_NAME, fileDesc)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message", is(String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_FILE_ID_NOT_FOUND.getMessage()), 22)))) // verify json element
			;
		
		// update image id=1 without required parameter
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + FILES + "/{id}", 1)
				.param(VALIDATION_ROLE, validationRole)
				//.param(FILE_NAME, fileName)
				//.param(IMAGE_NAME, fileDesc)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			//.andExpect(jsonPath("$.message", is("Required String parameter 'title' is not present"))) // verify json element
			;	
	}
	
	@Test
	void updateFileRoleNotExist() throws Exception {
		
		String fileName = FILE_UPDATED;
		String fileDesc = DESC_UPDATED;
		
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
						
		// update file id=1 with new title
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + FILES + "/{id}", 1)
				.param(VALIDATION_ROLE, ROLE_NOT_EXIST)
				.param(FILE_NAME, fileName)
				.param(IMAGE_NAME, fileDesc)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			//.andExpect(jsonPath("$.message", is(String.format(messageSource.getMessage(ValidatorCodes.ERROR_CODE_ACTION_INVALID.getMessage(), new Object[] {}, LocaleContextHolder.getLocale()), ActionType.UPDATE)))) // verify json root element message
			;
		
		// update file with id=22 (non existing)
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + FILES + "/{id}", 22)
				.param(VALIDATION_ROLE, ROLE_NOT_EXIST)
				.param(FILE_NAME, fileName)
				.param(IMAGE_NAME, fileDesc)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			//.andExpect(jsonPath("$.message", is(String.format(messageSource.getMessage(ValidatorCodes.ERROR_CODE_ACTION_INVALID.getMessage(), new Object[] {}, LocaleContextHolder.getLocale()), ActionType.UPDATE)))) // verify json root element message
			;
		
		// update file id=1 without required parameter
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + FILES + "/{id}", 1)
				.param(VALIDATION_ROLE, ROLE_NOT_EXIST)
				//.param(FILE_NAME, fileName)
				//.param(IMAGE_NAME, fileDesc)
				.contentType(APPLICATION_JSON_UTF8)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			//.andExpect(jsonPath("$.message", is("Required String parameter 'title' is not present"))) // verify json element
			;
	}
	
	@Test
	void deleteFileRoleAdmin() throws Exception {
		
		String validationRole = ROLE_ADMIN;
		
		// check if file id=1 exists
		int id = 1;
		FileVO fileVO = fileService.findById(id);
		
		assertNotNull(fileVO,"fileVO null");
		assertEquals(1, fileVO.getId());
		
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		
		// delete file
		mockMvc.perform(MockMvcRequestBuilders.delete(ROOT + FILES + "/{id}", id)
				.param(VALIDATION_ROLE, validationRole)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.message", is(MessagePool.getMessage(FILE_DELETE_SUCCESS)))) // verify json element
			;
		
		// get file with id=1 (non existing)
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + FILES + "/{id}", id)
				.param(VALIDATION_ROLE, validationRole)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.message", is(String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_FILE_ID_NOT_FOUND.getMessage()), id)))) // verify json element
			;		
	}
	
	@Test
	void deleteFileRoleUser() throws Exception {

		deleteFileFail(ROLE_USER);
	}
	
	@Test
	void deleteFileNoRole() throws Exception {

		deleteFileFail(null);
	}
	
	private void deleteFileFail(String validationRole) throws Exception {
		
		// check if image id=1 exists
		int id = 1;
		FileVO fileVO = fileService.findById(id);
		
		assertNotNull(fileVO,"fileVO null");
		assertEquals(1, fileVO.getId());

		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		
		// delete image
		mockMvc.perform(MockMvcRequestBuilders.delete(ROOT + FILES + "/{id}", id)
				.param(VALIDATION_ROLE, validationRole)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			.andExpect(jsonPath("$.message", is(String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_ACTION_INVALID.getMessage()), ActionType.DELETE)))) // verify json root element message
			;
	}
	
	@Test
	void deleteFileRoleNotExist() throws Exception {
		
		// check if image id=1 exists
		int id = 1;
		FileVO fileVO = fileService.findById(id);
		
		assertNotNull(fileVO,"fileVO null");
		assertEquals(1, fileVO.getId());

		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		
		// delete image
		mockMvc.perform(MockMvcRequestBuilders.delete(ROOT + FILES + "/{id}", id)
				.param(VALIDATION_ROLE, ROLE_NOT_EXIST)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			//.andExpect(jsonPath("$.message", is(String.format(messageSource.getMessage(ValidatorCodes.ERROR_CODE_ACTION_INVALID.getMessage(), new Object[] {}, LocaleContextHolder.getLocale()), ActionType.DELETE)))) // verify json root element message
			;
	}
}