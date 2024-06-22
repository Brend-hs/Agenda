package com.example.agenda.AgregarNota;

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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.agenda.Objetos.Nota;
import com.example.agenda.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Agregar_Nota extends AppCompatActivity {

    TextView Uid_Usuario, Correo_Usuario, Fecha_hora_actual, Fecha, Hora, Estado, Contacto;
    EditText Titulo, Descripcion;
    Button Btn_Calendario, Btn_Hora, Btn_Contactos;

    int mes, dia, anio, hora, minuto;

    Spinner SpinnerNotificacion, SpinnerCategoria;

    int REQUEST_CONTACT = 1;
    String ContactoSeleccionado = "";

    DatabaseReference BD_Firebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_agregar_nota);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Agregar nota");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        InicializarVariables();
        ObtenerDatos();
        Obtener_Fecha_Hora_Actual();
        EstablecerNotificacion();
        ObtenerOpcionNotificacion();
        EstabecerCategoria();
        Obtener_Contacto();
    }

    private void InicializarVariables(){
        Uid_Usuario = findViewById(R.id.Uid_Usuario);
        Correo_Usuario= findViewById(R.id.Correo_Usuario);
        Fecha_hora_actual= findViewById(R.id.Fecha_hora_actual);
        Fecha = findViewById(R.id.Fecha);
        Hora = findViewById(R.id.Hora);
        Estado = findViewById(R.id.Estado);
        Contacto = findViewById(R.id.Contacto);

        Titulo = findViewById(R.id.Titulo);
        Descripcion = findViewById(R.id.Descripcion);
        Btn_Calendario = findViewById(R.id.Btn_Calendario);
        Btn_Hora = findViewById(R.id.Btn_Hora);
        Btn_Contactos = findViewById(R.id.Btn_Contactos);

        SpinnerNotificacion = findViewById(R.id.SpinnerNotificacion);
        SpinnerCategoria = findViewById(R.id.SpinnerCategoria);

        BD_Firebase = FirebaseDatabase.getInstance().getReference();
    }

    private void ObtenerDatos(){
        String uid_recuperado = getIntent().getStringExtra("Uid");
        String correo_recuperado = getIntent().getStringExtra("Correo");

        Uid_Usuario.setText(uid_recuperado);
        Correo_Usuario.setText(correo_recuperado);
    }

    private void Obtener_Fecha_Hora_Actual(){
        String fecha_hora_registro = new SimpleDateFormat("dd-MM-yyyy/HH:mm:ss a",
                Locale.getDefault()).format(System.currentTimeMillis());
        Fecha_hora_actual.setText(fecha_hora_registro);
    }

    private void Guardar_Nota(){
        //Obtener los datoss
        String uid_usuario = Uid_Usuario.getText().toString();
        String correo_usuario = Correo_Usuario.getText().toString();
        String fecha_hora_actual = Fecha_hora_actual.getText().toString();
        String titulo = Titulo.getText().toString();
        String descripcion = Descripcion.getText().toString();
        String fecha = Fecha.getText().toString();
        String hora = Hora.getText().toString();
        String notificacion = SpinnerNotificacion.getSelectedItem().toString();
        String categoria = SpinnerCategoria.getSelectedItem().toString();
        String estado = Estado.getText().toString();
        
        //Validar cada uno de los datos
        if(!uid_usuario.isEmpty() && !correo_usuario.isEmpty() && !fecha_hora_actual.isEmpty() && !titulo.isEmpty() &&
                !descripcion.isEmpty() && !notificacion.isEmpty() &&
                !categoria.isEmpty() && !estado.isEmpty()){
            Nota nota = new Nota(correo_usuario+"/"+fecha_hora_actual,
                    uid_usuario,
                    correo_usuario,
                    fecha_hora_actual,
                    titulo,
                    descripcion,
                    fecha,
                    hora,
                    notificacion,
                    categoria,
                    ContactoSeleccionado,
                    estado);

            String Nota_Usuario = BD_Firebase.push().getKey();
            //Establecer el nombre de la base de datos
            String Nombre_BD = "Notas_Publicadas";

            BD_Firebase.child(Nombre_BD).child(Nota_Usuario).setValue(nota);
            Toast.makeText(this, "Se ha agregado la tarea exitosamente", Toast.LENGTH_SHORT).show();
            onBackPressed();
        }else {
            Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show();
        }

    }

    private void Establecer_Fecha(){
        Btn_Calendario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendario = Calendar.getInstance();
                dia = calendario.get(Calendar.DAY_OF_MONTH);
                mes = calendario.get(Calendar.MONTH);
                anio = calendario.get(Calendar.YEAR);

                DatePickerDialog datePickerDialog = new DatePickerDialog(Agregar_Nota.this, new DatePickerDialog.OnDateSetListener() {
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
                        Fecha.setText(diaFormateado+"/"+mesFormateado+"/"+AnioSeleccionado);
                    }
                }
                        ,anio,mes,dia);
                datePickerDialog.show();
            }
        });
    }

    private void Establecer_Hora(){
        Btn_Hora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendario = Calendar.getInstance();
                hora = calendario.get(Calendar.HOUR_OF_DAY);
                minuto = calendario.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(Agregar_Nota.this, new TimePickerDialog.OnTimeSetListener() {
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
                        Hora.setText(horaFormateada+":"+minutoFormateado+" "+AM_PM);
                    }
                }
                        ,hora,minuto,false);
                timePickerDialog.show();
            }
        });
    }

    private void EstablecerNotificacion() {
        ArrayAdapter<CharSequence> notificacionAdapter = ArrayAdapter.createFromResource(Agregar_Nota.this,
                R.array.Opciones_Notificacion, android.R.layout.simple_spinner_item);
        notificacionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SpinnerNotificacion.setAdapter(notificacionAdapter);
    }

    private void ObtenerOpcionNotificacion(){
        SpinnerNotificacion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String opcion = SpinnerNotificacion.getSelectedItem().toString();
                if(opcion.equals("Personalizado")){
                    Btn_Calendario.setEnabled(true);
                    Btn_Hora.setEnabled(true);
                    Establecer_Fecha();
                    Establecer_Hora();
                }else{
                    Btn_Calendario.setEnabled(false);
                    Btn_Hora.setEnabled(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void EstabecerCategoria(){
        ArrayAdapter<CharSequence> categoriaAdapter = ArrayAdapter.createFromResource(Agregar_Nota.this,
                R.array.Opciones_Categoria, android.R.layout.simple_spinner_item);
        categoriaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SpinnerCategoria.setAdapter(categoriaAdapter);
    }

    private void Obtener_Contacto(){
        Btn_Contactos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent contactoIntent = new Intent(Intent.ACTION_PICK);
                contactoIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                startActivityForResult(contactoIntent, REQUEST_CONTACT);
            }
        });
    }

    @SuppressLint("Range")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==REQUEST_CONTACT && resultCode==RESULT_OK){
            Uri contactoUri = data.getData();
            Cursor cursor = getContentResolver().query(contactoUri, new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},null,null,null);
            if(cursor!=null && cursor.moveToFirst()){
                ContactoSeleccionado = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                Contacto.setText(ContactoSeleccionado);
                cursor.close();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_agenda,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.Agregar_Nota_BD){
            Guardar_Nota();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}