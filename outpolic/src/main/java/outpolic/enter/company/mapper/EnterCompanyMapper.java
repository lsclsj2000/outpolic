package outpolic.enter.company.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;

import outpolic.enter.company.domain.EnterCompanyOutsourcingDTO;
import outpolic.enter.company.domain.EnterCompanyPortfolioDTO;
import outpolic.enter.company.domain.EnterCompanyProfileDTO;
import outpolic.enter.company.domain.EnterCompanyReviewDTO;

@Mapper
public interface EnterCompanyMapper {

	// 1. 회사 기본 정보 조회
    EnterCompanyProfileDTO getCompanyInfoById(@Param("entCd") String entCd);

    // 2. 회사 카테고리 목록 조회
    List<String> getCompanyCategoriesById(@Param("entCd") String entCd);
    
    // 3. 회사 포트폴리오 목록 조회
    List<EnterCompanyPortfolioDTO> getCompanyPortfoliosById(@Param("entCd") String entCd);

    // 4. 회사 외주 목록 조회
    List<EnterCompanyOutsourcingDTO> getCompanyOutsourcingById(@Param("entCd") String entCd);

    // 5. 회사 리뷰 목록 조회
    List<EnterCompanyReviewDTO> getCompanyReviewsById(@Param("entCd") String entCd);
}
