package com.keepassdroid.sync.views;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.keepass.R;
import com.keepassdroid.app.App;
import com.keepassdroid.sync.models.CredentialChange;
import com.keepassdroid.sync.models.ProcessProgress;
import com.keepassdroid.sync.models.ProcessProgressManager;
import com.keepassdroid.sync.utilities.Merger;

import java.util.ArrayList;
import java.util.List;

public class ProgressFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView changeRecyclerView;
    private ProgressAdapter progressAdapter;
    private CredentialsChangeListAdapter credentialsChangeListAdapter;
    private List<ProcessProgress> progressList;

    private List<CredentialChange> credentialChangeList;

    private TextView stateTextView;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_progress, container, false);

        stateTextView = view.findViewById(R.id.state_text);
        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        changeRecyclerView = view.findViewById(R.id.change_recycler_view);
        changeRecyclerView.setLayoutManager( new LinearLayoutManager(getContext()));

        // Initialize the list of ProcessProgress items
        progressList = new ArrayList<>();
        credentialChangeList = new ArrayList<>();

        // Set up the adapter
        progressAdapter = new ProgressAdapter(progressList);
        recyclerView.setAdapter(progressAdapter);

        credentialsChangeListAdapter = new CredentialsChangeListAdapter(credentialChangeList);
        changeRecyclerView.setAdapter(credentialsChangeListAdapter);

        // IMPORTANT: Initialize Merger Singleton
        Merger.init(App.getDB(), this.getActivity());

        ProcessProgressManager.init(this);
        try{
            ProcessProgressManager manager = ProcessProgressManager.getInstance();
            manager.addProcessProgress("Web socket connection", "Successful");

        }catch (Exception e){
            System.out.println(e.getLocalizedMessage());
        }
        finally{
            return view;
        }
    }

    public void addProcessProgress(ProcessProgress progress) {
        progressList.add(progress);
        progressAdapter.notifyItemInserted(progressList.size() - 1);
    }

    public void addCredentialChange(CredentialChange credentialChange){
        credentialChangeList.add(credentialChange);
        credentialsChangeListAdapter.notifyItemInserted(credentialChangeList.size() -1);
    }

    public void setStateText(String state){
        stateTextView.setText(state);
    }
}