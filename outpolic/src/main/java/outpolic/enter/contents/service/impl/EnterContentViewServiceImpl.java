package outpolic.enter.contents.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import outpolic.enter.contents.domain.EnterPerusalContent;
import outpolic.enter.contents.domain.EnterTotalView;
import outpolic.enter.contents.domain.EnterTodayView;
import outpolic.enter.contents.mapper.EnterContentViewMapper;
import outpolic.enter.contents.mapper.EnterTodayViewMapper;
import outpolic.enter.contents.service.EnterContentViewService;

@Service
@RequiredArgsConstructor
public class EnterContentViewServiceImpl implements EnterContentViewService {
	
	private final EnterContentViewMapper enterContentViewMapper;
	private final EnterTodayViewMapper enterTodayViewMapper;
	
	@Override
    @Transactional
    public void processContentView(String clCd, String mbrCd) {
        
        // --- total_view 처리 로직 ---
        int count = enterContentViewMapper.existsInTotalView(clCd);
        if (count == 0) {
            Integer maxTvNum = enterContentViewMapper.selectMaxTvCdNum();
            int nextTvNum = (maxTvNum == null) ? 1 : maxTvNum + 1;
            
            // [핵심 수정] 0으로 채우는 format 방식을 빼고, 단순 문자열 연결로 변경합니다.
            String newTvCd = "TV_C" + nextTvNum;
            
            EnterTotalView newView = new EnterTotalView();
            newView.setTvCd(newTvCd);
            newView.setClCd(clCd);
            enterContentViewMapper.insertNewTotalView(newView);
        } else {
            enterContentViewMapper.incrementTotalView(clCd);
        }
        
     // --- 2. today_view 처리 로직 (모두 올바른 이름으로 수정) ---
        int todayViewCount = enterTodayViewMapper.existsInTodayView(clCd);
        if (todayViewCount == 0) {
            Integer maxTdvNum = enterTodayViewMapper.selectMaxTdvCdNum();
            int nextTdvNum = (maxTdvNum == null) ? 1 : maxTdvNum + 1;
            String newTdvCd = "TDV_C" + nextTdvNum;

            EnterTodayView newTodayView = new EnterTodayView();
            newTodayView.setTdvCd(newTdvCd);
            newTodayView.setClCd(clCd);
            enterTodayViewMapper.insertNewTodayView(newTodayView);
        } else {
        	enterTodayViewMapper.incrementTodayView(clCd);
        }


        // --- perusal_content 처리 로직 (이 부분은 이미 올바른 방식이라 수정할 필요 없음) ---
        if (mbrCd != null && !mbrCd.isEmpty()) {
            Integer maxPcNum = enterContentViewMapper.selectMaxPcRcdNum();
            int nextPcNum = (maxPcNum == null) ? 1 : maxPcNum + 1;
            
            String newPcRcdCd = "PC_RCD_C" + nextPcNum;

            EnterPerusalContent record = new EnterPerusalContent();
            record.setPcRcdCd(newPcRcdCd);
            record.setClCd(clCd);
            record.setMbrCd(mbrCd);
            enterContentViewMapper.insertPerusalContent(record);
        }
    }
}
