package outpolic.user.company.service;

import java.util.List;

import outpolic.user.company.domain.UserCompanyOutsourcingDTO;
import outpolic.user.company.domain.UserCompanyPortfolioDTO;
import outpolic.user.company.domain.UserCompanyProfileDTO;
import outpolic.user.company.domain.UserCompanyReviewDTO;

public interface UserCompanyService {

	/**
     * 회사의 기본 프로필 정보(기본 정보 + 카테고리 목록)를 조회합니다.
     * @param entCd 회사 코드
     * @return 회사의 프로필 정보 DTO
     */
    UserCompanyProfileDTO getCompanyProfile(String entCd);

    /**
     * 특정 회사가 등록한 포트폴리오 목록을 조회합니다.
     * @param entCd 회사 코드
     * @return 포트폴리오 DTO 리스트
     */
    List<UserCompanyPortfolioDTO> getCompanyPortfolios(String entCd);

    /**
     * 특정 회사가 등록한 외주 목록을 조회합니다.
     * @param entCd 회사 코드
     * @return 외주 DTO 리스트
     */
    List<UserCompanyOutsourcingDTO> getCompanyOutsourcing(String entCd);

    /**
     * 특정 회사에 대한 리뷰 목록을 조회합니다.
     * @param entCd 회사 코드
     * @return 리뷰 DTO 리스트
     */
    List<UserCompanyReviewDTO> getCompanyReviews(String entCd);
}
