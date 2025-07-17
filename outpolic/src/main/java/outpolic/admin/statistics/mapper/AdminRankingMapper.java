package outpolic.admin.statistics.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.web.bind.annotation.RequestParam;

import outpolic.admin.statistics.domain.AdminPortfolioRankingContentsDTO;
import outpolic.admin.statistics.domain.AdminRankingContentsDTO;

@Mapper
public interface AdminRankingMapper {

	
	List<AdminPortfolioRankingContentsDTO> getAdminRankingPoContents(String targetDate);

	List<AdminRankingContentsDTO> getAdminRankingOsContents(String targetDate);
}
