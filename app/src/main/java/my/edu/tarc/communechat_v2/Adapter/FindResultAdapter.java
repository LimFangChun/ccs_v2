package my.edu.tarc.communechat_v2.Adapter;

import android.annotation.SuppressLint;
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
import my.edu.tarc.communechat_v2.MapsActivity;
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
        ImageButton buttonDirection;
        ProgressBar progressBarAddFriend;
        RelativeLayout layoutFindResult;
        ImageView imageViewProfilePicture;
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
        //initialize student object as final variable
        final Student student = new Student();
        student.setUser_id(Objects.requireNonNull(getItem(position)).getUser_id());
        student.setDisplay_name(Objects.requireNonNull(getItem(position)).getDisplay_name());
        student.setStatus(Objects.requireNonNull(getItem(position)).getStatus());
        student.setLast_online(Objects.requireNonNull(getItem(position)).getLast_online());
        student.setCourse(Objects.requireNonNull(getItem(position)).getCourse());
        student.setAcademic_year(Objects.requireNonNull(getItem(position)).getAcademic_year());
        student.setTutorial_group(Objects.requireNonNull(getItem(position)).getTutorial_group());
        student.setDistance(Objects.requireNonNull(getItem(position)).getDistance());
        student.setLast_longitude(Objects.requireNonNull(getItem(position)).getLast_longitude());
        student.setLast_latitude(Objects.requireNonNull(getItem(position)).getLast_latitude());

        final ViewHolder holder;
        if (convertView != null) {
            //if the view holder's item has been initialized before
            //get from tag, reuse the holder
            holder = (ViewHolder) convertView.getTag();
        } else {
            //else, initialize everything
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
            holder = new ViewHolder();

            //link to UI objects
            holder.textViewUserID = convertView.findViewById(R.id.textView_userID);
            holder.textViewUsername = convertView.findViewById(R.id.textView_username);
            holder.textViewDescription = convertView.findViewById(R.id.textView_description);
            holder.buttonAddFriend = convertView.findViewById(R.id.button_addFriend);
            holder.buttonDirection = convertView.findViewById(R.id.button_direction);
            holder.progressBarAddFriend = convertView.findViewById(R.id.progressBar_addFriend);
            holder.progressBarAddFriend.setVisibility(View.GONE);
            holder.layoutFindResult = convertView.findViewById(R.id.layout_findResult);
            holder.imageViewProfilePicture = convertView.findViewById(R.id.imageView_profilePic);
            convertView.setTag(holder);

            //a simple animation to load the items in list view
            Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.list_item_load);
            animation.setStartOffset(position * 100);
            convertView.setAnimation(animation);
        }

        //transform user status to unicode
        //Note: if in future the unicode for green button released
        //      change the online unicode to green button
        //      because it is more suitable
        String status;
        if (student.getStatus().equals("Offline")) {
            //red button
            status = "\uD83D\uDD34";
        } else {
            //blue button
            status = "\uD83D\uDD35";
        }

        //github: https://github.com/amulyakhare/TextDrawable/blob/master/README.md
        ColorGenerator colorGenerator = ColorGenerator.MATERIAL;
        TextDrawable drawable = TextDrawable.builder().buildRound(student.getDisplay_name().substring(0, 1), colorGenerator.getRandomColor());
        holder.imageViewProfilePicture.setImageDrawable(drawable);

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
                //temporarily disable the selected item, prevent user click it again and again
                holder.progressBarAddFriend.setVisibility(View.VISIBLE);
                holder.layoutFindResult.setClickable(false);

                //create an object to hold the required data and pass to MqttHelper class
                Friendship friendship = new Friendship();
                friendship.setUser_id(pref.getInt(User.COL_USER_ID, -1));
                friendship.setFriend_id(student.getUser_id());

                //give the topic a readable name
                String topic = "requestAddFriend/" + friendship.getUser_id();

                //header for encoding
                //see MqttHeader and MqttHelper.encode()
                String header = MqttHeader.REQ_ADD_FRIEND;

                //method that establish connection with Mqtt broker
                //shortcut: ctrl + click any method you want to see
                MainActivity.mqttHelper.connectPublishSubscribe(getContext(), topic, header, friendship);

                //setup a callback to receive message from broker
                MainActivity.mqttHelper.getMqttClient().setCallback(new MqttCallback() {
                    @Override
                    public void connectionLost(Throwable cause) {

                    }

                    @Override
                    public void messageArrived(String topic, MqttMessage message) throws Exception {
                        //on message arrived, decode the message
                        //Note: message always come with a fixed format
                        //message starts with a header, follow by either a JSON object array or a constant string
                        //see MqttHeader.SUCCESS, MqttHeader.NO_RESULT
                        MainActivity.mqttHelper.decode(message.toString());

                        //unsub from the topic, so we stop getting unnecessary message from broker
                        MainActivity.mqttHelper.unsubscribe(topic);

                        //check the header, whether the message is what we want
                        if (MainActivity.mqttHelper.getReceivedHeader().equals(MqttHeader.REQ_ADD_FRIEND_REPLY)) {
                            //initialize a alert dialog for user feedback
                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());

                            //check result, whether the add friend action was success
                            if (MainActivity.mqttHelper.getReceivedResult().equals(MqttHeader.SUCCESS)) {
                                //put proper user feedback message to dialog box
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

                                    //remove the item when animation has finished
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
                                //result is failed
                                alertDialog.setTitle("Failed");
                                alertDialog.setMessage("Could not send friend request to " + student.getDisplay_name());
                                alertDialog.setNeutralButton(R.string.ok, null);
                            }
                            alertDialog.show();
                        }

                        //allow user to interact with the item again
                        holder.layoutFindResult.setClickable(true);
                        holder.progressBarAddFriend.setVisibility(View.GONE);
                    }

                    @Override
                    public void deliveryComplete(IMqttDeliveryToken token) {

                    }
                });
            }
        });

        holder.buttonDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO check gps status
                //TODO too lazy to do it, junior please do it LUL
                Intent intent = new Intent(getContext(), MapsActivity.class);
                intent.putExtra(User.COL_LAST_LONGITUDE, student.getLast_longitude());
                intent.putExtra(User.COL_LAST_LATITUDE, student.getLast_latitude());
                intent.putExtra(User.COL_DISPLAY_NAME, student.getDisplay_name());

                getContext().startActivity(intent);
            }
        });

        return convertView;
    }


}
