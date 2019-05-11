package com.example.selfchat_rv;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.content.SharedPreferences;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import static com.example.selfchat_rv.MainActivity.DATA_LIST;

class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    public static final String PROJEC_ID = "oLi5oPOG4Q44q9nbekLE";
    public static final String PROJEC_ID_KEY = "project id";
    public static final String TIME_STAMP_KEY = "TimeStamp" ;
    public static final String TEXT_KEY = "Text";
    public static final String ID_KEY = "Id";
    public static final String COLLECTION = "messages";

    public int counterID = 0;

    private ArrayList<Message> myMessages;
    private recItemOnLongClick clicked;
    private int DataSize;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Gson gson;


    //FIREBASE
    // Access a Cloud Firestore instance from your Activity
    private FirebaseFirestore dataBase;

    MyAdapter(int size, SharedPreferences sp, SharedPreferences.Editor edit, FirebaseFirestore db){
        myMessages = new ArrayList<Message>();
        gson = new Gson();
        DataSize = size;
        sharedPreferences = sp;
        editor = edit;
        dataBase = db;
    }


    public  interface  recItemOnLongClick {
        void itemLongClick(View view, final int position);
    }


    void setData(ArrayList<Message> list) {
        myMessages = list;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        // each data item is just a string in this case and a time stamp
        TextView time;
        TextView textView;
        public MyViewHolder(View v) {
            super(v);
            textView = ((TextView) v.findViewById(R.id.plain_text_output));
            time = ((TextView) v.findViewById(R.id.timeStamp));
            v.setOnLongClickListener(this);
        }

//        public void display(String message) {
//            textView.setText(message);
//        }

        public boolean onLongClick(View v) {
            if (clicked != null) {
                clicked.itemLongClick(v, getAdapterPosition());
            }
            return true;
        }
    }



    public void supportConfigurationChange() {
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        // create a new view
        View v = (View) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.interior, parent, false);

        MyViewHolder viewHolderh = new MyViewHolder(v);
        return viewHolderh;


    }


    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
//        holder.display(myMessages.get(position));
        String message = myMessages.get(position).getText();
        String timestamp = myMessages.get(position).getTimeStamp( );
        holder.textView.setText(message);
        holder.time.setText(timestamp);

    }

    @Override
    public int getItemCount() {
        if (myMessages == null)
        {
            myMessages = new ArrayList<>();
            return 0;
        }
        return myMessages.size();
    }

    private void saveEditions()
    {
        editor.putInt(MainActivity.DATA_SIZE, DataSize);
        String wjson = gson.toJson(myMessages);
        editor.putString(DATA_LIST, wjson);
        editor.apply();
    }

    void addMsg(String msg, String id, String time) {
        myMessages.add(new Message(id, time, msg));
        DataSize ++;
        saveEditions();
        notifyDataSetChanged();

    }

    void deleteMessage(int position) {
        new DeleteFromFireBase().execute(myMessages.get(position).getId());
        myMessages.remove(position);
        DataSize --;
        saveEditions();
        notifyItemRemoved(position);
    }

    void setClickListener(recItemOnLongClick itemClick) {
        this.clicked = itemClick;
    }


    ArrayList<Message> getData() {

        return myMessages;
    }

    void loading(){
        String rjson = sharedPreferences.getString(DATA_LIST, "");
        Type type = new TypeToken<List<String>>() {
        }.getType();
        myMessages = gson.fromJson(rjson, type);
    }

    public int updateIDs(boolean flag)
    {
        if(flag){
            counterID++;}
        DocumentReference washingtonRef = dataBase.collection(COLLECTION).
                document(PROJEC_ID);

        washingtonRef
                .update(PROJEC_ID_KEY, counterID)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("", "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("", "Error updating document", e);
                    }
                });
        return counterID;
    }


    @SuppressLint("SimpleDateFormat")
    private
    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");


    // Add a new document to the fire base and increment local id
    public void AddMsgToFB(final String msgText){
        //create map and add text
        Map<String, Object> message = new HashMap<>();
        message.put(TEXT_KEY, msgText);

        //get set time
        String time = dateFormat.format(new Date());
        message.put(TIME_STAMP_KEY, time);
        // get set new id
        int id = updateIDs(true);
        message.put(ID_KEY, id);

        dataBase.collection(COLLECTION)
                .add(message)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("Success ", "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Fail ", "Error adding document", e);
                    }
                });
    }


    @SuppressLint("StaticFieldLeak")
    private class DeleteFromFireBase extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... strings) {
            dataBase.collection(COLLECTION).document(strings[0])
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("Success", "DocumentSnapshot successfully deleted!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("Fail ", "Error deleting document", e);
                        }
                    });
            return null;
        }
    }

    public void loadDataFromFB()
    {
        final ArrayList<Message> d = new ArrayList<Message>();
        dataBase.collection(COLLECTION)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            String id, timestamp, content;
                            Map<String, Object> msg;

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if(!document.getId().equals(PROJEC_ID))
                                {
                                    msg = document.getData();
                                    id = msg.get(ID_KEY) + "";
                                    timestamp = msg.get(TIME_STAMP_KEY) + "";
                                    content = msg.get(TEXT_KEY) + "";
                                    d.add(new Message(id, timestamp, content));
                                }
                            }

                            for (Message message: d)
                                addMsg(message.getId(), message.getTimeStamp(), message.getText());
                            loading();

                        } else {
                            Log.d(" ", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

}
