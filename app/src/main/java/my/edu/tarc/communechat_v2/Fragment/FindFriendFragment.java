package my.edu.tarc.communechat_v2.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import my.edu.tarc.communechat_v2.Adapter.FindFriendAdapter;
import my.edu.tarc.communechat_v2.FindFriendResult;
import my.edu.tarc.communechat_v2.R;
import my.edu.tarc.communechat_v2.model.FindFriendAdapterClass;

public class FindFriendFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ArrayList<FindFriendAdapterClass> list = new ArrayList<>();
        View view = inflater.inflate(R.layout.fragment_find_friend, container, false);

        list.add(new FindFriendAdapterClass(1,
                getString(R.string.find_by_programme),
                getString(R.string.find_by_programme_desc)));
        list.add(new FindFriendAdapterClass(2,
                getString(R.string.find_by_tut),
                getString(R.string.find_by_tut_desc)));
        list.add(new FindFriendAdapterClass(3,
                getString(R.string.find_by_address),
                getString(R.string.find_by_address_desc)));
        list.add(new FindFriendAdapterClass(4,
                getString(R.string.find_by_age),
                getString(R.string.find_by_age_desc)));
        list.add(new FindFriendAdapterClass(5,
                getString(R.string.suggest_friend),
                getString(R.string.suggest_friend_desc)));

        FindFriendAdapter adapter = new FindFriendAdapter(getActivity(), R.layout.adapter_find_friend, list);
        ListView listViewFind = (ListView) view.findViewById(R.id.listView_find_method);
        listViewFind.setAdapter(adapter);
        listViewFind.setOnItemClickListener(listener);
        return view;
    }

    private ListView.OnItemClickListener listener = new ListView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Intent intent = new Intent(getActivity(), FindFriendResult.class);
            intent.putExtra("Type", i);
            startActivity(intent);
        }
    };
}
