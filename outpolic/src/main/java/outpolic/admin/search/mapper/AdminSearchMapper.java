package outpolic.admin.search.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;

import outpolic.admin.search.domain.AdminContentItemDTO;
import outpolic.admin.search.domain.AdminContents;
import outpolic.admin.search.domain.AdminContentsDetailDTO;

@Mapper
public interface AdminSearchMapper {
	List<AdminContents> getContentsList(@Param("keyword") String keyword);
	
	// 콘텐츠 상세정보 
	AdminContentsDetailDTO getContentsDetailById(String contentsId);
	
	List<AdminContentItemDTO> findContentsByCategoryId(String categoryId);
	
	String findClCdByCntdCd(String contentsId);

    void deleteCategoryMappingByClCd(String clCd);
    void deleteTagMappingByClCd(String clCd);
    void deleteBookmarkByClCd(String clCd);
    void deleteOutsourcingPortfolioByPrtfCd(String prtfCd);
    void deletePortfolioByPrtfCd(String prtfCd);
    void deleteOutsourcingByOsCd(String osCd);
    void deleteContentListByClCd(String clCd);
}
