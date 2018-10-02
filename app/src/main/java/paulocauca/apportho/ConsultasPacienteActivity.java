package paulocauca.apportho;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.util.CollectionUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import paulocauca.apportho.adapters.GenericModelAdapter;

public class ConsultasPacienteActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private static final String TAG = "AndroidCameraApi";
    private ImageButton takePictureButton;
    GenericModelAdapter adapter;
    ListView listView;
    private static final int NUMBER_OF_COLS = 4;
    List<Map<String, List<Photos>>> items = new ArrayList<Map<String, List<Photos>>>();
    Map<String, String> sectionHeaderTitles = new HashMap<String, String>();
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }
    private String cameraId;
    protected CameraDevice cameraDevice;
    protected CameraCaptureSession cameraCaptureSessions;
    protected CaptureRequest captureRequest;
    protected CaptureRequest.Builder captureRequestBuilder;
    private Size imageDimension;
    private ImageReader imageReader;
    private File file;
    private static final int REQUEST_CAMERA_PERMISSION = 200;
    private boolean mFlashSupported;
    private Handler mBackgroundHandler;
    private HandlerThread mBackgroundThread;
    private String pacienteId;
    private List<Photos> photosList;
    private StorageReference mStorageRef;
    private DatabaseReference mDataReference;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mDataReference = FirebaseDatabase.getInstance().getReference("photos");
        mStorageRef = FirebaseStorage.getInstance().getReference().child(FirebaseAuth.getInstance().getCurrentUser().getUid());


        //initGrid();
        //adapter = new GenericModelAdapter(this,R.layout.list_item, items, sectionHeaderTitles, NUMBER_OF_COLS, null);
        //listView.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        pacienteId = getIntent().getStringExtra("pacienteID");
        setContentView(R.layout.activity_consultapacientes);
        photosList = new ArrayList<Photos>();
        takePictureButton = (ImageButton)findViewById(R.id.cameraBt);
        takePictureButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(ConsultasPacienteActivity.this, CameraApiActivity.class);
                intent.putExtra("pacienteID", pacienteId);
                startActivity(intent);
            }
        });

        Query selectedClinica = FirebaseDatabase.getInstance().getReference().child(user.getUid()).child("pacientes").orderByChild("clinica_id").equalTo("-LLXVIqSe_-0daqG_6ah");

      /*  selectedClinica.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for(DataSnapshot uniqueKeySnapshot : dataSnapshot.getChildren()){
                        Paciente paciente = uniqueKeySnapshot.getValue(Paciente.class);
                        paciente.uid = uniqueKeySnapshot.getKey();
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/

        Query selectedPhotos = FirebaseDatabase.getInstance().getReference().child(user.getUid()).child("photos");

        selectedPhotos.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
               if (dataSnapshot.exists()) {
                   for(DataSnapshot uniqueKeySnapshot : dataSnapshot.getChildren()){
                       Photos photo = uniqueKeySnapshot.getValue(Photos.class);
                       photosList.add(photo);
                   }
               }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void initGrid()
    {
        List<String> datas = new ArrayList<String>();
        List<String> itemTypesList = new ArrayList<String>();
        Calendar cal = Calendar.getInstance(Locale.getDefault());
        for(Photos photo : photosList)
        {
            if(!datas.contains(photo.dataconsulta)){
                datas.add(photo.dataconsulta);
                itemTypesList.add(photo.dataconsulta);

                cal.setTimeInMillis(Long.parseLong(photo.timestamp));
                String date = DateFormat.format("dd/MM/yyyy", cal).toString();
                sectionHeaderTitles.put(photo.dataconsulta, date);
            }
        }

        for (String itemType : itemTypesList){
            Map<String, List<Photos>> map = new HashMap<String, List<Photos>>();
            List<Object> list = new ArrayList<Object>();

            for(Photos photo : photosList){
                String itemName = photo.timestamp;
                String countryOfOrigin = photo.timestamp;
            }

            map.put(itemType, photosList);
            items.add(map);
        }
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
        startActivity(new Intent(ConsultasPacienteActivity.this, LoginActivity.class));

    }

    public void goBack() {
        auth.signOut();
        startActivity(new Intent(ConsultasPacienteActivity.this, PacientesActivity.class));
    }

}
