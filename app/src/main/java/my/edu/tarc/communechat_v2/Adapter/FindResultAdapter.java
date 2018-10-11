package my.edu.tarc.communechat_v2.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Objects;

import my.edu.tarc.communechat_v2.R;
import my.edu.tarc.communechat_v2.model.Student;

public class FindResultAdapter extends ArrayAdapter<Student> {
    private Context mContext;
    private int mResource;

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
        Student student = new Student();
        student.setUser_id(Objects.requireNonNull(getItem(position)).getUser_id());
        student.setUsername(Objects.requireNonNull(getItem(position)).getUsername());
        student.setStatus(Objects.requireNonNull(getItem(position)).getStatus());
        student.setLast_online(Objects.requireNonNull(getItem(position)).getLast_online());
        student.setCourse(Objects.requireNonNull(getItem(position)).getCourse());
        student.setAcademic_year(Objects.requireNonNull(getItem(position)).getAcademic_year());
        student.setTutorial_group(Objects.requireNonNull(getItem(position)).getTutorial_group());

        ViewHolder holder;

        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
            holder = new ViewHolder();

            holder.textViewUserID = convertView.findViewById(R.id.textView_userID);
            holder.textViewUsername = convertView.findViewById(R.id.textView_username);
            holder.textViewDescription = convertView.findViewById(R.id.textView_description);
            convertView.setTag(holder);
        }


        long lastOnlineAgo = student.getLast_online().getTime() - System.currentTimeMillis();
        //lastOnlineAgo would result in millisecond
        //convert to month first
        //if less than 1 month
        //display days instead
        StringBuilder temp2 = new StringBuilder();
        temp2.append("  ");
        if (lastOnlineAgo / 1000 / 60 / 60 / 24 / 30 != 0) {
            temp2.append(lastOnlineAgo / 1000 / 60 / 60 / 24 / 30).append(" month(s) ago");
        } else if (lastOnlineAgo / 1000 / 60 / 60 / 24 != 0) {
            temp2.append(lastOnlineAgo / 1000 / 60 / 60 / 24).append(" day(s) ago");
        } else if (lastOnlineAgo / 1000 / 60 / 60 != 0) {
            temp2.append(lastOnlineAgo / 1000 / 60 / 60 / 24).append(" hour(s) ago");
        } else if (lastOnlineAgo / 1000 / 60 / 60 / 24 != 0) {
            temp2.append(lastOnlineAgo / 1000 / 60).append(" minute(s) ago");
        } else {
            temp2.append(lastOnlineAgo / 1000).append(" second(s) ago");
        }
        holder.textViewUserID.setText(String.valueOf(student.getUser_id()));
        holder.textViewUsername.setText(student.getUsername());

        StringBuilder temp = new StringBuilder();
        holder.textViewDescription.setText(temp.append(" ")
                .append(student.getCourse())
                .append(student.getAcademic_year()).append(" ")
                .append("G").append(student.getTutorial_group())
                .append(" - ").append(temp2)
                .toString());

        return convertView;
    }
}
