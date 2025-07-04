package outpolic.enter.outsourcing.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import outpolic.enter.POAddtional.domain.CategorySearchDto;
import outpolic.enter.outsourcing.domain.EnterOutsourcing;


@Mapper
public interface OutsourcingMapper {
	// INSERT
	int insertOutsourcing(EnterOutsourcing outsourcing);
	int insertContentList(@Param("clCd") String clCd,@Param("cntdCd") String cntdCd);
	int insertCategoryMapping(@Param("ctgryCd") String ctgryCd, @Param("clCd") String clCd, @Param("mbrCd") String mbrCd);
    int insertTag(@Param("tagCd") String tagCd, @Param("tagName") String tagName, @Param("mbrCd") String mbrCd);
    int insertTagMapping(@Param("tagCd") String tagCd, @Param("clCd") String clCd, @Param("mbrCd") String mbrCd);
	
	
	
	// SELECT (Main)
	List<EnterOutsourcing> findOutsourcingDetailsByEntCd(String entCd);
	EnterOutsourcing findOutsourcingDetailsByOsCd(String osCd);
	
	// SELECT (Helper)
	String findLastOsCd();
	String findLatestTagCd();
	String findTagCdByName(String tagName);
	String findClCdByPrtfCd(String osCd);

	
	// SELECT (for ResultMap Collections)
	List<CategorySearchDto> findCategoriesByOsCd(String osCd);
	List<String> findTagNamesByOsCd(String osCd);
	
	// DELETE
	int deleteOutsourcingByOsCd(String osCd);
	int deleteContentListByClCd(String clCd);
	int deleteCategoryMappingByClCd(String clCd);
	int deleteTagMappingByClCd(String clCd);
	int deleteFileByClCd(String clCd);
	
	// UPDATE
	int updateOutsourcing(EnterOutsourcing outsourcing);
}
