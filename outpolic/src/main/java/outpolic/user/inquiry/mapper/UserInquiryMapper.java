package outpolic.user.inquiry.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

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
	
	// 모든 문의 유형 조회
	List<UserInquiryType> getAllInquiryTypes();
	
	// 문의 타입 조회
	List<UserInquiry> getUserInquiryTypeByCode(String inquiryTypeCode);
	
	// 문의 상세내용 조회
	UserInquiry getUserInquiryByCode(String inquiryCode);
	
	// 문의 목록 조회
	List<UserInquiry> getUserInquiryList();
}