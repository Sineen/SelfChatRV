package com.example.selfchat_rv;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

import androidx.annotation.RequiresApi;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "messages")
public class Messege implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    int id;

    @ColumnInfo(name = "msg")
    final String msg;

    public Messege(String msg) {
        this.msg = msg;
    }

    protected Messege(Parcel in) {
        id = in.readInt();
        msg = in.readString();
    }

    public static final Creator<Messege> CREATOR = new Creator<Messege>() {
        @Override
        public Messege createFromParcel(Parcel in) {
            return new Messege(in);
        }

        @Override
        public Messege[] newArray(int size) {
            return new Messege[size];
        }
    };

    public String getMsg() {
        return msg;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public int hashCode() {
        return Objects.hash(msg);
    }

    @Override
    public int describeContents() {
        return this.id;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(msg);
        parcel.writeInt(id);
    }
}
