package outpolic.admin.declaration.domain;

import lombok.Data;

@Data
public class SubmissionAttachment {
    private String saCode;          // sa_cd
    private String saReferCode;     // sa_refer_cd (참조 코드, 여기서는 decl_cd)
    private String saOrgnlName;     // sa_orgnl_nm (원본 파일명)
    private String saSrvrName;      // sa_srvr_nm (서버에 저장된 파일명)
    private String saPath;          // sa_path (파일 경로)
    private String saRegYmdt;       // sa_reg_ymdt (등록일시)
}
