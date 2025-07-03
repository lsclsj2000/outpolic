package outpolic.enter.portfolio.service.impl;

import outpolic.enter.portfolio.domain.EnterPortfolio;
import outpolic.enter.portfolio.domain.FileMetaData;
import outpolic.enter.portfolio.mapper.PortfolioMapper;
import outpolic.enter.portfolio.service.EnterPortfolioService;
import outpolic.enter.portfolio.service.FileService;
import outpolic.enter.portfolio.util.FilesUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EnterPortfolioServiceImpl implements EnterPortfolioService {
    
    private final PortfolioMapper portfolioMapper;
    private final FileService fileService;

    private void setThumbnailFromFiles(EnterPortfolio p) {
        if (p == null) return;
        List<FileMetaData> files = p.getFiles();
        if (files != null && !files.isEmpty()) {
            String relativePath = files.get(0).getFilePath();
            p.setPrtfThumbnailUrl("/file/display" + relativePath);
        }
    }
    
    @Override
    public List<EnterPortfolio> getPortfolioListByEntCd(String entCd) {
        List<EnterPortfolio> portfolioList = portfolioMapper.findPortfolioDetailsByEntCd(entCd);
        portfolioList.forEach(this::setThumbnailFromFiles);
        return portfolioList;
    }
    
    @Override
    public EnterPortfolio getPortfolioByPrtfCd(String prtfCd) {
        EnterPortfolio portfolio = portfolioMapper.findPortfolioDetailsByPrtfCd(prtfCd);
        setThumbnailFromFiles(portfolio);
        return portfolio;
    }

    @Override
    @Transactional
    public void addPortfolio(EnterPortfolio portfolio, List<MultipartFile> portfolioFiles, List<String> categoryCodes, String tags) throws IOException {
        String latestPrtfCd = portfolioMapper.findLatestPrtfCd();
        int nextNum = (latestPrtfCd == null) ? 1 : Integer.parseInt(latestPrtfCd.substring(4)) + 1;
        String newPrtfCd = String.format("PO_C%05d", nextNum);
        portfolio.setPrtfCd(newPrtfCd);
        
        portfolioMapper.insertPortfolio(portfolio);

        String newClCd = "LIST_" + newPrtfCd;
        portfolioMapper.insertContentList(newClCd, newPrtfCd);
        
        if (portfolioFiles != null && !portfolioFiles.isEmpty()) {
            fileService.addFiles(portfolioFiles.toArray(new MultipartFile[0]), "portfolio", newClCd, portfolio.getMbrCd());
        }
        
        updateMappings(newClCd, portfolio.getMbrCd(), categoryCodes, tags);
    }
    
    @Override
    @Transactional
    public void updatePortfolio(EnterPortfolio portfolio, List<MultipartFile> portfolioFiles, List<String> categoryCodes, String tags) throws IOException {
        portfolio.setPrtfMdfcnYmdt(LocalDateTime.now());
        portfolioMapper.updatePortfolio(portfolio);

        String clCd = portfolioMapper.findClCdByPrtfCd(portfolio.getPrtfCd());
        
        // 기존 파일 삭제 및 새 파일 추가 로직 (필요 시 구현)
        if (portfolioFiles != null && !portfolioFiles.isEmpty() && !portfolioFiles.get(0).isEmpty()) {
            // 기존 파일 DB 및 실제 파일 삭제
            List<FileMetaData> oldFiles = portfolioMapper.findFilesByPrtfCd(portfolio.getPrtfCd());
            oldFiles.forEach(fileService::deleteFile);
            portfolioMapper.deleteFileByClCd(clCd);
            
            // 새 파일 추가
            fileService.addFiles(portfolioFiles.toArray(new MultipartFile[0]), "portfolio", clCd, portfolio.getMbrCd());
        }

        portfolioMapper.deleteCategoryMappingByClCd(clCd);
        portfolioMapper.deleteTagMappingByClCd(clCd);
        updateMappings(clCd, portfolio.getMbrCd(), categoryCodes, tags);
    }

    @Override
    @Transactional
    public void deletePortfolio(String prtfCd) {
        String clCd = portfolioMapper.findClCdByPrtfCd(prtfCd);
        if (clCd != null) {
            List<FileMetaData> filesToDelete = portfolioMapper.findFilesByPrtfCd(prtfCd);
            filesToDelete.forEach(fileService::deleteFile);
            portfolioMapper.deleteFileByClCd(clCd);
            portfolioMapper.deleteCategoryMappingByClCd(clCd);
            portfolioMapper.deleteTagMappingByClCd(clCd);
            portfolioMapper.deleteContentListByClCd(clCd);
        }
        portfolioMapper.deletePortfolioByPrtfCd(prtfCd);
    }
    
    private void updateMappings(String clCd, String mbrCd, List<String> categoryCodes, String tags) {
        if (categoryCodes != null) {
            for (String ctgryCd : categoryCodes) {
                if(ctgryCd != null && !ctgryCd.trim().isEmpty()) portfolioMapper.insertCategoryMapping(ctgryCd, clCd, mbrCd);
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
}