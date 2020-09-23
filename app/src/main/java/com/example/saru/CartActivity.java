package com.example.saru;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.saru.Model.Cart;
import com.example.saru.Model.Products;
import com.example.saru.Prevalent.Prevalent;
import com.example.saru.ViewHolder.CartViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CartActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private Button nextProcessBtn;
    private TextView tctTotalAmount,txtMsg1;

    private int overTotalPrice = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        recyclerView = findViewById(R.id.cart_list);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        nextProcessBtn = (Button) findViewById(R.id.next_process_btn);
        tctTotalAmount = (TextView) findViewById(R.id.total_price);
txtMsg1=(TextView)findViewById(R.id.msg1);
        nextProcessBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                tctTotalAmount.setText("Total Price $= " + String.valueOf(overTotalPrice));
                Intent intent = new Intent(CartActivity.this, ConfirmFinalOrderActivity.class);
                intent.putExtra("Total Price", String.valueOf(overTotalPrice));
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        CheckOrderState();


        final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");
        FirebaseRecyclerOptions<Cart> options = new FirebaseRecyclerOptions.Builder<Cart>()
                .setQuery(cartListRef.child("Users View").child(Prevalent.currentOnlineUser.getPhone()).child("Products"), Cart.class)
                .build();
        FirebaseRecyclerAdapter<Cart, CartViewHolder> adapter = new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CartViewHolder cartViewHolder, int i, @NonNull final Cart cart) {
                cartViewHolder.txtProductName.setText("ProductName =" + cart.getName());
                cartViewHolder.txtProductQuantity.setText("Quantity =" + cart.getQuantity());
                cartViewHolder.txtProductPrice.setText("$"+ "price =" + cart.getPrice());
                int oneTypeProductPrice = ((Integer.valueOf(cart.getPrice()))) *Integer.valueOf(cart.getQuantity());
                overTotalPrice = overTotalPrice + oneTypeProductPrice;
                cartViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CharSequence options[] = new CharSequence[]
                                {
                                        "Edit",
                                        "Remove"
                                };
                        AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
                        builder.setTitle("Cart Options:");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (i == 0) {
                                    Intent intent = new Intent(CartActivity.this, ProductDetailsActivity.class);
                                    intent.putExtra("pid", cart.getPid());
                                    startActivity(intent);
                                }
                                if (i == 1) {
                                    cartListRef.child("Users View").child(Prevalent.currentOnlineUser.getPhone()).child("Products").child(cart.getPid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(CartActivity.this, "Item Removed", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(CartActivity.this, MenumainActivity.class);

                                                startActivity(intent);
                                            }
                                        }
                                    });
                                }
                            }
                        });
                        builder.show();
                    }
                });

            }

            @NonNull
            @Override
            public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_items_layout, parent, false);
                CartViewHolder cartViewHolder = new CartViewHolder(view);
                return cartViewHolder;
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }
      private void CheckOrderState(){
        DatabaseReference ordersRef;
        ordersRef=FirebaseDatabase.getInstance().getReference().child("Orders").child(Prevalent.currentOnlineUser.getPhone());
        ordersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if(dataSnapshot.exists())
          {
    String shippingState = dataSnapshot.child("state").getValue().toString();
    String userName= dataSnapshot.child("name").getValue().toString();
    if(shippingState.equals("shipped"))
    {
tctTotalAmount.setText("dear" +userName+"\n order is shipped succesfully" );
recyclerView.setVisibility(View.GONE);
txtMsg1.setVisibility(View.VISIBLE);
        txtMsg1.setText("Congratulations!! your order has been shipped successfully");
nextProcessBtn.setVisibility(View.GONE);

    }
    else if(shippingState.equals("not shipped"))
    {
        tctTotalAmount.setText("Shipping State =Not Shipped" );
        recyclerView.setVisibility(View.GONE);
        txtMsg1.setVisibility(View.VISIBLE);

        nextProcessBtn.setVisibility(View.GONE);
    }
           }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
}
}
