package outpolic.admin.member.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import outpolic.common.domain.Member;

@Mapper
public interface AdminMemberMapper {
	
	//회원 목록 조회
	List<Member> getMemberList();
}
