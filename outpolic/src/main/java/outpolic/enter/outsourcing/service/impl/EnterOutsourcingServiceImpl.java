package outpolic.enter.outsourcing.service.impl;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import outpolic.enter.outsourcing.domain.EnterOutsourcing;
import outpolic.enter.outsourcing.mapper.OutsourcingMapper;
import outpolic.enter.outsourcing.service.EnterOutsourcingService;
import outpolic.enter.portfolio.domain.FileMetaData;
import outpolic.enter.portfolio.service.FileService;

/*
@Service
@RequiredArgsConstructor
public class EnterOutsourcingServiceImpl implements EnterOutsourcingService{
	private final OutsourcingMapper outsourcingMapper;
	private final FileService fileService;
	
	private void setThumbnailFromFiles(EnterOutsourcing o) {
		if(o == null) return ;
		List<FileMetaData> files = o.getFiles();
		if(files != null && !files.isEmpty()) {
			String relativePath = files.get(0).getFilePath();
			o.setOsThumbnailUrl("/file/display"+relativePath);
		}
	}
	
	@Override
	public List<EnterOutsourcing> getOutsourcingListByEntCd(String entCd){
		List<EnterOutsourcing> outsourcingList = outsourcingMapper.findOutsourcingDetailsByEntCd(entCd);
		return outsourcingList;
	}	
	
	@Override
	@Transactional
	public void addOutsourcing(EnterOutsourcing outsourcing, List<MultipartFile> outsourcingFiles,
			List<String> categoryCodes, String tags) throws IOException {
			String latestOsCd = outsourcingMapper.findLastOsCd();
			int nextNum = (latestOsCd == null) ? 1: Integer.parseInt(latestOsCd.substring(4))+1;
			String newOsCd = String.format("OS_C%05d", nextNum);
			outsourcing.setOsCd(newOsCd);
			
			outsourcingMapper.insertOutsourcing(outsourcing);
			
			String newClCd = "LIST_" + newOsCd;
			outsourcingMapper.insertContentList(newClCd,newOsCd);
			
			if(outsourcingFiles != null && !outsourcingFiles.isEmpty()) {
				fileService.addFiles(outsourcingFiles.toArray(new MultipartFile[0]),"outsourcing",newClCd,outsourcing.getMbrCd());
			}
			updateMappings(newClCd,outsourcing.getMbrCd(),categoryCodes,tags);
			
			
		
	
	
	}
	@Override
	@Transactional
	public void updateOutsourcing(EnterOutsourcing outsourcing, List<MultipartFile> outsourcingFiles,List<String> categoryCodes,String tags)
			throws IOException {
		outsourcing.setOsMdfcnYmdt(LocalDateTime.now());
		outsourcingMapper.updateOutsourcing(outsourcing);
		
		String clCd = outsourcingMapper.findClCdByOsCd(outsourcing.getOsCd());
		
		if(outsourcingFiles != null && !outsourcingFiles.isEmpty() && !outsourcingFiles.get(0).isEmpty()) {
			List<FileMetaData> oldFiles = outsourcingMapper.findFilesByOsCd(outsourcing.getOsCd());
			oldFiles.forEach(fileService::deleteFile);
			outsourcingMapper.deleteFileByClCd(clCd);
			
			// 새파일 추가
			fileService.addFiles(outsourcingFiles.toArray(new MultipartFile[0]));
		}
		
		outsourcingMapper.deleteCategoryMappingByClCd(clCd);
		outsourcingMapper.deleteTagMappingByClCd(clCd);
		updateMappings(clCd, outsourcing.getMbrCd(), categoryCodes, tags);
	}
}
	
	
*/
	

