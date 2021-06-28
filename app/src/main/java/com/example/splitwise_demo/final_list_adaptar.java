package com.example.splitwise_demo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


public class final_list_adaptar extends RecyclerView.Adapter<final_list_adaptar.ViewHolder> {
   private List<itemshow_help_class> userlist;
   public final_list_adaptar(List<itemshow_help_class>userlist){
       this.userlist = userlist;
   }
    @NonNull
    @Override
    public final_list_adaptar.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_design,parent,false);



        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final_list_adaptar.ViewHolder holder, int position) {
       String name = userlist.get(position).getTransactionName();
       holder.setData(name);
    }

    @Override
    public int getItemCount() {
        return userlist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

       private TextView transactionName;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            transactionName = itemView.findViewById(R.id.transactionName);
        }

        public void setData(String name) {
            transactionName.setText(name);
        }
    }
}
