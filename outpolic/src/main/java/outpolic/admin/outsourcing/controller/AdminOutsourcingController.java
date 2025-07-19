package outpolic.admin.outsourcing.controller;

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
import outpolic.admin.outsourcing.dto.AdminOutsourcingDTO;
import outpolic.admin.outsourcing.dto.AdminOutsourcingSearchDTO; // 검색 DTO 임포트
import outpolic.admin.outsourcing.service.AdminOutsourcingService;
import outpolic.systems.file.domain.FileMetaData;

@Controller
@RequiredArgsConstructor
@RequestMapping(value="/admin")
public class AdminOutsourcingController {
	
	private final AdminOutsourcingService adminOutsourcingService;
	
	@GetMapping("/outsourcingList")
	// AdminOutsourcingSearchDTO를 ModelAttribute로 받음
	public String getOutsourcingList(
	    @ModelAttribute AdminOutsourcingSearchDTO searchDTO, // 검색 DTO를 파라미터로 추가
	    Model model) {
	    
	    List<AdminOutsourcingDTO> outsourcingList = adminOutsourcingService.getAllOutsourcings(searchDTO); // 검색 DTO 전달
	    model.addAttribute("title", "외주 관리");
	    model.addAttribute("outsourcingList", outsourcingList);
	    // 검색 조건을 뷰로 다시 전달하여 필터 상태를 유지 (restoreSearchConditions 함수가 사용)
	    model.addAttribute("searchParams", searchDTO); 
	    return "admin/outsourcing/outsourcingList";
	}

    @DeleteMapping("/outsourcing/delete/{osCd}")
    @ResponseBody
    public ResponseEntity<?> deleteOutsourcing(@PathVariable String osCd) {
        try {
            adminOutsourcingService.deleteOutsourcing(osCd);
            return ResponseEntity.ok(java.util.Map.of("success", true, "message", "외주 삭제 요청이 접수되었습니다."));
        } catch (Exception e) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(java.util.Map.of("success", false, "message", "외주 삭제 중 오류가 발생했습니다."));
        }
    }

    @GetMapping("/outsourcing/{osCd}/files")
    @ResponseBody
    public ResponseEntity<List<FileMetaData>> getOutsourcingFiles(@PathVariable String osCd) {
        List<FileMetaData> files = adminOutsourcingService.getOutsourcingFiles(osCd);
        return ResponseEntity.ok(files);
    }
}