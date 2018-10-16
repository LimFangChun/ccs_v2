package my.edu.tarc.communechat_v2.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Objects;

import my.edu.tarc.communechat_v2.R;
import my.edu.tarc.communechat_v2.model.Student;

public class FindResultAdapter extends ArrayAdapter<Student> {
    private Context mContext;
    private int mResource;
    private Student student;
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
        student = new Student();
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

        String status;
        if (student.getStatus().equals("Offline")) {
            status = "\uD83D\uDD34";
        } else {
            status = "✅";
        }

        holder.textViewUserID.setText(String.valueOf(student.getUser_id()));
        holder.textViewUsername.setText(Html.fromHtml(status + student.getDisplay_name()));

        StringBuilder temp = new StringBuilder();
        holder.textViewDescription.setText(temp
                .append(student.getCourse())
                .append(student.getAcademic_year()).append(" ")
                .append("G").append(student.getTutorial_group())
                .append(" - ").append(student.calculateLastOnline())
                .toString());

        return convertView;
    }


}
