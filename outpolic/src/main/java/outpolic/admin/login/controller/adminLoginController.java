package outpolic.admin.login.controller;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import outpolic.admin.login.dto.AdminLoginDTO;
import outpolic.admin.login.mapper.AdminLoginMapper;
import outpolic.admin.login.service.AdminLoginService;
import outpolic.common.domain.Member;

@Controller
@RequestMapping(value = "/admin")
@RequiredArgsConstructor
public class adminLoginController {

	private final AdminLoginService adminLoginService;
	private final AdminLoginMapper adminLoginMapper;

	@GetMapping("")
	public String adminMain(Model model, HttpSession session) {
		String adminName = (String) session.getAttribute("SName");
		model.addAttribute("adminName", adminName); // ✅ 뷰에 넘길 이름
		System.out.println("세션에서 가져온 관리자 이름: " + adminName);
		return "admin/main/adminMainView"; // ✅ 타임리프 뷰 이름
	}

	@GetMapping("/login")
	public String adminLoginView(HttpSession session) {
		String grade = (String) session.getAttribute("SGrd");
		if ("ADMIN".equals(grade)) {
			return "redirect:/admin";
		}

		return "admin/login/adminLoginView";
	}

	@PostMapping("/login")
	public String adminLogin(@RequestParam String memberId, @RequestParam String memberPw, HttpServletRequest request,
			Member member, HttpSession session, Model model, RedirectAttributes redirectAttributes) {
		Map<String, Object> loginResult = adminLoginService.loginAdmin(memberId, memberPw);
		boolean isMatched = (boolean) loginResult.get("isMatched");
		String clientIp = request.getRemoteAddr();
		
		if (isMatched) {
			AdminLoginDTO adminLoginDTO = (AdminLoginDTO) loginResult.get("adminLoginDTO");
			
			if (adminLoginDTO == null || adminLoginDTO.getMemberInfo() == null) {
				model.addAttribute("msg", "로그인 정보가 잘못되었습니다.");
				model.addAttribute("url", "/admin/login");
				return "user/mypage/alert";
			}

			Member memberInfo = adminLoginDTO.getMemberInfo();
			String grade = memberInfo.getGradeCode();
			
			adminLoginMapper.insertAdminLoginHistory(adminLoginMapper.getNextAdminLoginHistoryCode(),
														memberInfo.getMemberCode(), request.getRemoteAddr());
			

			if ("ADMIN".equals(grade)) {

				session.invalidate();

				session = request.getSession(true);
				
				session.setAttribute("SID", memberInfo.getMemberId());
				session.setAttribute("SName", memberInfo.getMemberName());
				session.setAttribute("SGrd", memberInfo.getGradeCode());
				session.setAttribute("SCD", memberInfo.getMemberCode());
				session.setAttribute("SACD", adminLoginDTO.getAdminCode());
				System.out.println("세션 저장 전 이름: " + memberInfo.getMemberName());
				redirectAttributes.addFlashAttribute("success", "로그인에 성공하였습니다");
				
				adminLoginService.updateAdminMemberLoginDate(memberInfo);

				return "redirect:/admin";
			} else {
				model.addAttribute("msg", "권한이 없는 사용자입니다.");
				model.addAttribute("url", "/");
				return "user/mypage/alert";
			}
		} else {
			model.addAttribute("msg", "아이디 혹은 비밀번호가 일치하지 않습니다.");
			model.addAttribute("url", "/admin/login");
			return "user/mypage/alert";
		}

	}

	// 로그아웃
	@GetMapping("/logout")
	public String adminLogout(HttpSession session, Model model) {
		String memberCode = (String) session.getAttribute("SCD");
		if (memberCode != null) {
			String loginHistoryCode = adminLoginService.getAdminLastLoginCode(memberCode);
			adminLoginService.updateAdminLogoutHistory(loginHistoryCode);
			session.invalidate(); // 세션 전체 제거
			model.addAttribute("msg", "로그아웃되었습니다.");
			model.addAttribute("url", "/admin/login");

		} else {
			model.addAttribute("msg", "로그인 상태가 아닙니다.");
			model.addAttribute("url", "/admin/login");
		}
		return "user/mypage/alert";
	}

}
