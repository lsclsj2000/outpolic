package outpolic.enter.outsourcing.service;

import java.io.IOException;
import java.util.List;
import outpolic.enter.outsourcing.domain.EnterOutsourcing;

public interface EnterOutsourcingService {
    
    List<EnterOutsourcing> getOutsourcingListByEntCd(String entCd);
    void addOutsourcing(EnterOutsourcing outsourcing, List<String> categoryCodes, String tags) throws IOException;
    void deleteOutsourcing(String osCd);
    EnterOutsourcing getOutsourcingByOsCd(String osCd);
    void updateOutsourcing(EnterOutsourcing outsourcing, List<String> categoryCodes, String tags) throws IOException; 
    
}