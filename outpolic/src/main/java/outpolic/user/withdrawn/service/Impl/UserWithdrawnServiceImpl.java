package outpolic.user.withdrawn.service.Impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import outpolic.user.withdrawn.dto.UserWithdrawnDTO;
import outpolic.user.withdrawn.mapper.UserWithdrawnMapper;
import outpolic.user.withdrawn.service.UserWithdrawnService;

@Service
@RequiredArgsConstructor
public class UserWithdrawnServiceImpl implements UserWithdrawnService {
	
	private final UserWithdrawnMapper userWithdrawnMapper;
	
	// 회원탈퇴 로직
	@Transactional
	@Override
	public void withdrawMember(String memberCode, String reason) {
		
		String wmCd = userWithdrawnMapper.getNextWithdrawnCode();
		
		int wmActiveDays = userWithdrawnMapper.getMemberActiveDays(memberCode);
		
		UserWithdrawnDTO dto = new UserWithdrawnDTO();
		dto.setWmCd(wmCd);
        dto.setMemberCode(memberCode);
        dto.setWmRsn(reason);
        dto.setWmActiveDays(wmActiveDays);
        dto.setGradeCode("USER");
        
        userWithdrawnMapper.insertWithMember(dto);
        
        userWithdrawnMapper.deleteAuthorityByCode(memberCode);
        userWithdrawnMapper.deleteBookmarkByCode(memberCode);
        userWithdrawnMapper.deletePointByCode(memberCode);
        userWithdrawnMapper.deleteSubscriptionByCode(memberCode);
        userWithdrawnMapper.deleteTicketByCode(memberCode);
        
        userWithdrawnMapper.updateMemberSttToWithdrawn(memberCode);
	}

}
