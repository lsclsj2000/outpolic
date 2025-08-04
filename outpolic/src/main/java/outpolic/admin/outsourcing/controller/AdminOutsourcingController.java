package outpolic.admin.outsourcing.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import outpolic.admin.outsourcing.dto.AdminOutsourcingDTO;
import outpolic.admin.outsourcing.dto.AdminOutsourcingSearchDTO;
import outpolic.admin.outsourcing.service.AdminOutsourcingService;
import outpolic.systems.file.domain.FileMetaData;

@Controller
@RequiredArgsConstructor
@RequestMapping(value="/admin")
public class AdminOutsourcingController {
	
	private final AdminOutsourcingService adminOutsourcingService;
	
	@GetMapping("/outsourcingList")
	public String getOutsourcingList(
	    @ModelAttribute AdminOutsourcingSearchDTO searchDTO,
	    Model model, HttpSession session) {
		List<String> permissions = (List<String>) session.getAttribute("SPermissions");
		if (!permissions.contains("CONTENT_ADMIN") && !permissions.contains("SYSTEM_ADMIN")) {
			model.addAttribute("msg", "접근 권한이 없습니다.");
			model.addAttribute("url", "/admin"); // 또는 돌아갈 페이지
			return "admin/login/alert"; // alert.html이라는 공용 alert 페이지
		}
		
	    List<AdminOutsourcingDTO> outsourcingList = adminOutsourcingService.getAllOutsourcings(searchDTO);
	    model.addAttribute("title", "외주 관리");
	    model.addAttribute("outsourcingList", outsourcingList);
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