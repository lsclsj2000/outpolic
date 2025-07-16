package outpolic.enter.portfolio.service.impl;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import outpolic.enter.outsourcing.domain.EnterOutsourcing;
import outpolic.enter.portfolio.domain.EnterPortfolio;
import outpolic.enter.portfolio.mapper.PortfolioMapper;
import outpolic.enter.portfolio.service.EnterPortfolioService;
import outpolic.systems.file.domain.FileMetaData;
import outpolic.systems.util.FilesUtils;

@Service
@RequiredArgsConstructor
public class EnterPortfolioServiceImpl implements EnterPortfolioService {

    private static final Logger logger = LoggerFactory.getLogger(EnterPortfolioServiceImpl.class);
    private final PortfolioMapper portfolioMapper;
    private final FilesUtils filesUtils;

    // @Value("${file.path}") // FilesUtils가 해당 값을 사용하므로 여기서 직접 사용하지 않습니다.
    // private String fileRealPath; // 이 필드는 FilesUtils에 이미 존재합니다.

    @Override
    public int countPortfoliosByEntCd(String entCd) { return portfolioMapper.countPortfoliosByEntCd(entCd); }
    @Override
    public List<EnterPortfolio> getPortfolioListByEntCd(String entCd) {
        List<EnterPortfolio> portfolios = portfolioMapper.findPortfolioDetailsByEntCd(entCd);
        return portfolios;
    }
    @Override
    public EnterPortfolio getPortfolioByPrtfCd(String prtfCd) {
        EnterPortfolio portfolio = portfolioMapper.findPortfolioDetailsByPrtfCd(prtfCd);
        return portfolio;
    }
    @Override
    public List<String> searchTags(String query) { return portfolioMapper.searchTagsByName(query); }
    @Override
    public String findEntCdByMbrCd(String mbrCd) { return portfolioMapper.findEntCdByMbrCd(mbrCd); }
    @Override
    public List<EnterOutsourcing> getLinkedOutsourcings(String prtfCd) { return portfolioMapper.findLinkedOutsourcingsByPrtfCd(prtfCd); }
    @Override
    public List<EnterOutsourcing> searchUnlinkedOutsourcings(String prtfCd, String entCd, String query) { return portfolioMapper.searchUnlinkedOutsourcings(prtfCd, entCd, query); }

    @Override
    public List<EnterPortfolio> searchPortfoliosByTitle(String query) {
        return portfolioMapper.searchPortfoliosByTitle(query);
    }

    @Override
    @Transactional
    public void addPortfolio(EnterPortfolio portfolio, List<String> categoryCodes, String tags, MultipartFile portfolioImage) throws IOException {
        String latestPrtfCd = portfolioMapper.findLatestPrtfCd();
        int nextPrtfNum = (latestPrtfCd == null || !latestPrtfCd.startsWith("PO_C")) ? 1 : Integer.parseInt(latestPrtfCd.substring(4)) + 1;
        String newPrtfCd = String.format("PO_C%05d", nextPrtfNum);
        portfolio.setPrtfCd(newPrtfCd);

        portfolio.setStcCd("SD_ACTIVE");

        String latestClCd = portfolioMapper.findLatestClCdForPortfolio();
        int nextClNum = (latestClCd == null || !latestClCd.startsWith("LIST_PO_C")) ? 1 : Integer.parseInt(latestClCd.substring(10)) + 1;
        String newClCd = String.format("LIST_PO_C%05d", nextClNum);

        portfolioMapper.insertContentList(newClCd, newPrtfCd);

        if (portfolioImage != null && !portfolioImage.isEmpty()) {
            // FilesUtils에 전달할 serviceName을 "모듈명/이미지타입" 형태로 구성
            String serviceName = "portfolio"; // 기본 서비스명
            String imageTypeDir = (portfolioImage.getContentType() != null && portfolioImage.getContentType().startsWith("image")) ? "image" : "files";
            String fullServicePath = serviceName + "/" + imageTypeDir; // 예: "portfolio/image" 또는 "portfolio/files"

            FileMetaData thumbnailMetaData = filesUtils.uploadFile(portfolioImage, fullServicePath);
            if (thumbnailMetaData != null) {
                portfolio.setPrtfThumbnailUrl(thumbnailMetaData.getFilePath());
                portfolioMapper.insertFileRecord(thumbnailMetaData, newClCd, portfolio.getMbrCd());
            }
        }

        portfolioMapper.addPortfolio(portfolio);

        updateMappings(categoryCodes, newClCd, portfolio.getMbrCd(), tags);
    }

