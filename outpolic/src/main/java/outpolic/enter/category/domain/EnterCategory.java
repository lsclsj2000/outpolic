package outpolic.enter.category.domain;

import java.util.List;

import org.springframework.data.annotation.Transient;

import lombok.Data;

@Data
public class EnterCategory {
	
	private String categoryId;
	private String categoryName;
	private String parentCategoryId;
	
	@Transient // DB 컬럼이 아니라는 의미
    private List<EnterCategory> children; 
}
