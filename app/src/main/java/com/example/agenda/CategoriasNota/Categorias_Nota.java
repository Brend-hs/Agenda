package com.example.agenda.CategoriasNota;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.agenda.ListarNotas.Listar_Notas;
import com.example.agenda.R;
import com.example.agenda.ViewHolder.ViewHolder_Categoria;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Categorias_Nota extends AppCompatActivity {

    RecyclerView recyclerViewCategorias;
    FloatingActionButton fab_categoria;
    Dialog dialogModificar,dialogAgregar;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference BASE_DE_DATOS;

    LinearLayoutManager linearLayoutManager;

    FirebaseRecyclerAdapter<String, ViewHolder_Categoria> firebaseRecyclerAdapter;
    FirebaseRecyclerOptions<String> options;

    FirebaseAuth auth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_categorias_nota);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Categorías");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        fab_categoria = findViewById(R.id.fab_categoria);
        recyclerViewCategorias = findViewById(R.id.recyclerViewCategorias);
        recyclerViewCategorias.setHasFixedSize(true);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        firebaseDatabase = FirebaseDatabase.getInstance();
        BASE_DE_DATOS = firebaseDatabase.getReference("Usuarios");

        dialogModificar = new Dialog(Categorias_Nota.this);
        dialogAgregar = new Dialog(Categorias_Nota.this);

        ListarCategorias();
        fab_categoria.setOnClickListener(v -> {
            dialogAgregar.setContentView(R.layout.dialogo_agregar_categoria);

            //Declarar vistas
            EditText Nueva_Categoria_Dialogo;
            Button Guardar_Categoria_Dialogo;

            //Inicializar las vistas
            Nueva_Categoria_Dialogo = dialogAgregar.findViewById(R.id.Nueva_Categoria_Dialogo);
            Guardar_Categoria_Dialogo = dialogAgregar.findViewById(R.id.Guardar_Categoria_Dialogo);

            dialogAgregar.show();

            Guardar_Categoria_Dialogo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String nuevaCategoria=Nueva_Categoria_Dialogo.getText().toString();
                    if(!nuevaCategoria.isEmpty()){
                        String key = BASE_DE_DATOS.child(user.getUid()).child("categorias").push().getKey();
                        BASE_DE_DATOS.child(user.getUid()).child("categorias").child(key).setValue(nuevaCategoria);
                        Toast.makeText(Categorias_Nota.this, "Categoría creada", Toast.LENGTH_SHORT).show();
                        dialogAgregar.dismiss();
                    }else{
                        Toast.makeText(Categorias_Nota.this, "Ingrese una categoría", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        });
    }

    private void ListarCategorias() {

        //Consulta
        Query query = BASE_DE_DATOS.child(user.getUid()).child("categorias");
        options = new FirebaseRecyclerOptions.Builder<String>()
                .setQuery(query, String.class)
                .build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<String, ViewHolder_Categoria>(options) {


            @Override
            protected void onBindViewHolder(ViewHolder_Categoria viewHolder_categoria, int position, String categoria) {
                viewHolder_categoria.SetearCategoria(getApplicationContext(),categoria);

                viewHolder_categoria.setOnClickListener(new ViewHolder_Categoria.ClickListener() {
                    @Override
                    public void onEditClick(View view, int position) {
                        //Realizar la conexión con el diseño
                        dialogModificar.setContentView(R.layout.dialogo_modificar_categoria);

                        //Declarar vistas
                        EditText Categoria_Dialogo;
                        Button Guardar_Dialogo;

                        //Inicializar las vistas
                        Categoria_Dialogo = dialogModificar.findViewById(R.id.Categoria_Dialogo);
                        Guardar_Dialogo = dialogModificar.findViewById(R.id.Guardar_Dialogo);

                        Categoria_Dialogo.setHint(categoria);

                        dialogModificar.show();
                        Guardar_Dialogo.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String categoriaModificada=Categoria_Dialogo.getText().toString();

                                if(!categoriaModificada.isEmpty()){
                                    Query query = BASE_DE_DATOS.child(user.getUid()).child("categorias").orderByValue().equalTo(categoria);
                                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            for(DataSnapshot ds : snapshot.getChildren()){
                                                ds.getRef().setValue(categoriaModificada);
                                            }
                                            Toast.makeText(Categorias_Nota.this, "Categoria modificada", Toast.LENGTH_SHORT).show();
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            Toast.makeText(Categorias_Nota.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    dialogModificar.dismiss();
                                }else{
                                    Toast.makeText(Categorias_Nota.this, "Ingrese una categoría", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                        Toast.makeText(Categorias_Nota.this, "Editar categoria", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onDeleteClick(View view, int position) {
                        String categoria = getItem(position);
                        AlertDialog.Builder builder = new AlertDialog.Builder(Categorias_Nota.this);
                        builder.setTitle("Eliminar categoria");
                        builder.setMessage("¿Desea eliminar la categoría "+categoria+"?");

                        builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Eliminar nota en la base de datos
                                Query query = BASE_DE_DATOS.child(user.getUid()).child("categorias").orderByValue().equalTo(categoria);
                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for(DataSnapshot ds : snapshot.getChildren()){
                                            ds.getRef().removeValue();
                                        }
                                        Toast.makeText(Categorias_Nota.this, "Categoria eliminada", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(Categorias_Nota.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });

                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(Categorias_Nota.this, "Cancelado por el usuario", Toast.LENGTH_SHORT).show();
                            }
                        });

                        //Toast.makeText(Categorias_Nota.this, "Eliminar Categoria", Toast.LENGTH_SHORT).show();
                        builder.create().show();
                    }
                });
            }

            @NonNull
            @Override
            public ViewHolder_Categoria onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_categoria,parent,false);
                ViewHolder_Categoria viewHolder_categoria = new ViewHolder_Categoria(view);

                return viewHolder_categoria;
            }

        };

        linearLayoutManager = new LinearLayoutManager(Categorias_Nota.this, LinearLayoutManager.VERTICAL,false);
        //linearLayoutManager.setStackFromEnd(true);//El recycler view empieza desde la parte superior

        recyclerViewCategorias.setLayoutManager(linearLayoutManager);
        recyclerViewCategorias.setAdapter(firebaseRecyclerAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(firebaseRecyclerAdapter!=null){
            firebaseRecyclerAdapter.startListening();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}