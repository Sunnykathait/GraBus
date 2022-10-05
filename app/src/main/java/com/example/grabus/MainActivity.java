package com.example.grabus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    TextView searchPlace_btn;

    ImageView search_img;
    EditText edt_search;
    ListView showPlaces;

    FirebaseFirestore firebaseFirestore;

    ArrayList<String> places_array;

    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseFirestore = FirebaseFirestore.getInstance();

        searchPlace_btn = findViewById(R.id.searchByplace_btn);

        search_img = findViewById(R.id.btn_search);
        edt_search = findViewById(R.id.edttxt_searchByBus);
        showPlaces = findViewById(R.id.showPlaces_lstview);

        places_array = new ArrayList<>();

        search_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(edt_search.getText().toString().trim().length() == 0){
                    edt_search.setError("No bus number selected");
                    return;
                }else{
                    edt_search.setError(null);

                    DocumentReference documentReference = firebaseFirestore.collection("BusNumber").document(edt_search.getText().toString());
                    documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()){
                                DocumentSnapshot documentSnapshot = task.getResult();
                                if(documentSnapshot.exists()){
                                    places_array = (ArrayList<String>) documentSnapshot.get("Places");
                                    adapter = new ArrayAdapter<String>(getApplicationContext() , android.R.layout.simple_list_item_1, (List<String>) places_array);
                                    showPlaces.setAdapter(adapter);
                                    adapter.notifyDataSetChanged();
                                }else{
                                    Toast.makeText(getApplicationContext() , "No bus found with this number",Toast.LENGTH_SHORT).show();
                                }
                            }else {
                                Toast.makeText(getApplicationContext() , "Failed with " + task.getException(),Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


                }
            }
        });

        searchPlace_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext() , searchPlacePage.class);
                startActivity(intent);
                finish();
            }
        });

    }
}