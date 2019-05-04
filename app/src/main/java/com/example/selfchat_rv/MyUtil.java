package com.example.selfchat_rv;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
public class MyUtil {

    static class MsgCallback extends DiffUtil.ItemCallback<Messege> {

        @Override
        public boolean areItemsTheSame(@NonNull Messege oldItem, @NonNull Messege newItem) {
            return oldItem.equals(newItem);
        }

        @Override
        public boolean areContentsTheSame(@NonNull Messege oldItem, @NonNull Messege newItem) {
            return oldItem.equals(newItem);
        }
    }


    interface MsgClickCallback {
        void onMsgClick(Messege msg);
    }

    static class MsgAdapter
            extends ListAdapter<Messege, MsgHolder> {

        public MsgAdapter() {
            super(new MsgCallback());
        }

        public MsgClickCallback callback;

        @NonNull
        @Override
        public MsgHolder onCreateViewHolder(@NonNull ViewGroup parent, int itemType) {
            Context context = parent.getContext();
            View itemView = LayoutInflater.from(context).inflate(R.layout.interior, parent, false);
            final MsgHolder holder = new MsgHolder(itemView);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Messege msg = getItem(holder.getAdapterPosition());
                    if (callback != null)
                        callback.onMsgClick(msg);
                }
            });
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull MsgHolder msgHolder, int position) {
            Messege msg = getItem(position);
            msgHolder.text.setText(msg.getMsg());
        }
    }


    static class MsgHolder
            extends RecyclerView.ViewHolder {

        public TextView text;

        public MsgHolder(@NonNull View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.plain_text_output);
        }
    }
}
