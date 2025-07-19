package outpolic.enter.withdrawn.service.Impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import outpolic.enter.withdrawn.dto.EnterOsInfoDTO;
import outpolic.enter.withdrawn.dto.EnterWithdrawnDTO;
import outpolic.enter.withdrawn.mapper.EnterWithdrawnMapper;
import outpolic.enter.withdrawn.service.EnterWithdrawnService;

@Service
@RequiredArgsConstructor
public class EnterWithdrawnServiceImpl implements EnterWithdrawnService {
	
	private final EnterWithdrawnMapper enterWithdrawnMapper;
	
	@Transactional
	@Override
	public void withdrawnEnterMember(String memberCode, String reason) {
		String wmCd = enterWithdrawnMapper.getNextWithdrawnCode();
		
		int wmActiveDays = enterWithdrawnMapper.getEnterMemberActiveDays(memberCode);
		
		EnterWithdrawnDTO dto = new EnterWithdrawnDTO();
		dto.setWmCd(wmCd);
		dto.setMemberCode(memberCode);
        dto.setWmRsn(reason);
        dto.setWmActiveDays(wmActiveDays);
        dto.setGradeCode("ENTER");
        
        enterWithdrawnMapper.insertWithMember(dto);
        
        enterWithdrawnMapper.deleteEnterAuthorityByCode(memberCode);
        enterWithdrawnMapper.deleteEnterBookmarkByCode(memberCode);
        enterWithdrawnMapper.deleteEnterPointByCode(memberCode);
        enterWithdrawnMapper.deleteEnterSubscriptionByCode(memberCode);
        enterWithdrawnMapper.deleteEnterTicketByCode(memberCode);
        
        enterWithdrawnMapper.updateEnterSttToWithdrawn(memberCode);
        
	}

	@Override
	public List<EnterOsInfoDTO> EnterOsIngSelectByCode(String memberCode) {
		return enterWithdrawnMapper.EnterOsIngSelectByCode(memberCode);
	}

}
