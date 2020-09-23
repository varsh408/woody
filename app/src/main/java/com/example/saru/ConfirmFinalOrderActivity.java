package com.example.saru;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.saru.Prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Hashtable;

public class ConfirmFinalOrderActivity extends AppCompatActivity
{

    private EditText nameEditText,phoneEditText,cityEditText,addressEditText;
    private Button confirmOrderBtn;
    private String totalAmunt="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_final_order);

        totalAmunt=getIntent().getStringExtra("Total Price");
confirmOrderBtn=(Button)findViewById(R.id.confirm_final_order_btn);
nameEditText=(EditText)findViewById(R.id.shipment_name);
phoneEditText=(EditText)findViewById(R.id.shipment_phone_number);
addressEditText=(EditText)findViewById(R.id.shipment_address);
cityEditText=(EditText)findViewById(R.id.shipment_city);

confirmOrderBtn.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view)
    {
    check();
    }
});

    }

    private void check()
    {
        if(TextUtils.isEmpty(nameEditText.getText().toString())){
            Toast.makeText(this, "Please provide your full name", Toast.LENGTH_SHORT).show();
        }
      else if(TextUtils.isEmpty(phoneEditText.getText().toString())){
            Toast.makeText(this, "Please provide your mobile number", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(addressEditText.getText().toString())){
            Toast.makeText(this, "Please provide your complete address", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(cityEditText.getText().toString())){
            Toast.makeText(this, "Please provide your city", Toast.LENGTH_SHORT).show();
        }
        else{
            confirmOrder();
        }

    }

    private void confirmOrder()
    {
        final String  saveCurrentDate, saveCurrentTime;
        Calendar callForDate=Calendar.getInstance();
        SimpleDateFormat currentDate=new SimpleDateFormat("MMM dd,yyyy");
        saveCurrentDate=currentDate.format(callForDate.getTime());
        SimpleDateFormat currentTime=new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime=currentDate.format(callForDate.getTime());
        final DatabaseReference ordersRef= FirebaseDatabase.getInstance().getReference().child("Orders").child(Prevalent.currentOnlineUser.getPhone());
        HashMap<String, Object> ordersMap=new HashMap<>();

        ordersMap.put("totalAmunt",totalAmunt);
        ordersMap.put("name",nameEditText.getText().toString());
        ordersMap.put("phone",phoneEditText.getText().toString());
        ordersMap.put("address",addressEditText.getText().toString());
        ordersMap.put("city",cityEditText.getText().toString());
        ordersMap.put("date",saveCurrentDate);
        ordersMap.put("time",saveCurrentTime);
        ordersMap.put("state","not shipped");

        ordersRef.updateChildren(ordersMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
           if(task.isSuccessful())
           {
               FirebaseDatabase.getInstance().getReference().child("Cart List")
                       .child("Users View")
                       .child(Prevalent.currentOnlineUser.getPhone())
                       .removeValue()
                       .addOnCompleteListener(new OnCompleteListener<Void>() {
                   @Override
                   public void onComplete(@NonNull Task<Void> task) {
if(task.isSuccessful()){
    Toast.makeText(ConfirmFinalOrderActivity.this,"Your Order Has Been Placed",Toast.LENGTH_SHORT).show();
    Intent intent=new Intent(ConfirmFinalOrderActivity.this,MenumainActivity.class);
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    startActivity(intent);
    finish();
}
                   }
               });
           }
            }
        });

    }

}
