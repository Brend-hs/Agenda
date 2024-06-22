package com.example.agenda.AgregarNota;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
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
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.agenda.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Agregar_Nota extends AppCompatActivity {

    TextView Uid_Usuario, Correo_Usuario, Fecha_hora_actual, Fecha, Hora, Estado;
    EditText Titulo, Descripcion;
    Button Btn_Calendario, Btn_Hora;

    int mes, dia, anio, hora, minuto;

    Spinner SpinnerNotificacion, SpinnerCategoria;

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

        EstablecerNotificacion();
        EstabecerCategoria();

        /*TO-DO:
        * Poder añadir otra categoria
        * Añadir contacto*/
    }

    private void InicializarVariables(){
        Uid_Usuario = findViewById(R.id.Uid_Usuario);
        Correo_Usuario= findViewById(R.id.Correo_Usuario);
        Fecha_hora_actual= findViewById(R.id.Fecha_hora_actual);
        Fecha = findViewById(R.id.Fecha);
        Hora = findViewById(R.id.Hora);
        Estado = findViewById(R.id.Estado);

        Titulo = findViewById(R.id.Titulo);
        Descripcion = findViewById(R.id.Descripcion);
        Btn_Calendario = findViewById(R.id.Btn_Calendario);
        Btn_Hora = findViewById(R.id.Btn_Hora);

        SpinnerNotificacion = findViewById(R.id.SpinnerNotificacion);
        SpinnerCategoria = findViewById(R.id.SpinnerCategoria);
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

    private void EstablecerNotificacion() {
        ArrayAdapter<CharSequence> notificacionAdapter = ArrayAdapter.createFromResource(Agregar_Nota.this,
                R.array.Opciones_Notificacion, android.R.layout.simple_spinner_item);
        notificacionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SpinnerNotificacion.setAdapter(notificacionAdapter);
    }

    private void EstabecerCategoria(){
        ArrayAdapter<CharSequence> categoriaAdapter = ArrayAdapter.createFromResource(Agregar_Nota.this,
                R.array.Opciones_Categoria, android.R.layout.simple_spinner_item);
        categoriaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SpinnerCategoria.setAdapter(categoriaAdapter);
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
            Toast.makeText(this, "Nota agregada", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}