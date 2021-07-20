package com.example.splitwise_demo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddNewUser extends AppCompatActivity {
    Button logout, addNewGrp, addNewUserToGrp, addexpense, resolve;
    EditText newGrpName, validUserName;
    Spinner grpDropdown;
    FirebaseDatabase database;
    String uid;
    ArrayList< String> spinner_grp;
    ArrayAdapter<String> stringArrayAdapter;
    String temp, fname;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_user);
        grpDropdown =(Spinner) findViewById(R.id.grpSelection);
        validUserName = findViewById(R.id.addFriend);
        addNewUserToGrp = findViewById(R.id.addUserToGrpBtn);
        database = FirebaseDatabase.getInstance();
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        //DatabaseReference myRef = database.getReference();
        spinner_grp= new ArrayList<String>();
        stringArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line,spinner_grp);
        stringArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        grpDropdown.setAdapter(stringArrayAdapter);
        onRetrieve();
//adding new user
        addNewUserToGrp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fname = validUserName.getText().toString().trim();
                //adding friend to a group
                Map<String, Object> newf = new HashMap<>();
                newf.put(fname,0);
                DatabaseReference myRef = database.getReference();
                System.out.print("temp");
                temp  = grpDropdown.getSelectedItem().toString().trim();
                myRef.child("users").child(uid).child(temp).updateChildren(newf);
                spinner_grp.clear();
            }
        });
    }
    public void onRetrieve(){
        spinner_grp.clear();
        stringArrayAdapter.notifyDataSetChanged();
//iteration int group via datasnapshot
        DatabaseReference root = FirebaseDatabase.getInstance().getReference("users").child(uid);
        root.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                spinner_grp.clear();
                spinner_grp.add("Select group");
                for( DataSnapshot ds : snapshot.getChildren()){
                    String k = ds.getKey().toString().trim();
                    if(k.equals("fullname")||k.equals("email")){
                        continue;
                    }
                    else{
                        spinner_grp.add(k);
                    }
                }
                stringArrayAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
}}