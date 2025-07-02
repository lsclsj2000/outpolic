package outpolic.enter.portfolio.service.impl;

import outpolic.enter.portfolio.domain.EnterPortfolio;
import outpolic.enter.portfolio.mapper.PortfolioMapper;
import outpolic.enter.portfolio.service.EnterPortfolioService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EnterPortfolioServiceImpl implements EnterPortfolioService {
    private final PortfolioMapper portfolioMapper;

    @Value("${file.upload.path}")
    private String uploadPath;

    @Override
    public List<EnterPortfolio> getPortfolioListByEntCd(String entCd) {
        List<EnterPortfolio> portfolioList = portfolioMapper.findPortfolioDetailsByEntCd(entCd);
        portfolioList.forEach(this::unpackThumbnailUrl);
        return portfolioList;
    }

    @Override
    public EnterPortfolio getPortfolioByPrtfCd(String prtfCd) {
        EnterPortfolio portfolio = portfolioMapper.findPortfolioDetailsByPrtfCd(prtfCd);
        unpackThumbnailUrl(portfolio);
        return portfolio;
    }

    @Override
    @Transactional
    public void addPortfolio(EnterPortfolio portfolio, List<MultipartFile> portfolioFiles, List<String> categoryCodes, String tags) throws IOException {
        String thumbnailUrl = uploadAndGetThumbnailUrl(portfolioFiles);
        if (thumbnailUrl != null) {
            portfolio.setPrtfCn("<THUMBNAIL>" + thumbnailUrl + "</THUMBNAIL>" + portfolio.getPrtfCn());
        }
        
        String latestPrtfCd = portfolioMapper.findLatestPrtfCd();
        int nextNum = (latestPrtfCd == null) ? 1 : Integer.parseInt(latestPrtfCd.substring(4)) + 1;
        String newPrtfCd = String.format("PO_C%05d", nextNum);
        portfolio.setPrtfCd(newPrtfCd);
        
        portfolioMapper.insertPortfolio(portfolio);

        String newClCd = "LIST_" + newPrtfCd;
        portfolioMapper.insertContentList(newClCd, newPrtfCd);
        updateMappings(newClCd, portfolio.getMbrCd(), categoryCodes, tags);
    }

    @Override
    @Transactional
    public void updatePortfolio(EnterPortfolio portfolio, List<MultipartFile> portfolioFiles, List<String> categoryCodes, String tags) throws IOException {
        String thumbnailUrl = uploadAndGetThumbnailUrl(portfolioFiles);
        EnterPortfolio originalPortfolio = getPortfolioByPrtfCd(portfolio.getPrtfCd());
        String finalContent = portfolio.getPrtfCn();

        if (thumbnailUrl != null) { // 새 썸네일이 있으면 교체
            finalContent = "<THUMBNAIL>" + thumbnailUrl + "</THUMBNAIL>" + portfolio.getPrtfCn();
        } else if (originalPortfolio.getPrtfThumbnailUrl() != null) { // 기존 썸네일 유지
            finalContent = "<THUMBNAIL>" + originalPortfolio.getPrtfThumbnailUrl() + "</THUMBNAIL>" + portfolio.getPrtfCn();
        }
        portfolio.setPrtfCn(finalContent);
        portfolio.setPrtfMdfcnYmdt(LocalDateTime.now());
        portfolioMapper.updatePortfolio(portfolio);

        String clCd = portfolioMapper.findClCdByPrtfCd(portfolio.getPrtfCd());
        portfolioMapper.deleteCategoryMappingByClCd(clCd);
        portfolioMapper.deleteTagMappingByClCd(clCd);
        updateMappings(clCd, portfolio.getMbrCd(), categoryCodes, tags);
    }

    @Override
    @Transactional
    public void deletePortfolio(String prtfCd) {
        String clCd = portfolioMapper.findClCdByPrtfCd(prtfCd);

        if (clCd != null) {
            // --- 1단계: content_list와 관련된 모든 데이터 삭제 ---
            portfolioMapper.deleteOutsourcingStatusByClCd(clCd);
            portfolioMapper.deleteFileByClCd(clCd);
            portfolioMapper.deleteCategoryMappingByClCd(clCd);
            portfolioMapper.deleteTagMappingByClCd(clCd);
            portfolioMapper.deleteBookmarkByClCd(clCd);
            portfolioMapper.deleteOutsourcingContractDetailsByClCd(clCd);
            portfolioMapper.deleteTotalViewByClCd(clCd);
            portfolioMapper.deleteRankingByClCd(clCd);
            portfolioMapper.deleteContentListByClCd(clCd);
        }

        // --- 2단계: portfolio와 관련된 모든 데이터 삭제 ---
        portfolioMapper.deleteOutsourcingPortfolioByPrtfCd(prtfCd); // ★★★ 이 라인을 새로 추가 ★★★

        // --- 3단계: 최종 원본 데이터 삭제 ---
        portfolioMapper.deletePortfolioByPrtfCd(prtfCd); // 이제 이 코드가 성공적으로 실행됩니다.
    }
    private void updateMappings(String clCd, String mbrCd, List<String> categoryCodes, String tags) {
        if (categoryCodes != null) {
            for (String ctgryCd : categoryCodes) {
                if (ctgryCd != null && !ctgryCd.trim().isEmpty()) {
                    portfolioMapper.insertCategoryMapping(ctgryCd, clCd, mbrCd);
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
                    int nextNum = (latestTagCd == null) ? 1 : Integer.parseInt(latestTagCd.substring(4)) + 1;
                    tagCd = String.format("T_C%05d", nextNum);
                    portfolioMapper.insertTag(tagCd, trimmedTagName, mbrCd);
                }
                portfolioMapper.insertTagMapping(tagCd, clCd, mbrCd);
            }
        }
    }

    private String uploadAndGetThumbnailUrl(List<MultipartFile> files) throws IOException {
        if (files == null || files.isEmpty() || files.get(0).isEmpty()) return null;
        MultipartFile firstFile = files.get(0);
        String serverFilename = UUID.randomUUID().toString() + "_" + firstFile.getOriginalFilename();
        File dest = new File(uploadPath + File.separator + serverFilename);
        if (!dest.getParentFile().exists()) {
            dest.getParentFile().mkdirs();
        }
        firstFile.transferTo(dest);
        return "/uploads/" + serverFilename;
    }

    private void unpackThumbnailUrl(EnterPortfolio p) {
        if (p == null || p.getPrtfCn() == null) return;
        String combinedContent = p.getPrtfCn();
        if (combinedContent.startsWith("<THUMBNAIL>")) {
            int endIndex = combinedContent.indexOf("</THUMBNAIL>");
            if (endIndex != -1) {
                p.setPrtfThumbnailUrl(combinedContent.substring(11, endIndex));
                p.setPrtfCn(combinedContent.substring(endIndex + 12));
            }
        }
    }
}