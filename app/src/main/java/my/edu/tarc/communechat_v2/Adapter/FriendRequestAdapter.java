package my.edu.tarc.communechat_v2.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.ArrayList;
import java.util.Objects;

import my.edu.tarc.communechat_v2.MainActivity;
import my.edu.tarc.communechat_v2.ProfileActivity;
import my.edu.tarc.communechat_v2.R;
import my.edu.tarc.communechat_v2.internal.MqttHeader;
import my.edu.tarc.communechat_v2.model.Friendship;
import my.edu.tarc.communechat_v2.model.Student;
import my.edu.tarc.communechat_v2.model.User;

public class FriendRequestAdapter extends ArrayAdapter<Student> {
    private Context mContext;
    private int mResource;
    private ArrayList<Student> mObject;
    private SharedPreferences pref;

    static class ViewHolder {
        ImageView imageViewProfilePic;
        TextView textViewUserID;
        TextView textViewUsername;
        TextView textViewDescription;
        Button buttonAccept;
        Button buttonDetail;
        ImageButton buttonCancel;
        ProgressBar progressBarLoading;
        RelativeLayout layoutMain;
    }

    public FriendRequestAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Student> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
        mObject = objects;
        pref = PreferenceManager.getDefaultSharedPreferences(getContext());
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final ViewHolder holder;
        final LayoutInflater inflater = LayoutInflater.from(mContext);
        if (convertView == null) {
            convertView = inflater.inflate(mResource, parent, false);

            holder = new ViewHolder();
            holder.imageViewProfilePic = convertView.findViewById(R.id.imageView_profilePic);
            holder.textViewUserID = convertView.findViewById(R.id.textView_userID);
            holder.textViewDescription = convertView.findViewById(R.id.textView_description);
            holder.textViewUsername = convertView.findViewById(R.id.textView_username);
            holder.buttonAccept = convertView.findViewById(R.id.button_accept);
            holder.buttonDetail = convertView.findViewById(R.id.button_detail);
            holder.buttonCancel = convertView.findViewById(R.id.button_cancel);
            holder.progressBarLoading = convertView.findViewById(R.id.progressBar_loading);
            holder.progressBarLoading.setVisibility(View.GONE);
            holder.layoutMain = convertView.findViewById(R.id.layout_main);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final Student user = new Student();
        user.setUser_id(Objects.requireNonNull(getItem(position)).getUser_id());
        user.setDisplay_name(Objects.requireNonNull(getItem(position)).getDisplay_name());
        user.setLast_online(Objects.requireNonNull(getItem(position)).getLast_online());
        user.setStatus(Objects.requireNonNull(getItem(position)).getStatus());
        user.setCourse(Objects.requireNonNull(getItem(position)).getCourse());
        user.setAcademic_year(Objects.requireNonNull(getItem(position)).getAcademic_year());
        user.setTutorial_group(Objects.requireNonNull(getItem(position)).getTutorial_group());

        String status;
        if (user.getStatus().equals("Offline")) {
            status = "\uD83D\uDD34";//red circle indicate offline
        } else {
            status = "\uD83D\uDD35";//blue circle indicate online
        }

        //github: https://github.com/amulyakhare/TextDrawable/blob/master/README.md
        ColorGenerator colorGenerator = ColorGenerator.MATERIAL;
        int color = colorGenerator.getColor(user.getDisplay_name());
        TextDrawable drawable = TextDrawable.builder().buildRound(user.getDisplay_name().substring(0, 1), color);
        holder.imageViewProfilePic.setImageDrawable(drawable);

        holder.textViewUserID.setText(String.valueOf(user.getUser_id()));
        holder.textViewUsername.setText(Html.fromHtml(String.valueOf(status + user.getDisplay_name())));

        StringBuilder sb = new StringBuilder();
        holder.textViewDescription.setText(sb.append(user.getCourse())
                .append(user.getAcademic_year()).append(" ")
                .append("G").append(user.getTutorial_group())
                .append(" - ").append(user.calculateLastOnline()));

        holder.buttonAccept.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.progressBarLoading.setVisibility(View.VISIBLE);
                holder.layoutMain.setClickable(false);

                Friendship friendship = new Friendship();
                friendship.setUser_id(pref.getInt(User.COL_USER_ID, -1));
                friendship.setFriend_id(user.getUser_id());

                String topic = "acceptFriend/" + friendship.getUser_id();
                String header = MqttHeader.ADD_FRIEND;
                MainActivity.mqttHelper.connectPublishSubscribe(getContext(), topic, header, friendship);
                MainActivity.mqttHelper.getMqttClient().setCallback(new MqttCallback() {
                    @Override
                    public void connectionLost(Throwable cause) {

                    }

                    @Override
                    public void messageArrived(String topic, MqttMessage message) throws Exception {
                        MainActivity.mqttHelper.decode(message.toString());
                        MainActivity.mqttHelper.unsubscribe(topic);

                        if (MainActivity.mqttHelper.getReceivedHeader().equals(MqttHeader.ADD_FRIEND_REPLY)) {
                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                            if (MainActivity.mqttHelper.getReceivedResult().equals(MqttHeader.SUCCESS)) {
                                alertDialog.setTitle("Success");
                                alertDialog.setMessage("Added " + user.getDisplay_name() + " as friend");
                                alertDialog.setNeutralButton(R.string.ok, null);

                                //update list view
                                //with some animation
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
                                holder.layoutMain.setAnimation(animation);
                            } else {
                                alertDialog.setTitle("Failed");
                                alertDialog.setMessage("Could not add " + user.getDisplay_name() + " as friend");
                                alertDialog.setNeutralButton(R.string.ok, null);
                            }
                            alertDialog.show();
                        }

                        holder.progressBarLoading.setVisibility(View.GONE);
                        holder.layoutMain.setClickable(true);
                    }

                    @Override
                    public void deliveryComplete(IMqttDeliveryToken token) {

                    }
                });
            }
        });

        holder.buttonDetail.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getContext(), "friend_id2: " + holder.textViewUserID.getText().toString(), Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getContext(), ProfileActivity.class);
                intent.putExtra(User.COL_USER_ID, Integer.parseInt(holder.textViewUserID.getText().toString()));
                getContext().startActivity(intent);
            }
        });

        holder.buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //set progress bar to visible
                holder.progressBarLoading.setVisibility(View.VISIBLE);

                //disable user interaction temporarily
                holder.layoutMain.setClickable(false);

                //prepare an object to encapsulate required data
                Friendship friendship = new Friendship();
                friendship.setUser_id(pref.getInt(User.COL_USER_ID, -1));
                friendship.setFriend_id(Integer.parseInt(holder.textViewUserID.getText().toString()));

                //prepare required header, topic and message to send to MQTT broker
                String topic = "deleteFriendRequest/" + friendship.getUser_id();
                String header = MqttHeader.DELETE_FRIEND;

                //commence connection, publish and subscribe data to MQTT broker
                MainActivity.mqttHelper.connectPublishSubscribe(getContext(), topic, header, friendship);
                MainActivity.mqttHelper.getMqttClient().setCallback(new MqttCallback() {
                    @Override
                    public void connectionLost(Throwable cause) {

                    }

                    //when message arrived to client
                    @Override
                    public void messageArrived(String topic, MqttMessage message) throws Exception {
                        //decode the received message first into JSON object array
                        MainActivity.mqttHelper.decode(message.toString());

                        //unsubscribe from the topic
                        MainActivity.mqttHelper.unsubscribe(topic);

                        //alert dialog to display result
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());

                        //check if the received header is what we want
                        if (MainActivity.mqttHelper.getReceivedHeader().equals(MqttHeader.DELETE_FRIEND_REPLY)) {

                            //if received result is success
                            //the friend request has been deleted
                            if (MainActivity.mqttHelper.getReceivedResult().equals(MqttHeader.SUCCESS)) {
                                alertDialog.setTitle("Success");
                                alertDialog.setMessage("Friend request from " + user.getDisplay_name() + " has been cancelled");
                                alertDialog.setNeutralButton(R.string.ok, null);

                                //if success, then remove the selected item from list view
                                //Update: added animation effect that slide from left to out
                                Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_left_to_right);
                                animation.setAnimationListener(new Animation.AnimationListener() {
                                    @Override
                                    public void onAnimationStart(Animation animation) {

                                    }

                                    @Override
                                    public void onAnimationEnd(Animation animation) {
                                        //remove the item upon animation finished
                                        mObject.remove(position);
                                        notifyDataSetChanged();
                                    }

                                    @Override
                                    public void onAnimationRepeat(Animation animation) {

                                    }
                                });
                                holder.layoutMain.setAnimation(animation);
                            } else {
                                alertDialog.setTitle(R.string.failed);
                                alertDialog.setMessage("Failed to cancel friend request from " + user.getDisplay_name());
                                alertDialog.setNeutralButton(R.string.ok, null);
                            }
                            alertDialog.show();

                            //remove progress bar
                            holder.progressBarLoading.setVisibility(View.GONE);

                            //enable user interaction again
                            holder.layoutMain.setClickable(true);
                        }
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
