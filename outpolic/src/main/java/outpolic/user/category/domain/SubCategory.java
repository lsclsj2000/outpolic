package outpolic.user.category.domain;

import lombok.Data;

@Data
public class SubCategory {

	private String id;
	private String name;
	private String url;
	
	public SubCategory (String id, String name) {
		this.id = id;
		this.name = name;
		this.url = "/user/products?category=" + id;
	}
	
}
