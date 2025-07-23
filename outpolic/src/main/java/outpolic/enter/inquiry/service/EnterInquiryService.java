package outpolic.enter.inquiry.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import outpolic.enter.inquiry.domain.EnterAnn;
import outpolic.enter.inquiry.domain.EnterInquiry;
import outpolic.enter.inquiry.domain.EnterInquiryType;


public interface EnterInquiryService {
	
	// 문의첨부파일 삭제
	//void deleteEnterInquiryFile(EnterInquiryFile enterInquiryFile);
		
	// 문의 등록
	void addenterInquiryWrite(EnterInquiry inquiry, MultipartFile[] attachmentFile);
	
	// 모든 문의 유형 조회
	List<EnterInquiryType> getAllInquiryTypes();
	
	// 문의 타입 조회
	List<EnterInquiry> getEnterInquiryTypeByCode(String inquiryTypeCode);
	
	
	// 문의 상세내용 조회
	EnterInquiry getEnterInquiryByCode(String inquiryCode);
	
	// 문의 목록 조회
	List<EnterInquiry> getEnterInquiryList();
	
	//특정 인물 문의 목록 조회
	List<EnterInquiry> getEnterInquiryListByCode(String memberCode);
	
	// 공지사항 상세 페이지 조회
	EnterAnn getEnterNoticeByCode(String annCode);
	
	// 공지사항 게시판 조회
	List<EnterAnn> getEnterNoticeList();
	
	// faq 목록 조회
	List<EnterAnn> getEnterFaqList();
	
	// 전체 게시판 조회
	List<EnterAnn> getEnterTotalList();
	
	// pagenation
	Page<EnterAnn> getEnterTotalList(Pageable pageable); 	
	Page<EnterInquiry> getEnterInquiryList(Pageable pageable);
	Page<EnterAnn> getEnterNoticeList(Pageable pageable, String sort);
	
	// 문의 목록 필터
	Page<EnterInquiry> getEnterInquiryListByMemberCodePaged(String memberCode, Pageable pageable);
	Page<EnterInquiry> getEnterInquiryListByMember(String memberCode, Pageable pageable);
	Page<EnterInquiry> getEnterInquiryListPaged(Pageable pageable, String sort, String memberCode);
	
	Page<EnterAnn> getEnterTotalList(Pageable pageable, String sort);

	
}