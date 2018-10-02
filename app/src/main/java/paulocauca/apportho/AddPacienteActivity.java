package paulocauca.apportho;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class AddPacienteActivity extends AppCompatActivity {

    private EditText nomePaciente;
    private FirebaseAuth auth;
    private String clinicaId;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paciente);
        clinicaId = getIntent().getStringExtra("clinicaID");

        //get current user
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        nomePaciente = (EditText) findViewById(R.id.nomepaciente);

        final Button button = (Button)findViewById(R.id.btn_salvarnovopaciente);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                DatabaseReference ref = FirebaseDatabase.getInstance()
                        .getReferenceFromUrl(Config.FIREBASE_URL);
                final Paciente paciente = new Paciente();
                paciente.name = nomePaciente.getText().toString();
                paciente.clinica_id = clinicaId;

            /*    Query selectedClinica = ref.child(user.getUid()).child("clinicas").equalTo(clinicaId);
                selectedClinica.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                            Clinica clinica = postSnapshot.getValue(Clinica.class);
                            paciente.clinica_id = clinicaId;
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });*/

                ref.child(user.getUid()).child("pacientes").push().setValue(paciente);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.voltar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.Logout:
                signOut();
                break;
            case R.id.AddClinica:
                break;
            case R.id.voltar:
                goBack();
            default:
                break;
        }
        return true;
    }

    public void signOut() {
        auth.signOut();
        startActivity(new Intent(AddPacienteActivity.this, LoginActivity.class));
    }

    public void goBack() {
        auth.signOut();
        startActivity(new Intent(AddPacienteActivity.this, MainActivity.class));
    }
}
