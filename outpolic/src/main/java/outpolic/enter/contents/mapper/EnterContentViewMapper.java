package outpolic.enter.contents.mapper;



import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import outpolic.enter.contents.domain.EnterTotalView;
import outpolic.enter.contents.domain.EnterPerusalContent;


@Mapper
public interface EnterContentViewMapper {

	int existsInTotalView(@Param("clCd") String clCd);
    
	//[추가]
	Integer selectMaxTvCdNum();
	Integer selectMaxPcRcdNum();
	
    int insertNewTotalView(EnterTotalView totalView);

    int incrementTotalView(@Param("clCd") String clCd);

    int insertPerusalContent(EnterPerusalContent perusalContent);
}
