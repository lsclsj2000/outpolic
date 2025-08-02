package outpolic.admin.announcement.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import outpolic.admin.announcement.domain.AdminAnn;


@Mapper
public interface AdminAnnMapper {
	
	// 공지사항 내역 조회
	List<AdminAnn> getAdminAnnList();
}