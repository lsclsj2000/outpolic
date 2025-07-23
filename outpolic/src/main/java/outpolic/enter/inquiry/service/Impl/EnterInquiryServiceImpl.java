package outpolic.enter.inquiry.service.Impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import outpolic.enter.inquiry.domain.EnterAnn;
import outpolic.enter.inquiry.domain.EnterInquiry;
import outpolic.enter.inquiry.domain.EnterInquiryFile;
import outpolic.enter.inquiry.domain.EnterInquiryType;
import outpolic.enter.inquiry.mapper.EnterInquiryMapper;
import outpolic.enter.inquiry.service.EnterInquiryService;
import outpolic.systems.file.domain.FileMetaData;
import outpolic.systems.util.FilesUtils;
import outpolic.user.mypage.mapper.UserMypageEditMapper;



@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class EnterInquiryServiceImpl implements EnterInquiryService {
	
	private final FilesUtils filesUtils;
	private final EnterInquiryMapper enterInquiryMapper;
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

	// 특정 인물 문의 조회
	@Override
	public List<EnterInquiry> getEnterInquiryListByCode(String memberCode) {
		return enterInquiryMapper.getEnterInquiryListByCode(memberCode);
	}


	@Override
	public List<EnterAnn> getEnterNoticeList() {
		// 공지사항 게시판 조회
		List<EnterAnn> noticeList = enterInquiryMapper.getEnterNoticeList();
		return noticeList;
	}


	@Override
	public EnterAnn getEnterNoticeByCode(String annCode) {
		// 공지사항 상세내용 조회
		return enterInquiryMapper.getEnterNoticeByCode(annCode);
	}


	@Override
	public List<EnterAnn> getEnterFaqList() {
		// faq 목록 조회
		List<EnterAnn> faqList = enterInquiryMapper.getEnterFaqList();
		return faqList;
	}


	@Override
	public List<EnterAnn> getEnterTotalList() {
		// 전체 게시판 조회
		List<EnterAnn> totalList = enterInquiryMapper.getEnterTotalList();
		return totalList;
	}
	
	@Override
	public Page<EnterAnn> getEnterTotalList(Pageable pageable) {
		// 전체 게시판 페이지네이션
		int offset = (int) pageable.getOffset();
		int limit = pageable.getPageSize();

		List<EnterAnn> list = enterInquiryMapper.getEnterTotalListPaged(offset, limit);
		int total = enterInquiryMapper.getEnterTotalCount();

		return new PageImpl<>(list, pageable, total);
	}
	
	@Override
	public Page<EnterInquiry> getEnterInquiryList(Pageable pageable) {
		// 문의 목록 페이지네이션
	    int offset = (int) pageable.getOffset();
	    int limit = pageable.getPageSize();
	    List<EnterInquiry> list = enterInquiryMapper.getEnterInquiryListPaged(offset, limit);
	    int total = enterInquiryMapper.getEnterInquiryListCount();
	    return new PageImpl<>(list, pageable, total);
	}

	@Override
	public Page<EnterAnn> getEnterNoticeList(Pageable pageable, String sort) {
		// 공지사항 목록 페이지네이션
	    int offset = (int) pageable.getOffset();
	    int limit = pageable.getPageSize();
	    List<EnterAnn> list = enterInquiryMapper.getEnterNoticeListPagedWithSort(offset, limit, sort);
	    int total = enterInquiryMapper.getEnterNoticeCount();
	    return new PageImpl<>(list, pageable, total);
	}

	@Override
	public Page<EnterInquiry> getEnterInquiryListByMemberCodePaged(String memberCode, Pageable pageable) {
		// 문의 목록 필터-사용자
	    int offset = (int) pageable.getOffset();
	    int limit = pageable.getPageSize();
	    List<EnterInquiry> list = enterInquiryMapper.getEnterInquiryListByCodePaged(memberCode, offset, limit);
	    int total = enterInquiryMapper.getEnterInquiryListByCodeCount(memberCode);
	    return new PageImpl<>(list, pageable, total);
	}
	
	@Override
	public Page<EnterInquiry> getEnterInquiryListPaged(Pageable pageable, String sort, String memberCode) {
	    int offset = (int) pageable.getOffset();
	    int limit = pageable.getPageSize();

	    List<EnterInquiry> list = enterInquiryMapper.getEnterInquiryListPaged(offset, limit, sort, memberCode);
	    int total = enterInquiryMapper.getEnterInquiryListCount(memberCode);

	    return new PageImpl<>(list, pageable, total);
	}


	@Override
	public Page<EnterInquiry> getEnterInquiryListByMember(String memberCode, Pageable pageable) {
	    return getEnterInquiryListByMemberCodePaged(memberCode, pageable);
	}
	
	@Override
	public Page<EnterAnn> getEnterTotalList(Pageable pageable, String sort) { // 🔧 정렬 파라미터 추가
	    int offset = (int) pageable.getOffset();
	    int limit = pageable.getPageSize();
	    List<EnterAnn> list = enterInquiryMapper.getEnterTotalListPaged(offset, limit, sort);
	    int total = enterInquiryMapper.getEnterTotalCount();
	    return new PageImpl<>(list, pageable, total);
	}

	

}
