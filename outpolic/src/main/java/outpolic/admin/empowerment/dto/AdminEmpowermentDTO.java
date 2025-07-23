package outpolic.admin.empowerment.dto;

import lombok.Data;

@Data
public class AdminEmpowermentDTO {
	// 관리자 테이블
	 private String admCd; 
	 private String mbrCd; 
	 private String stcCd;
	 private String admImpowerYmdt;
	
	 // 권한코드
	 private String arCd; 
	 private String grdCd; 
	 private String arNm; 
	 private String arExpln; 
	 // 등록자
	 private String arRegAdmCd; 
	 private String arRegYmdt; 
	 // 수정자
	 private String mdfcnAdmCd; 
	 private String arMdfcnYmdt; 
	 private String arSttsCd;
	 
	 // 관리자 권한 매핑
	 private String armId; 
	 private String empwerAdmCd; 
	 // 
	 private String ar2Cd; 
	 private String armRegYmdt;
	 
	 private int systemAdmin;    // 시스템관리 1/0
	 private int adminEmpower;   // 관리자 권한부여 1/0
	 private int contentAdmin;   // 콘텐츠 관리 1/0
	 private int csAdmin;        // 고객센터 관리 1/0
	 private int memberAdmin;      // 회원 관리 1/0
	 private int paymentAdmin;   // 결제 관리 1/0
}

