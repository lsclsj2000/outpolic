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

    int incrementTotalView(@Param("clCd") String clCd);

    int insertPerusalContent(UserPerusalContent perusalContent);
}
