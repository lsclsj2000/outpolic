package outpolic.user.company.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;

import outpolic.user.company.domain.UserCompanyOutsourcingDTO;
import outpolic.user.company.domain.UserCompanyPortfolioDTO;
import outpolic.user.company.domain.UserCompanyProfileDTO;
import outpolic.user.company.domain.UserCompanyReviewDTO;

@Mapper
public interface UserCompanyMapper {

	// 1. 회사 기본 정보 조회
    UserCompanyProfileDTO getCompanyInfoById(@Param("entCd") String entCd);

    // 2. 회사 카테고리 목록 조회
    List<String> getCompanyCategoriesById(@Param("entCd") String entCd);
    
    // 3. 회사 포트폴리오 목록 조회
    List<UserCompanyPortfolioDTO> getCompanyPortfoliosById(@Param("entCd") String entCd);

    // 4. 회사 외주 목록 조회
    List<UserCompanyOutsourcingDTO> getCompanyOutsourcingById(@Param("entCd") String entCd);

    // 5. 회사 리뷰 목록 조회
    List<UserCompanyReviewDTO> getCompanyReviewsById(@Param("entCd") String entCd);
}
