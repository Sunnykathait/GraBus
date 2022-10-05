package com.example.grabus;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class searchPlacePage extends AppCompatActivity {

    TextView searchBusPage;

    ImageView search_img;
    EditText edt_search;
    ListView showPlaces , showBuses;

    FirebaseFirestore firebaseFirestore;

    ArrayList<String> bus_array , places_name;;

    ArrayAdapter<String> adapter , adapter2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_place_page);

        searchBusPage = findViewById(R.id.searchByBusNumber_btn);

        search_img = findViewById(R.id.btn_search_2);
        edt_search = findViewById(R.id.edttxt_searchByPlace);
        showPlaces = findViewById(R.id.showBus_lstview1);
        showBuses = findViewById(R.id.showBus_lstview2);

        firebaseFirestore = FirebaseFirestore.getInstance();

        bus_array = new ArrayList<String>();

        DocumentReference documentReference = firebaseFirestore.collection("Places").document("placeName");
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if(documentSnapshot.exists()){
                        places_name = (ArrayList<String>) documentSnapshot.get("places");
                        liskPlaces();
                    }
                }
            }
        });

        showPlaces.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String placesItemAtPosition = (String) (showPlaces.getItemAtPosition(i));
                edt_search.setText(placesItemAtPosition);
            }
        });

        edt_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searchPlacePage.this.adapter.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                bus_array.clear();
            }
        });

        search_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Query query = firebaseFirestore.collection("BusNumber");
                query = query.whereArrayContains("Places",edt_search.getText().toString());
                query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(error != null){
                            return;
                        }
                        if(value != null){
                            List<DocumentSnapshot> snapshots = value.getDocuments();
                            bus_array.clear();
                            for (DocumentSnapshot document : snapshots){
                                bus_array.add(document.getId());
                            }
                        }
//                        adapter2.notifyDataSetChanged();
                        adapter2 = new ArrayAdapter<String>(getApplicationContext() , android.R.layout.simple_list_item_1 , bus_array);
                        showBuses.setAdapter(adapter2);
//                        adapter2.notifyDataSetChanged();
                    }
                });
            }
        });


        searchBusPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext() , MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    private void initBuses(ArrayList<String> bus_array) {
        ArrayList<String> temp = bus_array;
        bus_array.clear();
        bus_array = temp;
    }

    private void liskPlaces() {
        adapter = new ArrayAdapter<String>(getApplicationContext() , android.R.layout.simple_list_item_1, (List<String>) places_name);
        showPlaces .setAdapter(adapter);
//        adapter.notifyDataSetChanged();
    }
}