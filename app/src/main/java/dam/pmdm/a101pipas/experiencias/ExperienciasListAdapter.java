package dam.pmdm.a101pipas.experiencias;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import dam.pmdm.a101pipas.R;
import dam.pmdm.a101pipas.models.Experiencia;

public class ExperienciasListAdapter extends RecyclerView.Adapter<ExperienciasListAdapter.ExperienciaViewHolder> {

    private List<Experiencia> experienciaList;
    private OnExperienciaClickListener listener;

    public interface OnExperienciaClickListener {
        void onExperienciaClick(Experiencia experiencia);
    }

    // Constructor
    public ExperienciasListAdapter(List<Experiencia> experienciaList, OnExperienciaClickListener listener) {
        this.experienciaList = experienciaList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ExperienciaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflar el diseño del ítem
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_experiencias, parent, false);
        return new ExperienciaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExperienciaViewHolder holder, int position) {
        // Obtener la experiencia actual
        Experiencia experiencia = experienciaList.get(position);

        // Cargar imagen usando Picasso
        Picasso.get()
                .load(experiencia.getImagen())
                .placeholder(R.drawable.experiencia) // Imagen de carga
                .error(R.drawable.experiencia) // Imagen de error
                .into(holder.imgExperiencia);

        // Configurar datos de la tarjeta
        holder.tvTitulo.setText(experiencia.getTitulo());
        holder.tvDescripcion.setText(experiencia.getDescripcion());


        // Configurar enlace del mapa
        holder.tvMapLink.setOnClickListener(view -> {
            if (listener != null) {
                listener.onExperienciaClick(experiencia);
            }
        });

        //TODO: ARREGLAR SIN UTILIZAR IS COMPLETADA, HAY CAMPOS DESAFIOS_COMPLETADOS DENTRO DE CADA USUARIO
        // Mostrar o esconder el check de completado
//        if (experiencia.isCompletada()) {
//            holder.tvCheck.setVisibility(View.VISIBLE);
//            holder.tvCheck.setText(R.string.experiencias_listadas_exp_completada);
//        } else {
//            holder.tvCheck.setVisibility(View.GONE);
//        }
    }

    public void setExperiencias(List<Experiencia> experiencias) {
        this.experienciaList = experiencias;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return experienciaList.size();
    }


    static class ExperienciaViewHolder extends RecyclerView.ViewHolder {

        ImageView imgExperiencia, tvMapLink;
        TextView tvTitulo, tvDescripcion, tvCheck;

        public ExperienciaViewHolder(@NonNull View itemView) {
            super(itemView);

            imgExperiencia = itemView.findViewById(R.id.imgExperiencia);
            tvTitulo = itemView.findViewById(R.id.tvTitle);
            tvDescripcion = itemView.findViewById(R.id.tvDescription);
            tvMapLink = itemView.findViewById(R.id.tvMapLink);
            tvCheck = itemView.findViewById(R.id.tvCheck);
        }
    }
}


