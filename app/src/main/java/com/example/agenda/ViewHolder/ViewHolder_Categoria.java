package com.example.agenda.ViewHolder;

import android.content.Context;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.agenda.R;

import java.util.ArrayList;

public class ViewHolder_Categoria extends RecyclerView.ViewHolder {

    private ArrayList<String> Categorias;
    View cView;
    TextView Categoria_Item_C;
    ImageButton Btn_Editar, Btn_Eliminar;

    private ViewHolder_Categoria.ClickListener cClickListener;

    public interface ClickListener {
        void onEditClick(View view, int position);
        void onDeleteClick(View view, int position);
    }

    public ViewHolder_Categoria(@NonNull View itemView) {
        super(itemView);
        cView = itemView;

        // Inicialización de vistas
        Categoria_Item_C = itemView.findViewById(R.id.Categoria_Item_C);
        Btn_Editar = itemView.findViewById(R.id.Editar_Categoria);
        Btn_Eliminar = itemView.findViewById(R.id.Eliminar_Categoria);

        // Asignación de listeners
        Btn_Editar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cClickListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        cClickListener.onEditClick(v, position);
                    }
                }
            }
        });

        Btn_Eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cClickListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        cClickListener.onDeleteClick(v, position);
                    }
                }
            }
        });
    }

    public void SetearCategoria(Context context, String categoria) {
        Categoria_Item_C.setText(categoria);
    }

    public void setOnClickListener(ViewHolder_Categoria.ClickListener clickListener) {
        cClickListener = clickListener;
    }
}
