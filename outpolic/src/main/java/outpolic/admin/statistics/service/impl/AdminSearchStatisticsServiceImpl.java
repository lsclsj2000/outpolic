package outpolic.admin.statistics.service.impl;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import outpolic.admin.statistics.domain.AdminSearchStatisticsDTO;
import outpolic.admin.statistics.mapper.AdminSearchStatisticsMapper;
import outpolic.admin.statistics.service.AdminSearchStatisticsService;

@Service
@RequiredArgsConstructor
public class AdminSearchStatisticsServiceImpl implements AdminSearchStatisticsService{
	
	private final AdminSearchStatisticsMapper adminSearchStatisticsMapper;

    @Override
    @Transactional // PK 조회와 INSERT가 한 번에 성공하거나 실패하도록 보장
    @Scheduled(cron = "0 0 0 * * MON") // 매주 월요일 00:00 실행
    public void aggregateWeeklySearchTerms() {
        System.out.println("주간 검색어 통계 집계 스케줄러 실행...");

        // 1. 지난주 날짜 계산
        LocalDate today = LocalDate.now();
        LocalDate lastWeekMonday = today.minusWeeks(1).with(DayOfWeek.MONDAY);
        LocalDate lastWeekSunday = today.minusWeeks(1).with(DayOfWeek.SUNDAY);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String frYmd = lastWeekMonday.format(formatter);
        String toYmd = lastWeekSunday.format(formatter);

        // 2. 다음 PK를 DB에서 조회
        String nextStCd = adminSearchStatisticsMapper.getNextStCd();

        // 3. Mapper 메소드 호출 (계산된 값들을 인자로 전달)
        adminSearchStatisticsMapper.insertWeeklySearchStatistics(nextStCd, frYmd, toYmd);
        
        System.out.println("집계 완료: " + nextStCd + " (" + frYmd + " ~ " + toYmd + ")");
    }

    @Override
    public List<AdminSearchStatisticsDTO> getWeeklySearchStatistics(String startDate, String endDate, String type, int minCount) {
        
        // ★★ 1. Controller로부터 받은 파라미터를 담을 Map 객체를 생성합니다. ★★
        Map<String, Object> params = new HashMap<>();
        params.put("startDate", startDate);
        params.put("endDate", endDate);
        params.put("type", type);
        params.put("minCount", minCount);

        // ★★ 2. 새로 만든 findStatisticsByPeriod 메소드를 호출하고, 생성한 params를 전달합니다. ★★
        //    (기존 findWeeklySearchStatisticsByDate는 더 이상 사용하지 않습니다)
        return adminSearchStatisticsMapper.findStatisticsByPeriod(params);
    }

}
