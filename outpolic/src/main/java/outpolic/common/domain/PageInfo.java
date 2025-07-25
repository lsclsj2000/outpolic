package outpolic.common.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PageInfo {
    private int page;
    private int size;
    private int totalElements;

    public int getTotalPages() {
        return (int) Math.ceil((double) totalElements / size);
    }

    public boolean hasPrevious() {
        return page > 1;
    }

    public boolean hasNext() {
        return page < getTotalPages();
    }

    public int getNumber() {
        return Math.max(0, page - 1);
    }
}
