package org.babinkuk.vo;

import org.babinkuk.vo.diff.Diffable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * instance of this class is used to represent file data
 * 
 * @author BabinKuk
 *
 */
@Diffable(id = "id")
@JsonInclude(value = Include.NON_EMPTY)
public class FileVO {

	private int id;
	
	private String fileName;
	
	private String fileDesc;
	
	private byte[] data;
	
	//private String url;
	
	private long size;

	public FileVO() {
	}

	public FileVO(String fileName, String fileDesc, byte[] data) {
		this.fileName = fileName;
		this.fileDesc = fileDesc;
		this.data = data;
	}

	public FileVO(String fileName,	String imageName, byte[] data, long size) {
		this.fileName = fileName;
		this.fileDesc = imageName;
		this.data = data;
		this.size = size;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileDesc() {
		return fileDesc;
	}

	public void setFileDesc(String fileDesc) {
		this.fileDesc = fileDesc;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

//	public String getUrl() {
//		return url;
//	}
//
//	public void setUrl(String url) {
//		this.url = url;
//	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}
	
	@Override
	public String toString() {
		return "Image [id=" + id + ", fileName=" + fileName + ", fileDesc=" + fileDesc + "]";
	}
}