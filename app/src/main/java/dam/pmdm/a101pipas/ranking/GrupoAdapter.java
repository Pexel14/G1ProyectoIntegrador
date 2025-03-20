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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dam.pmdm.a101pipas.R;
import dam.pmdm.a101pipas.models.DesafioUsuario;
import dam.pmdm.a101pipas.models.User;

public class GrupoAdapter extends RecyclerView.Adapter<GrupoAdapter.RankingPrivadoViewHolder>{
    private ArrayList<User> listaUsuarios;

    private String desafio;
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

        if(desafio != null){
            Map<String, DesafioUsuario> desafioUsuario = usuario.getDesafios();

//            if (desafiosMap != null) {
//                Object desafioData = desafiosMap.get(desafio);
//                DesafioUsuario desafioUsuario = null;
//
//                if (desafioData instanceof HashMap) {
//                    // Convierte manualmente el HashMap a DesafioUsuario
//                    HashMap<String, String> map = (HashMap<String, String>) desafioData;
//                    String estado = map.get("estado");
//                    String experiencias = map.get("experiencias_completadas");
//                    desafioUsuario = new DesafioUsuario();
//                    desafioUsuario.setEstado(estado);
//                    desafioUsuario.setExperiencias_completadas(experiencias);
//                } else if (desafioData != null) {
//                    desafioUsuario = (DesafioUsuario) desafioData;
//                }
//
//                if (desafioUsuario != null) {
//                    // Actualiza la UI con desafioUsuario
//                }
//            }

            if(desafioUsuario != null){
                DesafioUsuario desafioUsuario1 = new DesafioUsuario(
                        desafioUsuario.get(desafio).getEstado(),
                        desafioUsuario.get(desafio).getExperiencias_completadas()
                );

                if(desafioUsuario1 != null){
                    if(desafioUsuario1.getExperiencias_completadas() != null){
                        if(!desafioUsuario1.getExperiencias_completadas().isEmpty()){
                            holder.tvRankingCompletados.setText(String.valueOf(desafioUsuario1.getExperiencias_completadas().split(",").length));
                        }
                    }else {
                        holder.tvRankingCompletados.setText("0");
                    }
                }
            }
        } else {
            holder.tvRankingCompletados.setText("0");
        }

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

    public void setDesafio(String desafio) {
        this.desafio = desafio;
    }
}
