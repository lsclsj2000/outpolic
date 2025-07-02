package outpolic.enter.portfolio.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import outpolic.enter.portfolio.domain.FileMetaData;

@Mapper
public interface FileMapper {
	int deleteFileByIdx(String fileIdx);
	FileMetaData getFileInfoByIdx(String fileIdx);
	List<FileMetaData> getFileList();
	int addFile(FileMetaData fileDto);
	int addFiles(List<FileMetaData> fileDtoList);
	String findLatestFcCode();
}