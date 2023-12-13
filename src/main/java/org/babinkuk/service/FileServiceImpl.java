package org.babinkuk.service;

import java.util.ArrayList;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.babinkuk.common.ApiResponse;
import org.babinkuk.config.MessagePool;
import org.babinkuk.dao.FileRepository;
import org.babinkuk.entity.FileDB;
import org.babinkuk.exception.ObjectException;
import org.babinkuk.exception.ObjectNotFoundException;
import org.babinkuk.mapper.FileMapper;
import org.babinkuk.validator.ValidatorCodes;
import org.babinkuk.vo.FileVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.babinkuk.config.Api.*;

/**
 * Service implementation that uses FileDBRepository to provide methods 
 * for saving new file, get file by id, get list of Files
 * 
 * @author BabinKuk
 *
 */
@Service
public class FileServiceImpl implements FileService {
	
	private final Logger log = LogManager.getLogger(getClass());
	
	@Autowired
	private FileRepository imageRepository;
	
	@Autowired
	private ObjectMapper mapper;
	
	@Autowired
	private FileMapper imageMapper;
	
	@Autowired
	public FileServiceImpl(FileRepository imageRepository) {
		this.imageRepository = imageRepository;
	}
	
	public FileServiceImpl() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public FileVO findById(int id) throws ObjectNotFoundException {
		
		Optional<FileDB> result = imageRepository.findById(id);
		
		FileDB image = null;
		FileVO imageVO = null;
		//String fileDownloadUri = null;
		
		if (result.isPresent()) {
			image = result.get();
			//log.info("image ({})", image);
			
			// mapping
			imageVO = imageMapper.toVO(image);
			
			/*fileDownloadUri = ServletUriComponentsBuilder
				.fromCurrentContextPath()
				.path(ROOT + FILES)
				.path("/")
				.path(String.valueOf(image.getId()))
				.toUriString();*/

			//imageVO.setUrl(fileDownloadUri);
			imageVO.setSize(image.getData().length);
			return imageVO;
		} else {
			// not found
			String message = String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_FILE_ID_NOT_FOUND.getMessage()), id);
			log.warn(message);
			throw new ObjectNotFoundException(message);
		}
	}
		
	@Override
	public ApiResponse saveFile(FileVO imageVO) throws ObjectException {
		
		ApiResponse response = new ApiResponse();
		
		response.setStatus(HttpStatus.OK);
		response.setMessage(MessagePool.getMessage(FILE_SAVE_SUCCESS));
		
		Optional<FileDB> entity = imageRepository.findById(imageVO.getId());
		
		FileDB image = null;
		
		if (entity.isPresent()) {
			image = entity.get();
			//log.info("imageVO ({})", imageVO);
			//log.info("mapping for update");
			
			// mapping
			//image = imageMapper.toEntity(imageVO, image);
			image.setFileName(imageVO.getFileName());
			image.setFileDesc(imageVO.getFileDesc());
		} else {
			// image not found
			//log.info("mapping for insert");
			
			// mapping
			image = imageMapper.toEntity(imageVO);
		}

		imageRepository.save(image);
		
		return response;
	}
	
	@Override
	public ApiResponse deleteFile(int id) throws ObjectNotFoundException {
		
		ApiResponse response = new ApiResponse();
		
		response.setStatus(HttpStatus.OK);
		response.setMessage(MessagePool.getMessage(FILE_DELETE_SUCCESS));
		
		imageRepository.deleteById(id);
		
		return response;
	}

	@Override
	public Iterable<FileVO> getAllFiles() {
		Iterable<FileVO> imagesVO = imageMapper.toVO(imageRepository.findAll());
		ArrayList<FileVO> images = new ArrayList<FileVO>();
		
		for (FileVO imageVO : imagesVO) {
			/*String fileDownloadUri = ServletUriComponentsBuilder
					.fromCurrentContextPath()
					.path(ROOT + FILES)
					.path("/")
					.path(String.valueOf(imageVO.getId()))
					.toUriString();*/
			
			FileVO img = imageVO;
			//img.setUrl(fileDownloadUri);
			img.setSize(imageVO.getData().length);
			
			images.add(img);
		}

		return images;
	}

	@Override
	public FileVO findByFileDesc(String name) {
		
		Optional<FileDB> result = imageRepository.findByFileDesc(name);
		
		FileDB image = null;
		FileVO imageVO = null;
		//String fileDownloadUri = null;
		
		if (result.isPresent()) {
			image = result.get();
			//log.info("image ({})", image);
			
			// mapping
			imageVO = imageMapper.toVO(image);
			
			/*fileDownloadUri = ServletUriComponentsBuilder
				.fromCurrentContextPath()
				.path(ROOT + FILES)
				.path("/")
				.path(String.valueOf(image.getId()))
				.toUriString();*/

			//imageVO.setUrl(fileDownloadUri);
			imageVO.setSize(image.getData().length);
			//log.info("imageVO ({})", imageVO);
		} else {
			// not found
			String message = String.format(MessagePool.getMessage(ValidatorCodes.ERROR_CODE_IMAGE_NAME_NOT_FOUND.getMessage()), name);
			log.warn(message);
			//throw new ObjectNotFoundException(message);
		}
		
		return imageVO;
	}
}
