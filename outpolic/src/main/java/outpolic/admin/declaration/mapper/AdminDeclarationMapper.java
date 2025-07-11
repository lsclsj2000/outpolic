package outpolic.admin.declaration.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import outpolic.admin.declaration.domain.AdminDeclaration;

@Mapper
public interface AdminDeclarationMapper {
	
	// 신고 타입 자원 조회
	List<AdminDeclaration> getAdminDeclarationTypeList();
	
	// 신고 내역 목록 조회
	List<AdminDeclaration> getAdminDeclarationList();
}
