package outpolic.enter.inquiry.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import outpolic.enter.inquiry.domain.EnterInquiry;
import outpolic.enter.inquiry.domain.EnterInquiryFile;
import outpolic.enter.inquiry.domain.EnterInquiryProcess;
import outpolic.enter.inquiry.domain.EnterInquiryType;


@Mapper
public interface EnterInquiryMapper {
	
	// 여기 쌤꺼 훔쳐온거
	// 문의첨부파일 삭제
	int deleteEnterInquiryFileByIdx(String saCode);
	
	// 문의첨부파일 데이터 조회
	EnterInquiryFile getEnterInquiryFileInfoByIdx(String saCode);
	
	// 문의첨부파일 데이터 목록 조회
	List<EnterInquiryFile> getEnterInquiryFileList();
	
	// 단일 문의첨부파일 업로드
	int addEnterInquiryFile(EnterInquiryFile inquiryFileDto);
	
	// 다중 문의첨부파일 업로드
	int addEnterInquiryFiles(List<EnterInquiryFile> inquiryFileDto);
	
	
	// 여기부터 내가쓴거.
	// 문의 처리 : 관리자 답변 조회
	List<EnterInquiryProcess> getEnterInquiryProcessList(String inquiryProcess);
	
	// 문의 첨부파일 등록
	int addenterInquiryAttachment(EnterInquiryFile attachment);
	
	// 문의 등록
	int addenterInquiryWrite(EnterInquiry inquiry);
	
	// 모든 문의 유형 조회
	List<EnterInquiryType> getAllInquiryTypes();
	
	// 문의 타입 조회
	List<EnterInquiry> getEnterInquiryTypeByCode(String inquiryTypeCode);
	
	// 문의 상세내용 조회
	EnterInquiry getEnterInquiryByCode(String inquiryCode);
	
	// 문의 목록 조회
	List<EnterInquiry> getEnterInquiryList();
}