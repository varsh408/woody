package com.example.saru;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class AdminCategoryActivity extends AppCompatActivity {
private ImageView beds,dinings;
private ImageView cupboards,sofas;
private ImageView doors,lamps;
private Button CheckOrdersBtn, Logoutbtn,maintainProductsbtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_category);
        CheckOrdersBtn=(Button)findViewById(R.id.check_orders_btn);
       maintainProductsbtn=(Button)findViewById(R.id.maintain_btn);
        maintainProductsbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(AdminCategoryActivity.this,MenumainActivity.class);
                intent.putExtra("Admins","Admins");
                startActivity(intent);

            }
        });





        Logoutbtn=(Button)findViewById(R.id.admin_logout_btn);
        Logoutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(AdminCategoryActivity.this,MainActivity.class);
                intent.putExtra("Admin","Admin");
                startActivity(intent);
                finish();
            }
        });
        CheckOrdersBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(AdminCategoryActivity.this,AdminNewOrdersActivity.class);

                startActivity(intent);

            }
        });
        beds=(ImageView)findViewById(R.id.bed);
        dinings=(ImageView)findViewById(R.id.dining);
        cupboards=(ImageView)findViewById(R.id.cupboard);
        sofas=(ImageView)findViewById(R.id.sofa);
        doors=(ImageView)findViewById(R.id.door);
        lamps=(ImageView)findViewById(R.id.lamp);

        beds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(AdminCategoryActivity.this,AdminAddNewProduct.class);
                intent.putExtra("category","beds");
                startActivity(intent);
            }
        });
       dinings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(AdminCategoryActivity.this,AdminAddNewProduct.class);
                intent.putExtra("category","dinings");
                startActivity(intent);
            }
        });
       cupboards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(AdminCategoryActivity.this,AdminAddNewProduct.class);
                intent.putExtra("category","cupboards");
                startActivity(intent);
            }
        });

       sofas.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent=new Intent(AdminCategoryActivity.this,AdminAddNewProduct.class);
            intent.putExtra("category","sofas");
            startActivity(intent);
        }
    });

        lamps.setOnClickListener(new View.OnClickListener() {
     @Override
          public void onClick(View view) {
        Intent intent=new Intent(AdminCategoryActivity.this,AdminAddNewProduct.class);
        intent.putExtra("category","lamps");
        startActivity(intent);
        }
        });

       doors.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
        Intent intent=new Intent(AdminCategoryActivity.this,AdminAddNewProduct.class);
        intent.putExtra("category","doors");
        startActivity(intent);
        }
        });
    }

}
