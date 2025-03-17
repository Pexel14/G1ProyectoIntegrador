package dam.pmdm.a101pipas.perfil;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.firebase.auth.FirebaseAuth;

import dam.pmdm.a101pipas.autenticacion.Login;
import dam.pmdm.a101pipas.R;
import dam.pmdm.a101pipas.databinding.FragmentAjustesBinding;

public class AjustesFragment extends Fragment {

    private FragmentAjustesBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAjustesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.contentLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showConfirmationDialog(getString(R.string.txt_message_alert_settings), () -> {
                    FirebaseAuth.getInstance().signOut();
                    Intent i = new Intent(requireActivity(), Login.class);
                    startActivity(i);
                    requireActivity().finish();
                });
            }
        });

        binding.btnAtrasAjustes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.navigation_perfil);
            }
        });

        binding.contentRequestAdmin.setOnClickListener(v -> enviarCorreo("",""));
        
        binding.contentFaq.setOnClickListener(v -> faq());

        binding.contentReport.setOnClickListener(v -> report());
    }

    private void report() {
        String[] reportOptions = {
                getString(R.string.no_aparecen_marcadores_en_el_mapa),
                getString(R.string.no_se_completa_una_experiencia_de_un_desaf_o),
                getString(R.string.no_puedo_unirme_a_un_grupo),
                getString(R.string.otro)
        };

        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.indique_su_problema_actual)
                .setItems(reportOptions, (dialog, which) -> {
                    String subject = getString(R.string.reporte_de_problema_en_la_aplicaci_n);
                    String body;

                    switch (which) {
                        case 0:
                            body = getString(R.string.txt1_report);
                            break;
                        case 1:
                            body = getString(R.string.txt2_report);
                            break;
                        case 2:
                            body = getString(R.string.txt3_report);
                            break;
                        case 3:
                            body = getString(R.string.txt4_report);
                            break;
                        default:
                            body = "";
                            break;
                    }

                    enviarCorreo(subject, body);
                })
                .setNegativeButton(R.string.cancelar, (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void enviarCorreo(String subject, String body) {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"101.support@101.es"});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, body);

        try {
            startActivity(Intent.createChooser(emailIntent, getString(R.string.elige_una_aplicaci_n_de_correo)));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getContext(), R.string.no_se_puede_enviar_el_correo_no_se_encuentran_aplicaciones_disponibles, Toast.LENGTH_SHORT).show();
        }
    }

    private void showConfirmationDialog(String message, Runnable onConfirmAction) {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.txt_title_alert_settings)
                .setMessage(message)
                .setPositiveButton(R.string.confirmar, (dialog, which) -> onConfirmAction.run())
                .setNegativeButton(R.string.cancelar, (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void faq() {
        FaqFragment faqBottomSheet = new FaqFragment();
        faqBottomSheet.show(getParentFragmentManager(), faqBottomSheet.getTag());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
