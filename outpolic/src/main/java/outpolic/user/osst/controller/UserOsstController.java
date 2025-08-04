package outpolic.user.osst.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import outpolic.user.osst.domain.UserOsst;
import outpolic.user.osst.domain.UserOsstRecord;
import outpolic.user.osst.domain.UserStepData;
import outpolic.user.osst.service.UserOsstService;
import outpolic.user.review.dto.UserOutsourcingReviewDTO;
import outpolic.user.review.service.UserOutsourcingReviewService;


@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserOsstController {
	
	private final UserOsstService userOsstService;
	
	private final UserOutsourcingReviewService userOutsourcingReviewService;
	
	@PostMapping("/submitRecord")
	@ResponseBody
	public String submitRecord(@RequestParam("ospCd") String ospCd,
	                           @RequestParam("osrType") String osrType,
	                           @RequestParam("osrTtl") String osrTtl,
	                           @RequestParam("osrCn") String osrCn,
	                           @RequestParam(value = "osrUpCd", required = false) String osrUpCd,
	                           @RequestParam(value = "file", required = false) MultipartFile file,
	                           HttpSession session) {
	    String mbrCd = (String) session.getAttribute("SCD");
	    if (mbrCd == null) return "FAIL: Unauthorized";

	    // 새로운 osr_cd 생성
	    String newCode = userOsstService.generateNewOsrCode();  // 서비스에서 구현해야 함

	    // 파일 처리 로직이 있다면 추가

	    // 저장용 DTO 구성
	    UserOsstRecord record = new UserOsstRecord();
	    record.setOsstRecordCode(newCode);
	    record.setOsstPrograssCode(ospCd);
	    record.setOsstRecordType(osrType.toUpperCase()); // REPORT 또는 FEEDBACK
	    record.setOsstRecordMbrCode(mbrCd);
	    record.setOsstRecordTitle(osrTtl);
	    record.setOsstRecordCn(osrCn);
	    record.setOsstRecordUpCode(osrUpCd);

	    boolean success = userOsstService.insertRecord(record); // insert 로직 서비스/매퍼에 추가

	    return success ? "OK" : "FAIL";
	}
	
	@GetMapping("/userOsstList")
	public String userOutsourcingStatusList(Model model, HttpSession session) {
		// 세션에서 회원 코드 가져오기
		String memberCode = (String) session.getAttribute("SCD");

		// 로그인된 회원 코드가 없으면 (로그인 안 된 상태) 리다이렉트 또는 에러 처리
		if (memberCode == null || memberCode.isBlank()) {
			
			return "redirect:/login";
		}

		// 진행 외주 목록 조회 (회원 코드를 서비스로 전달)
		List<UserOsst> osstList = userOsstService.getUserOsstList(memberCode);
		model.addAttribute("title", "진행 외주 목록");
		model.addAttribute("osstList", osstList);

		return "user/osst/userOutsourcingStatusList";
	}

	@GetMapping("/userOsstDetail")
	public String userOutsourcingStatus(@RequestParam("ocd_cd") String osstDetailCode,
	                                     Model model,
	                                     HttpSession session) {
	    // 세션에서 로그인된 회원 코드 가져오기
	    String memberCode = (String) session.getAttribute("SCD");
	    if (memberCode == null || memberCode.isBlank()) {
	        return "redirect:/login";
	    }

	    // memberCode를 전달하여 역할(memberRole)을 가져올 수 있게 처리
	    UserOsst userOsstDetail = userOsstService.getUserOsstDetail(osstDetailCode, memberCode);
	    List<UserStepData> groupedStepData = userOsstService.getGroupedStepData(osstDetailCode);
	    String memberRole = userOsstDetail.getMemberRole(); // SUPPLIER 또는 DEMANDER

	    // 최근 활동 구성
	    List<UserOsstRecord> recentActivities = new ArrayList<>();
	    for (UserStepData step : groupedStepData) {
	        recentActivities.addAll(step.getReports());
	        recentActivities.addAll(step.getFeedbacks());
	    }
	    recentActivities.sort((a, b) -> b.getOsstRecordRegYmdt().compareTo(a.getOsstRecordRegYmdt()));

	    // 뷰로 전달
	    model.addAttribute("UserOsstDetail", userOsstDetail);
	    model.addAttribute("groupedStepData", groupedStepData);
	    model.addAttribute("recentActivities", recentActivities);
	    model.addAttribute("memberRole", memberRole);

	    return "user/osst/userOutsourcingStatusDetail";
	}
	
	
	
	@PostMapping("/approveStep")
	@ResponseBody
	public String approveStep(@RequestParam("ospCd") String ospCd) {
		// 단계 승인
	    boolean result = userOsstService.approveStep(ospCd);
	    return result ? "OK" : "FAIL";
	}
	
	// 김한별 추가 부분
	
	/**
     * 신규 리뷰를 생성하는 API
     * @param reviewDTO (projectId, rvwEvl, rvwCn 포함)
     * @return 성공 시 "OK", 실패 시 에러 메시지
     */
    @PostMapping("/api/reviews")
    @ResponseBody
    public String submitReview(@RequestBody UserOutsourcingReviewDTO reviewDTO, HttpSession session) {
        String reviewerMbrCd = (String) session.getAttribute("SCD");
        if (reviewerMbrCd == null) {
            return "FAIL_AUTH";
        }
        
        // DTO에 세션 정보(리뷰 작성자) 설정
        reviewDTO.setReviewerMbrCd(reviewerMbrCd);
        
        
        // reviewDTO.getProjectId()가 정상적으로 호출됩니다.
        reviewDTO.setOscId(reviewDTO.getProjectId());

        boolean success = userOutsourcingReviewService.createReview(reviewDTO);
        return success ? "OK" : "FAIL_DB";
    }

    /**
     * 수정할 리뷰의 기존 정보를 조회하는 API
     * @param oscId 외주 계약 ID
     * @return 리뷰 정보 DTO
     */
    @GetMapping("/api/reviews")
    public ResponseEntity<UserOutsourcingReviewDTO> getReviewForEdit(@RequestParam("oscId") String ocdCd, HttpSession session) {
        String mbrCd = (String) session.getAttribute("SCD");
        if (mbrCd == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UserOutsourcingReviewDTO review = userOutsourcingReviewService.getReviewForEdit(ocdCd, mbrCd);
        if (review == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(review);
    }

    /**
     * 기존 리뷰를 수정하는 API
     * @param rvwCd 수정할 리뷰의 PK
     * @param reviewDTO (rating, content 포함)
     * @return 성공 시 "OK", 실패 시 에러 메시지
     */
    @PutMapping("/api/reviews/{rvwCd}")
    @ResponseBody
    public String updateReview(@PathVariable("rvwCd") String rvwCd,
                               @RequestBody UserOutsourcingReviewDTO reviewDTO,
                               HttpSession session) {
        String mbrCd = (String) session.getAttribute("SCD");
        if (mbrCd == null) {
            return "FAIL_AUTH";
        }

        reviewDTO.setRvwCd(rvwCd);
        // TODO: 로그인한 사용자가 실제 리뷰 작성자인지 검증하는 로직 추가 가능
        
        boolean success = userOutsourcingReviewService.updateReview(reviewDTO);
        return success ? "OK" : "FAIL_DB";
    }
    
    // projectId 필드를 받기 위해 UserOutsourcingReviewDTO에 필드를 추가하거나,
    // 아래와 같이 Map으로 받은 후 DTO로 변환할 수도 있습니다.
    // 하지만 일관성을 위해 DTO에 projectId 필드를 추가하는 것을 권장합니다.
    // 여기서는 JS와의 호환성을 위해 DTO에 임시 필드를 추가하는 방식을 제안합니다.
    // UserOutsourcingReviewDTO에 private String projectId; 를 추가해주세요.
}
