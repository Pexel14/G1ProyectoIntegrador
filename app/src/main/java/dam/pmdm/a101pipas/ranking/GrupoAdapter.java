package dam.pmdm.a101pipas.ranking;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import dam.pmdm.a101pipas.R;
import dam.pmdm.a101pipas.models.User;

public class GrupoAdapter extends RecyclerView.Adapter<GrupoAdapter.RankingPrivadoViewHolder>{
    private ArrayList<User> listaUsuarios;

    public GrupoAdapter(ArrayList<User> listaUsuarios){
        this.listaUsuarios = listaUsuarios;
    }

    @NonNull
    @Override
    public GrupoAdapter.RankingPrivadoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ranking_global_item, parent, false);
        return new GrupoAdapter.RankingPrivadoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RankingPrivadoViewHolder holder, int position) {
        User usuario = listaUsuarios.get(position);
        Log.d("Compartir", "HA ENTRADO EN EL BINDVIEWHOLDER");
        int backgroundColor;
        if (position == 0 || position == 2) {
            backgroundColor = R.color.color_principal;
        } else if (position == 1) {
            backgroundColor = R.color.color_secundario_ranking;
        } else {
            backgroundColor = (position % 2 != 0) ? R.color.color_fondo : R.color.white;
        }

        holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), backgroundColor));

        if(position <= 2){
            holder.tvRankingUsuario.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.white));
            holder.tvRankingCompletados.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.white));
            holder.tvRankingTop.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.white));
        }

        holder.tvRankingTop.setText(String.valueOf((position+1)));
        holder.tvRankingUsuario.setText(usuario.getUsername());
        holder.tvRankingCompletados.setText(String.valueOf(usuario.getExpCompletadas() * -1));
    }

    @Override
    public int getItemCount() {
        return listaUsuarios.size();
    }

    public void actualizarRanking(List<User> nuevosUsuarios) {
        this.listaUsuarios = new ArrayList<>(nuevosUsuarios);
        notifyDataSetChanged();
    }

    static class RankingPrivadoViewHolder extends RecyclerView.ViewHolder{
        TextView tvRankingTop, tvRankingUsuario, tvRankingCompletados, tvTitulo;

        public RankingPrivadoViewHolder(@NonNull View itemView) {
            super(itemView);
            //Asignamos vistas a las variables
            tvTitulo = itemView.findViewById(R.id.tvRankingTitulo);
            tvRankingTop = itemView.findViewById(R.id.tvRankingTop);
            tvRankingUsuario = itemView.findViewById(R.id.tvRankingUsuario);
            tvRankingCompletados = itemView.findViewById(R.id.tvRankingCompletados);
        }
    }
}
