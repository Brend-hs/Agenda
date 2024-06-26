package com.example.agenda.ListarNotas;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.agenda.ActualizarNota.Actualizar_Nota;
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
    Dialog dialog, dialogDetalle, dialog_filtrar;

    FirebaseAuth auth;
    FirebaseUser user;

    SharedPreferences sharedPreferences;

    String contactoSeleccionado;

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

        dialog_filtrar = new Dialog(Listar_Notas.this);

        firebaseDatabase = FirebaseDatabase.getInstance();
        BASE_DE_DATOS = firebaseDatabase.getReference("Notas_Publicadas");
        dialog = new Dialog(Listar_Notas.this);
        dialogDetalle = new Dialog(Listar_Notas.this);
        Estado_Filtro();
    }

    private void ListarTodasNotas(){
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
                        TextView Titulo_Detalle, Descripcion_Detalle, Fecha_Registro_Detalle, Notificacion_Detalle, Categoria_Detalle, Estado_Detalle, Contacto_Detalle,Entrega_Detalle;
                        Button Mensaje, Llamar;
                        //Realizar la conexión con el diseño
                        dialogDetalle.setContentView(R.layout.activity_detalle_nota);

                        Titulo_Detalle = dialogDetalle.findViewById(R.id.Titulo_Detalle);
                        Descripcion_Detalle = dialogDetalle.findViewById(R.id.Descripcion_Detalle);
                        Fecha_Registro_Detalle = dialogDetalle.findViewById(R.id.Fecha_Registro_Detalle);
                        Entrega_Detalle = dialogDetalle.findViewById(R.id.Fecha_Realizacion_Detalle);
                        Notificacion_Detalle = dialogDetalle.findViewById(R.id.Notificacion_Detalle);
                        Categoria_Detalle = dialogDetalle.findViewById(R.id.Categoria_Detalle);
                        Estado_Detalle = dialogDetalle.findViewById(R.id.Estado_Detalle);
                        Contacto_Detalle = dialogDetalle.findViewById(R.id.Contacto_Detalle);

                        Mensaje = dialogDetalle.findViewById(R.id.Mensaje);
                        Llamar = dialogDetalle.findViewById(R.id.Llamar);

                        String titulo = getItem(position).getTitulo();
                        String descripcion = getItem(position).getDescripcion();
                        String fecha_registro = getItem(position).getFecha_hora_actual();
                        String notificacion = getItem(position).getNotificacion();
                        String fecha = getItem(position).getFecha_nota();
                        String hora = getItem(position).getHora_nota();
                        String categoria = getItem(position).getCategoria();
                        String estado = getItem(position).getEstado();
                        String contacto = getItem(position).getContacto();
                        contactoSeleccionado = contacto;

                        Titulo_Detalle.setText(titulo);
                        Descripcion_Detalle.setText(descripcion);
                        Fecha_Registro_Detalle.setText(fecha_registro);
                        Entrega_Detalle.setText(fecha+" "+hora);
                        Notificacion_Detalle.setText(notificacion);
                        Categoria_Detalle.setText(categoria);
                        Estado_Detalle.setText(estado);
                        Contacto_Detalle.setText(contacto);

                        Mensaje.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (ContextCompat.checkSelfPermission(Listar_Notas.this,
                                        Manifest.permission.SEND_SMS)==PackageManager.PERMISSION_GRANTED){
                                    EnviarMensaje();
                                }else{
                                    SolicitudPermisoMensaje.launch(Manifest.permission.SEND_SMS);
                                }
                            }
                        });

                        Llamar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(ContextCompat.checkSelfPermission(Listar_Notas.this,
                                        Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED){
                                    LlamarContacto();
                                }else{
                                    SolicitudPermisoLlamada.launch(Manifest.permission.CALL_PHONE);
                                }
                            }
                        });

                        dialogDetalle.show();
                        Toast.makeText(Listar_Notas.this, "On item Click", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onItemLongClick(View view, int position) {

                        //Obtener los datos de la nota seleccionada
                        String id_nota = getItem(position).getId_nota();
                        String uid_usuario = getItem(position).getUid_usuario();
                        String correo = getItem(position).getCorreo();
                        String fecha_hora_actual= getItem(position).getFecha_hora_actual();
                        String titulo = getItem(position).getTitulo();
                        String descripcion = getItem(position).getDescripcion();
                        String fecha_nota = getItem(position).getFecha_nota();
                        String hora_nota = getItem(position).getHora_nota();
                        String notificacion = getItem(position).getNotificacion();
                        String categoria = getItem(position).getCategoria();
                        String contacto = getItem(position).getContacto();
                        String estado = getItem(position).getEstado();

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
                                //Toast.makeText(Listar_Notas.this, "Modificar nota", Toast.LENGTH_SHORT).show();
                                //startActivity(new Intent(Listar_Notas.this, Actualizar_Nota.class));
                                Intent intent = new Intent(Listar_Notas.this, Actualizar_Nota.class);
                                intent.putExtra("id_nota", id_nota);
                                intent.putExtra("uid_usuario", uid_usuario);
                                intent.putExtra("correo", correo);
                                intent.putExtra("fecha_hora_actual", fecha_hora_actual);
                                intent.putExtra("titulo", titulo);
                                intent.putExtra("descripcion", descripcion);
                                intent.putExtra("fecha_nota", fecha_nota);
                                intent.putExtra("hora_nota", hora_nota);
                                intent.putExtra("notificacion", notificacion);
                                intent.putExtra("categoria", categoria);
                                intent.putExtra("contacto", contacto);
                                intent.putExtra("estado", estado);
                                startActivity(intent);
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

    private void ListarNotasRealizadas() {
        // Consulta
        String estado_nota = "Realizado";
        Query query = BASE_DE_DATOS.orderByChild("uid_usuario").equalTo(user.getUid());
        options = new FirebaseRecyclerOptions.Builder<Nota>().setQuery(query, Nota.class).build();
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Nota, ViewHolder_Nota>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolder_Nota viewHolder_nota, int i, @NonNull Nota nota) {
                if ("Realizado".equals(nota.getEstado())) {
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
                    viewHolder_nota.itemView.setVisibility(View.VISIBLE);
                    viewHolder_nota.itemView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                } else {
                    // Oculta la vista si no cumple con el estado
                    viewHolder_nota.itemView.setVisibility(View.GONE);
                    viewHolder_nota.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
                }
            }

            @Override
            public ViewHolder_Nota onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_nota, parent, false);
                ViewHolder_Nota viewHolder_nota = new ViewHolder_Nota(view);
                viewHolder_nota.setOnClickListener(new ViewHolder_Nota.ClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Nota nota = getItem(position);
                        if (!"Realizado".equals(nota.getEstado())) {
                            return;
                        }

                        TextView Titulo_Detalle, Descripcion_Detalle, Fecha_Registro_Detalle, Notificacion_Detalle, Categoria_Detalle, Estado_Detalle, Contacto_Detalle, Entrega_Detalle;

                        // Realizar la conexión con el diseño
                        dialogDetalle.setContentView(R.layout.activity_detalle_nota);

                        Titulo_Detalle = dialogDetalle.findViewById(R.id.Titulo_Detalle);
                        Descripcion_Detalle = dialogDetalle.findViewById(R.id.Descripcion_Detalle);
                        Fecha_Registro_Detalle = dialogDetalle.findViewById(R.id.Fecha_Registro_Detalle);
                        Entrega_Detalle = dialogDetalle.findViewById(R.id.Fecha_Realizacion_Detalle);
                        Notificacion_Detalle = dialogDetalle.findViewById(R.id.Notificacion_Detalle);
                        Categoria_Detalle = dialogDetalle.findViewById(R.id.Categoria_Detalle);
                        Estado_Detalle = dialogDetalle.findViewById(R.id.Estado_Detalle);
                        Contacto_Detalle = dialogDetalle.findViewById(R.id.Contacto_Detalle);

                        String titulo = nota.getTitulo();
                        String descripcion = nota.getDescripcion();
                        String fecha_registro = nota.getFecha_hora_actual();
                        String notificacion = nota.getNotificacion();
                        String fecha = nota.getFecha_nota();
                        String hora = nota.getHora_nota();
                        String categoria = nota.getCategoria();
                        String estado = nota.getEstado();
                        String contacto = nota.getContacto();

                        Titulo_Detalle.setText(titulo);
                        Descripcion_Detalle.setText(descripcion);
                        Fecha_Registro_Detalle.setText(fecha_registro);
                        Entrega_Detalle.setText(fecha + " " + hora);
                        Notificacion_Detalle.setText(notificacion);
                        Categoria_Detalle.setText(categoria);
                        Estado_Detalle.setText(estado);
                        Contacto_Detalle.setText(contacto);

                        dialogDetalle.show();
                        Toast.makeText(Listar_Notas.this, "On item Click", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onItemLongClick(View view, int position) {
                        Nota nota = getItem(position);
                        if (!"Realizado".equals(nota.getEstado())) {
                            return;
                        }

                        // Obtener los datos de la nota seleccionada
                        String id_nota = nota.getId_nota();
                        String uid_usuario = nota.getUid_usuario();
                        String correo = nota.getCorreo();
                        String fecha_hora_actual = nota.getFecha_hora_actual();
                        String titulo = nota.getTitulo();
                        String descripcion = nota.getDescripcion();
                        String fecha_nota = nota.getFecha_nota();
                        String hora_nota = nota.getHora_nota();
                        String notificacion = nota.getNotificacion();
                        String categoria = nota.getCategoria();
                        String contacto = nota.getContacto();
                        String estado = nota.getEstado();

                        // Declarar las vistas
                        Button CD_Eliminar, CD_Actualizar;

                        // Realizar la conexión con el diseño
                        dialog.setContentView(R.layout.dialogo_opciones);

                        // Inicializar las vistas
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
                                // Toast.makeText(Listar_Notas.this, "Modificar nota", Toast.LENGTH_SHORT).show();
                                // startActivity(new Intent(Listar_Notas.this, Actualizar_Nota.class));
                                Intent intent = new Intent(Listar_Notas.this, Actualizar_Nota.class);
                                intent.putExtra("id_nota", id_nota);
                                intent.putExtra("uid_usuario", uid_usuario);
                                intent.putExtra("correo", correo);
                                intent.putExtra("fecha_hora_actual", fecha_hora_actual);
                                intent.putExtra("titulo", titulo);
                                intent.putExtra("descripcion", descripcion);
                                intent.putExtra("fecha_nota", fecha_nota);
                                intent.putExtra("hora_nota", hora_nota);
                                intent.putExtra("notificacion", notificacion);
                                intent.putExtra("categoria", categoria);
                                intent.putExtra("contacto", contacto);
                                intent.putExtra("estado", estado);
                                startActivity(intent);
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                    }
                });
                return viewHolder_nota;
            }
        };

        linearLayoutManager = new LinearLayoutManager(Listar_Notas.this, LinearLayoutManager.VERTICAL, false);
        linearLayoutManager.setReverseLayout(true); // Diseño inverso: del último registro al primero
        linearLayoutManager.setStackFromEnd(true); // El recycler view empiece desde la parte superior

        recyclerViewNotas.setLayoutManager(linearLayoutManager);
        recyclerViewNotas.setAdapter(firebaseRecyclerAdapter);
    }

    private void ListarNotasPendientes() {
        // Consulta
        String estado_nota = "Pendiente";
        Query query = BASE_DE_DATOS.orderByChild("uid_usuario").equalTo(user.getUid());
        options = new FirebaseRecyclerOptions.Builder<Nota>().setQuery(query, Nota.class).build();
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Nota, ViewHolder_Nota>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolder_Nota viewHolder_nota, int i, @NonNull Nota nota) {
                if ("Pendiente".equals(nota.getEstado())) {
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
                    viewHolder_nota.itemView.setVisibility(View.VISIBLE);
                    viewHolder_nota.itemView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                } else {
                    // Oculta la vista si no cumple con el estado
                    viewHolder_nota.itemView.setVisibility(View.GONE);
                    viewHolder_nota.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
                }
            }

            @Override
            public ViewHolder_Nota onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_nota, parent, false);
                ViewHolder_Nota viewHolder_nota = new ViewHolder_Nota(view);
                viewHolder_nota.setOnClickListener(new ViewHolder_Nota.ClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Nota nota = getItem(position);
                        if (!"Pendiente".equals(nota.getEstado())) {
                            return;
                        }

                        TextView Titulo_Detalle, Descripcion_Detalle, Fecha_Registro_Detalle, Notificacion_Detalle, Categoria_Detalle, Estado_Detalle, Contacto_Detalle, Entrega_Detalle;

                        // Realizar la conexión con el diseño
                        dialogDetalle.setContentView(R.layout.activity_detalle_nota);

                        Titulo_Detalle = dialogDetalle.findViewById(R.id.Titulo_Detalle);
                        Descripcion_Detalle = dialogDetalle.findViewById(R.id.Descripcion_Detalle);
                        Fecha_Registro_Detalle = dialogDetalle.findViewById(R.id.Fecha_Registro_Detalle);
                        Entrega_Detalle = dialogDetalle.findViewById(R.id.Fecha_Realizacion_Detalle);
                        Notificacion_Detalle = dialogDetalle.findViewById(R.id.Notificacion_Detalle);
                        Categoria_Detalle = dialogDetalle.findViewById(R.id.Categoria_Detalle);
                        Estado_Detalle = dialogDetalle.findViewById(R.id.Estado_Detalle);
                        Contacto_Detalle = dialogDetalle.findViewById(R.id.Contacto_Detalle);

                        String titulo = nota.getTitulo();
                        String descripcion = nota.getDescripcion();
                        String fecha_registro = nota.getFecha_hora_actual();
                        String notificacion = nota.getNotificacion();
                        String fecha = nota.getFecha_nota();
                        String hora = nota.getHora_nota();
                        String categoria = nota.getCategoria();
                        String estado = nota.getEstado();
                        String contacto = nota.getContacto();

                        Titulo_Detalle.setText(titulo);
                        Descripcion_Detalle.setText(descripcion);
                        Fecha_Registro_Detalle.setText(fecha_registro);
                        Entrega_Detalle.setText(fecha + " " + hora);
                        Notificacion_Detalle.setText(notificacion);
                        Categoria_Detalle.setText(categoria);
                        Estado_Detalle.setText(estado);
                        Contacto_Detalle.setText(contacto);

                        dialogDetalle.show();
                        Toast.makeText(Listar_Notas.this, "On item Click", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onItemLongClick(View view, int position) {
                        Nota nota = getItem(position);
                        if (!"Pendiente".equals(nota.getEstado())) {
                            return;
                        }

                        // Obtener los datos de la nota seleccionada
                        String id_nota = nota.getId_nota();
                        String uid_usuario = nota.getUid_usuario();
                        String correo = nota.getCorreo();
                        String fecha_hora_actual = nota.getFecha_hora_actual();
                        String titulo = nota.getTitulo();
                        String descripcion = nota.getDescripcion();
                        String fecha_nota = nota.getFecha_nota();
                        String hora_nota = nota.getHora_nota();
                        String notificacion = nota.getNotificacion();
                        String categoria = nota.getCategoria();
                        String contacto = nota.getContacto();
                        String estado = nota.getEstado();

                        // Declarar las vistas
                        Button CD_Eliminar, CD_Actualizar;

                        // Realizar la conexión con el diseño
                        dialog.setContentView(R.layout.dialogo_opciones);

                        // Inicializar las vistas
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
                                // Toast.makeText(Listar_Notas.this, "Modificar nota", Toast.LENGTH_SHORT).show();
                                // startActivity(new Intent(Listar_Notas.this, Actualizar_Nota.class));
                                Intent intent = new Intent(Listar_Notas.this, Actualizar_Nota.class);
                                intent.putExtra("id_nota", id_nota);
                                intent.putExtra("uid_usuario", uid_usuario);
                                intent.putExtra("correo", correo);
                                intent.putExtra("fecha_hora_actual", fecha_hora_actual);
                                intent.putExtra("titulo", titulo);
                                intent.putExtra("descripcion", descripcion);
                                intent.putExtra("fecha_nota", fecha_nota);
                                intent.putExtra("hora_nota", hora_nota);
                                intent.putExtra("notificacion", notificacion);
                                intent.putExtra("categoria", categoria);
                                intent.putExtra("contacto", contacto);
                                intent.putExtra("estado", estado);
                                startActivity(intent);
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                    }
                });
                return viewHolder_nota;
            }
        };

        linearLayoutManager = new LinearLayoutManager(Listar_Notas.this, LinearLayoutManager.VERTICAL, false);
        linearLayoutManager.setReverseLayout(true); // Diseño inverso: del último registro al primero
        linearLayoutManager.setStackFromEnd(true); // El recycler view empiece desde la parte superior

        recyclerViewNotas.setLayoutManager(linearLayoutManager);
        recyclerViewNotas.setAdapter(firebaseRecyclerAdapter);
    }

    private void ListarNotasAplazadas() {
        // Consulta
        String estado_nota = "Aplazado";
        Query query = BASE_DE_DATOS.orderByChild("uid_usuario").equalTo(user.getUid());
        options = new FirebaseRecyclerOptions.Builder<Nota>().setQuery(query, Nota.class).build();
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Nota, ViewHolder_Nota>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolder_Nota viewHolder_nota, int i, @NonNull Nota nota) {
                if ("Aplazado".equals(nota.getEstado())) {
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
                    viewHolder_nota.itemView.setVisibility(View.VISIBLE);
                    viewHolder_nota.itemView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                } else {
                    // Oculta la vista si no cumple con el estado
                    viewHolder_nota.itemView.setVisibility(View.GONE);
                    viewHolder_nota.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
                }
            }

            @Override
            public ViewHolder_Nota onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_nota, parent, false);
                ViewHolder_Nota viewHolder_nota = new ViewHolder_Nota(view);
                viewHolder_nota.setOnClickListener(new ViewHolder_Nota.ClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Nota nota = getItem(position);
                        if (!"Aplazado".equals(nota.getEstado())) {
                            return;
                        }

                        TextView Titulo_Detalle, Descripcion_Detalle, Fecha_Registro_Detalle, Notificacion_Detalle, Categoria_Detalle, Estado_Detalle, Contacto_Detalle, Entrega_Detalle;

                        // Realizar la conexión con el diseño
                        dialogDetalle.setContentView(R.layout.activity_detalle_nota);

                        Titulo_Detalle = dialogDetalle.findViewById(R.id.Titulo_Detalle);
                        Descripcion_Detalle = dialogDetalle.findViewById(R.id.Descripcion_Detalle);
                        Fecha_Registro_Detalle = dialogDetalle.findViewById(R.id.Fecha_Registro_Detalle);
                        Entrega_Detalle = dialogDetalle.findViewById(R.id.Fecha_Realizacion_Detalle);
                        Notificacion_Detalle = dialogDetalle.findViewById(R.id.Notificacion_Detalle);
                        Categoria_Detalle = dialogDetalle.findViewById(R.id.Categoria_Detalle);
                        Estado_Detalle = dialogDetalle.findViewById(R.id.Estado_Detalle);
                        Contacto_Detalle = dialogDetalle.findViewById(R.id.Contacto_Detalle);

                        String titulo = nota.getTitulo();
                        String descripcion = nota.getDescripcion();
                        String fecha_registro = nota.getFecha_hora_actual();
                        String notificacion = nota.getNotificacion();
                        String fecha = nota.getFecha_nota();
                        String hora = nota.getHora_nota();
                        String categoria = nota.getCategoria();
                        String estado = nota.getEstado();
                        String contacto = nota.getContacto();

                        Titulo_Detalle.setText(titulo);
                        Descripcion_Detalle.setText(descripcion);
                        Fecha_Registro_Detalle.setText(fecha_registro);
                        Entrega_Detalle.setText(fecha + " " + hora);
                        Notificacion_Detalle.setText(notificacion);
                        Categoria_Detalle.setText(categoria);
                        Estado_Detalle.setText(estado);
                        Contacto_Detalle.setText(contacto);

                        dialogDetalle.show();
                        Toast.makeText(Listar_Notas.this, "On item Click", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onItemLongClick(View view, int position) {
                        Nota nota = getItem(position);
                        if (!"Aplazado".equals(nota.getEstado())) {
                            return;
                        }

                        // Obtener los datos de la nota seleccionada
                        String id_nota = nota.getId_nota();
                        String uid_usuario = nota.getUid_usuario();
                        String correo = nota.getCorreo();
                        String fecha_hora_actual = nota.getFecha_hora_actual();
                        String titulo = nota.getTitulo();
                        String descripcion = nota.getDescripcion();
                        String fecha_nota = nota.getFecha_nota();
                        String hora_nota = nota.getHora_nota();
                        String notificacion = nota.getNotificacion();
                        String categoria = nota.getCategoria();
                        String contacto = nota.getContacto();
                        String estado = nota.getEstado();

                        // Declarar las vistas
                        Button CD_Eliminar, CD_Actualizar;

                        // Realizar la conexión con el diseño
                        dialog.setContentView(R.layout.dialogo_opciones);

                        // Inicializar las vistas
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
                                // Toast.makeText(Listar_Notas.this, "Modificar nota", Toast.LENGTH_SHORT).show();
                                // startActivity(new Intent(Listar_Notas.this, Actualizar_Nota.class));
                                Intent intent = new Intent(Listar_Notas.this, Actualizar_Nota.class);
                                intent.putExtra("id_nota", id_nota);
                                intent.putExtra("uid_usuario", uid_usuario);
                                intent.putExtra("correo", correo);
                                intent.putExtra("fecha_hora_actual", fecha_hora_actual);
                                intent.putExtra("titulo", titulo);
                                intent.putExtra("descripcion", descripcion);
                                intent.putExtra("fecha_nota", fecha_nota);
                                intent.putExtra("hora_nota", hora_nota);
                                intent.putExtra("notificacion", notificacion);
                                intent.putExtra("categoria", categoria);
                                intent.putExtra("contacto", contacto);
                                intent.putExtra("estado", estado);
                                startActivity(intent);
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                    }
                });
                return viewHolder_nota;
            }
        };

        linearLayoutManager = new LinearLayoutManager(Listar_Notas.this, LinearLayoutManager.VERTICAL, false);
        linearLayoutManager.setReverseLayout(true); // Diseño inverso: del último registro al primero
        linearLayoutManager.setStackFromEnd(true); // El recycler view empiece desde la parte superior

        recyclerViewNotas.setLayoutManager(linearLayoutManager);
        recyclerViewNotas.setAdapter(firebaseRecyclerAdapter);
    }

    private void LlamarContacto(){
        if(!contactoSeleccionado.isEmpty()){
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:"+contactoSeleccionado));
            startActivity(intent);
        }else{
            Toast.makeText(Listar_Notas.this, "El contacto no cuenta con un número telefonico", Toast.LENGTH_SHORT).show();
        }
    }

    private void EnviarMensaje(){
        if(!contactoSeleccionado.isEmpty()){
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("smsto:"+contactoSeleccionado));
            intent.putExtra("sms_body","");
            startActivity(intent);
        }else{
            Toast.makeText(Listar_Notas.this, "El contacto no cuenta con un número telefonico", Toast.LENGTH_SHORT).show();
        }
    }

    private ActivityResultLauncher<String> SolicitudPermisoLlamada =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted ->{
                if(isGranted){
                    LlamarContacto();
                }else{
                    Toast.makeText(this, "Permiso denegado", Toast.LENGTH_SHORT).show();
                }
    });

    private ActivityResultLauncher<String> SolicitudPermisoMensaje =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted->{
               if (isGranted){
                   EnviarMensaje();
               }else{
                   Toast.makeText(this, "Permiso denegado", Toast.LENGTH_SHORT).show();
               }
            });

    private void EliminarNota(String idNota) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Listar_Notas.this);
        builder.setTitle("Eliminar tarea");
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

    private void Vaciar_Registro_De_Notas() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Listar_Notas.this);
        builder.setTitle("Vaciar todos los registros");
        builder.setMessage("¿Estás seguro(a) de eliminar todas las notas?");

        builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Eliminacion de todas las notas
                Query query = BASE_DE_DATOS.orderByChild("uid_usuario").equalTo(user.getUid());
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()){
                            ds.getRef().removeValue();
                        }
                        Toast.makeText(Listar_Notas.this, "Todas las notas se han eliminado correctamente", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

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
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_notas, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        if(item.getItemId() == R.id.Vaciar_Todas_Las_Notas){
            Vaciar_Registro_De_Notas();
        }
        if (item.getItemId() == R.id.Filtrar_Notas){
            FiltrarNotas();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(firebaseRecyclerAdapter!=null){
            firebaseRecyclerAdapter.startListening();
        }
    }

    private void FiltrarNotas(){
        Button Todas_notas, Notas_Realizadas, Notas_Pendientes, Notas_Aplazadas;

        dialog_filtrar.setContentView(R.layout.cuadro_dialogo_filtrar_notas);

        Todas_notas = dialog_filtrar.findViewById(R.id.Todas_notas);
        Notas_Realizadas = dialog_filtrar.findViewById(R.id.Notas_Realizadas);
        Notas_Pendientes = dialog_filtrar.findViewById(R.id.Notas_Pendientes);
        Notas_Aplazadas = dialog_filtrar.findViewById(R.id.Notas_Aplazadas);

        Todas_notas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("Listar", "Todas");
                editor.apply();
                recreate();
                Toast.makeText(Listar_Notas.this, "Todas las notas", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        Notas_Realizadas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("Listar", "Realizadas");
                editor.apply();
                recreate();
                Toast.makeText(Listar_Notas.this, "Notas realizadas", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        Notas_Pendientes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("Listar", "Pendientes");
                editor.apply();
                recreate();
                Toast.makeText(Listar_Notas.this, "Notas pendientes", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        Notas_Aplazadas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("Listar", "Aplazadas");
                editor.apply();
                recreate();
                Toast.makeText(Listar_Notas.this, "Notas aplazadas", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        dialog_filtrar.show();
    }

    private void Estado_Filtro(){
        sharedPreferences = Listar_Notas.this.getSharedPreferences("Notas", MODE_PRIVATE);

        String estado_filtro = sharedPreferences.getString("Listar", "Todas");

        switch (estado_filtro) {
            case "Todas":
                ListarTodasNotas();
                break;
            case "Realizadas":
                ListarNotasRealizadas();
                break;
            case "Pendientes":
                ListarNotasPendientes();
                break;
            case "Aplazadas":
                ListarNotasAplazadas();
                break;
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}