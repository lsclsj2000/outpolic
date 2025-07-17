package outpolic.enter.company.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import outpolic.enter.company.domain.EnterCompanyOutsourcingDTO;
import outpolic.enter.company.domain.EnterCompanyPortfolioDTO;
import outpolic.enter.company.domain.EnterCompanyReviewDTO;
import outpolic.enter.company.service.EnterCompanyService;

@RestController // 이 어노테이션은 모든 메소드의 반환값이 JSON 등 데이터임을 의미합니다.
@RequestMapping("/api/enter/company") // API용 URL은 /api/company로 시작합니다.
@RequiredArgsConstructor
public class EnterCompanyApiController {

	private final EnterCompanyService enterCompanyService;

    @GetMapping("/{entCd}/portfolios")
    public List<EnterCompanyPortfolioDTO> getPortfolios(@PathVariable String entCd) {
        return enterCompanyService.getCompanyPortfolios(entCd);
    }

    @GetMapping("/{entCd}/outsourcing")
    public List<EnterCompanyOutsourcingDTO> getOutsourcing(@PathVariable String entCd) {
        return enterCompanyService.getCompanyOutsourcing(entCd);
    }

    @GetMapping("/{entCd}/reviews")
    public List<EnterCompanyReviewDTO> getReviews(@PathVariable String entCd) {
        return enterCompanyService.getCompanyReviews(entCd);
    }
}
