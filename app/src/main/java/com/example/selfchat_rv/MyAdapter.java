package com.example.selfchat_rv;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

import static com.example.selfchat_rv.MainActivity.DATA_LIST;

class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    public  interface  recItemOnLongClick {
        void itemLongClick(View view, final int position);
    }


    private ArrayList<String> myMessages;
    private recItemOnLongClick clicked;
    private int DataSize;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Gson gson;



    public void setData(ArrayList<String> list) {
        myMessages = list;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        // each data item is just a string in this case
        TextView textView;
        public MyViewHolder(View v) {
            super(v);
            textView = ((TextView) v.findViewById(R.id.plain_text_output));
            v.setOnLongClickListener(this);
        }

        public void display(String message) {
            textView.setText(message);
        }

        public boolean onLongClick(View v) {
            if (clicked != null) {
                clicked.itemLongClick(v, getAdapterPosition());
            }
            return true;
        }
    }


    public MyAdapter( int size, SharedPreferences sp, SharedPreferences.Editor edit){
        myMessages = new ArrayList<String>();
        gson = new Gson();
        DataSize = size;
        sharedPreferences = sp;
        editor = edit;
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

    public void saveEditions()
    {
        editor.putInt(MainActivity.DATA_SIZE, DataSize);
        String wjson = gson.toJson(myMessages);
        editor.putString(DATA_LIST, wjson);
        editor.apply();
    }

    public void addMsg(String msg) {
        myMessages.add(msg);
        DataSize ++;
        saveEditions();
        notifyDataSetChanged();

    }

    public void deleteMessage(int position) {
        myMessages.remove(position);
        DataSize --;
        saveEditions();
        notifyDataSetChanged();
    }

    public void setClickListener(recItemOnLongClick itemClick) {
        this.clicked = itemClick;
    }


    public ArrayList<String> getData() {
        return myMessages;
    }

    public void loading( ){
        String rjson = sharedPreferences.getString(DATA_LIST, "");
        Type type = new TypeToken<List<String>>() {
        }.getType();
        myMessages = gson.fromJson(rjson, type);
    }
}
