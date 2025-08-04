package outpolic.admin.announcement.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import outpolic.admin.announcement.domain.AdminAnn;
import outpolic.admin.announcement.domain.AdminAnnFile;
import outpolic.user.inquiry.domain.UserInquiryFile;


@Mapper
public interface AdminAnnMapper {
	
	// 공지사항 수정 저장
	int updateAdminAnn(AdminAnn adminAnn);
	
	// 공지사항 내역 조회
	List<AdminAnn> getAdminAnnList();
	
	// 공지사항 수정 모달창
	AdminAnn getAdminAnnDetail(String annCode);
	
	// 공지사항 내역 조회 - 필터
	List<AdminAnn> getAdminAnnListFiltered(Map<String, Object> paramMap);
	
	// 공지사항 등록
    void addAdminAnnWrite(AdminAnn ann);
    
    // 수정 모달창 파일 조회
    List<AdminAnnFile> getAdminAnnFileListByReferCd(String referCd);
    
    // 문의첨부파일 삭제
 	int deleteUserInquiryFileByIdx(String saCode);
 	
 	// 문의첨부파일 데이터 조회
 	AdminAnnFile getAdminAnnFileInfoByIdx(String saCode);
 	
 	// 문의첨부파일 데이터 목록 조회
 	List<AdminAnnFile> getAdminAnnFileList();
 	
 	// 첨부파일 코드 생성
 	String generateNewSaCode(); 
 	
 	// 단일 문의첨부파일 업로드
 	int addAdminAnnFile(AdminAnnFile annFileDto);
 	
 	// 다중 문의첨부파일 업로드
 	int addAdminAnnFiles(List<AdminAnnFile> annFileDto);
 	
 	// 문의 첨부파일 등록
 	int addAdminAnnAttachment(AdminAnnFile attachment);
}