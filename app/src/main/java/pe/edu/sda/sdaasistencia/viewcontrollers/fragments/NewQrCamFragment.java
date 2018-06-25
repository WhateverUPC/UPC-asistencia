package pe.edu.sda.sdaasistencia.viewcontrollers.fragments;


import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
import pe.edu.sda.sdaasistencia.R;
import pe.edu.sda.sdaasistencia.interfaces.APIService;
import pe.edu.sda.sdaasistencia.model.QrSyncResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewQrCamFragment extends Fragment implements ZXingScannerView.ResultHandler{

    private ZXingScannerView mScannerView;

    private String idAlumno;
    private String nombreAlumno;
    private String codigoQr;
    private Retrofit retrofit;
    private APIService apiService;

    public NewQrCamFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mScannerView = new ZXingScannerView(getActivity());
        mScannerView.setAutoFocus(true);
        idAlumno = getArguments().getString("idalumno");
        nombreAlumno = getArguments().getString("nombrealumno");
        Toast.makeText(getActivity(), nombreAlumno, Toast.LENGTH_SHORT).show();

        retrofit = new Retrofit.Builder().baseUrl("http://45.55.189.9/webservices/sda/asistenciasda/").build();
        apiService = retrofit.create(APIService.class);

        return mScannerView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    public void handleResult(Result rawResult) {
        codigoQr = rawResult.getText();
        Call<QrSyncResponse> syncCall = apiService.syncQr(idAlumno, codigoQr);
        syncCall.enqueue(new Callback<QrSyncResponse>() {
            @Override
            public void onResponse(Call<QrSyncResponse> call, Response<QrSyncResponse> response) {
                mScannerView.stopCamera();
                ToneGenerator toneBeep = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
                toneBeep.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200);
                Toast.makeText(getActivity(), "Se agregó el QR del alumno con éxito", Toast.LENGTH_SHORT).show();
                android.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.contentFrameLayout, new NewQrFragment(), "SyncNewQrFragment");
                ft.commit();

            }

            @Override
            public void onFailure(Call<QrSyncResponse> call, Throwable t) {
                Toast.makeText(getActivity(), "Hubo un error al agregar el QR, pruebe nuevamente", Toast.LENGTH_SHORT).show();
            }
        });

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mScannerView.resumeCameraPreview(NewQrCamFragment.this);
            }
        }, 2000);
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

}
