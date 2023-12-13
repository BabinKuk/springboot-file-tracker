package org.babinkuk.dao;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.babinkuk.entity.FileDB;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import static org.junit.jupiter.api.Assertions.*;
import static org.babinkuk.utils.ApplicationTestConstants.*;
import java.util.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@ActiveProfiles("test")
@DataJpaTest
public class FileRepositoryTest {
	
	public static final Logger log = LogManager.getLogger(FileRepositoryTest.class);
	
	@Autowired
	private JdbcTemplate jdbc;
	
	@PersistenceContext
	private EntityManager entityManager;
	
	@Autowired
	private FileRepository fileRepository;
	
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
		Iterable<FileDB> files = fileRepository.findAll();
		
		// assert
		assertNotNull(files,"files null");
		
		if (files instanceof Collection) {
			assertEquals(2, ((Collection<?>) files).size(), "files size not 2");
		}
		
		List<FileDB> fileList = new ArrayList<FileDB>();
		files.forEach(fileList::add);

		assertTrue(fileList.stream().anyMatch(file ->
			file.getFileName().equals(FILE_1) && file.getFileDesc().equals(DESC_1) && file.getId() == 1
		));
		
		assertTrue(fileList.stream().anyMatch(file ->
		file.getFileName().equals(FILE_2) && file.getFileDesc().equals(DESC_2) && file.getId() == 2
	));
	}
	
	@Test
	void getImageById() {
		
		// get file id=1
		Optional<FileDB> file = fileRepository.findById(1);
		
		// assert
		assertTrue(file.isPresent());
		validateExistingImageOne(file.get());
		
		// get file id=1
		file = fileRepository.findById(2);
		
		// assert
		assertTrue(file.isPresent());
		validateExistingImageTwo(file.get());
		
		// get non-existing file id=22
		file = fileRepository.findById(22);
		
		// assert
		assertFalse(file.isPresent());
	}
	
	@Test
	void getImageByFileName() {
		
		// get file
		Optional<FileDB> file = fileRepository.findByFileDesc(DESC_1);
		
		// assert
		assertTrue(file.isPresent());
		validateExistingImageOne(file.get());
		
		// get file
		file = fileRepository.findByFileDesc(DESC_2);
		
		// assert
		assertTrue(file.isPresent());
		validateExistingImageTwo(file.get());
		
		// get non-existing file
		file = fileRepository.findByFileDesc(DESC_NEW);
		
		// assert
		assertFalse(file.isPresent());
	}
	
	@Test
	void updateImage() {
		
		// get file id=1
		Optional<FileDB> file = fileRepository.findById(1);
		
		// assert
		assertTrue(file.isPresent());
		validateExistingImageOne(file.get());
		
		// update
		// set id=1: this is to force an update of existing item
		FileDB updatedImage = new FileDB();
		updatedImage = updateImage(file.get());
		
		FileDB savedImage = fileRepository.save(updatedImage);
		
		// assert
		assertNotNull(savedImage,"savedImage null");
		validateUpdatedImage(savedImage);
	}
	
	@Test
	void addImage() {
		
		// create file
		// set id=0: this is to force a save of new item
		FileDB file = new FileDB();
		file.setId(0);
		file.setFileName(FILE_NEW);
		file.setFileDesc(DESC_NEW);
		file.setData(DATA_NEW);
		FileDB savedImage = fileRepository.save(file);
		
		// assert
		assertNotNull(savedImage,"savedImage null");
		validateNewImage(savedImage);
	}

	@Test
	void deleteImage() {
		
		// get file id=1
		Optional<FileDB> file = fileRepository.findById(1);
		
		// assert
		assertTrue(file.isPresent());
				
		// delete file
		fileRepository.deleteById(1);
		
		file = fileRepository.findById(1);
		
		// assert
		assertFalse(file.isPresent());
		
		// clear persistence context and sync with db
		entityManager.flush();
		entityManager.clear();
		
		// get all files
		Iterable<FileDB> files = fileRepository.findAll();
		
		// assert
		assertNotNull(files,"files null");
		
		if (files instanceof Collection) {
			assertEquals(1, ((Collection<?>) files).size(), "files size not 1");
		}
		
		// clear persistence context and sync with db
		entityManager.flush();
		entityManager.clear();
		
		// get file id=2
		file = fileRepository.findById(2);
		
		// assert
		assertTrue(file.isPresent());
				
		// delete file
		fileRepository.deleteById(2);
		
		file = fileRepository.findById(2);
		
		// assert
		assertFalse(file.isPresent());
		
		// get all files
		files = fileRepository.findAll();
		
		// assert
		assertNotNull(files,"files null");
		
		if (files instanceof Collection) {
			assertEquals(0, ((Collection<?>) files).size(), "files size not 0");
		}
	}
	
	private void validateExistingImageOne(FileDB file) {
		
		assertNotNull(file,"file null");
		assertNotNull(file.getId(),"getTitle() null");
		assertNotNull(file.getFileName(),"getFileName() null");
		assertNotNull(file.getFileDesc(),"getImageName() null");
		//assertNotNull(file.getInstructor(),"getInstructor() null");
		assertEquals(1, file.getId());
		assertEquals(DESC_1, file.getFileDesc(),"getImageName() NOK");
		assertEquals(FILE_1, file.getFileName(),"getFileName() NOK");
	}
	
	private void validateExistingImageTwo(FileDB file) {
		
		assertNotNull(file,"file null");
		assertNotNull(file.getId(),"getTitle() null");
		assertNotNull(file.getFileName(),"getFileName() null");
		assertNotNull(file.getFileDesc(),"getImageName() null");
		//assertNotNull(file.getInstructor(),"getInstructor() null");
		assertEquals(2, file.getId());
		assertEquals(DESC_2, file.getFileDesc(),"getImageName() NOK");
		assertEquals(FILE_2, file.getFileName(),"getFileName() NOK");
	}
	
	private void validateUpdatedImage(FileDB file) {
		
		assertNotNull(file,"file null");
		assertNotNull(file,"file null");
		assertNotNull(file.getFileName(),"getFileName() null");
		assertNotNull(file.getFileDesc(),"getImageName() null");
		assertEquals(1, file.getId());
		assertEquals(FILE_UPDATED, file.getFileName(),"getFileName() NOK");
		assertEquals(DESC_UPDATED, file.getFileDesc(),"getImageName() NOK");
	}
	
	private void validateNewImage(FileDB file) {
		
		assertNotNull(file,"file null");
		assertNotNull(file.getFileName(),"getFileName() null");
		assertNotNull(file.getFileDesc(),"getImageName() null");
		//assertEquals(1, file.getId());
		assertEquals(FILE_NEW, file.getFileName(),"getFileName() NOK");
		assertEquals(DESC_NEW, file.getFileDesc(),"getImageName() NOK");
	}
	
	private FileDB updateImage(FileDB file) {
				
		// update with new data
		file.setFileDesc(DESC_UPDATED);
		file.setFileName(FILE_UPDATED);
		
		return file;
	}
}