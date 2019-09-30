package com.basm.payment.lambda.model;

public class PaymentEvent {

	private String to;
	
	private String amount;

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}
	
}
