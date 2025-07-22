package outpolic.admin.outsourcing.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import outpolic.admin.outsourcing.dto.AdminOutsourcingDTO;
import outpolic.admin.outsourcing.dto.AdminOutsourcingSearchDTO;
import outpolic.systems.file.domain.FileMetaData;

@Mapper
public interface AdminOutsourcingMapper {

	List<AdminOutsourcingDTO> findAllOutsourcingsForAdmin(@Param("searchDTO") AdminOutsourcingSearchDTO searchDTO);

	String findClCdByOsCd(@Param("osCd") String osCd);

	List<FileMetaData> findFilesByClCd(@Param("clCd") String clCd);
}