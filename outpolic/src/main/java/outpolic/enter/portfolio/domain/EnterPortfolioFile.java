package outpolic.enter.portfolio.domain;

import java.time.LocalDateTime;

// @Data // Lombok 사용 시
public class EnterPortfolioFile {
    private String fileCd; // file_cd
    private LocalDateTime fileRegYmdt; // file_reg_ymdt
    private LocalDateTime fileMdfcnYmdt; // file_mdfcn_ymdt
    private String fileExtn; // file_extn
    private String fileOrgnlNm; // file_orgnl_nm
    private String fileSrvrNm; // file_srvr_nm
    private String filePath; // file_path
    private String clCd; // cl_cd
    private String mbrCd; // mbr_cd
    private Long fileSize; // <-- file_size 추가

    // Lombok을 사용하지 않는 경우, 아래 생성자, getter/setter를 직접 추가해야 합니다.
    public EnterPortfolioFile() {}

    // Builder 패턴을 위한 private 생성자 (선택 사항, 필요하다면 구현)
    private EnterPortfolioFile(Builder builder) {
        this.fileCd = builder.fileCd;
        this.fileRegYmdt = builder.fileRegYmdt;
        this.fileMdfcnYmdt = builder.fileMdfcnYmdt;
        this.fileExtn = builder.fileExtn;
        this.fileOrgnlNm = builder.fileOrgnlNm;
        this.fileSrvrNm = builder.fileSrvrNm;
        this.filePath = builder.filePath;
        this.clCd = builder.clCd;
        this.mbrCd = builder.mbrCd;
        this.fileSize = builder.fileSize;
    }

    // --- Getter 메서드 ---
    public String getFileCd() { return fileCd; }
    public LocalDateTime getFileRegYmdt() { return fileRegYmdt; }
    public LocalDateTime getFileMdfcnYmdt() { return fileMdfcnYmdt; }
    public String getFileExtn() { return fileExtn; }
    public String getFileOrgnlNm() { return fileOrgnlNm; }
    public String getFileSrvrNm() { return fileSrvrNm; }
    public String getFilePath() { return filePath; }
    public String getClCd() { return clCd; }
    public String getMbrCd() { return mbrCd; }
    public Long getFileSize() { return fileSize; }

    // --- Setter 메서드 ---
    public void setFileCd(String fileCd) { this.fileCd = fileCd; }
    public void setFileRegYmdt(LocalDateTime fileRegYmdt) { this.fileRegYmdt = fileRegYmdt; }
    public void setFileMdfcnYmdt(LocalDateTime fileMdfcnYmdt) { this.fileMdfcnYmdt = fileMdfcnYmdt; }
    public void setFileExtn(String fileExtn) { this.fileExtn = fileExtn; }
    public void setFileOrgnlNm(String fileOrgnlNm) { this.fileOrgnlNm = fileOrgnlNm; }
    public void setFileSrvrNm(String fileSrvrNm) { this.fileSrvrNm = fileSrvrNm; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
    public void setClCd(String clCd) { this.clCd = clCd; }
    public void setMbrCd(String mbrCd) { this.mbrCd = mbrCd; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }

    // Builder 클래스 (선택 사항, 필요하다면 구현)
    public static class Builder {
        private String fileCd;
        private LocalDateTime fileRegYmdt;
        private LocalDateTime fileMdfcnYmdt;
        private String fileExtn;
        private String fileOrgnlNm;
        private String fileSrvrNm;
        private String filePath;
        private String clCd;
        private String mbrCd;
        private Long fileSize;

        public Builder fileCd(String fileCd) { this.fileCd = fileCd; return this; }
        public Builder fileRegYmdt(LocalDateTime fileRegYmdt) { this.fileRegYmdt = fileRegYmdt; return this; }
        public Builder fileMdfcnYmdt(LocalDateTime fileMdfcnYmdt) { this.fileMdfcnYmdt = fileMdfcnYmdt; return this; }
        public Builder fileExtn(String fileExtn) { this.fileExtn = fileExtn; return this; }
        public Builder fileOrgnlNm(String fileOrgnlNm) { this.fileOrgnlNm = fileOrgnlNm; return this; }
        public Builder fileSrvrNm(String fileSrvrNm) { this.fileSrvrNm = fileSrvrNm; return this; }
        public Builder filePath(String filePath) { this.filePath = filePath; return this; }
        public Builder clCd(String clCd) { this.clCd = clCd; return this; }
        public Builder mbrCd(String mbrCd) { this.mbrCd = mbrCd; return this; }
        public Builder fileSize(Long fileSize) { this.fileSize = fileSize; return this; }

        public EnterPortfolioFile build() {
            return new EnterPortfolioFile(this);
        }
    }
}