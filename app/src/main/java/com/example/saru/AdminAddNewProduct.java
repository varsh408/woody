package com.example.saru;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class AdminAddNewProduct extends AppCompatActivity {
    private String CategoryName,Desciption,Price,Pname,saveCurrentDate,saveCurrentTime;
    private Button AddNewProductButton;
    private ImageView InputProductImage;
    private EditText InputProductName,InputProductDescription,InputProductPrice;
    private static final int GalleryPick=1;
    private Uri ImageUri;
    private String productRandomKey,downloadImageUrl;
    private  StorageReference ProductImageRef;
    private DatabaseReference ProductsRef;
    private ProgressDialog loadingbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_new_product);
        Toast.makeText(AdminAddNewProduct.this,"welcome admin",Toast.LENGTH_SHORT).show();
        CategoryName=getIntent().getExtras().get("category").toString();
        ProductImageRef= FirebaseStorage.getInstance().getReference().child("Product Images");
        ProductsRef=FirebaseDatabase.getInstance().getReference().child("Products");




        AddNewProductButton=(Button)findViewById(R.id.add_new_product);
        InputProductImage=(ImageView)findViewById(R.id.select_product_image);
        InputProductName=(EditText)findViewById(R.id.product_name);
        InputProductDescription=(EditText)findViewById(R.id.product_description);
        InputProductPrice=(EditText)findViewById(R.id.product_price);
        loadingbar=new ProgressDialog(this);
        InputProductImage.setOnClickListener(   new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenGallery();
            }
        });
        AddNewProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ValidateProductData();
            }
        });
    }

    private void OpenGallery()
    {
        Intent galleryIntent=new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,GalleryPick);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==GalleryPick && resultCode==RESULT_OK && data!=null){
            ImageUri=data.getData();
            InputProductImage.setImageURI(ImageUri);
        }
    }
    private void ValidateProductData(){

   Desciption=InputProductDescription.getText().toString();
  Price=InputProductPrice.getText().toString();
  Pname =InputProductDescription.getText().toString();
  if(ImageUri==null){
    Toast.makeText(this, "Product Image is Mandatory", Toast.LENGTH_SHORT).show();
}
else if(TextUtils.isEmpty(Desciption))  {
    Toast.makeText(this, "Product Description is Mandatory", Toast.LENGTH_SHORT).show();
}

else if(TextUtils.isEmpty(Pname))  {
    Toast.makeText(this, "Product Product Name Is Mandatory", Toast.LENGTH_SHORT).show();
}

else if(TextUtils.isEmpty(Price))  {
    Toast.makeText(this, "Product Price Is Mandatory", Toast.LENGTH_SHORT).show();
}
else {
    StoreProductInformation();
}

    }

    private void StoreProductInformation() {
     loadingbar.setTitle("Add New Product   ");
            loadingbar.setMessage("please wait while we are your new product");
            loadingbar.setCanceledOnTouchOutside(false);
            loadingbar.show();



        Calendar calendar=Calendar.getInstance();
        SimpleDateFormat currentDate=new SimpleDateFormat("MMM dd,yyy");
        saveCurrentDate=currentDate.format(calendar.getTime());
        SimpleDateFormat currentTime=new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime=currentTime.format(calendar.getTime());
        productRandomKey=saveCurrentDate+saveCurrentTime;
        final StorageReference filepath=ProductImageRef.child(ImageUri.getLastPathSegment()+productRandomKey+".jpg");
        final UploadTask uploadTask=filepath.putFile(ImageUri);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e)
            {
String message=e.toString();
Toast.makeText(AdminAddNewProduct.this,"Error:"+message,Toast.LENGTH_SHORT).show();
loadingbar.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
Toast.makeText(AdminAddNewProduct.this,"Image uploaded successfully",Toast.LENGTH_SHORT).show();
                Task<Uri>urlTask=uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception
                    {
                       if(!task.isSuccessful()){
                           throw task.getException();
                       }
                       downloadImageUrl=filepath.getDownloadUrl().toString();
                       return filepath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task)
                    {
                        if(task.isSuccessful()){
                            downloadImageUrl=task.getResult().toString();
                            Toast.makeText(AdminAddNewProduct.this,"getting Product Image Url",Toast.LENGTH_SHORT).show();
                            saveProductInfoToDatabase();
                            
                        }
                    }
                });
            }
        });
    }

    private void saveProductInfoToDatabase() {
        HashMap<String,Object>productMap=new HashMap<>();
        productMap.put("pid",productRandomKey);
        productMap.put("date",saveCurrentDate);
        productMap.put("time",saveCurrentTime);
        productMap.put("description",Desciption);
        productMap.put("image",downloadImageUrl);
        productMap.put("category",CategoryName);
        productMap.put("price",Price);
        productMap.put("name",Pname);
        ProductsRef.child(productRandomKey).updateChildren(productMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            Intent intent=new Intent(AdminAddNewProduct.this,AdminCategoryActivity.class);
                            startActivity(intent);
                            loadingbar.dismiss();
                            Toast.makeText(AdminAddNewProduct.this,"Product Is Added Successfully.....",Toast.LENGTH_SHORT).show();
                        }
                        else {
                            loadingbar.dismiss();
                            String message=task.getException().toString();
                            Toast.makeText(AdminAddNewProduct.this,"Error"+message,Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }
}
