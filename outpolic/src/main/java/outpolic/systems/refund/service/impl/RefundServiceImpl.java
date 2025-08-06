package outpolic.systems.refund.service.impl;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import outpolic.systems.refund.dto.RefundDTO;
import outpolic.systems.refund.mapper.RefundMapper;
import outpolic.systems.refund.service.RefundService;

@Service
@RequiredArgsConstructor
public class RefundServiceImpl implements RefundService {

	private final RefundMapper refundMapper;

    @Override
    @Transactional
    public String processRefundRequest(String stlmCd, String mbrCd) {
        RefundDTO settlement = refundMapper.findSettlementForRefund(stlmCd);

        if (settlement == null || !settlement.getMbrCd().equals(mbrCd)) {
            throw new IllegalArgumentException("유효하지 않은 결제 정보입니다.");
        }
        if ("SD_REFUNDMENT".equals(settlement.getStcCd())) {
            throw new IllegalArgumentException("이미 환불 처리된 결제 건입니다.");
        }

        LocalDateTime paymentTime = settlement.getStlmYmdt().toLocalDateTime();
        if (ChronoUnit.DAYS.between(paymentTime, LocalDateTime.now()) > 7) {
            throw new IllegalArgumentException("결제 후 7일이 지나 환불할 수 없습니다.");
        }


        refundMapper.insertRefund(settlement);
        refundMapper.updateSettlementStatusToRefund(stlmCd);

        return "환불 요청이 정상적으로 처리되었습니다.";
    }
}
