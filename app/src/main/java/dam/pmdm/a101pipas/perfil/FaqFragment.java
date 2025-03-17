package dam.pmdm.a101pipas.perfil;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;

import dam.pmdm.a101pipas.R;

public class FaqFragment extends BottomSheetDialogFragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_faq, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerFaq);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        List<Pair<String, String>> faqList = new ArrayList<>();
        faqList.add(new Pair<>(getString(R.string.por_qu_no_veo_los_marcadores_en_el_mapa), getString(R.string.aseg_rate_de_tener_conexi_n_a_internet_y_de_que_la_app_tiene_permisos_de_ubicaci_n_activados)));
        faqList.add(new Pair<>(getString(R.string.no_puedo_unirme_a_un_grupo_qu_hago), getString(R.string.verifica_que_el_grupo_sea_p_blico_y_en_caso_de_que_sea_privado_que_sepas_la_contrase_a_si_el_problema_persiste_intenta_reiniciar_la_app)));
        faqList.add(new Pair<>(getString(R.string.c_mo_puedo_reportar_un_problema_en_la_app), getString(R.string.en_la_secci_n_de_ajustes_selecciona_reportar_un_problema_y_elige_el_tipo_de_incidencia)));
        faqList.add(new Pair<>(getString(R.string.c_mo_personalizo_mi_perfil), getString(R.string.desde_tu_perfil_puedes_cambiar_tu_nombre_de_usuario_y_foto_de_perfil_f_cilmente)));


        FaqAdapter adapter = new FaqAdapter(faqList);
        recyclerView.setAdapter(adapter);
    }
}