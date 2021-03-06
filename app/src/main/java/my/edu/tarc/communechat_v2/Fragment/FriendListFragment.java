package my.edu.tarc.communechat_v2.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import my.edu.tarc.communechat_v2.Adapter.FriendListAdapter;
import my.edu.tarc.communechat_v2.FriendRequestActivity;
import my.edu.tarc.communechat_v2.ProfileActivity;
import my.edu.tarc.communechat_v2.R;
import my.edu.tarc.communechat_v2.internal.MqttHeader;
import my.edu.tarc.communechat_v2.internal.MqttHelper;
import my.edu.tarc.communechat_v2.model.Student;
import my.edu.tarc.communechat_v2.model.User;

import static my.edu.tarc.communechat_v2.MainActivity.mqttHelper;

public class FriendListFragment extends Fragment {

    private ListView listViewFriendList;
    private FloatingActionButton fabAddFriend;
    private ProgressBar progressBarFriendList;
    private SharedPreferences pref;
    private String uniqueTopic;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friend_list, container, false);
        pref = PreferenceManager.getDefaultSharedPreferences(getActivity());

        listViewFriendList = view.findViewById(R.id.listView_friendList);
        fabAddFriend = view.findViewById(R.id.fab_addFriend);
        fabAddFriend.setVisibility(View.VISIBLE);
        progressBarFriendList = view.findViewById(R.id.progressBar_FriendList);
        progressBarFriendList.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(getContext(), R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);


        if (savedInstanceState == null){
            User user = new User();
            user.setUser_id(pref.getInt(User.COL_USER_ID, -1));
            uniqueTopic = "friendList/" + user.getUser_id();

            fabAddFriend.setOnClickListener(fabListener);
            listViewFriendList.setOnItemClickListener(listViewListener);
            progressBarFriendList.setVisibility(View.VISIBLE);

            mqttHelper.connectPublishSubscribe(getActivity(),
                    uniqueTopic,
                    MqttHeader.GET_FRIEND_LIST,
                    user);
            mqttHelper.getMqttClient().setCallback(mqttCallback);
        }
        return view;
    }

    private FloatingActionButton.OnClickListener fabListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            startActivity(new Intent(getActivity(), FriendRequestActivity.class));
        }
    };

    private ListView.OnItemClickListener listViewListener = new ListView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            TextView textViewUserID = (TextView) view.findViewById(R.id.textView_userID);
            int userID = Integer.parseInt(textViewUserID.getText().toString());

            Intent intent = new Intent(getActivity(), ProfileActivity.class);
            intent.putExtra(User.COL_USER_ID, userID);
            startActivity(intent);
        }
    };

    private MqttCallback mqttCallback = new MqttCallback() {
        @Override
        public void connectionLost(Throwable cause) {

        }

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
            MqttHelper helper = new MqttHelper();
            helper.decode(message.toString());
            if (helper.getReceivedHeader().equals(MqttHeader.GET_FRIEND_LIST_REPLY)) {
                if (helper.getReceivedResult().equals(MqttHeader.NO_RESULT)) {
                    ArrayList<String> result = new ArrayList<>();
                    result.add("Seems like you don't have any friend yet");
                    ArrayAdapter adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, result);
                    listViewFriendList.setAdapter(adapter);
                } else {
                    try {
                        JSONArray result = new JSONArray(helper.getReceivedResult());

                        ArrayList<Student> resultList = new ArrayList<>();
                        for (int i = 0; i <= result.length() - 1; i++) {
                            Student friend = new Student();
                            JSONObject temp = result.getJSONObject(i);
                            friend.setUser_id(temp.getInt(User.COL_USER_ID));
                            friend.setDisplay_name(temp.getString(Student.COL_DISPLAY_NAME));
                            friend.setStatus(temp.getString(Student.COL_STATUS));
                            friend.setLast_online(temp.getString(Student.COL_LAST_ONLINE));
                            friend.setCourse(temp.getString(Student.COL_COURSE));
                            friend.setAcademic_year(temp.getInt(Student.COL_ACADEMIC_YEAR));
                            friend.setTutorial_group(temp.getInt(Student.COL_TUTORIAL_GROUP));

                            resultList.add(friend);
                        }
                        FriendListAdapter adapter = new FriendListAdapter(getActivity(), R.layout.adapter_friend_list, resultList);
                        listViewFriendList.setAdapter(adapter);
                    } catch (JSONException | NullPointerException e) {
                        e.printStackTrace();
                    }
                }
                progressBarFriendList.setVisibility(View.INVISIBLE);
                mqttHelper.unsubscribe(uniqueTopic);
            }
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {

        }
    };
}
