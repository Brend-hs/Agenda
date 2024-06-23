package com.example.agenda.ViewHolder;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.agenda.R;

public class ViewHolder_Nota extends RecyclerView.ViewHolder {

    View mView;

    private ViewHolder_Nota.ClickListener mClickListener;
    public  interface ClickListener{
        void onItemClick(View view, int position);//Se ejecuta al presionar en el item
        void onItemLongClick(View view, int position);//Se ejecuta al mantener presiona el item
    }

    public void  setOnClickListener(ViewHolder_Nota.ClickListener clickListener){
        mClickListener = clickListener;
    }

    public ViewHolder_Nota(@NonNull View itemView) {
        super(itemView);
        mView = itemView;

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickListener.onItemClick(v,getAdapterPosition());
            }
        });

        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mClickListener.onItemLongClick(v,getAdapterPosition());
                return false;
            }
        });
    }

    public void SetearDatos(Context context, String id_nota, String uid_usuario, String correo_usuario,
                            String fecha_hora_registro, String titulo, String descripcion, String fecha_nota,
                            String hora_nota, String notificacion, String categoria, String contacto, String estado){
        //Declarar vistas
        TextView Id_nota_Item, Uid_Usuario_Item, Correo_Usuario_Item, Fecha_hora_registro_Item, Titulo_Item,
                Descripcion_Item, Entrega_Item, Notificacion_Item, Fecha_Item, Hora_Item, Categoria_Item, Estado_Item;

        LinearLayoutCompat LinearLayout_Item;

        //Establecer la conexion con el item
        Id_nota_Item = mView.findViewById(R.id.Id_nota_Item);
        Uid_Usuario_Item = mView.findViewById(R.id.Uid_Usuario_Item);
        Correo_Usuario_Item = mView.findViewById(R.id.Correo_Usuario_Item);
        Fecha_hora_registro_Item = mView.findViewById(R.id.Fecha_hora_registro_Item);
        Titulo_Item = mView.findViewById(R.id.Titulo_Item);
        Descripcion_Item = mView.findViewById(R.id.Descripcion_Item);
        Entrega_Item = mView.findViewById(R.id.Entrega_Item);
        Notificacion_Item = mView.findViewById(R.id.Notificacion_Item);
        Fecha_Item = mView.findViewById(R.id.Fecha_Item);
        Hora_Item = mView.findViewById(R.id.Hora_Item);
        Categoria_Item = mView.findViewById(R.id.Categoria_Item);
        Estado_Item = mView.findViewById(R.id.Estado_Item);
        LinearLayout_Item = mView.findViewById(R.id.LinearLayout_Item);

        //Setear la informacion dentro del item
        Id_nota_Item.setText(id_nota);
        Uid_Usuario_Item.setText(uid_usuario);
        Correo_Usuario_Item.setText(correo_usuario);
        Fecha_hora_registro_Item.setText(fecha_hora_registro);
        Titulo_Item.setText(titulo);
        Descripcion_Item.setText(descripcion);
        Entrega_Item.setText(fecha_nota+" "+hora_nota);
        Notificacion_Item.setText(notificacion);
        Fecha_Item.setText(fecha_nota);
        Hora_Item.setText(hora_nota);
        Categoria_Item.setText(categoria);
        Estado_Item.setText(estado);

        if(estado.equals("Pendiente")){
            LinearLayout_Item.setBackgroundColor(Color.parseColor("#990000"));
        } else if (estado.equals("Realizado")) {
            LinearLayout_Item.setBackgroundColor(Color.parseColor("#9ADE7B"));
        }else{
            LinearLayout_Item.setBackgroundColor(Color.parseColor("#FFBB64"));
        }
    }
}
