package com.example.agenda.ActualizarNota;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
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

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.agenda.AgregarNota.Agregar_Nota;
import com.example.agenda.R;
import com.google.firebase.database.DatabaseReference;

import java.util.Calendar;

public class Actualizar_Nota extends AppCompatActivity {
    TextView Id_nota_A, Uid_Usuario_A, Correo_Usuario_A, Fecha_hora_actual_A, Fecha_A, Hora_A, Estado_A, Contacto_A;
    EditText Titulo_A, Descripcion_A;
    Button Btn_Calendario_A, Btn_Hora_A, Btn_Contactos_A;

    String id_nota_R, uid_Usuario_R, correo_Usuario_R, fecha_hora_actual_R, titulo_R, descripcion_R, fecha_R, hora_R, estado_R, contacto_R, notificacion_R, categoria_R;

    int mes, dia, anio, hora, minuto;

    Spinner SpinnerNotificacion_A, SpinnerCategoria_A;
    ImageView Tarea_Finalizada, Tarea_No_Finalizada;

    int REQUEST_CONTACT = 1;
    String ContactoSeleccionado = "";

    DatabaseReference BD_Firebase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_actualizar_nota);
        InicializarVistas();
        RecuperarDatos();
        SetearDatos();
        EstablecerNotificacion();
        ObtenerOpcionNotificacion();
        EstabecerCategoria();
        ComprobarEstadoNota();
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
    private void ObtenerOpcionNotificacion(){
        SpinnerNotificacion_A.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String opcion = SpinnerNotificacion_A.getSelectedItem().toString();
                if(opcion.equals("Personalizado")){
                    Btn_Calendario_A.setEnabled(true);
                    Btn_Hora_A.setEnabled(true);
                    Establecer_Fecha();
                    Establecer_Hora();
                }else{
                    Btn_Calendario_A.setEnabled(false);
                    Btn_Hora_A.setEnabled(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    private void Establecer_Fecha(){
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

    private void EstabecerCategoria(){
        ArrayAdapter<CharSequence> categoriaAdapter = ArrayAdapter.createFromResource(Actualizar_Nota.this,
                R.array.Opciones_Categoria, android.R.layout.simple_spinner_item);
        categoriaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SpinnerCategoria_A.setAdapter(categoriaAdapter);
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
}
