package outpolic.enter.portfolio.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors; // Collectors import 추가

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import outpolic.enter.outsourcing.domain.EnterOutsourcing;
import outpolic.enter.portfolio.domain.EnterPortfolio;
import outpolic.enter.portfolio.mapper.PortfolioMapper;
import outpolic.enter.portfolio.service.EnterPortfolioService;
import outpolic.systems.file.domain.FileMetaData;

@Service
@RequiredArgsConstructor
public class EnterPortfolioServiceImpl implements EnterPortfolioService {

    private static final Logger logger = LoggerFactory.getLogger(EnterPortfolioServiceImpl.class);
    private final PortfolioMapper portfolioMapper;

    @Value("${file.path}")
    private String fileRealPath;

    // --- 기존 메서드들 (수정 없음) ---
    @Override
    public int countPortfoliosByEntCd(String entCd) { return portfolioMapper.countPortfoliosByEntCd(entCd);
    }
    @Override
    public List<EnterPortfolio> getPortfolioListByEntCd(String entCd) { return portfolioMapper.findPortfolioDetailsByEntCd(entCd);
    }
    @Override
    public EnterPortfolio getPortfolioByPrtfCd(String prtfCd) { return portfolioMapper.findPortfolioDetailsByPrtfCd(prtfCd);
    }
    @Override
    public List<String> searchTags(String query) { return portfolioMapper.searchTagsByName(query);
    }
    @Override
    public String findEntCdByMbrCd(String mbrCd) { return portfolioMapper.findEntCdByMbrCd(mbrCd);
    }
    @Override
    public List<EnterOutsourcing> getLinkedOutsourcings(String prtfCd) { return portfolioMapper.findLinkedOutsourcingsByPrtfCd(prtfCd);
    }
    @Override
    public List<EnterOutsourcing> searchUnlinkedOutsourcings(String prtfCd, String entCd, String query) { return portfolioMapper.searchUnlinkedOutsourcings(prtfCd, entCd, query);
    }

    @Override
    public List<EnterPortfolio> searchPortfoliosByTitle(String query) {
        return portfolioMapper.searchPortfoliosByTitle(query);
    }

    // --- 파일 처리 로직이 포함된 메서드들 ---
    @Override
    @Transactional
    public void addPortfolio(EnterPortfolio portfolio, List<String> categoryCodes, String tags, MultipartFile portfolioImage) throws IOException {
        String latestPrtfCd = portfolioMapper.findLatestPrtfCd();
        int nextNum = 1;
        if (latestPrtfCd != null && latestPrtfCd.startsWith("PO_C")) {
            try {
                nextNum = Integer.parseInt(latestPrtfCd.substring(4)) + 1;
            } catch (NumberFormatException e) {
                logger.warn("Failed to parse latestPrtfCd: {}", latestPrtfCd, e);
            }
        }
        String newPrtfCd = String.format("PO_C%05d", nextNum);
        portfolio.setPrtfCd(newPrtfCd);
        portfolio.setStcCd("SD_ACTIVE"); 
        
        String newClCd = "LIST_" + newPrtfCd;
        portfolioMapper.insertContentList(newClCd, newPrtfCd);

        // 썸네일 이미지 처리
        if (portfolioImage != null && !portfolioImage.isEmpty()) {
            // 파일을 물리적으로 저장하고, FileMetaData 객체를 받습니다.
            FileMetaData thumbnailMetaData = storeFile(portfolioImage, newClCd, portfolio.getMbrCd(), "portfolio");
            if (thumbnailMetaData != null) {
                portfolio.setPrtfThumbnailUrl(thumbnailMetaData.getFilePath()); // DTO에 웹 접근 경로 설정
                portfolioMapper.insertFileRecord(thumbnailMetaData); // FileMetaData 객체 DB에 삽입
            }
        }
        
        portfolioMapper.addPortfolio(portfolio); 
        updateMappings(newClCd, portfolio.getMbrCd(), categoryCodes, tags);
    }

    @Override
    @Transactional
    public void updatePortfolio(EnterPortfolio portfolio, List<String> categoryCodes, String tags, MultipartFile portfolioImage) throws IOException {
        String prtfCd = portfolio.getPrtfCd();
        String clCd = portfolioMapper.findClCdByPrtfCd(prtfCd);
        String originalMbrCd = portfolioMapper.findMbrCdByClCd(clCd);

        if (originalMbrCd == null) {
            throw new IllegalStateException("포트폴리오의 원본 등록자 정보를 찾을 수 없습니다.");
        }
        
        // 썸네일 이미지 업데이트 처리
        if (portfolioImage != null && !portfolioImage.isEmpty()) {
            // 기존 썸네일 파일 삭제 (물리적 + DB 메타데이터)
            EnterPortfolio originalPortfolio = portfolioMapper.findPortfolioDetailsByPrtfCd(prtfCd);
            if (originalPortfolio != null && originalPortfolio.getPrtfThumbnailUrl() != null) {
                deleteFile(originalPortfolio.getPrtfThumbnailUrl());
            }
            portfolioMapper.deleteFilesByClCd(clCd);
            // 새 썸네일 파일 저장 및 메타데이터 DB에 삽입
            FileMetaData newThumbnailMetaData = storeFile(portfolioImage, clCd, originalMbrCd, "portfolio");
            if (newThumbnailMetaData != null) {
                portfolio.setPrtfThumbnailUrl(newThumbnailMetaData.getFilePath());
                portfolioMapper.insertFileRecord(newThumbnailMetaData);
            }
        } else {
            // 새 파일이 업로드되지 않았다면, 기존 썸네일 URL을 유지 (DB 업데이트 시 null로 덮어쓰지 않도록)
            EnterPortfolio currentPortfolio = portfolioMapper.findPortfolioDetailsByPrtfCd(prtfCd);
            if (currentPortfolio != null) {
                portfolio.setPrtfThumbnailUrl(currentPortfolio.getPrtfThumbnailUrl());
            }
        }

        portfolio.setPrtfMdfcnYmdt(LocalDateTime.now());
        portfolioMapper.updatePortfolio(portfolio);

        portfolioMapper.deleteCategoryMappingByClCd(clCd);
        portfolioMapper.deleteTagMappingByClCd(clCd);
        updateMappings(clCd, originalMbrCd, categoryCodes, tags);
    }
    
