package org.egov.waterconnection.consumer;

import static org.egov.waterconnection.constants.WCConstants.TENANTID_MDC_STRING;

import java.util.HashMap;

import org.egov.waterconnection.service.PaymentUpdateService;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ReceiptConsumer {
	@Autowired
	private PaymentUpdateService paymentUpdateService;
	
	@Value("${state.level.tenant.id}")
	private String stateLevelTenantID;

    @KafkaListener(topicPattern = "${kafka.topics.receipt.topic.pattern}")
	public void listenPayments(final HashMap<String, Object> record) {

		// Adding in MDC so that tracer can add it in header
		MDC.put(TENANTID_MDC_STRING, stateLevelTenantID);

		paymentUpdateService.process(record);
	}
}
