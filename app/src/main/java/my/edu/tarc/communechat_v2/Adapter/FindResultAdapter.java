package my.edu.tarc.communechat_v2.Adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.ArrayList;
import java.util.Objects;

import my.edu.tarc.communechat_v2.MainActivity;
import my.edu.tarc.communechat_v2.R;
import my.edu.tarc.communechat_v2.internal.MqttHeader;
import my.edu.tarc.communechat_v2.model.Friendship;
import my.edu.tarc.communechat_v2.model.Student;
import my.edu.tarc.communechat_v2.model.User;

public class FindResultAdapter extends ArrayAdapter<Student> {
    private Context mContext;
    private int mResource;
    private Student student = new Student();
    private ViewHolder holder;

    //holder class for each row
    //Google search "Android Best practice to handle a custom listview adapter"
    //or "Mitch Tabian Android tutorial #9"
    //do in this way will allow the app to load only what user see
    //rather than load all data into list view when the activity is created
    //that would cause lag issues
    //you can use recycle view but that only available API 21 or later
    //our target is target API 19 which recycle view is not available
    static class ViewHolder {
        TextView textViewUserID;
        TextView textViewUsername;
        TextView textViewDescription;
        ImageButton buttonAddFriend;
        ProgressBar progressBarAddFriend;
    }

    public FindResultAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Student> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @SuppressLint("ViewHolder")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        student.setUser_id(Objects.requireNonNull(getItem(position)).getUser_id());
        student.setDisplay_name(Objects.requireNonNull(getItem(position)).getDisplay_name());
        student.setStatus(Objects.requireNonNull(getItem(position)).getStatus());
        student.setLast_online(Objects.requireNonNull(getItem(position)).getLast_online());
        student.setCourse(Objects.requireNonNull(getItem(position)).getCourse());
        student.setAcademic_year(Objects.requireNonNull(getItem(position)).getAcademic_year());
        student.setTutorial_group(Objects.requireNonNull(getItem(position)).getTutorial_group());

        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
            holder = new ViewHolder();

            holder.textViewUserID = convertView.findViewById(R.id.textView_userID);
            holder.textViewUsername = convertView.findViewById(R.id.textView_username);
            holder.textViewDescription = convertView.findViewById(R.id.textView_description);
            holder.buttonAddFriend = convertView.findViewById(R.id.button_addFriend);
            holder.progressBarAddFriend = convertView.findViewById(R.id.progressBar_addFriend);
            convertView.setTag(holder);
        }

        holder.textViewUserID.setText(String.valueOf(student.getUser_id()));
        holder.textViewUsername.setText(student.getDisplay_name());

        StringBuilder temp = new StringBuilder();
        holder.textViewDescription.setText(temp
                .append(student.getCourse())
                .append(student.getAcademic_year()).append(" ")
                .append("G").append(student.getTutorial_group())
                .append(" - ").append(student.calculateLastOnline())
                .toString());

        holder.buttonAddFriend.setOnClickListener(buttonListener);

        return convertView;
    }

    private Button.OnClickListener buttonListener = new Button.OnClickListener() {
        @Override
        public void onClick(View view) {
            holder.progressBarAddFriend.setVisibility(View.VISIBLE);
            Friendship friendship = new Friendship();
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getContext());
            friendship.setUser_id(pref.getInt(User.COL_USER_ID, -1));
            friendship.setFriend_id(student.getUser_id());

            String topic = "addFriend/" + friendship.getUser_id();
            String header = MqttHeader.ADD_FRIEND;
            MainActivity.mqttHelper.connectPublishSubscribe(getContext(), topic, header, friendship);
            MainActivity.mqttHelper.getMqttClient().setCallback(mqttCallback);
        }
    };

    private MqttCallback mqttCallback = new MqttCallback() {
        @Override
        public void connectionLost(Throwable cause) {

        }

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
            holder.progressBarAddFriend.setVisibility(View.GONE);
            MainActivity.mqttHelper.decode(message.toString());
            if (MainActivity.mqttHelper.getReceivedHeader().equals(MqttHeader.ADD_FRIEND_REPLY)) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                if (!MainActivity.mqttHelper.getReceivedResult().equals(MqttHeader.NO_RESULT)) {
                    alertDialog.setTitle("Success");
                    alertDialog.setMessage("Added " + student.getDisplay_name() + " as friend");
                    alertDialog.setNeutralButton(R.string.ok, null);
                } else {
                    alertDialog.setTitle("Failed");
                    alertDialog.setMessage("Failed to add " + student.getDisplay_name() + " as friend");
                    alertDialog.setNeutralButton(R.string.ok, null);
                }
                alertDialog.show();
            }
            MainActivity.mqttHelper.unsubscribe(topic);
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {

        }
    };
}
