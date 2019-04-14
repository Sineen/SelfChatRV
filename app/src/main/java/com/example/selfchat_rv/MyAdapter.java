package com.example.selfchat_rv;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    public void setData(ArrayList<String> list) {
        myMessages = list;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        TextView textView;
        MyViewHolder(View v) {
            super(v);
            textView = ((TextView) v.findViewById(R.id.plain_text_output));
        }

        public void display(String message) {
            textView.setText(message);
        }

    }

    ArrayList<String> myMessages;

    public MyAdapter(){
        myMessages = new ArrayList<String>();
    }

    public void supportConfigurationChange() {
        notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        // create a new view
        View v = (View) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.interior, parent, false);

        MyViewHolder viewHolderh = new MyViewHolder(v);
        return viewHolderh;


    }


    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.display(myMessages.get(position));
    }

    @Override
    public int getItemCount() {
        return myMessages.size();
    }

    public void addMsg(String msg) {
        myMessages.add(msg);
        notifyDataSetChanged();

    }

    public ArrayList<String> getData() {
        return myMessages;
    }
}
