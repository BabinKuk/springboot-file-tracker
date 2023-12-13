package org.babinkuk.dao;

import java.util.Optional;

import org.babinkuk.entity.FileDB;
import org.springframework.data.repository.CrudRepository;

/**
 * extends Spring Data JpaRepository which has methods to store and retrieve files
 * 
 * @author BabinKuk
 *
 */
public interface FileRepository extends CrudRepository<FileDB, Integer> {

	Optional<FileDB> findByFileDesc(String desc);
	
}
