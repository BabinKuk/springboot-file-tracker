package org.babinkuk.mapper;

import java.util.Objects;
import org.apache.commons.lang.StringUtils;
import org.babinkuk.entity.FileDB;
import org.babinkuk.vo.FileVO;
import org.mapstruct.BeanMapping;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

/**
 * mapper for the entity @link {@link FileDB} and its DTO {@link FileVO}
 * 
 * @author BabinKuk
 */
@Mapper
(
	componentModel = "spring",
	unmappedSourcePolicy = ReportingPolicy.WARN,
	imports = {StringUtils.class, Objects.class},
	//if needed add uses = {add different classes for complex objects}
	uses = {} 
)
public interface FileMapper {
	
	// for insert
	@Named("toEntity")
	FileDB toEntity(FileVO imageVO);
	
	// for update
	@Named("toEntity")
	FileDB toEntity(FileVO imageVO, @MappingTarget FileDB image);
	
	@Named("toVO")
	FileVO toVO(FileDB image);
	
	@IterableMapping(qualifiedByName = "toVO")
	@BeanMapping(ignoreByDefault = true)
	Iterable<FileVO> toVO(Iterable<FileDB> imageList);
	
}