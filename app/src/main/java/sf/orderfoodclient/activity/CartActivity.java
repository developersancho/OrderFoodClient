package sf.orderfoodclient.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import info.hoang8f.widget.FButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sf.orderfoodclient.R;
import sf.orderfoodclient.common.Common;
import sf.orderfoodclient.database.Database;
import sf.orderfoodclient.helper.CartAdapter;
import sf.orderfoodclient.model.Order;
import sf.orderfoodclient.model.Request;
import sf.orderfoodclient.model.firebase.Token;
import sf.orderfoodclient.model.firebase.MyResponse;
import sf.orderfoodclient.model.firebase.Notification;
import sf.orderfoodclient.model.firebase.Sender;
import sf.orderfoodclient.remote.APIService;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class CartActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference requests;

    public TextView txtTotalPrice;
    FButton btnPlaceOrder;

    List<Order> cart = new ArrayList<>();
    CartAdapter adapter;

    APIService mService;

    Place shippingAddress;
    String address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set font
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/restaurant_font.otf")
                .setFontAttrId(R.attr.fontPath)
                .build());


        setContentView(R.layout.activity_cart);

        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Requests");
        mService = Common.getFCMService();

        recyclerView = (RecyclerView) findViewById(R.id.listCart);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        txtTotalPrice = (TextView) findViewById(R.id.txtTotal);
        btnPlaceOrder = (FButton) findViewById(R.id.btnPlaceOrder);
        btnPlaceOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cart.size() > 0) {
                    showAlertDialog();
                } else {
                    Toast.makeText(CartActivity.this, "Your cart is empty", Toast.LENGTH_SHORT).show();
                }
            }
        });

        loadListFood();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private void showAlertDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(CartActivity.this);
        alertDialog.setTitle("One  More Step");
        alertDialog.setMessage("Enter your address: ");
        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);

        LayoutInflater inflater = this.getLayoutInflater();
        View order_address_comment = inflater.inflate(R.layout.order_address_comment, null);

        //final MaterialEditText edtAddress = (MaterialEditText) order_address_comment.findViewById(R.id.edtAddress);
        final PlaceAutocompleteFragment edtAddress = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        // hide search icon
        edtAddress.getView().findViewById(R.id.place_autocomplete_search_button).setVisibility(View.GONE);
        // set hint for autocomplete edit text
        ((EditText) edtAddress.getView().findViewById(R.id.place_autocomplete_search_input))
                .setHint("Enter your address");
        // set text size
        ((EditText) edtAddress.getView().findViewById(R.id.place_autocomplete_search_input))
                .setTextSize(14);

        // get address from place autocomplete
        edtAddress.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                shippingAddress = place;
            }

            @Override
            public void onError(Status status) {
                Log.e("ERROR", status.getStatusMessage());
            }
        });

        final MaterialEditText edtComment = (MaterialEditText) order_address_comment.findViewById(R.id.edtComment);
        final RadioButton rdBtnShipToAddress = (RadioButton) order_address_comment.findViewById(R.id.rdBtnShipToAddress);
        final RadioButton rdBtnHomeAddress = (RadioButton) order_address_comment.findViewById(R.id.rdBtnHomeAddress);

        rdBtnHomeAddress.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (!TextUtils.isEmpty(Common.currentUser.getHomeAddress())
                            || Common.currentUser.getHomeAddress() != null) {
                        address = Common.currentUser.getHomeAddress();
                        // set text size
                        ((EditText) edtAddress.getView().findViewById(R.id.place_autocomplete_search_input))
                                .setText(address);
                    } else {
                        Toast.makeText(CartActivity.this, "Please update your home address", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    ((EditText) edtAddress.getView().findViewById(R.id.place_autocomplete_search_input))
                            .setText("");
                }
            }
        });

        rdBtnShipToAddress.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (shippingAddress != null)
                        address = shippingAddress.getAddress().toString();
                }
            }
        });


        alertDialog.setView(order_address_comment);
        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);

        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


                if (!rdBtnShipToAddress.isChecked() && !rdBtnHomeAddress.isChecked()) {
                    // if both radio is not selected
                    if (shippingAddress != null)
                        address = shippingAddress.getAddress().toString();
                    else {
                        Toast.makeText(CartActivity.this, "Please enter address or select option addres", Toast.LENGTH_SHORT).show();
                        // remove  fragment
                        getFragmentManager().beginTransaction()
                                .remove(getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment))
                                .commit();
                        return;
                    }
                }

                if (!TextUtils.isEmpty(address)) {
                    Toast.makeText(CartActivity.this, "Please enter address or select option addres", Toast.LENGTH_SHORT).show();
                    // remove  fragment
                    getFragmentManager().beginTransaction()
                            .remove(getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment))
                            .commit();
                    return;
                }


                Request request = new Request(
                        Common.currentUser.getPhone(),
                        Common.currentUser.getName(),
                        address,
                        txtTotalPrice.getText().toString(),
                        edtComment.getText().toString(),
                        cart,
                        String.format("%s,%s", shippingAddress.getLatLng().latitude, shippingAddress.getLatLng().longitude)
                );

                String order_number = String.valueOf(System.currentTimeMillis());
                requests.child(order_number).setValue(request);

                new Database(getBaseContext()).cleanCart();
                /*Toast.makeText(CartActivity.this, "Thanks, Order Place", Toast.LENGTH_SHORT).show();
                finish();*/
                sendNotificationOrder(order_number);

            }
        });

        alertDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                // remove  fragment
                getFragmentManager().beginTransaction()
                        .remove(getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment))
                        .commit();
            }
        });

        alertDialog.show();

    }

    private void sendNotificationOrder(final String order_number) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("FoodTokens");
        Query data = tokens.orderByChild("serverToken").equalTo(true);
        data.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapShot : dataSnapshot.getChildren()) {
                    Token serverToken = postSnapShot.getValue(Token.class);

                    Notification notification = new Notification("INFORMATION", "You have new order " + order_number);
                    Sender content = new Sender(serverToken.getToken(), notification);

                    mService.sendNotification(content)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if (response.code() == 200) {

                                        if (response.body().getSuccess() == 1) {
                                            Toast.makeText(CartActivity.this, "Thanks, Order Place", Toast.LENGTH_SHORT).show();
                                            finish();
                                        } else {
                                            Toast.makeText(CartActivity.this, "Failed !!!", Toast.LENGTH_SHORT).show();
                                        }

                                    } else {
                                        Toast.makeText(CartActivity.this, "Not 200", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {
                                    Toast.makeText(CartActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(CartActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadListFood() {
        cart = new Database(getBaseContext()).getCarts();
        adapter = new CartAdapter(cart, this);
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);

        int total = 0;
        for (Order order : cart)
            total += (Integer.parseInt(order.getPrice())) * (Integer.parseInt(order.getQuantity()));
        Locale locale = new Locale("en", "US");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);

        txtTotalPrice.setText(fmt.format(total));

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle().equals(Common.DELETE)) {
            deleteCart(item.getOrder());
        }
        return true;
    }

    private void deleteCart(int position) {
        // we will remove item at List<Order> by position
        cart.remove(position);
        // after that, we will delete all old data from SQLite
        new Database(this).cleanCart();
        // and final , we update new data from List<Order> to SQLite
        for (Order item : cart)
            new Database(this).addToCart(item);

        loadListFood();
    }
}
