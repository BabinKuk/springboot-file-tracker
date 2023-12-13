package org.babinkuk.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.babinkuk.exception.ObjectNotFoundException;
import org.babinkuk.utils.ApplicationTestUtils;
import org.babinkuk.vo.FileVO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.babinkuk.utils.ApplicationTestConstants.*;

import java.util.Collection;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class FileServiceTest {
	
	public static final Logger log = LogManager.getLogger(FileServiceTest.class);
	
	@Autowired
	private JdbcTemplate jdbc;
	
	@PersistenceContext
	private EntityManager entityManager;
	
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
	void getAllImages() {
		
		// get all files
		Iterable<FileVO> files = fileService.getAllFiles();
		
		// assert
		if (files instanceof Collection<?>) {
			assertEquals(2, ((Collection<?>) files).size(), "files size not 2");
		}
		
		// add another file
		FileVO fileVO = ApplicationTestUtils.createFile();
		
		fileService.saveFile(fileVO);
		
		// clear persistence context and sync with db
		entityManager.flush();
		entityManager.clear();
		
		files = fileService.getAllFiles();
		
		// assert
		if (files instanceof Collection<?>) {
			assertEquals(3, ((Collection<?>) files).size(), "files size not 3 after insert");
		}
		
		// delete file
		fileService.deleteFile(1);
		
		// clear persistence context and sync with db
		entityManager.flush();
		entityManager.clear();
		
		files = fileService.getAllFiles();
		
		// assert
		if (files instanceof Collection<?>) {
			assertEquals(2, ((Collection<?>) files).size(), "files size not 2 after delete");
		}
	}
	
	@Test
	void getImageById() {
		
		// get file id=1
		FileVO fileVO = fileService.findById(1);
		
		// assert
		validateExistingFileOne(fileVO);
		
		// get file id=2
		fileVO = fileService.findById(2);
		
		// assert
		validateExistingFileTwo(fileVO);
		
		// assert not existing file
		Exception exception = assertThrows(ObjectNotFoundException.class, () -> {
			fileService.findById(22);
		});
		
		String expectedMessage = "File with id=22 not found.";
		String actualMessage = exception.getMessage();

	    assertTrue(actualMessage.contains(expectedMessage));
	}
	
	@Test
	void getFileeByImageName() {
		
		// get file
		FileVO fileVO = fileService.findByFileDesc(DESC_1);
		
		// assert
		validateExistingFileOne(fileVO);
		
		// get file
		fileVO = fileService.findByFileDesc(DESC_2);
		
		// assert
		validateExistingFileTwo(fileVO);
		
		// assert not existing file
		fileVO = fileService.findByFileDesc(DESC_NEW);
				
		// assert
		assertNull(fileVO, "fileVO null");
	}
	
	@Test
	void addFile() {
		
		// create file
		FileVO fileVO = ApplicationTestUtils.createFile();
		
		fileService.saveFile(fileVO);
		
		// clear persistence context and sync with db
		entityManager.flush();
		entityManager.clear();
		
		fileVO = fileService.findByFileDesc(DESC_NEW);
		
		// assert
		validateNewImage(fileVO);
	}
	
	@Test
	void updateImage() {

		// get file id=1
		FileVO fileVO = fileService.findById(1);
		
		validateExistingFileOne(fileVO);
		
		// update with new data
		fileVO = ApplicationTestUtils.updateExistingImage(fileVO);
		
		fileService.saveFile(fileVO);
		
		// clear persistence context and sync with db
		entityManager.flush();
		entityManager.clear();
		
		// fetch again
		FileVO fileVO2 = fileService.findById(1);
		
		// assert
		validateUpdatedImage(fileVO2);
	}
	
	@Test
	void deleteImage() {
		
		// first get file id=1
		FileVO fileVO = fileService.findById(1);
		
		// assert
		validateExistingFileOne(fileVO);
		
		// delete
		fileService.deleteFile(1);
		
		// clear persistence context and sync with db
		entityManager.flush();
		entityManager.clear();
		
		// assert not existing student
		Exception exception = assertThrows(ObjectNotFoundException.class, () -> {
			fileService.findById(1);
		});
			
		String expectedMessage = "File with id=1 not found.";
		String actualMessage = exception.getMessage();
		
	    assertTrue(actualMessage.contains(expectedMessage));

		// delete not existing file
		exception = assertThrows(EmptyResultDataAccessException.class, () -> {
			fileService.deleteFile(22);
		});
	}
	
	private void validateExistingFileOne(FileVO file) {
		
		assertNotNull(file,"file null");
		assertNotNull(file.getId(),"getTitle() null");
		assertNotNull(file.getFileName(),"getFileName() null");
		assertNotNull(file.getFileDesc(),"getImageName() null");
		//assertNotNull(file.getInstructor(),"getInstructor() null");
		assertEquals(1, file.getId());
		assertEquals(DESC_1, file.getFileDesc(),"getImageName() NOK");
		assertEquals(FILE_1, file.getFileName(),"getFileName() NOK");
	}
	
	private void validateExistingFileTwo(FileVO file) {
		
		assertNotNull(file,"file null");
		assertNotNull(file.getId(),"getTitle() null");
		assertNotNull(file.getFileName(),"getFileName() null");
		assertNotNull(file.getFileDesc(),"getImageName() null");
		//assertNotNull(file.getInstructor(),"getInstructor() null");
		assertEquals(2, file.getId());
		assertEquals(DESC_2, file.getFileDesc(),"getImageName() NOK");
		assertEquals(FILE_2, file.getFileName(),"getFileName() NOK");
	}
	
	private void validateUpdatedImage(FileVO file) {
		
		assertNotNull(file,"file null");
		assertNotNull(file,"file null");
		assertNotNull(file.getFileName(),"getFileName() null");
		assertNotNull(file.getFileDesc(),"getImageName() null");
		assertEquals(1, file.getId());
		assertEquals(FILE_UPDATED, file.getFileName(),"getFileName() NOK");
		assertEquals(DESC_UPDATED, file.getFileDesc(),"getImageName() NOK");
	}
	
	private void validateNewImage(FileVO file) {
		
		assertNotNull(file,"file null");
		assertNotNull(file.getFileName(),"getFileName() null");
		assertNotNull(file.getFileDesc(),"getImageName() null");
		//assertEquals(1, file.getId());
		assertEquals(FILE_NEW, file.getFileName(),"getFileName() NOK");
		assertEquals(DESC_NEW, file.getFileDesc(),"getImageName() NOK");
	}
}
