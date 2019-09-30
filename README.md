# payment-lambda-aws
A Lambda function that processes SQS messages to make payments on the Stellar network

Our Lambda function responds to messages on a SQS Queue. 
It expects the **messageAttributes** to contain the fields **to** and **amount**.

To is the account on the **Stellar testnet** to which we will payout funds.
Amount is the amount to be paid.

We sign transactions in this lambda with a testnet account as well.  
The private key is stored in **secrets manager** under **payment.lambda**.

