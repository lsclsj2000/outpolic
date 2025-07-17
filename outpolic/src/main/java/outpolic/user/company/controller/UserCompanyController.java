package outpolic.user.company.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import outpolic.user.company.domain.UserCompanyProfileDTO;
import outpolic.user.company.service.UserCompanyService;

@Controller
@RequestMapping("/user/company")
@RequiredArgsConstructor
public class UserCompanyController {

	private final UserCompanyService userCompanyService;
	
	@GetMapping("/{entCd}")
    public String companyProfileView(@PathVariable String entCd, Model model) {
        
        // 1. Service를 호출하여 회사의 기본 프로필 정보(기본 정보 + 카테고리)를 가져옵니다.
        UserCompanyProfileDTO companyProfile = userCompanyService.getCompanyProfile(entCd);

        // 2. 만약 해당 ID의 회사가 없다면, 에러 페이지를 보여줍니다.
        if (companyProfile == null) {
            // "error/404"와 같은 에러 페이지 템플릿이 있다고 가정합니다.
            return "error/404"; 
        }
        
        // 3. View(HTML)로 데이터를 전달하기 위해 Model 객체에 담습니다.
        //    'profile'이라는 이름으로 DTO를, 'entCd'라는 이름으로 ID를 전달합니다.
        model.addAttribute("profile", companyProfile);
        model.addAttribute("companyPageId", entCd); // JavaScript가 API를 호출할 때 사용하기 위함입니다.
        
        // 4. 렌더링할 HTML 파일의 경로를 반환합니다.
        return "user/company/userCompanyProfileView";
    }
}
