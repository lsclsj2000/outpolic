package outpolic.admin.dashboard.domain;

import java.util.List;

import lombok.Data;

@Data
public class AdminDashboardDTO {

	private long totalMemberCount;      // 전체 회원 수
    private long todayNewMemberCount;   // 오늘 가입한 회원 수
    private long totalPortfolioCount;   // 전체 포트폴리오 수 (활성 기준)
    private long totalOutsourcingCount; // 전체 외주 수 (활성 기준)
    private long totalRevenue;          // 총 누적 매출 (계약 완료 기준)
    private long monthlyRevenue;        // 이번 달 매출
    
    // --- 차트 데이터 ---
    // 최근 7일간 신규 가입자 수 추이
    private List<DailyCount> weeklyNewMembers; 
    
    // 내부 클래스로 차트 데이터 구조 정의
    @Data
    public static class DailyCount {
        private String date; // 날짜 (예: "2023-10-27")
        private long count;  // 수치
    }
}
