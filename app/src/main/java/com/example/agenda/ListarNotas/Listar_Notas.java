package com.example.agenda.ListarNotas;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import com.example.agenda.Objetos.Nota;
import com.example.agenda.R;
import com.example.agenda.ViewHolder.ViewHolder_Nota;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Listar_Notas extends AppCompatActivity {

    RecyclerView recyclerViewNotas;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference BASE_DE_DATOS;

    LinearLayoutManager linearLayoutManager;
    FirebaseRecyclerAdapter<Nota, ViewHolder_Nota> firebaseRecyclerAdapter;
    FirebaseRecyclerOptions<Nota> options;
    Dialog dialog, dialogDetalle;

    FirebaseAuth auth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_listar_notas);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Mis Tareas");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        recyclerViewNotas = findViewById(R.id.recyclerViewNotas);
        recyclerViewNotas.setHasFixedSize(true); //Adapte su tamaño a los cmabios de la lista

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        firebaseDatabase = FirebaseDatabase.getInstance();
        BASE_DE_DATOS = firebaseDatabase.getReference("Notas_Publicadas");
        dialog = new Dialog(Listar_Notas.this);
        dialogDetalle = new Dialog(Listar_Notas.this);
        ListarNotasUsuarios();
    }

    private void ListarNotasUsuarios(){
        //Consulta
        Query query = BASE_DE_DATOS.orderByChild("uid_usuario").equalTo(user.getUid());
        options = new FirebaseRecyclerOptions.Builder<Nota>().setQuery(query,Nota.class).build();
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Nota, ViewHolder_Nota>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolder_Nota viewHolder_nota, int i, @NonNull Nota nota) {
                viewHolder_nota.SetearDatos(
                        getApplicationContext(),
                        nota.getId_nota(),
                        nota.getUid_usuario(),
                        nota.getCorreo(),
                        nota.getFecha_hora_actual(),
                        nota.getTitulo(),
                        nota.getDescripcion(),
                        nota.getFecha_nota(),
                        nota.getHora_nota(),
                        nota.getNotificacion(),
                        nota.getCategoria(),
                        nota.getContacto(),
                        nota.getEstado()
                );
            }

            @Override
            public ViewHolder_Nota onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_nota,parent,false);
                ViewHolder_Nota viewHolder_nota = new ViewHolder_Nota(view);
                viewHolder_nota.setOnClickListener(new ViewHolder_Nota.ClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        TextView Titulo_Detalle, Descripcion_Detalle, Fecha_Registro_Detalle, Notificacion_Detalle, Categoria_Detalle, Estado_Detalle, Contacto_Detalle;

                        //Realizar la conexión con el diseño
                        dialogDetalle.setContentView(R.layout.activity_detalle_nota);

                        Titulo_Detalle = dialogDetalle.findViewById(R.id.Titulo_Detalle);
                        Descripcion_Detalle = dialogDetalle.findViewById(R.id.Descripcion_Detalle);
                        Fecha_Registro_Detalle = dialogDetalle.findViewById(R.id.Fecha_Registro_Detalle);
                        Notificacion_Detalle = dialogDetalle.findViewById(R.id.Notificacion_Detalle);
                        Categoria_Detalle = dialogDetalle.findViewById(R.id.Categoria_Detalle);
                        Estado_Detalle = dialogDetalle.findViewById(R.id.Estado_Detalle);
                        Contacto_Detalle = dialogDetalle.findViewById(R.id.Contacto_Detalle);

                        String titulo = getItem(position).getTitulo();
                        String descripcion = getItem(position).getDescripcion();
                        String fecha_registro = getItem(position).getFecha_hora_actual();
                        String notificacion = getItem(position).getNotificacion();
                        String fecha = getItem(position).getFecha_nota();
                        String hora = getItem(position).getHora_nota();
                        String categoria = getItem(position).getCategoria();
                        String estado = getItem(position).getEstado();
                        String contacto = getItem(position).getContacto();

                        Titulo_Detalle.setText(titulo);
                        Descripcion_Detalle.setText(descripcion);
                        Fecha_Registro_Detalle.setText(fecha_registro);
                        if(notificacion.equals("Personalizado")){
                            Notificacion_Detalle.setText(fecha+" "+hora);
                        }else{
                            Notificacion_Detalle.setText(notificacion);
                        }
                        Categoria_Detalle.setText(categoria);
                        Estado_Detalle.setText(estado);
                        Contacto_Detalle.setText(contacto);

                        dialogDetalle.show();
                        Toast.makeText(Listar_Notas.this, "On item Click", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onItemLongClick(View view, int position) {
                        String id_nota = getItem(position).getId_nota();

                        //Declarar las vistas
                        Button CD_Eliminar, CD_Actualizar;

                        //Realizar la conexión con el diseño
                        dialog.setContentView(R.layout.dialogo_opciones);

                        //Inicializar las vistas
                        CD_Eliminar = dialog.findViewById(R.id.CD_Eliminar);
                        CD_Actualizar = dialog.findViewById(R.id.CD_Actualizar);

                        CD_Eliminar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                EliminarNota(id_nota);
                                dialog.dismiss();
                            }
                        });

                        CD_Actualizar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(Listar_Notas.this, "Modificar nota", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                    }
                });
                return viewHolder_nota;
            }
        };

        linearLayoutManager = new LinearLayoutManager(Listar_Notas.this, LinearLayoutManager.VERTICAL,false);
        linearLayoutManager.setReverseLayout(true);//Diseño inverso: del ultimo registro al primero
        linearLayoutManager.setStackFromEnd(true);//El recycler view empiece desde la parte superior

        recyclerViewNotas.setLayoutManager(linearLayoutManager);
        recyclerViewNotas.setAdapter(firebaseRecyclerAdapter);
    }

    private void EliminarNota(String idNota) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Listar_Notas.this);
        builder.setTitle("Eliminar nota");
        builder.setMessage("¿Desea eliminar la tarea?");
        builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Eliminar nota en la base de datos
                Query query = BASE_DE_DATOS.orderByChild("id_nota").equalTo(idNota);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds : snapshot.getChildren()){
                            ds.getRef().removeValue();
                        }
                        Toast.makeText(Listar_Notas.this, "Tarea eliminada", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(Listar_Notas.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(Listar_Notas.this, "Cancelado por el usuario", Toast.LENGTH_SHORT).show();
            }
        });

        builder.create().show();
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