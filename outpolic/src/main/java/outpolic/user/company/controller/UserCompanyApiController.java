package outpolic.user.company.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import outpolic.user.company.domain.UserCompanyOutsourcingDTO;
import outpolic.user.company.domain.UserCompanyPortfolioDTO;
import outpolic.user.company.domain.UserCompanyReviewDTO;
import outpolic.user.company.service.UserCompanyService;

@RestController // 이 어노테이션은 모든 메소드의 반환값이 JSON 등 데이터임을 의미합니다.
@RequestMapping("/api/user/company") // API용 URL은 /api/company로 시작합니다.
@RequiredArgsConstructor
public class UserCompanyApiController {

	private final UserCompanyService userCompanyService;

    @GetMapping("/{entCd}/portfolios")
    public List<UserCompanyPortfolioDTO> getPortfolios(@PathVariable String entCd) {
        return userCompanyService.getCompanyPortfolios(entCd);
    }

    @GetMapping("/{entCd}/outsourcing")
    public List<UserCompanyOutsourcingDTO> getOutsourcing(@PathVariable String entCd) {
        return userCompanyService.getCompanyOutsourcing(entCd);
    }

    @GetMapping("/{entCd}/reviews")
    public List<UserCompanyReviewDTO> getReviews(@PathVariable String entCd) {
        return userCompanyService.getCompanyReviews(entCd);
    }
}
