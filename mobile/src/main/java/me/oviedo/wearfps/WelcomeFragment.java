package me.oviedo.wearfps;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class WelcomeFragment extends Fragment {

    private WelcomeFragmentInterface mInterface;

    private TextView statusText;
    private ProgressBar progressBar;
    private Button retryButton;

    public WelcomeFragment() {
        // Required empty public constructor
    }

    public static WelcomeFragment newInstance() {
        WelcomeFragment fragment = new WelcomeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_welcome, container, false);
        findViews(root);
        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mInterface.retryClick();
            }
        });
        return root;
    }

    private void findViews(View root) {
        statusText = (TextView) root.findViewById(R.id.connectStatusText);
        progressBar = (ProgressBar) root.findViewById(R.id.connectProgressbar);
        retryButton = (Button) root.findViewById(R.id.retryConnectButton);
    }

    public void setFindingServer() {
        statusText.setText(getText(R.string.finding_server));
        retryButton.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }

    public void setError() {
        statusText.setText(getText(R.string.server_not_found));
        progressBar.setVisibility(View.GONE);
        retryButton.setVisibility(View.VISIBLE);
    }

    public void setReady() {
        statusText.setText(getText(R.string.server_ready));
        retryButton.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof WelcomeFragmentInterface) {
            mInterface = (WelcomeFragmentInterface) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement WelcomeFragmentInterface");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mInterface = null;
    }

    public interface WelcomeFragmentInterface {
        void retryClick();
    }
}
