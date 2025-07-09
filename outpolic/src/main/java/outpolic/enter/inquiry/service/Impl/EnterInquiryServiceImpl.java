package outpolic.enter.inquiry.service.Impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import outpolic.enter.inquiry.domain.EnterInquiry;
import outpolic.enter.inquiry.domain.EnterInquiryFile;
import outpolic.enter.inquiry.domain.EnterInquiryType;
import outpolic.enter.inquiry.mapper.EnterInquiryMapper;
import outpolic.enter.inquiry.service.EnterInquiryService;
import outpolic.systems.file.domain.FileMetaData;
import outpolic.systems.util.FilesUtils;



@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class EnterInquiryServiceImpl implements EnterInquiryService {
	
	private final FilesUtils filesUtils;
	private final EnterInquiryMapper enterInquiryMapper;
	
	// 아래 3개는 선생님꺼임.
	/*
	@Override
	public void deleteEnterInquiryFile(EnterInquiryFile enterInquiryFile) {
		// 문의첨부파일 삭제
		
		String path = enterInquiryFile.getSaPath();
		Boolean isDelete = enterInquiryFilesUtils.deleteFileByPath(path);
		if(isDelete) enterInquiryMapper.deleteEnterInquiryFileByIdx(enterInquiryFile.getSaCode());
	}
	*/
	
	@Override
	public void addenterInquiryWrite(EnterInquiry inquiry, MultipartFile[] attachmentFile) {
		
		// 문의 등록
		enterInquiryMapper.addenterInquiryWrite(inquiry);
		
		log.info("문의등록 후 : {}", inquiry);
		
		if(attachmentFile != null && !attachmentFile[0].isEmpty()) {
			var fileList = filesUtils.uploadFiles(attachmentFile, "inquiry");
			
			List<EnterInquiryFile> enterInquiryFileList = new ArrayList<>();
			EnterInquiryFile enterInquiryFile = null;
			for(FileMetaData file : fileList) {
				enterInquiryFile = new EnterInquiryFile.Builder()
													 .saCode(file.getFileIdx())
													 .saReferCode(inquiry.getInquiryCode())
													 .saOrgnlName(file.getFileOriginalName())
													 .saSrvrName(file.getFileNewName())
													 .saPath(file.getFilePath())
													 .saExtn(file.getFileExtension())
													 .saSize(file.getFileSize())
													 .mbrCode(inquiry.getMemberCode())
													 .build();
				enterInquiryFileList.add(enterInquiryFile);
			}
			
			enterInquiryMapper.addEnterInquiryFiles(enterInquiryFileList);			
		}
		
	}
	
	
	@Override
	public List<EnterInquiry> getEnterInquiryTypeByCode(String inquiryTypeCode) {
	
		// 문의 타입 조회	
		return enterInquiryMapper.getEnterInquiryTypeByCode(inquiryTypeCode);
	}

	
	@Override
	public EnterInquiry getEnterInquiryByCode(String inquiryCode) {
		// 문의 상세내용 조회
		return enterInquiryMapper.getEnterInquiryByCode(inquiryCode);
	}

	
	@Override
	public List<EnterInquiry> getEnterInquiryList() {
		// 문의 목록 조회
		List<EnterInquiry> inquiryList = enterInquiryMapper.getEnterInquiryList();
		return inquiryList;
	}

	
	@Override
	public List<EnterInquiryType> getAllInquiryTypes() {
		// 문의 타입 조회
		return enterInquiryMapper.getAllInquiryTypes();
	}
}
