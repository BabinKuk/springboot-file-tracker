package org.babinkuk.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.babinkuk.vo.FileVO;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import static org.babinkuk.utils.ApplicationTestConstants.*;

public class ApplicationTestUtils {
	
	public static final Logger log = LogManager.getLogger(ApplicationTestUtils.class);
	
	public static MockMultipartFile createMultipartFile() {
		
		return new MockMultipartFile(DESC_NEW, FILE_NEW, MediaType.TEXT_PLAIN_VALUE, DATA_NEW);
	}
	
	public static FileVO createFile() {
		
		// create course
		// set id 0: this is to force a save of new item ... instead of update
		FileVO imageVO = new FileVO();
		imageVO.setFileName(FILE_NEW);
		imageVO.setFileDesc(DESC_NEW);
		imageVO.setData(DATA_NEW);
		imageVO.setId(0);
		
		return imageVO;
	}
	public static FileVO updateExistingImage(FileVO imageVO) {
		
		// update with new data
		imageVO.setFileName(FILE_UPDATED);
		imageVO.setFileDesc(DESC_UPDATED);
		return imageVO;
	}
}
