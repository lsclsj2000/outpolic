package outpolic.enter.inquiry.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;

import outpolic.enter.inquiry.domain.EnterAnn;
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
	
	// 첨부파일 코드 생성
	String generateNewSaCode(); 
	
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
	
	// 문의 프로세스 등록
	int addenterInquiryProcess(EnterInquiry inquiry);
	
	// 모든 문의 유형 조회
	List<EnterInquiryType> getAllInquiryTypes();
	
	// 문의 타입 조회
	List<EnterInquiry> getEnterInquiryTypeByCode(String inquiryTypeCode);
	
	// 문의 상세내용 조회
	EnterInquiry getEnterInquiryByCode(String inquiryCode);
	
	// 문의 목록 조회
	List<EnterInquiry> getEnterInquiryList();
	
	//ssy 마이페이지용 추가 -> 특정 회원 문의 목록 조회
	List<EnterInquiry> getEnterInquiryListByCode(String memberCode);
	
	// 공지사항 게시판 조회
	List<EnterAnn> getEnterNoticeList();
	
	// 공지사항 상세 페이지 조회
	EnterAnn getEnterNoticeByCode(String annCode);
    
    // faq 목록 조회
 	List<EnterAnn> getEnterFaqList();
 	
 	// 전체 게시판 조회
 	List<EnterAnn> getEnterTotalList();
 	
 	// 전체 게시판 페이지네이션
 	List<EnterAnn> getEnterTotalListPaged(@Param("offset") int offset, @Param("limit") int limit);
 	int getEnterTotalCount();
 	
 	// 문의 목록 페이지네이션
 	List<EnterInquiry> getEnterInquiryListPaged(@Param("offset") int offset, @Param("limit") int limit);
 	int getEnterInquiryListCount();
 	
 	// 공지사항 목록 페이지네이션
 	List<EnterAnn> getEnterNoticeListPaged(@Param("offset") int offset, @Param("limit") int limit);
 	int getEnterNoticeCount();
 	
 	// 문의 목록 필터-사용자
 	List<EnterInquiry> getEnterInquiryListByCodePaged(@Param("mbrCd") String mbrCd, @Param("offset") int offset, @Param("limit") int limit);
 	int getEnterInquiryListByCodeCount(@Param("mbrCd") String mbrCd);
 	

 	int getEnterInquiryListCount(@Param("memberCode") String memberCode);
 	
 	List<EnterInquiry> getEnterInquiryListPaged(
 		    @Param("offset") int offset,
 		    @Param("limit") int limit,
 		    @Param("sort") String sort,
 		    @Param("memberCode") String memberCode
 		);
	
 	
 	List<EnterAnn> getEnterTotalListPaged(@Param("offset") int offset,
            @Param("limit") int limit,
            @Param("sort") String sort);
 	
 	List<EnterAnn> getEnterNoticeListPagedWithSort(@Param("offset") int offset,
            @Param("limit") int limit,
            @Param("sort") String sort);
}




