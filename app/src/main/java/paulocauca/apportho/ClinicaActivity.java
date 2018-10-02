package paulocauca.apportho;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by guilh on 24/09/2017.
 */

public class ClinicaActivity extends AppCompatActivity {

    private EditText nomeClinica;
    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //get current user
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        setContentView(R.layout.activity_clinica);
        nomeClinica = (EditText) findViewById(R.id.nomeclinica);

        final Button button = (Button)findViewById(R.id.btn_salvarnovaclinica);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Clinica clinica = new Clinica();
                clinica.name = nomeClinica.getText().toString();

                DatabaseReference ref = FirebaseDatabase.getInstance()
                        .getReferenceFromUrl(Config.FIREBASE_URL);
                String teste = user.getUid();
                ref.child(user.getUid()).child("clinicas").push().setValue(clinica);
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
        startActivity(new Intent(ClinicaActivity.this, LoginActivity.class));

    }

    public void goBack() {
        auth.signOut();
        startActivity(new Intent(ClinicaActivity.this, MainActivity.class));
    }
}
