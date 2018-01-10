package sf.orderfoodclient.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import sf.orderfoodclient.R;
import sf.orderfoodclient.common.Common;
import sf.orderfoodclient.helper.ItemClickListener;
import sf.orderfoodclient.helper.OrderViewHolder;
import sf.orderfoodclient.model.Order;
import sf.orderfoodclient.model.Request;

public class OrderStatusActivity extends AppCompatActivity {

    public RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference requests;

    FirebaseRecyclerAdapter<Request, OrderViewHolder> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status);

        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Requests");

        recyclerView = (RecyclerView) findViewById(R.id.listOrders);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        loadOrders(Common.currentUser.getPhone());

    }

    private void loadOrders(String phone) {
        adapter = new FirebaseRecyclerAdapter<Request, OrderViewHolder>(
                Request.class,
                R.layout.order_item,
                OrderViewHolder.class,
                requests.orderByChild("phone").equalTo(phone)) {

            @Override
            protected void populateViewHolder(OrderViewHolder viewHolder, Request model, int position) {
                viewHolder.order_id.setText(adapter.getRef(position).getKey());
                viewHolder.order_status.setText(convertCodeToStatus(model.getStatus()));
                viewHolder.order_phone.setText(model.getPhone());
                viewHolder.order_address.setText(model.getAddress());

                final Request clickItem = model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Toast.makeText(OrderStatusActivity.this, "" + clickItem.getName() + "-" + clickItem.getPhone(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        };

        recyclerView.setAdapter(adapter);
    }

    private String convertCodeToStatus(String status) {
        if (status.equals("0"))
            return "Placed";
        else if (status.equals("1"))
            return "On my way";
        else
            return "Shipped";
    }
}
