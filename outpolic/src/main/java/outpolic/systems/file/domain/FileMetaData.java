package outpolic.systems.file.domain;

public class FileMetaData {

	private String fileIdx;
	private String fileOriginalName;
	private String fileNewName;
	private String filePath;
	private String fileExtension;
	private Long fileSize;
	private String clCd; // 콘텐츠 목록 코드
    private String mbrCd;  // 등록자 회원 코드
	
	public FileMetaData() {}
	
	// Builder를 통해 객체를 생성하는 생성자
	private FileMetaData(Builder builder) {
		this.fileIdx = builder.fileIdx;
		this.fileOriginalName = builder.fileOriginalName;
		this.fileNewName = builder.fileNewName;
		this.filePath = builder.filePath;
		this.fileExtension = builder.fileExtension;
		this.fileSize = builder.fileSize;
		this.clCd = builder.clCd;     // clCd 초기화 추가
		this.mbrCd = builder.mbrCd;     // mbrCd 초기화 추가
	}
	
	// --- Getter/Setter (전체) ---
	public void setFileIdx(String fileIdx) { this.fileIdx = fileIdx; }
	public String getFileIdx() { return fileIdx; }
    public void setFileOriginalName(String fileOriginalName) { this.fileOriginalName = fileOriginalName; }
	public String getFileOriginalName() { return fileOriginalName; }
    public void setFileNewName(String fileNewName) { this.fileNewName = fileNewName; }
	public String getFileNewName() { return fileNewName; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
	public String getFilePath() { return filePath; }
    public void setFileExtension(String fileExtension) { this.fileExtension = fileExtension; }
	public String getFileExtension() { return fileExtension; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }
	public Long getFileSize() { return fileSize; }
    public void setClCd(String clCd) { this.clCd = clCd; }
    public String getClCd() { return clCd; }
    public void setMbrCd(String mbrCd) { this.mbrCd = mbrCd; }
    public String getMbrCd() { return mbrCd; }


    //--- Builder 클래스 (수정된 부분) ---
	public static class Builder {
	
		private String fileIdx;
		private String fileOriginalName;
		private String fileNewName;
		private String filePath;
		private String fileExtension;
		private Long fileSize;
        // ▼▼▼ 1. Builder 내부에 필드 추가 ▼▼▼
        private String clCd;
        private String mbrCd;
		
		public Builder() {}

		public Builder(FileMetaData fileMetaData) {
			this.fileIdx = fileMetaData.fileIdx;
			this.fileOriginalName = fileMetaData.fileOriginalName;
			this.fileNewName = fileMetaData.fileNewName;
			this.filePath = fileMetaData.filePath;
			this.fileExtension = fileMetaData.fileExtension;
			this.fileSize = fileMetaData.fileSize;
            this.clCd = fileMetaData.clCd;
            this.mbrCd = fileMetaData.mbrCd;
		}

		public Builder fileIdx(String fileIdx) { this.fileIdx = fileIdx; return this; }
		public Builder fileOriginalName(String fileOriginalName) { this.fileOriginalName = fileOriginalName; return this; }
		public Builder fileNewName(String fileNewName) { this.fileNewName = fileNewName; return this; }
		public Builder filePath(String filePath) { this.filePath = filePath; return this; }
		public Builder fileExtension(String fileExtension) { this.fileExtension = fileExtension; return this; }
		public Builder fileSize(Long fileSize) { this.fileSize = fileSize; return this; }
        
        // ▼▼▼ 2. clCd와 mbrCd를 설정하는 메서드 추가 ▼▼▼
        public Builder clCd(String clCd) { this.clCd = clCd; return this; }
        public Builder mbrCd(String mbrCd) { this.mbrCd = mbrCd; return this; }
		
		public FileMetaData build() {
			return new FileMetaData(this);
		}
	}
}
