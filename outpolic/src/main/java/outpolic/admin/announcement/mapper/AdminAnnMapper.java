package outpolic.admin.announcement.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import outpolic.admin.announcement.domain.AdminAnn;


@Mapper
public interface AdminAnnMapper {
	
	// 공지사항 수정 저장
	int updateAdminAnn(AdminAnn adminAnn);
	
	// 공지사항 내역 조회
	List<AdminAnn> getAdminAnnList();
	
	// 공지사항 수정 모달창
	AdminAnn getAdminAnnDetail(String annCode);
	
	// 공지사항 내역 조회 - 필터
	List<AdminAnn> getAdminAnnListFiltered(Map<String, Object> paramMap);
}