package my.edu.tarc.communechat_v2.Fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import my.edu.tarc.communechat_v2.Adapter.FindFriendAdapter;
import my.edu.tarc.communechat_v2.AdvancedSearchActivity;
import my.edu.tarc.communechat_v2.FindFriendResult;
import my.edu.tarc.communechat_v2.R;
import my.edu.tarc.communechat_v2.model.FindFriendAdapterClass;
import my.edu.tarc.communechat_v2.model.User;

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
                getString(R.string.find_by_location),
                getString(R.string.find_by_location_desc)));
        list.add(new FindFriendAdapterClass(6,
                getString(R.string.suggest_friend),
                getString(R.string.suggest_friend_desc)));
        list.add(new FindFriendAdapterClass(7,
                getString(R.string.advanced_search),
                getString(R.string.advanced_search_desc)));

        FindFriendAdapter adapter = new FindFriendAdapter(getActivity(), R.layout.adapter_find_friend, list);
        ListView listViewFind = (ListView) view.findViewById(R.id.listView_find_method);
        listViewFind.setAdapter(adapter);
        listViewFind.setOnItemClickListener(listener);
        return view;
    }

    private ListView.OnItemClickListener listener = new ListView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            if (i == 4) {//find by location/GPS
                //check GPS status
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getContext());
                LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                if ((locationManager != null && !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) ||
                        (pref.getFloat(User.COL_LAST_LONGITUDE, -1) == -1 && pref.getFloat(User.COL_LAST_LATITUDE, -1) == -1)) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                    alertDialog.setTitle(R.string.gps_not_found);
                    alertDialog.setMessage(R.string.gps_not_found_desc2);
                    alertDialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));

                            Intent intent = new Intent(getActivity(), FindFriendResult.class);
                            intent.putExtra("Type", i);
                            startActivity(intent);
                        }
                    });
                    alertDialog.setNegativeButton(R.string.no, null);
                    alertDialog.create().show();
                } else {
                    Intent intent = new Intent(getActivity(), FindFriendResult.class);
                    intent.putExtra("Type", i);
                    startActivity(intent);
                }
            } else if (i == 6) {
                Intent intent = new Intent(getActivity(), AdvancedSearchActivity.class);
                intent.putExtra("Type", i);
                startActivity(intent);
            } else {
                Intent intent = new Intent(getActivity(), FindFriendResult.class);
                intent.putExtra("Type", i);
                startActivity(intent);
            }

        }
    };
}
