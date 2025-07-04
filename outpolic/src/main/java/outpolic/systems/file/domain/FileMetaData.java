package outpolic.systems.file.domain;

public class FileMetaData {

	private String fileIdx;
	private String fileOriginalName;
	private String fileNewName;
	private String filePath;
	private String fileExtension;
	private Long fileSize;
	
	public FileMetaData() {}
	
	private FileMetaData(Builder builder) {
		this.fileIdx = builder.fileIdx;
		this.fileOriginalName = builder.fileOriginalName;
		this.fileNewName = builder.fileNewName;
		this.filePath = builder.filePath;
		this.fileExtension = builder.fileExtension;
		this.fileSize = builder.fileSize;
	}
	
	
	public void setFileIdx(String fileIdx) {
		this.fileIdx = fileIdx;
	}


	public void setFileOriginalName(String fileOriginalName) {
		this.fileOriginalName = fileOriginalName;
	}


	public void setFileNewName(String fileNewName) {
		this.fileNewName = fileNewName;
	}


	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public void setFileExtension(String fileExtension) {
		this.fileExtension = fileExtension;
	}

	public void setFileSize(Long fileSize) {
		this.fileSize = fileSize;
	}


	public String getFileIdx() {
		return fileIdx;
	}

	public String getFileOriginalName() {
		return fileOriginalName;
	}

	public String getFileNewName() {
		return fileNewName;
	}

	public String getFilePath() {
		return filePath;
	}
	
	public String getFileExtension() {
		return fileExtension;
	}

	public Long getFileSize() {
		return fileSize;
	}

	public static class Builder {
	
		private String fileIdx;
		private String fileOriginalName;
		private String fileNewName;
		private String filePath;
		private String fileExtension;
		private Long fileSize;
		
		public Builder() {}

		public Builder(FileMetaData fileMetaData) {
			this.fileIdx = fileMetaData.fileIdx;
			this.fileOriginalName = fileMetaData.fileOriginalName;
			this.fileNewName = fileMetaData.fileNewName;
			this.filePath = fileMetaData.filePath;
			this.fileExtension = fileMetaData.fileExtension;
			this.fileSize = fileMetaData.fileSize;
		}

		public Builder fileIdx(String fileIdx) {
			this.fileIdx = fileIdx;
			return this;
		}

		public Builder fileOriginalName(String fileOriginalName) {
			this.fileOriginalName = fileOriginalName;
			return this;
		}

		public Builder fileNewName(String fileNewName) {
			this.fileNewName = fileNewName;
			return this;
		}

		public Builder filePath(String filePath) {
			this.filePath = filePath;
			return this;
		}
		
		public Builder fileExtension(String fileExtension) {
			this.fileExtension = fileExtension;
			return this;
		}

		public Builder fileSize(Long fileSize) {
			this.fileSize = fileSize;
			return this;
		}
		
		public FileMetaData build() {
			return new FileMetaData(this);
		}
	}
}













