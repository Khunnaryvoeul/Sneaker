package com.example.sneaker.views;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.sneaker.R;
import com.example.sneaker.utils.adapter.CartAdapter;
import com.example.sneaker.utils.model.ShoeCart;
import com.example.sneaker.viewmodel.CartViewModel;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CartActivity extends AppCompatActivity implements CartAdapter.CartClickedListeners {

    private CartViewModel cartViewModel;
    private TextView totalCartPriceTv, textView;
    private AppCompatButton checkoutBtn;
    private CardView cardView;
    private CartAdapter cartAdapter;
    private PaymentSheet paymentSheet;
    private String paymentIntentClientSecret;
    private PaymentSheet.CustomerConfiguration customerConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        initializeVariables();

        // Observe changes in cart items
        cartViewModel.getAllCartItems().observe(this, shoeCarts -> {
            double price = 0;
            cartAdapter.setShoeCartList(shoeCarts);
            for (ShoeCart shoeCart : shoeCarts) {
                price += shoeCart.getTotalItemPrice();
            }
            totalCartPriceTv.setText(String.valueOf(price));
        });

        checkoutBtn.setOnClickListener(view -> {
            if (paymentIntentClientSecret != null) {
                paymentSheet.presentWithPaymentIntent(paymentIntentClientSecret, new PaymentSheet.Configuration("Example, Inc.", customerConfig));
            }
        });
    }

    private void initializeVariables() {
        cartAdapter = new CartAdapter(this);
        textView = findViewById(R.id.textView2);
        cardView = findViewById(R.id.cartActivityCardView);
        totalCartPriceTv = findViewById(R.id.cartActivityTotalPriceTv);
        checkoutBtn = findViewById(R.id.cartActivityCheckoutBtn);
        cartViewModel = new ViewModelProvider(this).get(CartViewModel.class);
        RecyclerView recyclerView = findViewById(R.id.cartRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(cartAdapter);

        // Initialize PaymentSheet
        paymentSheet = new PaymentSheet(this, this::onPaymentSheetResult);

        // Fetch payment information
        fetchPaymentApi();
    }

    private void fetchPaymentApi() {

        //url is from connecting to firebase cloud functions
        //customer, ephemeral key is from connecting to stripe account created
        // this block of code fetches stripe payment
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url = "https://us-central1-sneaker-5d3ae.cloudfunctions.net/Sneaker?amt=50";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    Log.e("R", response);
                    try {
                        final JSONObject result = new JSONObject(response);
                        customerConfig = new PaymentSheet.CustomerConfiguration(
                                result.getString("customer"),
                                result.getString("ephemeralKey")
                        );
                        paymentIntentClientSecret = result.getString("paymentIntent");
                        PaymentConfiguration.init(getApplicationContext(), result.getString("publishableKey"));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    error.printStackTrace();
                    // Handle error (e.g., show a Toast)
                    Toast.makeText(this, "Failed to fetch payment information", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> paramV = new HashMap<>();
                paramV.put("authKey", "abc");
                return paramV;
            }
        };
        queue.add(stringRequest);
    }



    private void onPaymentSheetResult(final PaymentSheetResult paymentSheetResult) {
        if (paymentSheetResult instanceof PaymentSheetResult.Canceled) {
            // Payment Canceled
        } else if (paymentSheetResult instanceof PaymentSheetResult.Failed) {
            // Payment Failed
        } else if (paymentSheetResult instanceof PaymentSheetResult.Completed) {
            // Payment Complete
            cartViewModel.deleteAllCartItems();
            textView.setVisibility(View.INVISIBLE);
            checkoutBtn.setVisibility(View.INVISIBLE);
            totalCartPriceTv.setVisibility(View.INVISIBLE);
            cardView.setVisibility(View.VISIBLE);
        }
    }

    //handling cart interactions
    @Override
    public void onDeleteClicked(ShoeCart shoeCart) {
        cartViewModel.deleteCartItem(shoeCart);
    }

    @Override
    public void onDeletedClicked(ShoeCart shoeCart) {

    }

    @Override
    public void onPlusClicked(ShoeCart shoeCart) {
        int quantity = shoeCart.getQuantity() + 1;
        cartViewModel.updateQuantity(shoeCart.getId(), quantity);
        cartViewModel.updatePrice(shoeCart.getId(), quantity * shoeCart.getShoePrice());
        cartAdapter.notifyDataSetChanged();
    }

    @Override
    public void onMinusClicked(ShoeCart shoeCart) {
        int quantity = shoeCart.getQuantity() - 1;
        if (quantity != 0) {
            cartViewModel.updateQuantity(shoeCart.getId(), quantity);
            cartViewModel.updatePrice(shoeCart.getId(), quantity * shoeCart.getShoePrice());
            cartAdapter.notifyDataSetChanged();
        } else {
            cartViewModel.deleteCartItem(shoeCart);
        }
    }
}
