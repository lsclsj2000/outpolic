package outpolic.systems.util;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class Pageable {

	private int currentPage = 1;
	private int rowPerPage = 5;
	private int offset = 0;
	
	public void setCurrentPage(int currentPage) {
		this.currentPage = Math.max(currentPage, this.currentPage);
		setOffset();
	}
	
	public void setRowPerPage(int rowPerPage) {
		this.rowPerPage = Math.max(rowPerPage, this.rowPerPage);
		setOffset();
	}
	
	public void setOffset() {
		this.offset = (currentPage - 1) * rowPerPage;
	}
}







