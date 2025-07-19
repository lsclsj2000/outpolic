package outpolic.admin.portfolio.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import outpolic.admin.portfolio.dto.AdminPortfolioDTO;
import outpolic.admin.portfolio.dto.AdminPortfolioSearchDTO;
import outpolic.systems.file.domain.FileMetaData;

@Mapper
public interface AdminPortfolioMapper {
	List<AdminPortfolioDTO> findAllPortfoliosForAdmin(@Param("searchDTO") AdminPortfolioSearchDTO searchDTO);
	
	String findClCdByPrtfCd(@Param("prtfCd") String prtfCd);
	
	List<FileMetaData> findFilesByClCd(@Param("clCd") String clCd);
}
