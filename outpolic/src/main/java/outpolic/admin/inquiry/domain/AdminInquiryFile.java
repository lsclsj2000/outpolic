package outpolic.admin.inquiry.domain;

import java.time.LocalDateTime;

public class AdminInquiryFile {

    private String saCode;          // 첨부파일코드 (sa_cd)
    private String saReferCode;     // 참조코드 (sa_refer_cd) - 새로 추가
    private String saOrgnlName;     // 원본파일명 (sa_orgnl_nm)
    private String saSrvrName;      // 서버파일명 (sa_srvr_nm)
    private String saPath;          // 저장경로 (sa_path)
    private String saExtn;          // 확장자 (sa_extn) - 새로 추가
    private Long saSize;            // 파일크기 (sa_size) - Long 타입 유지
    private String mbrCode;         // 등록자 (mbr_cd) - 새로 추가
    private LocalDateTime saRegYmdt; // 등록일시 (sa_reg_ymdt) - 새로 추가, LocalDateTime 타입

    // 기본 생성자
    public AdminInquiryFile() {}

    // Builder 패턴을 위한 private 생성자
    private AdminInquiryFile(Builder builder) {
        this.saCode = builder.saCode;
        this.saReferCode = builder.saReferCode;
        this.saOrgnlName = builder.saOrgnlName;
        this.saSrvrName = builder.saSrvrName;
        this.saPath = builder.saPath;
        this.saExtn = builder.saExtn;
        this.saSize = builder.saSize;
        this.mbrCode = builder.mbrCode;
        this.saRegYmdt = builder.saRegYmdt;
    }

    // --- Getter 메서드 ---
    public String getSaCode() {
        return saCode;
    }

    public String getSaReferCode() {
        return saReferCode;
    }

    public String getSaOrgnlName() {
        return saOrgnlName;
    }

    public String getSaSrvrName() {
        return saSrvrName;
    }

    public String getSaPath() {
        return saPath;
    }

    public String getSaExtn() {
        return saExtn;
    }

    public Long getSaSize() {
        return saSize;
    }

    public String getMbrCode() {
        return mbrCode;
    }

    public LocalDateTime getSaRegYmdt() {
        return saRegYmdt;
    }

    // --- Setter 메서드 ---
    public void setSaCode(String saCode) {
        this.saCode = saCode;
    }

    public void setSaReferCode(String saReferCode) {
        this.saReferCode = saReferCode;
    }

    public void setSaOrgnlName(String saOrgnlName) {
        this.saOrgnlName = saOrgnlName;
    }

    public void setSaSrvrName(String saSrvrName) {
        this.saSrvrName = saSrvrName;
    }

    public void setSaPath(String saPath) {
        this.saPath = saPath;
    }

    public void setSaExtn(String saExtn) {
        this.saExtn = saExtn;
    }

    public void setSaSize(Long saSize) {
        this.saSize = saSize;
    }

    public void setMbrCode(String mbrCode) {
        this.mbrCode = mbrCode;
    }

    public void setSaRegYmdt(LocalDateTime saRegYmdt) {
        this.saRegYmdt = saRegYmdt;
    }

    // --- Builder 클래스 ---
    public static class Builder {
        private String saCode;
        private String saReferCode;
        private String saOrgnlName;
        private String saSrvrName;
        private String saPath;
        private String saExtn;
        private Long saSize;
        private String mbrCode;
        private LocalDateTime saRegYmdt;

        public Builder() {}

        // 기존 DTO 객체로 Builder 초기화 (선택 사항)
        public Builder(AdminInquiryFile attachment) {
            this.saCode = attachment.saCode;
            this.saReferCode = attachment.saReferCode;
            this.saOrgnlName = attachment.saOrgnlName;
            this.saSrvrName = attachment.saSrvrName;
            this.saPath = attachment.saPath;
            this.saExtn = attachment.saExtn;
            this.saSize = attachment.saSize;
            this.mbrCode = attachment.mbrCode;
            this.saRegYmdt = attachment.saRegYmdt;
        }

        public Builder saCode(String saCode) {
            this.saCode = saCode;
            return this;
        }

        public Builder saReferCode(String saReferCode) {
            this.saReferCode = saReferCode;
            return this;
        }

        public Builder saOrgnlName(String saOrgnlName) {
            this.saOrgnlName = saOrgnlName;
            return this;
        }

        public Builder saSrvrName(String saSrvrName) {
            this.saSrvrName = saSrvrName;
            return this;
        }

        public Builder saPath(String saPath) {
            this.saPath = saPath;
            return this;
        }

        public Builder saExtn(String saExtn) {
            this.saExtn = saExtn;
            return this;
        }

        public Builder saSize(Long saSize) {
            this.saSize = saSize;
            return this;
        }

        public Builder mbrCode(String mbrCode) {
            this.mbrCode = mbrCode;
            return this;
        }

        public Builder saRegYmdt(LocalDateTime saRegYmdt) {
            this.saRegYmdt = saRegYmdt;
            return this;
        }

        public AdminInquiryFile build() {
            return new AdminInquiryFile(this);
        }
    }
}
