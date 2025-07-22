package outpolic.enter.outsourcing.mapper;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import outpolic.enter.outsourcing.domain.EnterOutsourcing;
import outpolic.enter.portfolio.domain.EnterPortfolio;
import outpolic.systems.file.domain.FileMetaData;
@Mapper
public interface OutsourcingMapper {
    // --- 조회 (SELECT) ---
    List<EnterOutsourcing> findOutsourcingDetailsByEntCd(String entCd);
    EnterOutsourcing findOutsourcingDetailsByOsCd(String osCd);
    List<String> searchTagsByName(@Param("query") String query);
    String findLatestOsCd();
    String findLatestTagCd();
    String findTagCdByName(String tagName);
    String findClCdByOsCd(String osCd);
    String findLatestOpCd();
    // ★ 추가: 모든 외주 목록을 가져오는 메서드 (findAllOutsourcings 오류 해결)
    List<EnterOutsourcing> findAllOutsourcings();
    int deletePerusalContentByClCd(String clCd);

    // --- 저장 (INSERT) ---
    int insertOutsourcing(EnterOutsourcing outsourcing);
    int insertContentList(@Param("clCd") String clCd, @Param("cntdCd") String cntdCd);
    int insertCategoryMapping(@Param("ctgryCd") String ctgryCd, @Param("clCd") String clCd, @Param("mbrCd") String mbrCd);
    int insertTag(@Param("tagCd") String tagCd, @Param("tagName") String tagName, @Param("mbrCd") String mbrCd);
    int insertTagMapping(@Param("tagCd") String tagCd, @Param("clCd") String clCd, @Param("mbrCd") String mbrCd);
    int insertOutsourcingPortfolio(@Param("opCd") String opCd, @Param("osCd") String osCd, @Param("prtfCd") String prtfCd, @Param("entCd") String entCd);
    // --- 수정(UPDATE) ---
    int updateOutsourcing(EnterOutsourcing outsourcing);
    // --- 삭제 (DELETE) ---
    int deleteOutsourcingByOsCd(String osCd);
    int deleteContentListByClCd(String clCd);
    int deleteCategoryMappingByClCd(String clCd);
    int deleteTagMappingByClCd(String clCd);
    int deleteOutsourcingPortfolioByOsCd(String osCd);
    int deleteBookmarkByClCd(String clCd);
    int deleteOutsourcingContractDetailsByClCd(String clCd);
    int deleteOutsourcingStatusByOcdCd(String ocdCd);
    int deleteOutsourcingStatusByClCd(String clCd);
    int deleteRankingByClCd(String clCd);
    int deleteTodayViewByClCd(String clCd);
    int deleteTotalViewByClCd(String clCd);
    int deletePortfolioLinkByPrtfCd(@Param("prtfCd") String prtfCd);
    void updateOutsourcingStep1(EnterOutsourcing outsourcing);
    // --- 외주-포트폴리오 연결을 위한 매퍼 메서드 ---
    List<EnterPortfolio> findLinkedPortfoliosByOsCd(@Param("osCd") String osCd);
    int unlinkOutsourcingFromPortfolio(@Param("osCd") String osCd, @Param("prtfCd") String prtfCd);
    void updateOutsourcingRepresentativeCategory(@Param("osCd") String osCd, @Param("ctgryId") String ctgryId);
    List<EnterPortfolio> findUnlinkedPortfolios(@Param("osCd") String osCd, @Param("entCd") String entCd, @Param("query") String query);
    int insertFiles(@Param("files") List<FileMetaData> files, @Param("clCd") String clCd, @Param("mbrCd") String mbrCd); // clCd, mbrCd를 파라미터로 받도록 수정 [cite: 3]
    /**
     * 회원 코드(mbrCd)로 기업 코드(entCd)를 조회합니다.
     * @param mbrCd
     * @return
     */
    @Select("SELECT ent_cd FROM enterprise WHERE mbr_cd = #{mbrCd}")
    String findEntCdByMbrCd(String mbrCd);
    String findLatestClCd();
    List<FileMetaData> findFilesByClCd(String clCd);
    int deleteFilesByClCd(String clCd);
    // 썸네일 URL 업데이트를 위한 메서드 추가 [cite: 3]
    void updateOutsourcingThumbnail(@Param("osCd") String osCd, @Param("thumbnailUrl") String thumbnailUrl);
    FileMetaData findFileMetaDataByFileCd(@Param("fileCd") String fileCd); // file_cd로 파일 메타데이터 조회 [cite: 3]
    int deleteFilesByFileCd(@Param("fileCd") String fileCd); // file_cd로 파일 삭제 [cite: 3]
}