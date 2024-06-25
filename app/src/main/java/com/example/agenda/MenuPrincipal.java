package com.example.agenda;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.agenda.AgregarNota.Agregar_Nota;
import com.example.agenda.CategoriasNota.Categorias_Nota;
import com.example.agenda.ListarNotas.Listar_Notas;
import com.example.agenda.NotasArchivadas.Notas_Archivadas;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MenuPrincipal extends AppCompatActivity {
    Button AgregarNotas, ListarNotas, Archivados, Categorias, AcercaDe, CerrarSesion;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;

    TextView UidPrincipal, NombresPrincipal, CorreoPrincipal;

    ProgressBar progressBarDatos;

    LinearLayoutCompat Linear_Nombres, Linear_Correo;

    DatabaseReference Usuarios;

    ArrayList<String> categorias;
    Dialog dialog_informacion;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_menu_principal);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Agenda");

        UidPrincipal = findViewById(R.id.UidPrincipal);
        NombresPrincipal = findViewById(R.id.NombresPrincipal);
        CorreoPrincipal = findViewById(R.id.CorreoPrincipal);
        progressBarDatos = findViewById(R.id.progressBarDatos);

        Linear_Nombres = findViewById(R.id.Linear_Nombres);
        Linear_Correo = findViewById(R.id.Linear_Correo);

        dialog_informacion = new Dialog(this);

        Usuarios = FirebaseDatabase.getInstance().getReference("Usuarios"); //El nombre debe coincidir como esta en Firebase

        AgregarNotas = findViewById(R.id.AgregarNotas);
        ListarNotas = findViewById(R.id.ListarNotas);
        Archivados = findViewById(R.id.Archivados);
        Categorias = findViewById(R.id.Categorias);
        //Perfil = findViewById(R.id.Perfil);
        AcercaDe = findViewById(R.id.AcercaDe);
        CerrarSesion = findViewById(R.id.CerrarSesion);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        AgregarNotas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Obtenemos la información de los textview
                String uid_usuario = UidPrincipal.getText().toString();
                String correo_usuario = CorreoPrincipal.getText().toString();

                Intent intent = new Intent(MenuPrincipal.this, Agregar_Nota.class);
                intent.putExtra("Uid",uid_usuario);
                intent.putExtra("Correo",correo_usuario);
                intent.putExtra("Categorias",categorias);

                startActivity(intent);
            }
        });

        ListarNotas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MenuPrincipal.this, Listar_Notas.class));
                Toast.makeText(MenuPrincipal.this, "Listar Tareas", Toast.LENGTH_SHORT).show();
            }
        });

        Archivados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MenuPrincipal.this, Notas_Archivadas.class));
                Toast.makeText(MenuPrincipal.this, "Archivar Tareas", Toast.LENGTH_SHORT).show();
            }
        });

        Categorias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MenuPrincipal.this, Categorias_Nota.class));
                Toast.makeText(MenuPrincipal.this, "Categorias", Toast.LENGTH_SHORT).show();
            }
        });

        /*Perfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MenuPrincipal.this, Perfil_Usuario.class));
                Toast.makeText(MenuPrincipal.this, "Perfil del Usuario", Toast.LENGTH_SHORT).show();
            }
        });*/

        AcercaDe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Informacion();
            }
        });

        CerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SalirAplicacion();
            }
        });
    }

    private void Informacion(){
        Button EntendidoInfo;
        dialog_informacion.setContentView(R.layout.dialogo_informacion);
        EntendidoInfo = dialog_informacion.findViewById(R.id.EntendidoInfo);

        EntendidoInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_informacion.dismiss();
            }
        });

        dialog_informacion.show();
        dialog_informacion.setCanceledOnTouchOutside(false);
    }

    @Override
    protected void onStart() {
        ComprobarInicioDeSesion();
        super.onStart();
    }

    private void ComprobarInicioDeSesion(){
        if(user!=null){
            //El usuario ha iniciado sesión
            CargaDeDatos();
        }else{
            //Lo dirige al MainActivity
            startActivity(new Intent(MenuPrincipal.this, MainActivity.class));
            finish();
        }
    }
    private void CargaDeDatos(){
        Usuarios.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Si el usuario existe
                if (snapshot.exists()){
                    //El progressbar se oculta
                    progressBarDatos.setVisibility(View.GONE);

                    //Los textview se muestran
                    //UidPrincipal.setVisibility(View.VISIBLE);
                    //NombresPrincipal.setVisibility(View.VISIBLE);
                    //CorreoPrincipal.setVisibility(View.VISIBLE);
                    Linear_Nombres.setVisibility(View.VISIBLE);
                    Linear_Correo.setVisibility(View.VISIBLE);

                    //Obtenemos los datos
                    String uid= ""+snapshot.child("uid").getValue();
                    String nombres= ""+snapshot.child("nombre").getValue();
                    String correo=""+snapshot.child("correo").getValue();
                    // Inicializamos la lista de categorías
                    categorias = new ArrayList<>();

                    Object categoriasObject = snapshot.child("categorias").getValue();
                    if (categoriasObject instanceof HashMap) {
                        HashMap<String, String> categoriasMap = (HashMap<String, String>) categoriasObject;
                        categorias = new ArrayList<>(categoriasMap.values());
                    } else if (categoriasObject instanceof List) {
                        categorias = (ArrayList<String>) categoriasObject;
                    } else {
                        // Manejar el caso en que la estructura de datos no sea la esperada
                        Toast.makeText(MenuPrincipal.this, "Error al obtener categorías", Toast.LENGTH_SHORT).show();
                    }

                    //Setear los datos en los respectivos texview
                    UidPrincipal.setText(uid);
                    NombresPrincipal.setText(nombres);
                    CorreoPrincipal.setText(correo);

                    //Habilitar los botones del menú
                    AgregarNotas.setEnabled(true);
                    ListarNotas.setEnabled(true);
                    Archivados.setEnabled(true);
                    Categorias.setEnabled(true);
                    //Perfil.setEnabled(true);
                    AcercaDe.setEnabled(true);
                    CerrarSesion.setEnabled(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void SalirAplicacion() {
        firebaseAuth.signOut();
        startActivity(new Intent(MenuPrincipal.this, MainActivity.class));
        Toast.makeText(this, "Cierre de sesión éxitoso", Toast.LENGTH_SHORT).show();
    }
}