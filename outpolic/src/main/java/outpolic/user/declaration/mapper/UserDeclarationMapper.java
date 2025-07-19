package outpolic.user.declaration.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import outpolic.user.declaration.domain.UserDeclaration;

@Mapper
public interface UserDeclarationMapper {
	
	// 신고 사유 드롭다운 조회
	List<UserDeclaration> getDeclarationReasonsByType(String dtCd);
	
	// 신고 타입 드롭다운 조회
	List<UserDeclaration> getActiveDeclarationTypes();
}
