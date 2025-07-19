package outpolic.admin.portfolio.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute; // ModelAttribute 임포트
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.RequiredArgsConstructor;
import outpolic.admin.portfolio.dto.AdminPortfolioDTO;
import outpolic.admin.portfolio.dto.AdminPortfolioSearchDTO; // 검색 DTO 임포트
import outpolic.admin.portfolio.service.AdminPortfolioService;
import outpolic.systems.file.domain.FileMetaData;

@Controller
@RequiredArgsConstructor
@RequestMapping(value="/admin")
public class AdminPortfolioController {
	
	private final AdminPortfolioService adminPortfolioService;
	
	@GetMapping("/portfolioList")
	public String adminPortfolioListView(
	    @ModelAttribute AdminPortfolioSearchDTO searchDTO, // 검색 DTO 파라미터 추가
	    Model model) {
	    
		List<AdminPortfolioDTO> portfolioList = adminPortfolioService.getAllPortfolios(searchDTO); // 검색 DTO 전달
		model.addAttribute("title", "포트폴리오 관리");
		model.addAttribute("portfolioList", portfolioList);
		model.addAttribute("searchParams", searchDTO); // 검색 조건 유지를 위해 뷰로 전달
		return "admin/portfolio/adminPortfolioListView";
	}

    // 포트폴리오 첨부 파일 조회 API
    @GetMapping("/portfolio/{prtfCd}/files")
    @ResponseBody
    public ResponseEntity<List<FileMetaData>> getPortfolioFiles(@PathVariable String prtfCd) {
        List<FileMetaData> files = adminPortfolioService.getPortfolioFiles(prtfCd);
        return ResponseEntity.ok(files);
    }

    // 포트폴리오 삭제 API
    @DeleteMapping("/portfolio/delete/{prtfCd}")
    @ResponseBody
    public ResponseEntity<?> deletePortfolio(@PathVariable String prtfCd) {
        try {
            adminPortfolioService.deletePortfolio(prtfCd);
            return ResponseEntity.ok(java.util.Map.of("success", true, "message", "포트폴리오 삭제 요청이 접수되었습니다."));
        } catch (Exception e) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(java.util.Map.of("success", false, "message", "포트폴리오 삭제 중 오류가 발생했습니다."));
        }
    }
}