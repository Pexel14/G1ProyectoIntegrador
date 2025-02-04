package dam.pmdm.a101pipas;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


public class DesafioRecyclerAdapter extends RecyclerView.Adapter<DesafioRecyclerAdapter.DesafioViewHolder> {

    // Lista de usuarios para mostrar en el Recycler
    private List<DesafioPerfil> desafioList;

    public DesafioRecyclerAdapter(List<DesafioPerfil> desafioList) {
        this.desafioList = desafioList;
    }

    // 1. Método que infla el layout
    @NonNull
    @Override
    public DesafioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_profile_item, parent, false);
        return new DesafioViewHolder(view);
    }

    // 2 . Método que asigna los datos a cada vista dentro del ViewHolder

    @Override
    public void onBindViewHolder(@NonNull DesafioViewHolder holder, int position) {

        // Obtener el usuario dentro de la lista
        DesafioPerfil desafio = desafioList.get(position);

        //Cargamos la imagen con la librería Picasso

        holder.tvTitle.setText(desafio.getTitulo());
        holder.pbDesafio.setProgress(desafio.getPorcentajeProgreso(), true);

    }

    @Override
    public int getItemCount() {
        return desafioList.size();
    }

    // Clase interna DesafioViewHolder. Extiende de RecyclerView.ViewHolder
    static class DesafioViewHolder extends RecyclerView.ViewHolder{
        ProgressBar pbDesafio;
        TextView tvTitle;

        public DesafioViewHolder(@NonNull View itemView) {
            super(itemView);

            // Asignamos vistas a las variables
            tvTitle = itemView.findViewById(R.id.tvNombreDesafio);
            pbDesafio = itemView.findViewById(R.id.pbDesafio);

        }
    }
}