    @Override
    @Transactional
    public void deletePortfolio(String prtfCd) {
        EnterPortfolio portfolioToDelete = portfolioMapper.findPortfolioDetailsByPrtfCd(prtfCd);
        if (portfolioToDelete != null && portfolioToDelete.getPrtfThumbnailUrl() != null) {
            deleteFile(portfolioToDelete.getPrtfThumbnailUrl());
        }
        
        String clCd = portfolioMapper.findClCdByPrtfCd(prtfCd);
        if (clCd != null) {
            portfolioMapper.deleteCategoryMappingByClCd(clCd);
            portfolioMapper.deleteTagMappingByClCd(clCd);
            portfolioMapper.deleteBookmarkByClCd(clCd);
            portfolioMapper.deleteOutsourcingPortfolioByPrtfCd(prtfCd);
            portfolioMapper.deleteContentListByClCd(clCd);
            portfolioMapper.deleteFilesByClCd(clCd);
        }
        portfolioMapper.deletePortfolioByPrtfCd(prtfCd);
    }

    // --- Private Helper Methods ---
    private void updateMappings(String clCd, String mbrCd, List<String> categoryCodes, String tags) {
        if (categoryCodes != null) {
            for (String ctgryId : categoryCodes) {
                if (ctgryId != null && !ctgryId.trim().isEmpty()) {
                    portfolioMapper.insertCategoryMapping(ctgryId, clCd, mbrCd);
                }
            }
        }
        
        if (tags != null && !tags.trim().isEmpty()) {
            for (String tagName : tags.split(",")) {
                String trimmedTagName = tagName.trim();
                if (trimmedTagName.isEmpty()) continue;
                String tagCd = portfolioMapper.findTagCdByName(trimmedTagName);
                if (tagCd == null) {
                    String latestTagCd = portfolioMapper.findLatestTagCd();
                    int nextNum = 1;
                    if (latestTagCd != null && latestTagCd.startsWith("T_C")) {
                        try {
                            nextNum = Integer.parseInt(latestTagCd.substring(4)) + 1;
                        } catch (NumberFormatException e) {
                            logger.warn("Failed to parse latestTagCd: {}", latestTagCd, e);
                        }
                    }
                    tagCd = String.format("T_C%05d", nextNum);
                    portfolioMapper.insertTag(tagCd, trimmedTagName, mbrCd);
                }
                portfolioMapper.insertTagMapping(tagCd, clCd, mbrCd);
            }
        }
    }

    /**
     * 파일을 물리적으로 저장하고, FileMetaData 객체를 반환합니다.
     * 이 메서드는 파일의 물리적 저장과 메타데이터 생성을 담당합니다.
     * @param file 업로드할 MultipartFile
     * @param clCd 콘텐츠 목록 코드 (FileMetaData에 저장될)
     * @param mbrCd 등록자 회원 코드 (FileMetaData에 저장될)
     * @param serviceName 서비스 구분 (예: "portfolio", "outsourcing")
     * @return 생성된 FileMetaData 객체
     * @throws IOException 파일 저장 중 발생할 수 있는 예외
     */
    private FileMetaData storeFile(MultipartFile file, String clCd, String mbrCd, String serviceName) throws IOException {
       
        if (file == null || file.isEmpty()) return null;

        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        
        Path directoryPath = Paths.get(fileRealPath.trim(), "attachment", serviceName, datePath);
        if (!Files.exists(directoryPath)) {
            Files.createDirectories(directoryPath);
        }
        
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        int dotIndex = originalFilename.lastIndexOf('.');
        if (dotIndex > 0) extension = originalFilename.substring(dotIndex);
        String newFileName = UUID.randomUUID().toString() + extension;
        
        Path filePath = directoryPath.resolve(newFileName); 
        
        file.transferTo(filePath.toFile());
        String webAccessPath = "/" + Paths.get("attachment", serviceName, datePath, newFileName).toString().replace("\\", "/");
        
        String fileIdx = "FILE_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMddHHmmss")) + UUID.randomUUID().toString().substring(0, 8);
        return new FileMetaData.Builder()
                .fileIdx(fileIdx)
                .fileOriginalName(originalFilename)
                .fileNewName(newFileName) 
                .fileExtension(extension.replace(".", ""))
                .filePath(webAccessPath)
                
                .fileSize(file.getSize())
                .clCd(clCd)
                .mbrCd(mbrCd)
                .build();
    }

    private String deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isBlank()) return null;
        String relativePathInStorage = fileUrl.replaceFirst("/", "").replace("\\", "/");

        Path filePathToDelete = Paths.get(fileRealPath.trim(), relativePathInStorage);
        try {
            Files.deleteIfExists(filePathToDelete);
            logger.info("파일 삭제 성공: {}", filePathToDelete);
            return "SUCCESS";
        } catch (IOException e) {
            logger.error("파일 삭제 실패: {}", filePathToDelete, e);
            return "FAILURE";
        }
    }
	@Override
	public List<EnterPortfolio> getLinkedOutsourcingsByOsCd(String osCd) {
		// TODO Auto-generated method stub
		return null;
	}
}