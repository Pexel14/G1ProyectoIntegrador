package dam.pmdm.a101pipas.ranking;

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

public class RankingPrivadoAdapter extends RecyclerView.Adapter<RankingPrivadoAdapter.RankingViewHolder>{

    private String grupo;
    private ArrayList<User> listaUsuarios;

    public RankingPrivadoAdapter(String grupo, ArrayList<User> listaUsuarios){
        this.grupo = grupo;
        this.listaUsuarios = listaUsuarios;
    }

    @NonNull
    @Override
    public RankingPrivadoAdapter.RankingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ranking_global_item, parent, false);
        return new RankingPrivadoAdapter.RankingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RankingPrivadoAdapter.RankingViewHolder holder, int position) {
        User usuario = listaUsuarios.get(position);
        int backgroundColor;
        if (position == 0 || position == 2) {
            backgroundColor = R.color.color_principal;
        } else if (position == 1) {
            backgroundColor =R.color.color_secundario_ranking;
        } else {
            backgroundColor = (position % 2 != 0) ? R.color.color_fondo : R.color.white;
        }

        holder.tvTitulo.setText(String.format(holder.itemView.getContext().getString(R.string.ranking_privado_titulo), grupo));

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
        this.listaUsuarios.clear();
        this.listaUsuarios.addAll(nuevosUsuarios);
        notifyDataSetChanged();
    }

    static class RankingViewHolder extends RecyclerView.ViewHolder{
        TextView tvRankingTop, tvRankingUsuario, tvRankingCompletados, tvTitulo;

        public RankingViewHolder(@NonNull View itemView) {
            super(itemView);
            //Asignamos vistas a las variables
            tvTitulo = itemView.findViewById(R.id.tvRankingTitulo);
            tvRankingTop = itemView.findViewById(R.id.tvRankingTop);
            tvRankingUsuario = itemView.findViewById(R.id.tvRankingUsuario);
            tvRankingCompletados = itemView.findViewById(R.id.tvRankingCompletados);
        }

    }

}
