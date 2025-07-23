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
	
	@GetMapping("/api/review")
    public ResponseEntity<UserOutsourcingReviewDTO> getReviewForEdit(@RequestParam("oscId") String oscId, HttpSession session) {
        String mbrCd = (String) session.getAttribute("SCD");
        if (mbrCd == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UserOutsourcingReviewDTO review = userOutsourcingReviewService.getReviewForEdit(oscId, mbrCd);
        if (review == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(review);
    }

    @PutMapping("/api/review/{rvwCd}")
    @ResponseBody
    public String updateReview(@PathVariable("rvwCd") String rvwCd,
                               @RequestBody Map<String, String> payload,
                               HttpSession session) {
        String mbrCd = (String) session.getAttribute("SCD");
        if (mbrCd == null) return "FAIL_AUTH";

        UserOutsourcingReviewDTO reviewDTO = new UserOutsourcingReviewDTO();
        reviewDTO.setRvwCd(rvwCd);
        reviewDTO.setRvwEvl(new BigDecimal(payload.get("rating")));
        reviewDTO.setRvwCn(payload.get("content"));

        // TODO: 수정 권한 검증 로직 추가 가능 (해당 리뷰의 작성자인지 확인)

        boolean success = userOutsourcingReviewService.updateReview(reviewDTO);
        return success ? "OK" : "FAIL_DB";
    }
	
	@PostMapping("/osst/review")
    @ResponseBody
    public String submitReview(@RequestBody Map<String, String> payload, HttpSession session) {
		String reviewerMbrCd = (String) session.getAttribute("SCD");
        if (reviewerMbrCd == null) {
            return "FAIL_AUTH"; // 인증 실패
        }

        try {
            String oscId = payload.get("projectId");
            BigDecimal rvwEvl = new BigDecimal(payload.get("rating"));
            String rvwCn = payload.get("content");
            
            if (oscId == null || rvwEvl == null || rvwCn == null || rvwCn.trim().isEmpty()) {
                return "FAIL_VALIDATION"; // 필수 데이터 누락
            }

            UserOutsourcingReviewDTO reviewDTO = new UserOutsourcingReviewDTO();
            reviewDTO.setOscId(oscId);
            reviewDTO.setReviewerMbrCd(reviewerMbrCd); // 세션에서 가져온 작성자 코드
            reviewDTO.setRvwEvl(rvwEvl);
            reviewDTO.setRvwCn(rvwCn);

            boolean success = userOutsourcingReviewService.createReview(reviewDTO);


            return success ? "OK" : "FAIL_DB";
        } catch (NumberFormatException e) {
            return "FAIL_RATING_FORMAT"; // 별점 숫자 형식 오류
        } catch (Exception e) {
            // 로그 기록 (실제 운영 시)
            return "FAIL_SERVER"; // 기타 서버 오류
        }
    
    }

}
