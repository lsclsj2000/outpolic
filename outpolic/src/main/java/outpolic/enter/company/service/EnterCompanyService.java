package outpolic.enter.company.service;

import java.util.List;

import outpolic.enter.company.domain.EnterCompanyOutsourcingDTO;
import outpolic.enter.company.domain.EnterCompanyPortfolioDTO;
import outpolic.enter.company.domain.EnterCompanyProfileDTO;
import outpolic.enter.company.domain.EnterCompanyReviewDTO;

public interface EnterCompanyService {

	/**
     * 회사의 기본 프로필 정보(기본 정보 + 카테고리 목록)를 조회합니다.
     * @param entCd 회사 코드
     * @return 회사의 프로필 정보 DTO
     */
    EnterCompanyProfileDTO getCompanyProfile(String entCd);

    /**
     * 특정 회사가 등록한 포트폴리오 목록을 조회합니다.
     * @param entCd 회사 코드
     * @return 포트폴리오 DTO 리스트
     */
    List<EnterCompanyPortfolioDTO> getCompanyPortfolios(String entCd);

    /**
     * 특정 회사가 등록한 외주 목록을 조회합니다.
     * @param entCd 회사 코드
     * @return 외주 DTO 리스트
     */
    List<EnterCompanyOutsourcingDTO> getCompanyOutsourcing(String entCd);

    /**
     * 특정 회사에 대한 리뷰 목록을 조회합니다.
     * @param entCd 회사 코드
     * @return 리뷰 DTO 리스트
     */
    List<EnterCompanyReviewDTO> getCompanyReviews(String entCd);
}
