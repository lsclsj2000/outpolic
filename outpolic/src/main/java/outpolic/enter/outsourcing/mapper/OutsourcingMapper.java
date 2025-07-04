package outpolic.enter.outsourcing.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import outpolic.enter.outsourcing.domain.EnterOutsourcing;

@Mapper
public interface OutsourcingMapper {
    int insertOutsourcing(EnterOutsourcing outsourcing);
    int insertContentList(@Param("clCd") String clCd, @Param("cntdCd") String cntdCd);
    int insertCategoryMapping(@Param("ctgryCd") String ctgryCd, @Param("clCd") String clCd, @Param("mbrCd") String mbrCd);
    int insertTag(@Param("tagCd") String tagCd, @Param("tagName") String tagName, @Param("mbrCd") String mbrCd);
    int insertTagMapping(@Param("tagCd") String tagCd, @Param("clCd") String clCd, @Param("mbrCd") String mbrCd);
    
    List<EnterOutsourcing> findOutsourcingDetailsByEntCd(String entCd);
    
    String findLatestOsCd();
    String findLatestTagCd();
    String findTagCdByName(String tagName);
    
    EnterOutsourcing findOutsourcingDetailsByOsCd(String osCd);
    String findClCdByOsCd(String osCd);
    int deleteCategoryMappingByClCd(String clCd);
    int deleteTagMappingByClCd(String clCd);
    int updateOutsourcing(EnterOutsourcing outsourcing);
    
    int deleteContentListByClCd(String clCd);
    int deleteOutsourcingByOsCd(String osCd);
}
