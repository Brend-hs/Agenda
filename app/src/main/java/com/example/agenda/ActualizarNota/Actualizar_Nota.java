package com.example.agenda.ActualizarNota;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.agenda.AgregarNota.Agregar_Nota;
import com.example.agenda.CategoriasNota.Categorias_Nota;
import com.example.agenda.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class Actualizar_Nota extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    TextView Id_nota_A, Uid_Usuario_A, Correo_Usuario_A, Fecha_hora_actual_A, Fecha_A, Hora_A, Estado_A, Contacto_A, Estado_nuevo;
    EditText Titulo_A, Descripcion_A;
    Button Btn_Calendario_A, Btn_Hora_A, Btn_Contactos_A;

    String id_nota_R, uid_Usuario_R, correo_Usuario_R, fecha_hora_actual_R, titulo_R, descripcion_R, fecha_R, hora_R, estado_R, contacto_R, notificacion_R, categoria_R;

    int mes, dia, anio, hora, minuto;

    Spinner SpinnerNotificacion_A, SpinnerCategoria_A, Spinner_estado;
    ImageView Tarea_Finalizada, Tarea_No_Finalizada;

    int REQUEST_CONTACT = 1;
    String ContactoSeleccionado = "";
    FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference BD_Firebase;
    ArrayList<String> categorias;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_actualizar_nota);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Actualizar tarea");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        InicializarVistas();
        RecuperarDatos();
        SetearDatos();
        SeleccionarFecha();
        Establecer_Hora();
        EstablecerNotificacion();
        EstablecerCategoria();
        Obtener_Contacto();
        ComprobarEstadoNota();
        Spinner_Estado();
    }

    private void InicializarVistas(){
        Id_nota_A = findViewById(R.id.Id_nota_A);
        Uid_Usuario_A = findViewById(R.id.Uid_Usuario_A);
        Correo_Usuario_A = findViewById(R.id.Correo_Usuario_A);
        Fecha_hora_actual_A = findViewById(R.id.Fecha_hora_actual_A);
        Fecha_A = findViewById(R.id.Fecha_A);
        Hora_A = findViewById(R.id.Hora_A);
        Estado_A = findViewById(R.id.Estado_A);
        Contacto_A = findViewById(R.id.Contacto_A);

        Titulo_A = findViewById(R.id.Titulo_A);
        Descripcion_A = findViewById(R.id.Descripcion_A);
        Btn_Calendario_A = findViewById(R.id.Btn_Calendario_A);
        Btn_Hora_A = findViewById(R.id.Btn_Hora_A);
        Btn_Contactos_A = findViewById(R.id.Btn_Contactos_A);

        SpinnerNotificacion_A = findViewById(R.id.SpinnerNotificacion_A);
        SpinnerCategoria_A = findViewById(R.id.SpinnerCategoria_A);

        Tarea_Finalizada = findViewById(R.id.Tarea_Finalizada);
        Tarea_No_Finalizada = findViewById(R.id.Tarea_No_Finalizada);

        Spinner_estado = findViewById(R.id.Spinner_estado);
        Estado_nuevo = findViewById(R.id.Estado_nuevo);

        BD_Firebase = FirebaseDatabase.getInstance().getReference("Usuarios");
    }

    private void RecuperarDatos(){
        Bundle intent = getIntent().getExtras();

        id_nota_R = intent.getString("id_nota");
        uid_Usuario_R = intent.getString("uid_usuario");
        correo_Usuario_R = intent.getString("correo");
        fecha_hora_actual_R = intent.getString("fecha_hora_actual");
        titulo_R = intent.getString("titulo");
        descripcion_R = intent.getString("descripcion");
        fecha_R = intent.getString("fecha_nota");
        hora_R = intent.getString("hora_nota");
        notificacion_R = intent.getString("notificacion");
        categoria_R = intent.getString("categoria");
        estado_R = intent.getString("estado");
        contacto_R = intent.getString("contacto");
    }
    private void EstablecerNotificacion() {
        ArrayAdapter<CharSequence> notificacionAdapter = ArrayAdapter.createFromResource(Actualizar_Nota.this,
                R.array.Opciones_Notificacion, android.R.layout.simple_spinner_item);
        notificacionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SpinnerNotificacion_A.setAdapter(notificacionAdapter);
    }

    private void SeleccionarFecha(){
        Btn_Calendario_A.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendario = Calendar.getInstance();
                dia = calendario.get(Calendar.DAY_OF_MONTH);
                mes = calendario.get(Calendar.MONTH);
                anio = calendario.get(Calendar.YEAR);

                DatePickerDialog datePickerDialog = new DatePickerDialog(Actualizar_Nota.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int AnioSeleccionado, int MesSeleccionado, int DiaSeleccionado) {
                        String diaFormateado, mesFormateado;

                        //Obtener dia
                        if(DiaSeleccionado<10){
                            diaFormateado = "0"+String.valueOf(DiaSeleccionado); //9/mes/año -> 09/mes/año
                        }else{
                            diaFormateado = String.valueOf(DiaSeleccionado);
                        }

                        //Obtener el mes
                        int Mes = MesSeleccionado+1; //Para que no inicialice en 0
                        if(Mes<10){
                            mesFormateado = "0"+String.valueOf(Mes);// dia/9/año -> dia/09/año
                        }else{
                            mesFormateado = String.valueOf(Mes);
                        }

                        //Seter fecha en textview
                        Fecha_A.setText(diaFormateado+"/"+mesFormateado+"/"+AnioSeleccionado);
                    }
                }
                        ,anio,mes,dia);
                datePickerDialog.show();
            }
        });
    }

    private void Establecer_Hora(){
        Btn_Hora_A.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendario = Calendar.getInstance();
                hora = calendario.get(Calendar.HOUR_OF_DAY);
                minuto = calendario.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(Actualizar_Nota.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int HoraSeleccionada, int MinutoSeleccionado) {
                        String horaFormateada, minutoFormateado;
                        String AM_PM="AM";
                        //Obtener hora
                        if(HoraSeleccionada>=12){
                            AM_PM="PM";
                            if(HoraSeleccionada>=13 && HoraSeleccionada<24){
                                HoraSeleccionada-=12;
                                horaFormateada = String.valueOf(HoraSeleccionada);
                            }else{
                                HoraSeleccionada = 12;
                                horaFormateada = String.valueOf(HoraSeleccionada);
                            }
                        } else if (HoraSeleccionada==0) {
                            HoraSeleccionada=12;
                            horaFormateada = String.valueOf(HoraSeleccionada);
                        }

                        if(HoraSeleccionada<9){
                            horaFormateada = "0"+String.valueOf(HoraSeleccionada);
                        }else{
                            horaFormateada = String.valueOf(HoraSeleccionada);
                        }

                        //Obtener minutos
                        if(MinutoSeleccionado<9){
                            minutoFormateado = "0"+String.valueOf(MinutoSeleccionado);
                        }else{
                            minutoFormateado = String.valueOf(MinutoSeleccionado);
                        }

                        //Seter hora en textview
                        Hora_A.setText(horaFormateada+":"+minutoFormateado+" "+AM_PM);
                    }
                }
                        ,hora,minuto,false);
                timePickerDialog.show();
            }
        });
    }

    private void EstablecerCategoria(){

        BD_Firebase.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> categoriasObtenidas = new ArrayList<>();
                //Si el usuario existe
                if (snapshot.exists()){
                    // Inicializamos la lista de categorías
                    categorias = new ArrayList<>();

                    Object categoriasObject = snapshot.child("categorias").getValue();
                    if (categoriasObject instanceof HashMap) {
                        HashMap<String, String> categoriasMap = (HashMap<String, String>) categoriasObject;
                        categorias = new ArrayList<>(categoriasMap.values());
                        categoriasObtenidas = categorias;
                        categoriasObtenidas.add("Otra");
                    } else if (categoriasObject instanceof List) {
                        categorias = (ArrayList<String>) categoriasObject;
                        categoriasObtenidas = categorias;
                        categoriasObtenidas.add("Otra");
                    } else {
                        // Manejar el caso en que la estructura de datos no sea la esperada
                        Toast.makeText(Actualizar_Nota.this, "Error al obtener categorías", Toast.LENGTH_SHORT).show();
                    }
                    if(!categoriasObtenidas.isEmpty()){
                        ArrayAdapter<String> categoriaAdapter = new ArrayAdapter<>(Actualizar_Nota.this, android.R.layout.simple_spinner_item, categoriasObtenidas);
                        categoriaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        SpinnerCategoria_A.setAdapter(categoriaAdapter);
                        ComprobarCategoria();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void ComprobarCategoria(){
        SpinnerCategoria_A.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String opcion = parent.getItemAtPosition(position).toString();
                if(opcion.equals("Otra")){
                    Intent intent = new Intent(Actualizar_Nota.this, Categorias_Nota.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void Obtener_Contacto(){
        Btn_Contactos_A.setOnClickListener(v -> {
            Intent contactoIntent = new Intent(Intent.ACTION_PICK);
            contactoIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
            startActivityForResult(contactoIntent, REQUEST_CONTACT);
        });
    }

    @SuppressLint("Range")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_CONTACT && resultCode==RESULT_OK){
            Uri contactoUri = data.getData();
            Cursor cursor = getContentResolver().query(contactoUri, new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},null,null,null);
            if(cursor!=null && cursor.moveToFirst()){
                ContactoSeleccionado = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                Contacto_A.setText(ContactoSeleccionado);
                cursor.close();
            }
        }
    }

    private void SetearDatos(){
        Id_nota_A.setText(id_nota_R);
        Uid_Usuario_A.setText(uid_Usuario_R);
        Correo_Usuario_A.setText(correo_Usuario_R);
        Fecha_hora_actual_A.setText(fecha_hora_actual_R);
        Titulo_A.setText(titulo_R);
        Descripcion_A.setText(descripcion_R);
        Fecha_A.setText(fecha_R);
        Hora_A.setText(hora_R);
        Estado_A.setText(estado_R);
        Contacto_A.setText(contacto_R);

        // Establecer valor recibido en Spinner de Notificación
        SpinnerNotificacion_A.setSelection(getIndex(SpinnerNotificacion_A, notificacion_R));
        // Establecer valor recibido en Spinner de Categoría
        SpinnerCategoria_A.setSelection(getIndex(SpinnerCategoria_A, categoria_R));

    }

    // Método para obtener el índice de la opción seleccionada en el Spinner
    private int getIndex(Spinner spinner, String value){
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(value)) {
                return i;
            }
        }
        return 0; // Valor por defecto
    }

    //Identificar el estado de la nota
    private void ComprobarEstadoNota(){
        String estado_nota = Estado_A.getText().toString();
        if(estado_nota.equals("Pendiente")){
            Tarea_No_Finalizada.setVisibility(View.VISIBLE);
        }
        if(estado_nota.equals("Realizado")){
            Tarea_Finalizada.setVisibility(View.VISIBLE);
        }
        if(estado_nota.equals("Aplazado")){
            Tarea_No_Finalizada.setVisibility(View.VISIBLE);
        }
    }

    private void Spinner_Estado(){
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.Estados_nota, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner_estado.setAdapter(adapter);
        Spinner_estado.setOnItemSelectedListener(this);
    }

    private void ActualizarNotaBD(){
        //Obtener los datoss
        String tituloActualizar = Titulo_A.getText().toString();
        String descripcionActualizar = Descripcion_A.getText().toString();
        String fechaActualizar = Fecha_A.getText().toString();
        String horaActualizar = Hora_A.getText().toString();
        String notificacionActualizar = SpinnerNotificacion_A.getSelectedItem().toString();
        String categoriaActualizar = SpinnerCategoria_A.getSelectedItem().toString();
        String contactoActualizar = Contacto_A.getText().toString();
        String estadoActualizar = Estado_nuevo.getText().toString();

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("Notas_Publicadas");

        //Consulta
        Query query = databaseReference.orderByChild("id_nota").equalTo(id_nota_R);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()){
                    ds.getRef().child("titulo").setValue(tituloActualizar);
                    ds.getRef().child("descripcion").setValue(descripcionActualizar);
                    ds.getRef().child("fecha_nota").setValue(fechaActualizar);
                    ds.getRef().child("hora_nota").setValue(horaActualizar);
                    ds.getRef().child("notificacion").setValue(notificacionActualizar);
                    ds.getRef().child("contacto").setValue(contactoActualizar);
                    ds.getRef().child("categoria").setValue(categoriaActualizar);
                    ds.getRef().child("estado").setValue(estadoActualizar);
                }
                Toast.makeText(Actualizar_Nota.this, "Nota actualizada con exito", Toast.LENGTH_SHORT).show();
                onBackPressed();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        String ESTADO_ACTUAL = Estado_A.getText().toString();

        String Posicion_2 = parent.getItemAtPosition(2).toString();

        String estado_seleccionado = parent.getItemAtPosition(position).toString();
        Estado_nuevo.setText(estado_seleccionado);

        if(ESTADO_ACTUAL.equals("Realizado")){
            Estado_nuevo.setText(Posicion_2);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_actualizar,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.Actualizar_Nota_BD){
            ActualizarNotaBD();
            //Toast.makeText(this,"Nota actualizada", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp(){
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
