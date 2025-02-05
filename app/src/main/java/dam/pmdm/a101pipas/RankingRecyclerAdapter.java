package dam.pmdm.a101pipas;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import dam.pmdm.a101pipas.models.User;

public class RankingRecyclerAdapter extends RecyclerView.Adapter<RankingRecyclerAdapter.RankingViewHolder>{
    private ArrayList<User> listaUsuarios;

    public RankingRecyclerAdapter(ArrayList<User> listaUsuarios) {
        this.listaUsuarios = listaUsuarios;
    }

    @NonNull
    @Override
    public RankingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ranking_global_item, parent, false);
        return new RankingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RankingRecyclerAdapter.RankingViewHolder holder, int position) {
        User usuario = listaUsuarios.get(position);
        int backgroundColor;
        if (position == 0 || position == 2) {
            backgroundColor = R.color.color_principal;
        } else if (position == 1) {
            backgroundColor =R.color.color_secundario_ranking;
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

    static class RankingViewHolder extends RecyclerView.ViewHolder{
        TextView tvRankingTop, tvRankingUsuario, tvRankingCompletados;

        public RankingViewHolder(@NonNull View itemView) {
            super(itemView);
            //Asignamos vistas a las variables
            tvRankingTop = itemView.findViewById(R.id.tvRankingTop);
            tvRankingUsuario = itemView.findViewById(R.id.tvRankingUsuario);
            tvRankingCompletados = itemView.findViewById(R.id.tvRankingCompletados);
        }

    }

}
