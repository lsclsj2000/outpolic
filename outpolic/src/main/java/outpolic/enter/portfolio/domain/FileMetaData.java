package outpolic.enter.portfolio.domain;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FileMetaData {
    private String fileIdx;
    private LocalDateTime fileRegYmdt;
    private LocalDateTime fileMdfcnYmdt;
    private String fileExtn;
    private String fileOriginalName;
    private String fileNewName;
    private String filePath;
    private String clCd;
    private String mbrCd;
}