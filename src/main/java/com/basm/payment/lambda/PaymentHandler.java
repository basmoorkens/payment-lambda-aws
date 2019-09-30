package com.basm.payment.lambda;

import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.amazonaws.services.lambda.runtime.events.SQSEvent.MessageAttribute;
import com.amazonaws.services.lambda.runtime.events.SQSEvent.SQSMessage;
import com.basm.payment.lambda.model.PaymentEvent;
import com.basm.payment.lambda.service.StellarService;

public class PaymentHandler implements RequestHandler<SQSEvent, Void> {

	private StellarService stellarService;

	public Void handleRequest(SQSEvent event, Context context) {
		stellarService = new StellarService();
		for (SQSMessage msg : event.getRecords()) {
			PaymentEvent paymentEvent = parseEvent(msg.getMessageAttributes());
			System.out.println("Received payment event " + paymentEvent.getTo() + " - " + paymentEvent.getAmount());
			try {
				String submittedHash = stellarService.makePayment(paymentEvent);
				System.out.println("Made payment to " + paymentEvent.getTo() + " for " + paymentEvent.getAmount());
			} catch (RuntimeException e) {
				System.out.println(e.getMessage());
				System.out.println("Could not process payment to " + paymentEvent.getTo());
			}
		}
		return null;
	}

	public PaymentEvent parseEvent(Map<String, MessageAttribute> eventParams) {
		PaymentEvent event = new PaymentEvent();
		event.setTo(eventParams.get("To").getStringValue());
		event.setAmount(eventParams.get("Amount").getStringValue());
		return event;
	}

}
