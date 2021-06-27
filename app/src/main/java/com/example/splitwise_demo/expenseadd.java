package com.example.splitwise_demo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class expenseadd extends AppCompatActivity {
    Button addexpensebtn;
    EditText amount;
    Spinner groupSelect , friend1 , friend2 ;
    FirebaseDatabase database;
    String uid , temp , friend1_name , friend2_name , amt;

    ArrayList< String> spinner_grp;
    ArrayAdapter<String> stringArrayAdapter;
    ArrayList< String> spinner_grp_friend;
    ArrayAdapter<String> stringArrayAdapter_friend;
    Integer new_value1;
    Integer new_value2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        temp="";
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expenseadd);
        amount = findViewById(R.id.amount);
        groupSelect = findViewById(R.id.groupSelect);
        friend1 = findViewById(R.id.friend1);
        friend2 = findViewById(R.id.friend2);
        addexpensebtn = findViewById(R.id.addexpensebtn);
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();


        spinner_grp= new ArrayList<String>();
        stringArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line,spinner_grp);
        stringArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        groupSelect.setAdapter(stringArrayAdapter);
        onRetrieve();


        spinner_grp_friend = new ArrayList<String>();
        stringArrayAdapter_friend = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, spinner_grp_friend);
        stringArrayAdapter_friend.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        friend1.setAdapter(stringArrayAdapter_friend);
        friend2.setAdapter(stringArrayAdapter_friend);

        groupSelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                temp  = groupSelect.getSelectedItem().toString().trim();
                onRetrieve_friend();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        addexpensebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                friend1_name = friend1.getSelectedItem().toString().trim();
                friend2_name = friend2.getSelectedItem().toString().trim();

                amt = amount.getText().toString().trim();
                int value1 = Integer.parseInt(amt);

                DatabaseReference root1= FirebaseDatabase.getInstance().getReference("users").child(uid).child(temp);

                root1.child(friend1_name).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (!task.isSuccessful()) {
                            Log.e("firebase", "Error getting data", task.getException());
                        }
                        else {
                            int x = ((Number)task.getResult().getValue()).intValue();
                            x = x - value1;
                            Map<String, Object> updateAmnt = new HashMap<>();
                            updateAmnt.put(friend1_name, x);
                            root1.updateChildren(updateAmnt);
                        }
                    }
                });

                root1.child(friend2_name).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (!task.isSuccessful()) {
                            Log.e("firebase", "Error getting data", task.getException());
                        }
                        else {
                            int x = ((Number)task.getResult().getValue()).intValue();
                            x = x + value1;
                            Map<String, Object> updateAmnt = new HashMap<>();
                            updateAmnt.put(friend2_name, x);
                            root1.updateChildren(updateAmnt);
                        }
                    }
                });

//                root1.addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        for( DataSnapshot ds : snapshot.getChildren()){
//                            if(ds.getKey().equals(friend1_name)){
//                                int x = ((Number)ds.getValue()).intValue();
//                                x= x + value1;
//                                new_value1 = x;
//                                Toast.makeText(expenseadd.this, String.valueOf(new_value1), Toast.LENGTH_SHORT).show();
//                                break;
//                            }
//                        }
//                    }
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
//                Object x1 = (Integer)new_value1;
//                root1.child(friend1_name).setValue(x1);
//
//
//                root1.addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        for( DataSnapshot ds : snapshot.getChildren()){
//                            if(ds.getKey()==friend2_name){
//                                int x = ((Number)ds.getValue()).intValue();
//                                x = x - value1;
//                                new_value2 = x;
//                                Toast.makeText(expenseadd.this, String.valueOf(new_value2), Toast.LENGTH_SHORT).show();
//                                break;
//                            }
//                        }
//                    }
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
//                Object x2 = (Integer)new_value2;
//                root1.child(friend2_name).setValue(x2);
            }
        });
    }
    public void onRetrieve(){
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
    public void onRetrieve_friend(){
        spinner_grp_friend.clear();
        stringArrayAdapter_friend.notifyDataSetChanged();

        DatabaseReference root = FirebaseDatabase.getInstance().getReference("users").child(uid).child(temp);
        root.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                spinner_grp_friend.clear();
                spinner_grp_friend.add("select friend");
                for( DataSnapshot ds : snapshot.getChildren()){
                    String k = ds.getKey().toString().trim();
                    spinner_grp_friend.add(k);
                }
                stringArrayAdapter_friend.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}