package com.keepassdroid.sync.views;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.keepass.R;
import com.keepassdroid.sync.models.CredentialChange;

import java.util.List;


public class CredentialsChangeListAdapter extends RecyclerView.Adapter<CredentialsChangeListAdapter.CredentialChangeViewHolder> {
    List<CredentialChange> credentialChangeList;

    public CredentialsChangeListAdapter(List<CredentialChange> credentialChangeList) {
        this.credentialChangeList = credentialChangeList;
    }

    @NonNull
    @Override
    public CredentialChangeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.credential_change, parent, false);
        return new CredentialChangeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CredentialChangeViewHolder holder, int position) {
        CredentialChange credentialChange = credentialChangeList.get(position);
        holder.bind(credentialChange);
    }

    @Override
    public int getItemCount() {
        return this.credentialChangeList.size();
    }

    class CredentialChangeViewHolder extends RecyclerView.ViewHolder{
        private TextView titleView;
        private TextView usernameView;
        private TextView changeTypeView;

        public CredentialChangeViewHolder(@NonNull View itemView) {
            super(itemView);
            titleView = itemView.findViewById(R.id.cred_title);
            usernameView = itemView.findViewById(R.id.cred_username);
            changeTypeView = itemView.findViewById(R.id.cred_change);
        }

        public void bind(CredentialChange credentialChange) {
            titleView.setText(credentialChange.getTitle());
            usernameView.setText(credentialChange.getUsername());
            changeTypeView.setText(credentialChange.getChangeType());
        }
    }
}
