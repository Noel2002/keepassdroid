package com.keepassdroid.sync.views;

import androidx.activity.result.ActivityResultLauncher;
import androidx.fragment.app.Fragment;

import android.app.AlertDialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.android.keepass.R;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.net.URI;
import com.keepassdroid.sync.utilities.JPAKESession;
import com.keepassdroid.sync.utilities.WebSocketClient;

public class PairFragment extends Fragment {

    private Button btn_scan;
    private WebSocketClient webSocketClient;
    private ActivityResultLauncher<ScanOptions> barLauncher;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pair, container, false);

        btn_scan = view.findViewById(R.id.btn_scan);
        btn_scan.setOnClickListener(v -> scanCode());

        // Initialize the ActivityResultLauncher
        barLauncher = registerForActivityResult(new ScanContract(), result -> {
            if (result.getContents() != null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Result");
                builder.setMessage(result.getContents());
                builder.setPositiveButton("OK", (dialogInterface, i) -> dialogInterface.dismiss()).show();

                String qrContent = result.getContents();
                String[] splitted = qrContent.split("\\?");
                String sharedPassword = splitted[1];
                String url = splitted[0];

                // Debug
                System.out.println("Password: " + sharedPassword);
                JPAKESession.sharedPassword = sharedPassword;

                // Connect to web socket
                connectWebSocket(url);
            }
        });

        return view;
    }

    private void scanCode() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Volume up to flash on");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);
        barLauncher.launch(options);
    }

    private void showAlert(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("OK", (dialogInterface, i) -> dialogInterface.dismiss()).show();
    }

    private void connectWebSocket(String url) {
        new Thread(() -> {
            try {
                URI uri = new URI(url);
                this.webSocketClient = new WebSocketClient(uri);
            } catch (Exception e) {
                e.printStackTrace();
                requireActivity().runOnUiThread(() -> showAlert("Error", "Failed to connect to WebSocket: " + e.getMessage()));
            }
        }).start();
    }
}