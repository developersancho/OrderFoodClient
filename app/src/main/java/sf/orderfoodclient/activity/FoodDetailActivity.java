package sf.orderfoodclient.activity;

import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import sf.orderfoodclient.R;
import sf.orderfoodclient.model.Food;

public class FoodDetailActivity extends AppCompatActivity {

    TextView txtFoodName, txtFoodPrice, txtFoodDescription;
    ImageView img_food;
    CollapsingToolbarLayout collapsingToolbarLayout;
    FloatingActionButton btnCart;
    ElegantNumberButton btnNumber;

    String foodId = "";
    FirebaseDatabase database;
    DatabaseReference foods;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);

        database = FirebaseDatabase.getInstance();
        foods = database.getReference("Foods");

        txtFoodName = (TextView) findViewById(R.id.txtFoodName);
        txtFoodPrice = (TextView) findViewById(R.id.txtFoodPrice);
        txtFoodDescription = (TextView) findViewById(R.id.txtFoodDescription);

        img_food = (ImageView) findViewById(R.id.img_food);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);
        btnCart = (FloatingActionButton) findViewById(R.id.btnCart);
        btnNumber = (ElegantNumberButton) findViewById(R.id.btnNumber);

        if (getIntent() != null)
            foodId = getIntent().getStringExtra("FoodId");

        if (!foodId.isEmpty()){
            getDetailFood(foodId);
        }

    }

    private void getDetailFood(String foodId) {
        foods.child(foodId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Food food = dataSnapshot.getValue(Food.class);

                Picasso.with(getBaseContext()).load(food.getImage())
                        .into(img_food);

                collapsingToolbarLayout.setTitle(food.getName());

                txtFoodName.setText(food.getName());
                txtFoodDescription.setText(food.getDescription());
                txtFoodPrice.setText(food.getPrice());


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }
}
