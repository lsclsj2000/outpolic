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

    // ▼▼▼ 누락되었던 메서드 구현 추가 ▼▼▼
    @Override
    public List<EnterPortfolio> searchPortfoliosByTitle(String query) {
        return portfolioMapper.searchPortfoliosByTitle(query);
    }

    // --- 파일 처리 로직이 포함된 메서드들 (기존과 동일) ---
    @Override
    @Transactional
    public void addPortfolio(EnterPortfolio portfolio, List<String> categoryCodes, String tags, MultipartFile portfolioImage) throws IOException {
        String latestPrtfCd = portfolioMapper.findLatestPrtfCd();
        int nextNum = 1; // 기본값 1
        if (latestPrtfCd != null && latestPrtfCd.startsWith("PO_C")) {
            try {
                nextNum = Integer.parseInt(latestPrtfCd.substring(5)) + 1;
            } catch (NumberFormatException e) {
                logger.warn("Failed to parse latestPrtfCd: {}", latestPrtfCd, e);
            }
        }
        String newPrtfCd = String.format("PO_C%05d", nextNum);
        portfolio.setPrtfCd(newPrtfCd);
        portfolio.setStcCd("SD_ACTIVE"); 
        
        String newClCd = "LIST_" + newPrtfCd;
        portfolioMapper.insertContentList(newClCd, newPrtfCd);

        if (portfolioImage != null && !portfolioImage.isEmpty()) {
            String thumbnailUrl = storeFile(portfolioImage, newClCd, portfolio.getMbrCd(), "portfolio"); // clCd와 mbrCd 전달
            portfolio.setPrtfThumbnailUrl(thumbnailUrl);
            // FileMetaData 객체를 생성하고 DB에 삽입 (insertFileRecord 사용)
            FileMetaData thumbnailMetaData = new FileMetaData.Builder()
                .fileIdx("FILE_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMddHHmmss")) + UUID.randomUUID().toString().substring(0, 8))
                .fileOriginalName(portfolioImage.getOriginalFilename())
                .fileNewName(thumbnailUrl.substring(thumbnailUrl.lastIndexOf('/') + 1))
                .fileExtension(thumbnailUrl.substring(thumbnailUrl.lastIndexOf('.') + 1))
                .filePath(thumbnailUrl)
                .fileSize(portfolioImage.getSize())
                .clCd(newClCd)
                .mbrCd(portfolio.getMbrCd())
                .build();
            portfolioMapper.insertFileRecord(thumbnailMetaData);
        }
        
        portfolioMapper.insertPortfolio(portfolio); // insertPortfolio로 변경
        updateMappings(newClCd, portfolio.getMbrCd(), categoryCodes, tags);
    }

    @Override
    @Transactional
    public void updatePortfolio(EnterPortfolio portfolio, List<String> categoryCodes, String tags, MultipartFile portfolioImage) throws IOException {
        String prtfCd = portfolio.getPrtfCd();
        String clCd = portfolioMapper.findClCdByPrtfCd(prtfCd);
        String originalMbrCd = portfolioMapper.findMbrCdByClCd(clCd);

        if (originalMbrCd == null) throw new IllegalStateException("포트폴리오의 원본 등록자 정보를 찾을 수 없습니다.");
        
        // 기존 파일 삭제 및 새 파일 업로드 처리
        if (portfolioImage != null && !portfolioImage.isEmpty()) {
            // 기존 썸네일 파일 삭제
            EnterPortfolio originalPortfolio = portfolioMapper.findPortfolioDetailsByPrtfCd(prtfCd);
            if (originalPortfolio != null && originalPortfolio.getPrtfThumbnailUrl() != null) {
                deleteFile(originalPortfolio.getPrtfThumbnailUrl());
            }
            // DB에서 기존 파일 메타데이터 삭제
            portfolioMapper.deleteFilesByClCd(clCd); 

            // 새 썸네일 파일 저장
            String newThumbnailUrl = storeFile(portfolioImage, clCd, originalMbrCd, "portfolio"); // clCd와 mbrCd 전달
            portfolio.setPrtfThumbnailUrl(newThumbnailUrl);

            // 새 파일 메타데이터 DB에 삽입
            FileMetaData newThumbnailMetaData = new FileMetaData.Builder()
                .fileIdx("FILE_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMddHHmmss")) + UUID.randomUUID().toString().substring(0, 8))
                .fileOriginalName(portfolioImage.getOriginalFilename())
                .fileNewName(newThumbnailUrl.substring(newThumbnailUrl.lastIndexOf('/') + 1))
                .fileExtension(newThumbnailUrl.substring(newThumbnailUrl.lastIndexOf('.') + 1))
                .filePath(newThumbnailUrl)
                .fileSize(portfolioImage.getSize())
                .clCd(clCd)
                .mbrCd(originalMbrCd)
                .build();
            portfolioMapper.insertFileRecord(newThumbnailMetaData);
        } else {
            // 새 파일이 업로드되지 않았을 경우, 기존 파일 정보를 유지할지 여부에 따라 처리
            // 여기서는 기존 썸네일 URL을 DTO에 다시 설정하여 DB에서 업데이트되지 않도록 함
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
            portfolioMapper.deleteFilesByClCd(clCd); // 연결된 파일 삭제
        }
        portfolioMapper.deletePortfolioByPrtfCd(prtfCd);
    }

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
                    int nextNum = 1; // 기본값 1
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
    
    // storeFile 메서드 수정: FileMetaData에 clCd와 mbrCd를 설정할 수 있도록
    private String storeFile(MultipartFile file, String clCd, String mbrCd, String serviceName) throws IOException {
        if (file == null || file.isEmpty()) return null;
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        Path directoryPath = Paths.get(fileRealPath, "attachment", serviceName, datePath);
        if (!Files.exists(directoryPath)) Files.createDirectories(directoryPath);
        String extension = "";
        String originalFilename = file.getOriginalFilename();
        int dotIndex = originalFilename.lastIndexOf('.');
        if (dotIndex > 0) extension = originalFilename.substring(dotIndex);
        String newFileName = UUID.randomUUID().toString() + extension;
        Path filePath = directoryPath.resolve(newFileName);
        file.transferTo(filePath.toFile());

        // 파일 메타데이터는 여기서 DB에 바로 저장하지 않고, 호출하는 곳에서 FileMetaData 객체를 생성하여 저장
        return "/attachment/" + serviceName + "/" + datePath + "/" + newFileName;
    }

    // deleteFile 메서드 수정: OS 독립적인 경로 처리
    private void deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isBlank()) return;
        String relativePath = fileUrl.replaceFirst("/attachment", "");
        try {
            Path filePath = Paths.get(fileRealPath, relativePath);
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            logger.error("파일 삭제 실패: {}", fileUrl, e);
        }
    }
	@Override
	public List<EnterPortfolio> getLinkedPortfoliosByOsCd(String osCd) {
		return portfolioMapper.findLinkedPortfoliosByOsCd(osCd);
	}

    
    
}