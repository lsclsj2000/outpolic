package outpolic.enter.company.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import outpolic.enter.company.domain.EnterCompanyOutsourcingDTO;
import outpolic.enter.company.domain.EnterCompanyPortfolioDTO;
import outpolic.enter.company.domain.EnterCompanyProfileDTO;
import outpolic.enter.company.domain.EnterCompanyReviewDTO;
import outpolic.enter.company.mapper.EnterCompanyMapper;
import outpolic.enter.company.service.EnterCompanyService;

@Service
@RequiredArgsConstructor
public class EnterCompanyServiceImpl implements EnterCompanyService{

	// @RequiredArgsConstructor가 final 필드를 자동으로 주입해줍니다.
    private final EnterCompanyMapper enterCompanyMapper;

    /**
     * 회사의 기본 프로필 정보와 카테고리 정보를 조합하는 핵심 로직입니다.
     */
    @Override
    @Transactional(readOnly = true) // 여러 조회 작업이 하나의 논리적 단위이므로 트랜잭션으로 묶습니다.
    public EnterCompanyProfileDTO getCompanyProfile(String entCd) {
        
        // 1. Mapper를 호출하여 기본 정보와 리뷰 요약을 가져옵니다.
        EnterCompanyProfileDTO profile = enterCompanyMapper.getCompanyInfoById(entCd);
        
        // 회사가 존재하지 않으면, 더 이상 진행하지 않고 null을 반환합니다.
        if (profile == null) {
            return null;
        }

        // 2. Mapper를 호출하여 해당 회사의 카테고리 목록을 가져옵니다.
        List<String> categories = enterCompanyMapper.getCompanyCategoriesById(entCd);
        
        // 3. 가져온 카테고리 목록을 기본 정보 DTO에 설정(조합)합니다.
        profile.setCategories(categories);
        
        // 4. 모든 정보가 합쳐진 최종 DTO를 반환합니다.
        return profile;
    }

    /**
     * 포트폴리오 목록 조회는 단순 호출이므로, 바로 Mapper를 호출하여 반환합니다.
     */
    @Override
    public List<EnterCompanyPortfolioDTO> getCompanyPortfolios(String entCd) {
        return enterCompanyMapper.getCompanyPortfoliosById(entCd);
    }

    /**
     * 외주 목록 조회도 단순 호출입니다.
     */
    @Override
    public List<EnterCompanyOutsourcingDTO> getCompanyOutsourcing(String entCd) {
        return enterCompanyMapper.getCompanyOutsourcingById(entCd);
    }

    /**
     * 리뷰 목록 조회도 단순 호출입니다.
     */
    @Override
    public List<EnterCompanyReviewDTO> getCompanyReviews(String entCd) {
        return enterCompanyMapper.getCompanyReviewsById(entCd);
    }
}
