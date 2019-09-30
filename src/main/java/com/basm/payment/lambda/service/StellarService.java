package com.basm.payment.lambda.service;

import java.io.IOException;

import org.stellar.sdk.AssetTypeNative;
import org.stellar.sdk.KeyPair;
import org.stellar.sdk.Memo;
import org.stellar.sdk.Network;
import org.stellar.sdk.PaymentOperation;
import org.stellar.sdk.Server;
import org.stellar.sdk.Transaction;
import org.stellar.sdk.responses.AccountResponse;
import org.stellar.sdk.responses.SubmitTransactionResponse;

import com.basm.payment.lambda.model.PaymentEvent;

public class StellarService {

	private final static long TRANSACTION_TIMEOUT_IN_MILIS = 60 * 1000; 
	
	private final Server stellarServer;

	private SecretsManagerService secretsManagerService;

	public StellarService() {
		secretsManagerService = new SecretsManagerService();
		stellarServer = new Server("https://horizon-testnet.stellar.org");
		Network.useTestNetwork();
	}

	/**
	 * Make a payment from our account to the account in the payment event.
	 * 
	 * @param paymentEvent
	 * @return returns the transaction hash of a successfully submitted transaction.
	 */
	public String makePayment(PaymentEvent paymentEvent) {
		KeyPair toAccount = KeyPair.fromAccountId(paymentEvent.getTo());
		KeyPair signingKeypair = KeyPair.fromSecretSeed(secretsManagerService.getApplicationWalletPrivateKey());
		AccountResponse signer = getAccountForKeyPair(signingKeypair);

		// Create the transaction
		Transaction transaction = new Transaction.Builder(signer)
									.addOperation(new PaymentOperation.Builder(toAccount, new AssetTypeNative(), paymentEvent.getAmount())
														.build())
									.addMemo(Memo.text("Have some money bro. "))
									.setTimeout(TRANSACTION_TIMEOUT_IN_MILIS)
									.build();
		transaction.sign(signingKeypair);

		
		// Submit the transaction to horizon
		try {
			SubmitTransactionResponse transactionResponse = stellarServer.submitTransaction(transaction);
			if (!transactionResponse.isSuccess()) {
				throw new RuntimeException("The transaction was not submitted successfully.");
			}
			System.out.println("Submitted transaction and got transaction hash " + transactionResponse.getHash());
			return transactionResponse.getHash();
		} catch (Exception e) {
			throw new RuntimeException("The transaction was not submitted successfully.");
		}
	}

	/**
	 * Fetch the Stellar account which will be used to send funds from.
	 * 
	 * @param singinKeypair the keypair for which you want to fetch the account
	 *                      data.
	 */
	private AccountResponse getAccountForKeyPair(KeyPair keypair) {
		try {
			AccountResponse account = stellarServer.accounts().account(keypair);
			return account;
		} catch (IOException e) {
			System.out.println("Could not fetch the signing account from horizon.");
			throw new RuntimeException("Could not fetch the signing account from horizon");
		}
	}

}
