package outpolic.admin.member.service;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import outpolic.admin.member.dto.AdminMemberDTO;
import outpolic.common.domain.Member;

public interface AdminEnterService {
	// 기업 목록 조회
	List<AdminMemberDTO> getEnterList();

	// 상태에 따른 기업 목록 조회
	List<AdminMemberDTO> getEnterListByStatus(String statusCode);

	// 특정 기업 정보 조회
	AdminMemberDTO selectEnterByCode(String memberCode);

	// 특정 기업 정보 업데이트
	int updateAdminEnterEditInfo(AdminMemberDTO adminMemberDTO);

	// 필터링
	List<AdminMemberDTO> selectFilteredEnterpriseMembers(@Param("statusCode") String statusCode,
			@Param("orderBy") String orderBy);

	// 검색
	List<AdminMemberDTO> searchEnterpriseMembers(String keyword);

	int getEnterCount();

	List<AdminMemberDTO> getEnterListForPg(@Param("startRow") int startRow, @Param("rowPerPage") int rowPerPage);
}
