package my.edu.tarc.communechat_v2.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import my.edu.tarc.communechat_v2.Adapter.FriendListAdapter;
import my.edu.tarc.communechat_v2.FriendRequestActivity;
import my.edu.tarc.communechat_v2.MainActivity;
import my.edu.tarc.communechat_v2.R;
import my.edu.tarc.communechat_v2.internal.MqttHeader;
import my.edu.tarc.communechat_v2.model.Student;
import my.edu.tarc.communechat_v2.model.User;

public class FriendListFragment extends Fragment {

    ListView listViewFriendRequest;
    FloatingActionButton fabAddFriend;
    SharedPreferences pref;
    String uniqueTopic;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friend_list, container, false);

        pref = PreferenceManager.getDefaultSharedPreferences(getActivity());

        listViewFriendRequest = view.findViewById(R.id.listView_friendRequest);
        fabAddFriend = view.findViewById(R.id.fab_addFriend);

        User user = new User();
        user.setUser_id(pref.getInt(User.COL_USER_ID, -1));
        uniqueTopic = "friendlist/" + user.getUser_id();

        fabAddFriend.setOnClickListener(fabListener);

        MainActivity.mqttHelper.connectPublishSubscribe(getActivity(),
                uniqueTopic,
                MqttHeader.GET_FRIEND_LIST,
                user);
        MainActivity.mqttHelper.getMqttClient().setCallback(mqttCallback);
        return view;
    }

    private FloatingActionButton.OnClickListener fabListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            startActivity(new Intent(getActivity(), FriendRequestActivity.class));
        }
    };

    private MqttCallback mqttCallback = new MqttCallback() {
        @Override
        public void connectionLost(Throwable cause) {

        }

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
            MainActivity.mqttHelper.decode(message.toString());
            if (MainActivity.mqttHelper.getReceivedHeader().equals(MqttHeader.GET_FRIEND_LIST_REPLY)) {
                if (MainActivity.mqttHelper.getReceivedResult().equals(MqttHeader.NO_RESULT)) {
                    ArrayList<String> result = new ArrayList<>();
                    result.add("Seems like you don't have any friend yet");
                    ArrayAdapter adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, result);
                    listViewFriendRequest.setAdapter(adapter);
                } else {
                    try {
                        JSONArray result = new JSONArray(MainActivity.mqttHelper.getReceivedResult());
                        Student friend = new Student();
                        ArrayList<User> resultList = new ArrayList<>();
                        for (int i = 0; i < result.length() - 1; i++) {
                            JSONObject temp = result.getJSONObject(i);
                            friend.setUser_id(temp.getInt(Student.COL_USER_ID));
                            friend.setUsername(temp.getString(Student.COL_USERNAME));
                            friend.setStatus(temp.getString(Student.COL_STATUS));
                            friend.setLast_online(temp.getString(Student.COL_LAST_ONLINE));

                            resultList.add(friend);
                        }
                        FriendListAdapter adapter = new FriendListAdapter(getActivity(), R.layout.adapter_friend_list, resultList);
                        listViewFriendRequest.setAdapter(adapter);
                    } catch (JSONException | NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            }
            MainActivity.mqttHelper.unsubscribe(uniqueTopic);
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {

        }
    };
}
