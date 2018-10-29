package my.edu.tarc.communechat_v2.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import my.edu.tarc.communechat_v2.ProfileActivity;
import my.edu.tarc.communechat_v2.R;
import my.edu.tarc.communechat_v2.TestEncryptionActivity;

public class ProfileFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        Button button_activity = view.findViewById(R.id.button_activity);
        button_activity.setOnClickListener(buttonListener);
        Button button_activiy1 = view.findViewById(R.id.button_activity1);
        button_activiy1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), TestEncryptionActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }

    private Button.OnClickListener buttonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(view.getContext(), ProfileActivity.class);
            startActivity(intent);
        }
    };
}
