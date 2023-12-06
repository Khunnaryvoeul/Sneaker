
const functions = require("firebase-functions");
const stripe = require('stripe')('sk_test_51OJgWyJkxsEIdX6o8nUYdDQGkHXDSUWuX72GLJWAQYrsYMBD8LVGtCQVq7A4z5Vyw2UlOy5TEOLYgX13d644LYZk00XAfmndJe');


exports.Sneaker = functions.https.onRequest(async (request, Response) => {
 
  const amount = request.query.amt;
  const customer = await stripe.customers.create();
  const ephemeralKey = await stripe.ephemeralKeys.create(
    {customer: customer.id},
    {apiVersion: '2023-10-16'}
  );
  const paymentIntent = await stripe.paymentIntents.create({
    amount: amount,
    currency: 'usd',
    customer: customer.id,
    
    automatic_payment_methods: {
      enabled: true,
    },
  });

  Response.json({
    paymentIntent: paymentIntent.client_secret,
    ephemeralKey: ephemeralKey.secret,
    customer: customer.id,
    publishableKey: 'pk_test_51OJgWyJkxsEIdX6o7Mar2oj9sxYNCGg3rqyJvo0eOipwRdE2es0WBrxY4QvfDam1d3TnyLsUtTLIKl9OJ8BJoQu500kHW7eERW'
  });
});