    @Override
    @Transactional
    public void updatePortfolio(EnterPortfolio portfolio, List<String> categoryCodes, String tags, MultipartFile portfolioImage) throws IOException {
        String prtfCd = portfolio.getPrtfCd();
        String originalMbrCd = portfolioMapper.findMbrCdByPrtfCd(prtfCd);

        if (originalMbrCd == null) {
            throw new IllegalStateException("포트폴리오의 원본 등록자 정보를 찾을 수 없습니다. 포트폴리오 코드(prtfCd)가 잘못되었을 수 있습니다.");
        }

        String clCd = portfolioMapper.findClCdByPrtfCd(prtfCd);

        if (portfolioImage != null && !portfolioImage.isEmpty()) {
            if (clCd != null) {
                List<FileMetaData> existingFiles = portfolioMapper.findFilesByClCd(clCd);
                for (FileMetaData file : existingFiles) {
                    filesUtils.deleteFileByPath(file.getFilePath());
                }
                portfolioMapper.deleteFilesByClCd(clCd);
            }

            // FilesUtils에 전달할 serviceName을 "모듈명/이미지타입" 형태로 구성
            String serviceName = "portfolio";
            String imageTypeDir = (portfolioImage.getContentType() != null && portfolioImage.getContentType().startsWith("image")) ? "image" : "files";
            String fullServicePath = serviceName + "/" + imageTypeDir; // 예: "portfolio/image" 또는 "portfolio/files"

            FileMetaData newThumbnailMetaData = filesUtils.uploadFile(portfolioImage, fullServicePath);
            if (newThumbnailMetaData != null) {
                portfolio.setPrtfThumbnailUrl(newThumbnailMetaData.getFilePath());
                portfolioMapper.insertFileRecord(newThumbnailMetaData, clCd, originalMbrCd);
            }
        }

        portfolio.setPrtfMdfcnYmdt(LocalDateTime.now());
        portfolioMapper.updatePortfolio(portfolio);

        if (clCd != null) {
            portfolioMapper.deleteCategoryMappingByClCd(clCd);
            portfolioMapper.deleteTagMappingByClCd(clCd);
            updateMappings(categoryCodes, clCd, originalMbrCd, tags);
        }
    }

    private void updateMappings(List<String> categoryCodes, String clCd, String mbrCd, String tags) {
        if (categoryCodes != null && !categoryCodes.isEmpty()) {
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
                            logger.warn("태그 코드 파싱 오류: {}", latestTagCd, e);
                        }
                    }
                    tagCd = String.format("T_C%05d", nextNum);
                    portfolioMapper.insertTag(tagCd, trimmedTagName, mbrCd);
                }
                portfolioMapper.insertTagMapping(tagCd, clCd, mbrCd);
            }
        }
    }

    @Override
    @Transactional
    public void deletePortfolio(String prtfCd) {
        String clCd = portfolioMapper.findClCdByPrtfCd(prtfCd);
        if (clCd != null) {
            List<FileMetaData> filesToDelete = portfolioMapper.findFilesByClCd(clCd);
            for (FileMetaData file : filesToDelete) {
                filesUtils.deleteFileByPath(file.getFilePath());
            }
            portfolioMapper.deletePerusalContentByClCd(clCd); // <-- 이 줄을 추가합니다.

            portfolioMapper.deleteCategoryMappingByClCd(clCd);
            portfolioMapper.deleteTagMappingByClCd(clCd);
            portfolioMapper.deleteBookmarkByClCd(clCd);
            portfolioMapper.deleteFilesByClCd(clCd);
            portfolioMapper.deleteOutsourcingContractDetailsByClCd(clCd);

            portfolioMapper.deleteRankingByClCd(clCd);
            portfolioMapper.deleteTodayViewByClCd(clCd);
            portfolioMapper.deleteTotalViewByClCd(clCd);

            portfolioMapper.deleteOutsourcingPortfolioByPrtfCd(prtfCd);

            portfolioMapper.deleteContentListByClCd(clCd);
        }

        portfolioMapper.deletePortfolioByPrtfCd(prtfCd);
    }

    @Override
    public List<EnterOutsourcing> getLinkedOutsourcingsByOsCd(String osCd) {
        return portfolioMapper.findLinkedOutsourcingsByOsCd(osCd);
    }

    @Override
    public List<EnterPortfolio> searchUnlinkedPortfolios(String osCd, String entCd, String query) {
        return portfolioMapper.findUnlinkedPortfolios(osCd, entCd, query);
    }
}