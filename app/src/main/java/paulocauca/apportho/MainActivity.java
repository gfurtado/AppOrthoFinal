package paulocauca.apportho;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by guilh on 01/07/2017.
 */

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    ArrayList<String> list = new ArrayList<>();
    ArrayList<Clinica> clinicas = new ArrayList<>();
    private ChildEventListener clinicaListener;

    private FirebaseUser user;
    private DatabaseReference mDatabase;
    ListView listaClinicas;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listclinica);

        user = FirebaseAuth.getInstance().getCurrentUser();
        auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child(user.getUid());
    }

    private AdapterView.OnItemClickListener listClick = new AdapterView.OnItemClickListener () {
        public void onItemClick(AdapterView parent, View v,int position, long id){
            String itemValue = (String) listaClinicas.getItemAtPosition(position);
            Clinica cl = clinicas.get(position);

            Intent intent = new Intent(MainActivity.this, PacientesActivity.class);
            intent.putExtra("clinicaID", cl.uid);
            startActivity(intent);
        }
    };

    @Override
    protected void onStart() {
        super.onStart();

        listaClinicas = (ListView) findViewById(R.id.listaClinicas);
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,list);
        listaClinicas.setAdapter(adapter);
        listaClinicas.setOnItemClickListener(listClick);


        mDatabase.child("clinicas").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot uniqueKeySnapshot : dataSnapshot.getChildren()){
                    Clinica clinica = uniqueKeySnapshot.getValue(Clinica.class);
                    clinica.setUID(uniqueKeySnapshot.getKey());
                    list.add(clinica.name);
                    clinicas.add(clinica);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    protected void onStop() {
        super.onStop();

        if (clinicaListener != null) {
            mDatabase.removeEventListener(clinicaListener);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.addclinica, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.Logout:
                signOut();
                break;
            case R.id.AddClinica:
                goToClinicas();
                break;
            default:
                break;
        }
        return true;
    }

    public void signOut() {
        auth.signOut();
        startActivity(new Intent(MainActivity.this, LoginActivity.class));

    }

    public void goToClinicas() {
        startActivity(new Intent(MainActivity.this, ClinicaActivity.class));

    }
}
