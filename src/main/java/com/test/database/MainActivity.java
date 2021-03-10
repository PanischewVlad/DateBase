package com.test.database;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private  FirebaseFirestore db;
    private RecyclerView rvList;
    private static String TAG = "MAIN_ACTIVITY";
    private Button btnAdd;
    public Button btnDel;
    String id;
    public static String TABLE_NAME = "mytable";


    private ContentValues contentValues;
    private SQLiteDatabase sqLiteDatabase;
    private DBHealper dbHealper;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = FirebaseFirestore.getInstance();
        Map<String, Object> user = new HashMap<>();
        user.put("first", "Ada");
        user.put("last", "Lovelace");
        user.put("born", 1815);




     /*   db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                Log.d(TAG, "Value is: " + value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });*/
        btnAdd = findViewById(R.id.btnAdd);
        btnDel = findViewById(R.id.btnDel);
        dbHealper = new DBHealper(this);
        connectToDatabase();
        rvList = findViewById(R.id.rvList);



        btnAdd.setOnClickListener(v -> {
            delData();

        });

        btnDel.setOnClickListener(v -> {
            addData(user);
        });

    }


    private void addData(Map<String, Object> user){// Create a new user with a first and last name
// Add a new document with a generated ID
        db.collection("users")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }

    private void addData(String name, String email){
        contentValues = new ContentValues();
        contentValues.put(DBHealper.NAME,name);
        contentValues.put(DBHealper.EMAIL,email);

        sqLiteDatabase.insert(DBHealper.TABLE_NAME, null,contentValues);
        DatabaseReference scoresRef = FirebaseDatabase.getInstance().getReference("scores");
        scoresRef.keepSynced(true);
    }

    private void connectToDatabase(){
        sqLiteDatabase =  dbHealper.getWritableDatabase();
    }

private Cursor getData(){
       Cursor cursor = sqLiteDatabase.query(DBHealper.TABLE_NAME,null ,null ,null ,null ,null ,null );
       return cursor;
}

private ArrayList<UserModel> parsData(Cursor cursor){
        ArrayList<UserModel> tmp = new ArrayList<>();

        if (cursor.moveToFirst()){
            int idColIdx = cursor.getColumnIndex(DBHealper.ID);
            int nameColIdx = cursor.getColumnIndex(DBHealper.NAME);
            int emailColIdx = cursor.getColumnIndex(DBHealper.EMAIL);

            do{
                long id = cursor.getLong(idColIdx);
                String name = cursor.getString(nameColIdx);
                String email = cursor.getString(emailColIdx);
                UserModel userModel = new UserModel(id, name, email);
                tmp.add(userModel);
            }while (cursor.moveToNext());

        }
        return tmp;
}



    private void   delData(){
       sqLiteDatabase.delete(DBHealper.TABLE_NAME, null,null);

}
}