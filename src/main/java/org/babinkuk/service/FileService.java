package org.babinkuk.service;

import org.babinkuk.common.ApiResponse;
import org.babinkuk.exception.ObjectException;
import org.babinkuk.exception.ObjectNotFoundException;
import org.babinkuk.vo.FileVO;

/**
 * Service to provide methods for saving new file, get file by id, get list of Files etc.
 * 
 * @author BabinKuk
 *
 */
public interface FileService {
	
	/**
	 * get files list
	 * 
	 * @return Iterable<FileVO>
	 */
	public Iterable<FileVO> getAllFiles();
	
	/**
	 * get file
	 * 
	 * @param id
	 * @return FileVO
	 * @throws ObjectNotFoundException
	 */
	public FileVO findById(int id) throws ObjectNotFoundException;
	
	/**
	 * save file (on insert/update)
	 * 
	 * @param fileVO
	 * @return
	 * @throws ObjectException
	 */
	public ApiResponse saveFile(FileVO imageVO) throws ObjectException;
	
	/**
	 * delete file
	 * 
	 * @param id
	 * @return
	 * @throws ObjectNotFoundException
	 */
	public ApiResponse deleteFile(int id) throws ObjectNotFoundException;

	/**
	 * get file by desc
	 * 
	 * @param name
	 * @return FileVO
	 * @throws ObjectNotFoundException
	 */
	public FileVO findByFileDesc(String desc) throws ObjectNotFoundException;
		
}
