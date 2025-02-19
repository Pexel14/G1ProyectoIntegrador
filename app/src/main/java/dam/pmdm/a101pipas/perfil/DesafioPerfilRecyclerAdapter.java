package dam.pmdm.a101pipas.perfil;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import dam.pmdm.a101pipas.R;
import dam.pmdm.a101pipas.models.Desafio;


public class DesafioPerfilRecyclerAdapter extends RecyclerView.Adapter<DesafioPerfilRecyclerAdapter.DesafioViewHolder> {

    // Lista de usuarios para mostrar en el Recycler
    private List<Desafio> desafioList;

    public DesafioPerfilRecyclerAdapter(List<Desafio> desafioList) {
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
        Desafio desafio = desafioList.get(position);

        //Cargamos la imagen con la librería Picasso

        holder.tvTitle.setText(desafio.getTitulo());
//        holder.tvTitle.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.black));

        holder.pbDesafio.setProgress(desafio.getPorcentajeProgreso());

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

    // TODO PRUEBAS
    public void actualizarLista(List<Desafio> nuevaLista) {
        this.desafioList.clear();
        this.desafioList.addAll(nuevaLista);
        notifyDataSetChanged();
    }

}
