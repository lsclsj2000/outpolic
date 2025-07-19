package outpolic.user.declaration.service.Impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import outpolic.systems.file.domain.FileMetaData;
import outpolic.systems.util.FilesUtils;
import outpolic.user.declaration.domain.UserDeclaration;
import outpolic.user.declaration.mapper.UserDeclarationMapper;
import outpolic.user.declaration.service.UserDeclarationService;
import outpolic.user.inquiry.domain.UserInquiryFile;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UserDeclarationServiceImpl implements UserDeclarationService{
	
	private final FilesUtils filesUtils;
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
	
	@Override
	public List<UserDeclaration> searchTarget(String type, String keyword) {
	    switch (type) {
	        case "portfolio":
	            return declarationMapper.searchPortfolio(keyword);
	        case "outsourcing":
	            return declarationMapper.searchOutsourcing(keyword);
	        case "enter":  // 기업회원
	            return declarationMapper.searchEnterprise(keyword);
	        case "user":   // 일반회원
	            return declarationMapper.searchGeneralMember(keyword);
	        default:
	            return Collections.emptyList(); // 잘못된 type일 경우 빈 리스트 반환
	    }
	}

	@Override
	public void addDeclarationWithAttachments(UserDeclaration declaration, MultipartFile[] attachments) {
	    // 신고 등록
	    declarationMapper.insertDeclaration(declaration);

	    // 첨부파일 업로드
	    if (attachments != null && attachments.length > 0 && !attachments[0].isEmpty()) {
	        List<FileMetaData> uploadedFiles = filesUtils.uploadFiles(attachments, "declaration");

	        List<UserInquiryFile> fileList = new ArrayList<>();
	        for (FileMetaData file : uploadedFiles) {
	            UserInquiryFile attach = new UserInquiryFile.Builder()
	                .saCode(file.getFileIdx())
	                .saReferCode(declaration.getDeclCode())
	                .saOrgnlName(file.getFileOriginalName())
	                .saSrvrName(file.getFileNewName())
	                .saPath(file.getFilePath())
	                .saExtn(file.getFileExtension())
	                .saSize(file.getFileSize())
	                .mbrCode(declaration.getMbrCd())
	                .build();

	            fileList.add(attach);
	        }

	        declarationMapper.insertDeclarationAttachments(fileList);
	    }
	}

}
