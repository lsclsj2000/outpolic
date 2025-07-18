package outpolic.user.contents.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import outpolic.user.contents.domain.UserPerusalContent;
import outpolic.user.contents.domain.UserTotalView;
import outpolic.user.contents.domain.UserTodayView;
import outpolic.user.contents.mapper.UserContentViewMapper;
import outpolic.user.contents.mapper.UserTodayViewMapper;
import outpolic.user.contents.service.UserContentViewService;

@Service
@RequiredArgsConstructor
public class UserContentViewServiceImpl implements UserContentViewService {
	
	private final UserContentViewMapper userContentViewMapper;
	private final UserTodayViewMapper userTodayViewMapper;
	
	@Override
    @Transactional
    public void processContentView(String clCd, String mbrCd) {
        
        // --- total_view 처리 로직 ---
        int count = userContentViewMapper.existsInTotalView(clCd);
        if (count == 0) {
            Integer maxTvNum = userContentViewMapper.selectMaxTvCdNum();
            int nextTvNum = (maxTvNum == null) ? 1 : maxTvNum + 1;
            
            // [핵심 수정] 0으로 채우는 format 방식을 빼고, 단순 문자열 연결로 변경합니다.
            String newTvCd = "TV_C" + nextTvNum;
            
            UserTotalView newView = new UserTotalView();
            newView.setTvCd(newTvCd);
            newView.setClCd(clCd);
            userContentViewMapper.insertNewTotalView(newView);
        } else {
            userContentViewMapper.incrementTotalView(clCd);
        }
        
     // --- 2. today_view 처리 로직 (모두 올바른 이름으로 수정) ---
        int todayViewCount = userTodayViewMapper.existsInTodayView(clCd);
        if (todayViewCount == 0) {
            Integer maxTdvNum = userTodayViewMapper.selectMaxTdvCdNum();
            int nextTdvNum = (maxTdvNum == null) ? 1 : maxTdvNum + 1;
            String newTdvCd = "TDV_C" + nextTdvNum;

            UserTodayView newTodayView = new UserTodayView();
            newTodayView.setTdvCd(newTdvCd);
            newTodayView.setClCd(clCd);
            userTodayViewMapper.insertNewTodayView(newTodayView);
        } else {
        	userTodayViewMapper.incrementTodayView(clCd);
        }


        // --- perusal_content 처리 로직 (이 부분은 이미 올바른 방식이라 수정할 필요 없음) ---
        if (mbrCd != null && !mbrCd.isEmpty()) {
            Integer maxPcNum = userContentViewMapper.selectMaxPcRcdNum();
            int nextPcNum = (maxPcNum == null) ? 1 : maxPcNum + 1;
            
            String newPcRcdCd = "PC_RCD_C" + nextPcNum;

            UserPerusalContent record = new UserPerusalContent();
            record.setPcRcdCd(newPcRcdCd);
            record.setClCd(clCd);
            record.setMbrCd(mbrCd);
            userContentViewMapper.insertPerusalContent(record);
        }
    }
}
