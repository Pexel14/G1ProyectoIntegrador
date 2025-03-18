package dam.pmdm.a101pipas.desafios.inicio;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import dam.pmdm.a101pipas.R;
import dam.pmdm.a101pipas.experiencias.ListadoExperienciasFragment;
import dam.pmdm.a101pipas.experiencias.ListadoExperienciasViewModel;
import dam.pmdm.a101pipas.geolocalizacion.GeolocalizacionViewModel;
import dam.pmdm.a101pipas.models.Desafio;

public class InicioAdapter extends RecyclerView.Adapter<InicioAdapter.InicioViewHolder> {

    private List<Desafio> listaDesafios;
    private Fragment fragment;

    public InicioAdapter(List<Desafio> listaDesafios, Fragment fragment) {
        this.listaDesafios = listaDesafios;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public InicioAdapter.InicioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_tarjeta_desafio_inicio, parent, false);
        return new InicioAdapter.InicioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InicioAdapter.InicioViewHolder holder, int position) {
        Desafio desafio = listaDesafios.get(position);

        holder.tvDescripcion.setText(desafio.getDescripcion());
        holder.tvEtiuqetas.setText(desafio.getEtiquetas());
        holder.tvCiudad.setText(desafio.getCiudad());
        holder.tvDesafio.setText(desafio.getTitulo());

        GeolocalizacionViewModel geolocalizacionViewModel = new ViewModelProvider(fragment.requireActivity()).get(GeolocalizacionViewModel.class);

        holder.ivMaps.setOnClickListener(v -> {
            geolocalizacionViewModel.setDesafioId(desafio.getTitulo());
            Navigation.findNavController(v).navigate(R.id.navigation_geolocalizacion);
        });

        ListadoExperienciasViewModel listadoExperienciasViewModel = new ViewModelProvider(fragment.requireActivity()).get(ListadoExperienciasViewModel.class);

        holder.itemView.setOnClickListener(v -> {
            listadoExperienciasViewModel.setIdDesafio(desafio.getId(), desafio.getTitulo());
            ListadoExperienciasFragment.setVolverAtras(0);
            Navigation.findNavController(v).navigate(R.id.navigation_listado_experiencias);
        });

    }

    public void setListaDesafios(List<Desafio> listaDesafios) {
        this.listaDesafios = listaDesafios;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return listaDesafios.size();
    }

    public static class InicioViewHolder extends RecyclerView.ViewHolder{
        TextView tvCiudad, tvDesafio, tvEtiuqetas, tvDescripcion;
        ImageView ivMaps;

        public InicioViewHolder(@NonNull View itemView){
            super(itemView);
            tvCiudad = itemView.findViewById(R.id.tvUbicacionTarjetaDescubrir);
            tvDesafio = itemView.findViewById(R.id.tvTituloTarjetaInicio);
            tvEtiuqetas = itemView.findViewById(R.id.tvEtiquetasTarjetaInicio);
            tvDescripcion = itemView.findViewById(R.id.tvDescripcionTarjetaInicio);
            ivMaps = itemView.findViewById(R.id.imgTarjetaInicio);
        }

    }

}
