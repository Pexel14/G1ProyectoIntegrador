package dam.pmdm.a101pipas.desafios.descubrir;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import dam.pmdm.a101pipas.R;
import dam.pmdm.a101pipas.experiencias.ListadoExperienciasViewModel;
import dam.pmdm.a101pipas.models.Desafio;

public class DescubrirAdapter extends RecyclerView.Adapter<DescubrirAdapter.DescubrirViewHolder> {

    private List<Desafio> listaDesafios;
    private Fragment fragment;

    public DescubrirAdapter(List<Desafio> listaDesafios, Fragment fragment){
        this.listaDesafios = listaDesafios;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public DescubrirAdapter.DescubrirViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_tarjeta_desafio_descubrir, parent, false);
        return new DescubrirAdapter.DescubrirViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DescubrirAdapter.DescubrirViewHolder holder, int position) {
        Desafio desafio = listaDesafios.get(position);

        holder.tvTitulo.setText(desafio.getTitulo());
        holder.tvCiudad.setText(desafio.getCiudad());

        ListadoExperienciasViewModel listadoExperienciasViewModel = new ViewModelProvider(fragment.requireActivity()).get(ListadoExperienciasViewModel.class);

        holder.itemView.setOnClickListener(v -> {
            listadoExperienciasViewModel.setIdDesafio(desafio.getId(), desafio.getTitulo());
            Navigation.findNavController(v).navigate(R.id.navigation_listado_experiencias);
        });

    }

    @Override
    public int getItemCount() {
        return listaDesafios.size();
    }

    static class DescubrirViewHolder extends RecyclerView.ViewHolder{

        TextView tvCiudad, tvTitulo;

        public DescubrirViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCiudad = itemView.findViewById(R.id.tvUbicacionTarjetaDescubrir);
            tvTitulo = itemView.findViewById(R.id.tvTituloTarjetaDescubrir);
        }
    }

}
