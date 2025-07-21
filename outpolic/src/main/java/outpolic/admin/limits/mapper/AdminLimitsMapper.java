package outpolic.admin.limits.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;

import outpolic.admin.limits.domain.AdminLimits;

@Mapper
public interface AdminLimitsMapper {
	
	// 회원 권한 조회
	List<AdminLimits> getAdminLimitsAuthorityList();
	
	// 제재 내역 조회
	List<AdminLimits> getAdminLimitsList();
	
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
}
