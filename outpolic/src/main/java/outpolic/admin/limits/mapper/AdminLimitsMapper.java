package outpolic.admin.limits.mapper;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;

import outpolic.admin.limits.domain.AdminDeclarationFullInfo;
import outpolic.admin.limits.domain.AdminLimits;
import outpolic.admin.limits.domain.AdminLimitsReason;

@Mapper
public interface AdminLimitsMapper {
	
	int updateMemberAuthority(AdminLimits adminLimits);
	
	AdminLimits selectMemberAuthorityByMemberCode(String memberCode);

	
	// 회원 권한 조회
	List<AdminLimits> getAdminLimitsAuthorityList();
	
	// 회원 권한 검색
	List<AdminLimits> selectAdminLimitAuthority(@Param("keyword") String keyword,
												@Param("levelValue") String levelValue);
	
	// 제재 내역 조회
	List<AdminLimits> getAdminLimitsList();
	
	// 제재 회원 검색
	List<AdminLimits> selectAdminLimitsList(@Param("keyword") String keyword,
											@Param("searchType") String searchType,
											@Param("selectDateType") String selectDateType,
											@Param("levelSearch") String levelSearch,
											@Param("startDate") String startDate,
										    @Param("endDate") String endDate);
	
	// 제재 타입 자원 조회
	List<AdminLimits> getAdminLimitsTypeList();
	
	// 제재 사유 자원 조회
	List<AdminLimits> getAdminLimitsReasonList();
	
	// 제재 기간 자원 조회
	List<AdminLimits> getAdminLimitsPeriodList();
	
	// 제재 타입 등록
	int insertLimitsType(AdminLimits adminLimits);

	// 제재 타입 수정
	int updateLimitsType(AdminLimits adminLimits);

	// 특정 제재 타입 조회 (수정 모달용)
	AdminLimits selectLimitsTypeById(String limitsTypeCode);
	
	// 제재 기간 등록
	int insertLimitsPeriod(AdminLimits adminLimits);

	// 제재 기간 수정
	int updateLimitsPeriod(AdminLimits adminLimits);

	// 특정 제재 기간 조회 (수정 모달용)
	AdminLimits selectLimitsPeriodById(String limitsPeriodCode);
	
	// 제재 사유 등록
	int insertLimitsReason(AdminLimits adminLimits);

	// 제재 사유 수정
	int updateLimitsReason(AdminLimits adminLimits);

	// 특정 제재 사유 조회 (수정 모달용)
	AdminLimits selectLimitsReasonById(String limitsReasonCode);

	// 신고 타입 목록 조회
	List<AdminLimits> getDeclarationTypeList();

	// 신고 사유 목록 조회 (타입별 필터링 가능)
	List<AdminLimits> getDeclarationReasonList(@Param("dt_cd") String dt_cd);
	
	// 제재 처리 자동 로직
	AdminDeclarationFullInfo getDeclarationInfo(String declCd);
    Integer getCumulativeCount(@Param("mbrCd") String mbrCd, @Param("drCd") String drCd);
    AdminLimitsReason getMatchedLimitsReason(@Param("dtCd") String dtCd, @Param("drCd") String drCd, @Param("cnt") int cnt);
    Timestamp getEndDateByPeriod(@Param("lpCd") String lpCd, @Param("start") Timestamp start);
    void insertLimits(AdminLimits limits);
    void insertCumulative(@Param("mbrCd") String mbrCd, @Param("drCd") String drCd, @Param("cnt") int cnt);
    void updateCumulative(@Param("mbrCd") String mbrCd, @Param("drCd") String drCd, @Param("cnt") int cnt);
    
    // 제재 자원 조회 - 필터
    List<AdminLimits> getFilteredLimitsTypeList(Map<String, Object> paramMap);
    List<AdminLimits> getFilteredLimitsPeriodList(Map<String, Object> paramMap);
    List<AdminLimits> getFilteredLimitsReasonList(Map<String, Object> paramMap);
}
