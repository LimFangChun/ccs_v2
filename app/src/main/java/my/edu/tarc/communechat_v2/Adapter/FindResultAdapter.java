package my.edu.tarc.communechat_v2.Adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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
    private ArrayList<Student> mObject;
    private SharedPreferences pref;

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
        RelativeLayout layoutFindResult;
    }

    public FindResultAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Student> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
        mObject = objects;
        pref = PreferenceManager.getDefaultSharedPreferences(getContext());
    }

    @SuppressLint("ViewHolder")
    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final Student student = new Student();
        student.setUser_id(Objects.requireNonNull(getItem(position)).getUser_id());
        student.setDisplay_name(Objects.requireNonNull(getItem(position)).getDisplay_name());
        student.setStatus(Objects.requireNonNull(getItem(position)).getStatus());
        student.setLast_online(Objects.requireNonNull(getItem(position)).getLast_online());
        student.setCourse(Objects.requireNonNull(getItem(position)).getCourse());
        student.setAcademic_year(Objects.requireNonNull(getItem(position)).getAcademic_year());
        student.setTutorial_group(Objects.requireNonNull(getItem(position)).getTutorial_group());
        student.setDistance(Objects.requireNonNull(getItem(position)).getDistance());

        final ViewHolder holder;
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
            holder.progressBarAddFriend.setVisibility(View.GONE);
            holder.layoutFindResult = convertView.findViewById(R.id.layout_findResult);
            convertView.setTag(holder);
            Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.list_item_load);
            animation.setStartOffset(position * 100);
            convertView.setAnimation(animation);
        }

        String status;
        if (student.getStatus().equals("Offline")) {
            status = "\uD83D\uDD34";
        } else {
            status = "\uD83D\uDD35";
        }

        holder.textViewUserID.setText(String.valueOf(student.getUser_id()));

        String distance = "";
        if (student.getDistance() != 0) {
            distance = " - " + Math.round(student.getDistance()) + "km away";
        }
        holder.textViewUsername.setText(Html.fromHtml(status + student.getDisplay_name() + distance));

        StringBuilder temp = new StringBuilder();
        holder.textViewDescription.setText(temp
                .append(student.getCourse())
                .append(student.getAcademic_year()).append(" ")
                .append("G").append(student.getTutorial_group())
                .append(" - ").append(student.calculateLastOnline())
                .toString());

        holder.buttonAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.progressBarAddFriend.setVisibility(View.VISIBLE);
                holder.layoutFindResult.setClickable(false);

                Friendship friendship = new Friendship();
                friendship.setUser_id(pref.getInt(User.COL_USER_ID, -1));
                friendship.setFriend_id(student.getUser_id());

                String topic = "requestAddFriend/" + friendship.getUser_id();
                String header = MqttHeader.REQ_ADD_FRIEND;
                MainActivity.mqttHelper.connectPublishSubscribe(getContext(), topic, header, friendship);
                MainActivity.mqttHelper.getMqttClient().setCallback(new MqttCallback() {
                    @Override
                    public void connectionLost(Throwable cause) {

                    }

                    @Override
                    public void messageArrived(String topic, MqttMessage message) throws Exception {
                        MainActivity.mqttHelper.decode(message.toString());
                        MainActivity.mqttHelper.unsubscribe(topic);

                        if (MainActivity.mqttHelper.getReceivedHeader().equals(MqttHeader.REQ_ADD_FRIEND_REPLY)) {
                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                            if (MainActivity.mqttHelper.getReceivedResult().equals(MqttHeader.SUCCESS)) {
                                alertDialog.setTitle("Success");
                                alertDialog.setMessage("Friend request has sent to " + student.getDisplay_name());
                                alertDialog.setNeutralButton(R.string.ok, null);

                                //update list view
                                //Update: added animation fade out
                                Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.fade_out);
                                animation.setAnimationListener(new Animation.AnimationListener() {
                                    @Override
                                    public void onAnimationStart(Animation animation) {

                                    }

                                    @Override
                                    public void onAnimationEnd(Animation animation) {
                                        mObject.remove(position);
                                        notifyDataSetChanged();
                                    }

                                    @Override
                                    public void onAnimationRepeat(Animation animation) {

                                    }
                                });
                                holder.layoutFindResult.setAnimation(animation);
                            } else {
                                alertDialog.setTitle("Failed");
                                alertDialog.setMessage("Could not send friend request to " + student.getDisplay_name());
                                alertDialog.setNeutralButton(R.string.ok, null);
                            }
                            alertDialog.show();
                        }

                        holder.layoutFindResult.setClickable(true);
                        holder.progressBarAddFriend.setVisibility(View.GONE);
                    }

                    @Override
                    public void deliveryComplete(IMqttDeliveryToken token) {

                    }
                });
            }
        });

        return convertView;
    }


}
