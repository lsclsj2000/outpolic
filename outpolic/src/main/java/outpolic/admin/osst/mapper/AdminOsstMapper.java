package outpolic.admin.osst.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import outpolic.admin.osst.domain.AdminOsst;

@Mapper
public interface AdminOsstMapper {
	
	// 외주 진행 목록 조회
	List<AdminOsst> getAdminOsstList();
}
