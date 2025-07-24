package outpolic.user.inquiry.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;

import outpolic.user.inquiry.domain.UserAnn;
import outpolic.user.inquiry.domain.UserInquiry;
import outpolic.user.inquiry.domain.UserInquiryFile;
import outpolic.user.inquiry.domain.UserInquiryProcess;
import outpolic.user.inquiry.domain.UserInquiryType;


@Mapper
public interface UserInquiryMapper {
	
	// 여기 쌤꺼 훔쳐온거
	// 문의첨부파일 삭제
	int deleteUserInquiryFileByIdx(String saCode);
	
	// 문의첨부파일 데이터 조회
	UserInquiryFile getUserInquiryFileInfoByIdx(String saCode);
	
	// 문의첨부파일 데이터 목록 조회
	List<UserInquiryFile> getUserInquiryFileList();
	
	// 첨부파일 코드 생성
	String generateNewSaCode(); 
	
	// 단일 문의첨부파일 업로드
	int addUserInquiryFile(UserInquiryFile inquiryFileDto);
	
	// 다중 문의첨부파일 업로드
	int addUserInquiryFiles(List<UserInquiryFile> inquiryFileDto);
	
	
	// 여기부터 내가쓴거.
	// 문의 처리 : 관리자 답변 조회
	List<UserInquiryProcess> getUserInquiryProcessList(String inquiryProcess);
	
	// 문의 첨부파일 등록
	int adduserInquiryAttachment(UserInquiryFile attachment);
	
	// 문의 등록
	int adduserInquiryWrite(UserInquiry inquiry);
	
	// 문의 프로세스 등록
	int adduserInquiryProcess(UserInquiry inquiry);
	
	// 모든 문의 유형 조회
	List<UserInquiryType> getAllInquiryTypes();
	
	// 문의 타입 조회
	List<UserInquiry> getUserInquiryTypeByCode(String inquiryTypeCode);
	
	// 문의 상세내용 조회
	UserInquiry getUserInquiryByCode(String inquiryCode);
	
	// 문의 목록 조회
	List<UserInquiry> getUserInquiryList();
	
	//ssy 마이페이지용 추가 -> 특정 회원 문의 목록 조회
	List<UserInquiry> getUserInquiryListByCode(String memberCode);
	
	// 공지사항 게시판 조회
	List<UserAnn> getUserNoticeList();
	
	// 공지사항 상세 페이지 조회
    UserAnn getUserNoticeByCode(String annCode);
    
    // faq 목록 조회
 	List<UserAnn> getUserFaqList();
 	
 	// 전체 게시판 조회
 	List<UserAnn> getUserTotalList();
 	
 	// 전체 게시판 페이지네이션
 	List<UserAnn> getUserTotalListPaged(@Param("offset") int offset, @Param("limit") int limit);
 	int getUserTotalCount();
 	
 	// 문의 목록 페이지네이션
 	List<UserInquiry> getUserInquiryListPaged(@Param("offset") int offset, @Param("limit") int limit);
 	int getUserInquiryListCount();
 	
 	// 공지사항 목록 페이지네이션
 	List<UserAnn> getUserNoticeListPaged(@Param("offset") int offset, @Param("limit") int limit);
 	int getUserNoticeCount();
 	
 	// 문의 목록 필터-사용자
 	List<UserInquiry> getUserInquiryListByCodePaged(@Param("mbrCd") String mbrCd, @Param("offset") int offset, @Param("limit") int limit);
 	int getUserInquiryListByCodeCount(@Param("mbrCd") String mbrCd);
 	

 	int getUserInquiryListCount(@Param("memberCode") String memberCode);
 	
 	List<UserInquiry> getUserInquiryListPaged(
 		    @Param("offset") int offset,
 		    @Param("limit") int limit,
 		    @Param("sort") String sort,
 		    @Param("memberCode") String memberCode
 		);
	
 	
 	List<UserAnn> getUserTotalListPaged(@Param("offset") int offset,
            @Param("limit") int limit,
            @Param("sort") String sort);
 	
 	List<UserAnn> getUserNoticeListPagedWithSort(@Param("offset") int offset,
            @Param("limit") int limit,
            @Param("sort") String sort);
}




