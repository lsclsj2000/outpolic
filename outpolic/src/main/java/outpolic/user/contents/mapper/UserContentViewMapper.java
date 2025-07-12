package outpolic.user.contents.mapper;



import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import outpolic.user.contents.domain.UserPerusalContent;
import outpolic.user.contents.domain.UserTotalView;


@Mapper
public interface UserContentViewMapper {

int existsInTotalView(@Param("clCd") String clCd);
    
	//[추가]
	Integer selectMaxTvCdNum();
	Integer selectMaxPcRcdNum();
	
    int insertNewTotalView(UserTotalView totalView);

    /**
     * [수정] total_view의 기존 행 조회수를 증가시키는 메소드
     */
    int incrementTotalView(@Param("clCd") String clCd);

    /**
     * perusal_content에 열람 기록을 추가하는 메소드 (이것은 그대로 유지)
     */
    int insertPerusalContent(UserPerusalContent perusalContent);
}
