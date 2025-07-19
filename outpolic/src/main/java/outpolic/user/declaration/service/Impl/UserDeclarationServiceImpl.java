package outpolic.user.declaration.service.Impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import outpolic.user.declaration.domain.UserDeclaration;
import outpolic.user.declaration.mapper.UserDeclarationMapper;
import outpolic.user.declaration.service.UserDeclarationService;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UserDeclarationServiceImpl implements UserDeclarationService{
	
	private final UserDeclarationMapper declarationMapper;
	
	@Override
	public List<UserDeclaration> getDeclarationReasonsByType(String dtCd) {
		// 신고 사유 드롭다운 조회
		return declarationMapper.getDeclarationReasonsByType(dtCd);
	}

	@Override
	public List<UserDeclaration> getActiveDeclarationTypes() {
		// 신고 타입 드롭다운 조회
		return declarationMapper.getActiveDeclarationTypes();
	}

}
