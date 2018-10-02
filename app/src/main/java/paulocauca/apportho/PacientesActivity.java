package paulocauca.apportho;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PacientesActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    ArrayList<String> list = new ArrayList<>();
    ArrayList<Paciente> pacientes = new ArrayList<>();
    private ChildEventListener pacienteListener;
    private String clinicaId;
    ListView listaPacientes;

    private FirebaseUser user;

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listpacientes);
        clinicaId = getIntent().getStringExtra("clinicaID");
        user = FirebaseAuth.getInstance().getCurrentUser();
//      mDatabase = FirebaseDatabase.getInstance().getReference().child(user.getUid()).child("pacientes").orderByChild("clinica_id").equalTo(clinicaId);


    }

    private AdapterView.OnItemClickListener listClick = new AdapterView.OnItemClickListener () {
        public void onItemClick(AdapterView parent, View v, int position, long id){
            String itemValue = (String) listaPacientes.getItemAtPosition(position);
            Paciente pc = pacientes.get(position);

            Intent intent = new Intent(PacientesActivity.this, ConsultasPacienteActivity.class);
            intent.putExtra("pacienteID", pc.uid);
            startActivity(intent);
        }
    };

    @Override
    protected void onStart() {
        super.onStart();

        listaPacientes = (ListView) findViewById(R.id.listaPacientes);
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,list);
        listaPacientes.setAdapter(adapter);
        listaPacientes.setOnItemClickListener(listClick);

        Query selectedClinica = FirebaseDatabase.getInstance().getReference().child(user.getUid()).child("pacientes").orderByChild("clinica_id").equalTo(clinicaId);

        selectedClinica.addListenerForSingleValueEvent(new ValueEventListener() {
           @Override
           public void onDataChange(DataSnapshot dataSnapshot) {
               if (dataSnapshot.exists()) {
                   for(DataSnapshot uniqueKeySnapshot : dataSnapshot.getChildren()){
                       Paciente paciente = uniqueKeySnapshot.getValue(Paciente.class);
                       paciente.uid = uniqueKeySnapshot.getKey();
                       list.add(paciente.name);
                       pacientes.add(paciente);
                   }
                   adapter.notifyDataSetChanged();
               }
           }
           @Override
           public void onCancelled(DatabaseError databaseError) {

           }
       });


      /*  mDatabase.child("pacientes").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot uniqueKeySnapshot : dataSnapshot.getChildren()){
                    Paciente paciente = uniqueKeySnapshot.getValue(Paciente.class);
                    paciente.uid = uniqueKeySnapshot.getKey();
                    list.add(paciente.name);
                    pacientes.add(paciente);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/

    }

    @Override
    protected void onStop() {
        super.onStop();

        if (pacienteListener != null) {
            mDatabase.removeEventListener(pacienteListener);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.addpaciente, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.Logout:
                signOut();
                break;
            case R.id.AddPaciente:
                goToCreatePaciente();
                break;
            default:
                break;
        }
        return true;
    }

    public void signOut() {
        auth.signOut();
        startActivity(new Intent(PacientesActivity.this, LoginActivity.class));

    }

    public void goToCreatePaciente() {
        Intent intent = new Intent(PacientesActivity.this, AddPacienteActivity.class);
        intent.putExtra("clinicaID", clinicaId);
        startActivity(intent);
    }
}
