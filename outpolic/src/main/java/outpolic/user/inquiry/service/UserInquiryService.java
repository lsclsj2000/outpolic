// outpolic.user.inquiry.service.UserInquiryService.java
package outpolic.user.inquiry.service;

import java.util.List;

import outpolic.user.inquiry.domain.UserInquiry;
import outpolic.user.inquiry.domain.UserInquiryType; // 이 임포트도 추가해야 합니다.

public interface UserInquiryService {

	// 문의 등록
	void adduserInquiryWrite(UserInquiry inquiry);

	// ✅ 새로 추가할 메서드: 모든 문의 유형을 조회
	List<UserInquiryType> getAllInquiryTypes(); // <---- 이 줄을 추가해주세요.

	// 문의 타입 조회
	List<UserInquiry> getUserInquiryTypeByCode(String inquiryTypeCode);

	// 문의 상세내용 조회
	UserInquiry getUserInquiryByCode(String inquiryCode);

	// 문의 목록 조회
	List<UserInquiry> getUserInquiryList();
}