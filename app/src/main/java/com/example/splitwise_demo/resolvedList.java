package com.example.splitwise_demo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public class resolvedList extends AppCompatActivity {

    Button resolveBtn;
    Spinner groupSelect;
    FirebaseDatabase database;
    String uid;
    ArrayList< String> spinner_grp;
    ArrayAdapter<String> stringArrayAdapter;
    final_list_adaptar final_list_adaptar;
    Map<String, Integer> data = new HashMap<>();
    RecyclerView recylerview;
    LinearLayoutManager linearLayoutManager;
    List<itemshow_help_class> list;

    // original
    PriorityQueue<Pair<Integer, String>> neg = new PriorityQueue<>( 11,new Comparator<Pair<Integer, String>>() {
        @Override
        public int compare(Pair<Integer, String> o1, Pair<Integer, String> o2) {
            if (o1.first*(-1) < o2.first*(-1)) return +1;
            if (o1.first*(-1) == o2.first*(-1)) return 0;
            return -1;
        }
    });
    // neg value
    PriorityQueue<Pair<Integer, String>> pos = new PriorityQueue<>(11, new Comparator<Pair<Integer, String>>() {
        @Override
        public int compare(Pair<Integer, String> o1, Pair<Integer, String> o2) {
            if (o1.first*(-1) < o2.first*(-1)) return +1;
            if (o1.first*(-1) == o2.first*(-1)) return 0;
            return -1;
        }
    });
    ArrayList<Pair<String, Pair<String, Integer>>> ans = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resolved_list);

        resolveBtn = findViewById(R.id.resolveBtn);
        groupSelect =(Spinner) findViewById(R.id.groupSelect);

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        spinner_grp= new ArrayList<String>();
        stringArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line,spinner_grp);
        stringArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        groupSelect.setAdapter(stringArrayAdapter);
        onrRetrieve();

        resolveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String grpName = groupSelect.getSelectedItem().toString().trim();
//                Toast.makeText(resolvedList.this, grpName, Toast.LENGTH_SHORT).show();

                DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                DatabaseReference favoritesRef = rootRef.child("users").child(uid).child(grpName);

                favoritesRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull  DataSnapshot snapshot) {
                        for(DataSnapshot ds : snapshot.getChildren()) {
                            String key = ds.getKey();
                            Integer value = ds.getValue(Integer.class);
//                            Toast.makeText(resolvedList.this, key+"="+Integer.toString(value), Toast.LENGTH_SHORT).show();
                            data.put(key, value);
                        }
                        finalAns();
//                        print();    // For printing the map to check if data is fetched correctly
                     //   showOnapp();    // For printing the arrayLis to check the resolved amounts



                        // NOW HAVE TO IMPLEMENT RECYCLER VIEW

                        initData();
                        initRecyclerView();

                    }
                    @Override
                    public void onCancelled(@NonNull  DatabaseError error) {

                    }
                });


            }
        });

    }
    public void initData(){
    list = new ArrayList<>();

        for (int i = 0; i < ans.size(); i++){

                 list.add( new itemshow_help_class( "* "+ans.get(i).first+" pays "+ ans.get(i).second.second+" to "+ans.get(i).second.first));
        }




    }
    public void initRecyclerView(){
        recylerview =findViewById(R.id.recylerview);

        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recylerview.setLayoutManager(linearLayoutManager);
        final_list_adaptar = new final_list_adaptar(list);
        recylerview.setAdapter(final_list_adaptar);
        final_list_adaptar.notifyDataSetChanged();
    }

    public void finalAns(){
        for (Map.Entry<String,Integer> entry : data.entrySet()){
            if(entry.getValue()<0){
                neg.add(new Pair <> (entry.getValue(), entry.getKey()));
            }
            else if(entry.getValue()>0){
                pos.add(new Pair <> ((-1)*entry.getValue(), entry.getKey()));
            }
        }

        // To check that priority queue has the correct data
//        while (!pos.isEmpty()) {
//            Toast.makeText(resolvedList.this, pos.peek().first+" = "+pos.peek().second, Toast.LENGTH_SHORT).show();
//            pos.poll();
//        }
//        while (!neg.isEmpty()) {
//            Toast.makeText(resolvedList.this, neg.peek().first+" = "+neg.peek().second, Toast.LENGTH_SHORT).show();
//            neg.poll();
//        }

        while(!neg.isEmpty() || !pos.isEmpty()){
            Integer negVal = neg.peek().first*(-1);
            String negName = neg.peek().second;
            Integer posVal = pos.peek().first*(-1);
            String posName = pos.peek().second;
            if(negVal > posVal){
                // delete from pos
                // pos pays to neg amnt
                Pair<String, Integer> temp = new Pair<>(neg.peek().second, posVal);
                ans.add(new Pair <> (pos.peek().second, temp));
                neg.poll();
                neg.add(new Pair<> ((negVal-posVal)*(-1), negName));
                pos.poll();
            }else {
                // delete from neg
                // pos pays to neg amnt
                Pair<String, Integer> temp = new Pair<>(neg.peek().second, negVal);
                ans.add(new Pair <> (pos.peek().second, temp));
                pos.poll();
                if(negVal!=posVal) {
                    pos.add(new Pair<>((posVal - negVal) * (-1), posName));
                }
                neg.poll();
            }
        }
    }

    public void showOnapp(){
        for (int i = 0; i < ans.size(); i++){
         //   Toast.makeText(resolvedList.this,
           //         ans.get(i).first+" pays "+ ans.get(i).second.second+" to "+ans.get(i).second.first,
             //       Toast.LENGTH_SHORT).show();
        }
    }

    public void print(){
        for (Map.Entry<String,Integer> entry : data.entrySet()){
            Toast.makeText(resolvedList.this, entry.getKey()+"="+Integer.toString(entry.getValue()), Toast.LENGTH_SHORT).show();
        }
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
