package com.example.splitwise_demo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Main Activity";
    Button logout, addNewGrp, addNewUserToGrp, addexpense;
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
        setContentView(R.layout.activity_main);

        addexpense = findViewById(R.id.addexpense);
        logout = findViewById(R.id.logoutBtn);
        addNewGrp = findViewById(R.id.addNewGrpBtn);
        addNewUserToGrp = findViewById(R.id.addUserToGrpBtn);
        newGrpName = findViewById(R.id.newGroup);
        validUserName = findViewById(R.id.addFriend);
        grpDropdown =(Spinner) findViewById(R.id.grpSelection);
        database = FirebaseDatabase.getInstance();
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(), Login.class));
                finish();
            }
        });

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        List<String> grps= new ArrayList<String>();
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference favoritesRef = rootRef.child("users").child(uid).child("groups");
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    String key = ds.getKey();
                    String value = ds.getValue(String.class);
                    grps.add(value);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        favoritesRef.addListenerForSingleValueEvent(eventListener);


        addNewGrp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference myRef = database.getReference();

                String grpName = newGrpName.getText().toString().trim();
                final String[] name = new String[1];
                //extracting name of the user
                DatabaseReference favoritesRef = rootRef.child("users").child(uid).child("fullname");
                ValueEventListener eventListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        name[0] = dataSnapshot.getValue(String.class);
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                };
                favoritesRef.addListenerForSingleValueEvent(eventListener);


                //if present do not create flag error
                //else make one
                grps.add(grpName);
                Map<String, Object> newGRP = new HashMap<>();
                newGRP.put(grps.get(grps.size()-1),Integer.toString(grps.size()-1) );
                // myRef.child("users").child(uid).child("groups").updateChildren(newGRP);
                myRef.child("users").child(uid).updateChildren(newGRP).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(MainActivity.this, grps.get(grps.size()-1)+" added to your group list ", Toast.LENGTH_SHORT).show();
                        spinner_grp.clear();
                        onrRetrieve();
                        stringArrayAdapter.notifyDataSetChanged();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        Toast.makeText(MainActivity.this, "Error in adding grpname to DB!", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

         addexpense.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 startActivity(new Intent(getApplicationContext(), expenseadd.class));

             }
         });


        spinner_grp= new ArrayList<String>();
        stringArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line,spinner_grp);
        stringArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        grpDropdown.setAdapter(stringArrayAdapter);
        onrRetrieve();

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


    // to retrieve data
    public void onrRetrieve(){
        spinner_grp.clear();
        stringArrayAdapter.notifyDataSetChanged();

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
    }
}

