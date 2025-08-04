package outpolic.admin.announcement.service.Impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import outpolic.admin.announcement.domain.AdminAnn;
import outpolic.admin.announcement.domain.AdminAnnFile;
import outpolic.admin.announcement.mapper.AdminAnnMapper;
import outpolic.admin.announcement.service.AdminAnnService;
import outpolic.systems.file.domain.FileMetaData;
import outpolic.systems.util.FilesUtils;
import outpolic.user.inquiry.domain.UserInquiryFile;



@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AdminAnnServiceImpl implements AdminAnnService {
	
	private final FilesUtils filesUtils;
	private final AdminAnnMapper adminAnnMapper;

	@Override
	public List<AdminAnn> getAdminAnnList() {
		// 공지사항 내역 조회
		List<AdminAnn> adminAnnList = adminAnnMapper.getAdminAnnList();
		if(adminAnnList != null) {			
			adminAnnList.forEach(adminAnn -> {
				String content = adminAnn.getAnnCn();
		    	if(content != null) {
		    		content = content.replaceAll("<[^>]+>", "");
		    		adminAnn.setAnnCn(content);
		    	}
			});
		}
		
		return adminAnnList;
	}
	
	@Override
	public AdminAnn getAdminAnnDetail(String annCode) {
	    AdminAnn ann = adminAnnMapper.getAdminAnnDetail(annCode);
	    
	    if(ann != null) {
	    	String content = ann.getAnnCn();
	    	if(content != null) {
	    		content = content.replaceAll("<[^>]+>", "");
	    		ann.setAnnCn(content);
	    	}
	    	// 첨부파일 리스트 별도로 조회
	    	List<AdminAnnFile> files = adminAnnMapper.getAdminAnnFileListByReferCd(annCode);
	    	ann.setAdminAnnFiles(files);
	    }


	    return ann;
	}
	
	@Override
	public int updateAdminAnn(AdminAnn adminAnn) {
		// 공지사항 수정 저장
	    return adminAnnMapper.updateAdminAnn(adminAnn);
	}
	
	@Override
	public List<AdminAnn> getAdminAnnListFiltered(Map<String, Object> paramMap) {
		// 공지사항 내역 조회 - 필터
		List<AdminAnn> adminAnnList = adminAnnMapper.getAdminAnnListFiltered(paramMap);
		if(adminAnnList != null) {			
			adminAnnList.forEach(adminAnn -> {
				String content = adminAnn.getAnnCn();
		    	if(content != null) {
		    		content = content.replaceAll("<[^>]+>", "");
		    		adminAnn.setAnnCn(content);
		    	}
			});
		}
		
	    return adminAnnList;
	}
	
	@Override
	public void addAdminAnnWrite(AdminAnn ann, MultipartFile[] attachmentFile) {
		
		// 문의 등록
		adminAnnMapper.addAdminAnnWrite(ann);
		
		log.info("문의등록 후 : {}", ann);
		
		if(attachmentFile != null && !attachmentFile[0].isEmpty()) {
			var fileList = filesUtils.uploadFiles(attachmentFile, "announcement");
			
			List<AdminAnnFile> adminAnnFileList = new ArrayList<>();
			AdminAnnFile adminAnnFile = null;
			for(FileMetaData file : fileList) {
				adminAnnFile = new AdminAnnFile.Builder()
													 .saCode(file.getFileIdx())
													 .saReferCode(ann.getAnnCode())
													 .saOrgnlName(file.getFileOriginalName())
													 .saSrvrName(file.getFileNewName())
													 .saPath(file.getFilePath())
													 .saExtn(file.getFileExtension())
													 .saSize(file.getFileSize())
													 .mbrCode(ann.getAnnAdmCode())
													 .build();
				adminAnnFileList.add(adminAnnFile);
			}
			
			adminAnnMapper.addAdminAnnFiles(adminAnnFileList);			
		}
		
	}


}
