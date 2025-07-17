package outpolic.user.inquiry.service.Impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import outpolic.systems.file.domain.FileMetaData;
import outpolic.systems.util.FilesUtils;
import outpolic.user.inquiry.domain.UserInquiry;
import outpolic.user.inquiry.domain.UserInquiryFile;
import outpolic.user.inquiry.domain.UserInquiryType;
import outpolic.user.inquiry.mapper.UserInquiryMapper;
import outpolic.user.inquiry.service.UserInquiryService;
import outpolic.user.mypage.mapper.UserMypageEditMapper;



@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UserInquiryServiceImpl implements UserInquiryService {
	
	private final FilesUtils filesUtils;
	private final UserInquiryMapper userInquiryMapper;
	private final UserMypageEditMapper userMypageEditMapper;
	
	// 아래 3개는 선생님꺼임.
	/*
	@Override
	public void deleteUserInquiryFile(UserInquiryFile userInquiryFile) {
		// 문의첨부파일 삭제
		
		String path = userInquiryFile.getSaPath();
		Boolean isDelete = userInquiryFilesUtils.deleteFileByPath(path);
		if(isDelete) userInquiryMapper.deleteUserInquiryFileByIdx(userInquiryFile.getSaCode());
	}
	*/
	
	@Override
	public void adduserInquiryWrite(UserInquiry inquiry, MultipartFile[] attachmentFile) {
		
		// 문의 등록
		userInquiryMapper.adduserInquiryWrite(inquiry);
		
		log.info("문의등록 후 : {}", inquiry);
		
		if(attachmentFile != null && !attachmentFile[0].isEmpty()) {
			var fileList = filesUtils.uploadFiles(attachmentFile, "inquiry");
			
			List<UserInquiryFile> userInquiryFileList = new ArrayList<>();
			UserInquiryFile userInquiryFile = null;
			for(FileMetaData file : fileList) {
				userInquiryFile = new UserInquiryFile.Builder()
													 .saCode(file.getFileIdx())
													 .saReferCode(inquiry.getInquiryCode())
													 .saOrgnlName(file.getFileOriginalName())
													 .saSrvrName(file.getFileNewName())
													 .saPath(file.getFilePath())
													 .saExtn(file.getFileExtension())
													 .saSize(file.getFileSize())
													 .mbrCode(inquiry.getMemberCode())
													 .build();
				userInquiryFileList.add(userInquiryFile);
			}
			
			userInquiryMapper.addUserInquiryFiles(userInquiryFileList);			
		}
		userInquiryMapper.adduserInquiryProcess(inquiry);
		
	}
	
	
	@Override
	public List<UserInquiry> getUserInquiryTypeByCode(String inquiryTypeCode) {
	
		// 문의 타입 조회	
		return userInquiryMapper.getUserInquiryTypeByCode(inquiryTypeCode);
	}



	
	@Override
	public UserInquiry getUserInquiryByCode(String inquiryCode) {
		// 문의 상세내용 조회
		return userInquiryMapper.getUserInquiryByCode(inquiryCode);
	}

	
	@Override
	public List<UserInquiry> getUserInquiryList() {
		// 문의 목록 조회
		List<UserInquiry> inquiryList = userInquiryMapper.getUserInquiryList();
		return inquiryList;
	}

	
	@Override
	public List<UserInquiryType> getAllInquiryTypes() {
		// 문의 타입 조회
		return userInquiryMapper.getAllInquiryTypes();
	}

	// 특정 인물 문의 조회
	@Override
	public List<UserInquiry> getUserInquiryListByCode(String memberCode) {
		return userInquiryMapper.getUserInquiryListByCode(memberCode);
	}
	
	

}
