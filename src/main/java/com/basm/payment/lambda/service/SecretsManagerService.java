package com.basm.payment.lambda.service;

import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder;
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest;
import com.amazonaws.services.secretsmanager.model.GetSecretValueResult;
import com.amazonaws.services.secretsmanager.model.InvalidRequestException;
import com.amazonaws.services.secretsmanager.model.ResourceNotFoundException;

import shadow.com.google.gson.JsonObject;
import shadow.com.google.gson.JsonParser;

public class SecretsManagerService {

	private final static String SECRET_NAME = "payment.lambda";

	private final static String AWS_REGION = "eu-west-1";

	private final static String SECRET_ID = "signer.key";

	private AWSSecretsManager client;

	public SecretsManagerService() {
		client = AWSSecretsManagerClientBuilder.standard().withRegion(AWS_REGION).build();
	}

	/**
	 * Fetch the applications wallet's private key from AWS secrets manager.
	 * 
	 * @return The PK for our application wallet
	 */
	public String getApplicationWalletPrivateKey() {
		GetSecretValueRequest request = new GetSecretValueRequest().withSecretId(SECRET_NAME);
		GetSecretValueResult result = null;
		try {
			result = client.getSecretValue(request);
			if (result == null) {
				throw new RuntimeException("Could not access our applications private key");
			}
		} catch (ResourceNotFoundException e) {
			System.out.println("Could not find secret " + SECRET_NAME);
			throw e;
		} catch (InvalidRequestException e) {
			System.out.println("Invalid request to secrets manager " + e.getMessage());
			throw e;
		}
		JsonObject jsonObject = new JsonParser().parse(result.getSecretString()).getAsJsonObject();
		return jsonObject.get(SECRET_ID).getAsString();
	}

}
