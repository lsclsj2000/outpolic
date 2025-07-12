package outpolic.common.util; // 적절한 공통 유틸리티 패키지 선택

import org.springframework.stereotype.Component;

@Component("statusTextFormatter") // Thymeleaf에서 @statusTextFormatter로 참조할 수 있도록 빈 이름 지정
public class StatusTextFormatter {

    public String getStatusText(String statusCode) {
        switch (statusCode) {
            case "SD_BEFORE": return "확인 전";
            case "SD_PROCESS_ING": return "처리 중";
            case "SD_APPROVED": return "승인";
            case "SD_REJECTED": return "거절";
            case "SD_CANCELED": return "취소됨";
            case "SD_ACTIVE": return "활성"; // 예시: 다른 상태 코드 추가
            case "SD_DORMANT": return "휴면";
            // 필요한 모든 상태 코드에 대한 매핑을 여기에 추가
            default: return statusCode; // 매핑되지 않은 코드는 그대로 반환
        }
    }
}