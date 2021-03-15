package com.test.database.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.test.database.DBHealper;
import com.test.database.R;
import com.test.database.UserModel;
import com.test.database.adpter.RecipesAdapter;
import com.test.database.models.RecipeModel;
import com.test.database.models.Result;
import com.test.database.network.NetworkServise;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private  FirebaseFirestore db;
    private RecyclerView rvRecipes;
    private static String TAG = "MAIN_ACTIVITY";
    private Button btnAdd;
    public Button btnDel;
    String id;
    public static String TABLE_NAME = "mytable";

    RecipesAdapter adapter;
    private ContentValues contentValues;
    private SQLiteDatabase sqLiteDatabase;
    private DBHealper dbHealper;
    private  Callback<RecipeModel> callback = new Callback<RecipeModel>() {
        @Override
        public void onResponse(Call<RecipeModel> call, Response<RecipeModel> response) {
            List<Result> resipes = response.body().getResults();
            adapter.setList(resipes);
            for(Result result; resipes){
               addData(result);
            }


        }

        @Override
        public void onFailure(Call<RecipeModel> call, Throwable t) {
            Toast.makeText(getBaseContext(), t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

        }
    };




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

        adapter = new RecipesAdapter();



        getRecipes("","",3).enqueue(callback);

        rvRecipes = findViewById(R.id.rvRecipes);
        rvRecipes.setLayoutManager(new LinearLayoutManager(this));
        rvRecipes.setAdapter(adapter);

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);


        btnAdd.setOnClickListener(v -> {
            delData();

        });

        btnDel.setOnClickListener(v -> {
            addData(user);
        });

    }

    private Call<RecipeModel> getRecipes(String  ingredients, String query, int pages){
        return NetworkServise.getInstance().getApi().getRecipes(ingredients, query, pages);

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

  /*  private void addData(String name, String email){
        contentValues = new ContentValues();
        contentValues.put(DBHealper.NAME,name);
        contentValues.put(DBHealper.EMAIL,email);

        sqLiteDatabase.insert(DBHealper.TABLE_NAME, null,contentValues);
        DatabaseReference scoresRef = FirebaseDatabase.getInstance().getReference("scores");
        scoresRef.keepSynced(true);
    }
*/
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