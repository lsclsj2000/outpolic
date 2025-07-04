package outpolic.enter.outsourcing.service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import outpolic.enter.outsourcing.domain.EnterOutsourcing;

public interface EnterOutsourcingService {
	
	List<EnterOutsourcing> getOutsourcingListByEntCd(String entCd);
	void addOutsourcing(EnterOutsourcing outsourcing, List<MultipartFile> outsourcingFiles,
			List<String> categoryCodes, String tags) throws IOException;
	void deleteOutsourcing(String osCd);
	EnterOutsourcing getOutsourcingByOscd(String osCd);
	void updateOutsourcing(EnterOutsourcing outsourcing, List<MultipartFile> outsourcingFiles,
			List<String> categoryCodes,String tags) throws IOException; 
	
}
