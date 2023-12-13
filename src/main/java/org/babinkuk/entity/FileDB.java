package org.babinkuk.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;


/**
 * data model corresponding to images table in database
 * 
 * @author BabinKuk
 *
 */
@Entity
@Table(name = "file")
public class FileDB {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;
	
	@Column(name = "file_name")
	private String fileName;
	
	@Column(name = "file_desc")
	private String fileDesc;
	
	@Column(name = "data")
	@Lob
	private byte[] data;

	public FileDB() {
	}

	public FileDB(String fileName, String fileDesc, byte[] data) {
		this.fileName = fileName;
		this.fileDesc = fileDesc;
		this.data = data;
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

	@Override
	public String toString() {
		return "Image [id=" + id + ", fileName=" + fileName + ", fileDesc=" + fileDesc + "]";
	}
}