package outpolic.user.declaration.domain;

import java.util.List;

import lombok.Data;
import outpolic.user.inquiry.domain.UserInquiryFile;

@Data
public class UserDeclaration {
	
	// 신고 타입 관련 필드
    private String dtCode;
    private String dtName;
    private String dtExpln;
    private String dtStcCode;

    // 신고 사유 관련 필드
    private String drCode;
    private String drDtCode; // 이 필드는 필요에 따라 유지하거나, dtCode를 사용하는 것이 좋습니다.
    private String drName;
    private String drExpln;
    private String drStcCode;

    // 신고 대상 검색 결과 매핑용 필드
    private String prtfCode;
    private String osCode;
    private String entCode;
    private String mbrCode; // 일반회원 검색 시 사용
    private String prtfTitle;
    private String prtfType;
    private String osTitle;
    private String osType;	
    private String entTitle;
    private String entType;
    private String mbrTitle;
    private String mbrType;

    // 실제 신고(declaration) 테이블 매핑용 필드
    private String declCode; // decl_cd 컬럼에 매핑 (selectKey로 값 받아옴)
    private String mbrCd;    // 신고자(회원) 코드. DB의 mbr_cd 컬럼에 매핑
    private String declCn;
    private String declTargetCd;
    private String stcCd;
    private String declRegYmdt; // 등록일자

    // 신고 파일 첨부를 위함
    private List<UserInquiryFile> attachedFiles;
}
