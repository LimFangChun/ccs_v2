package my.edu.tarc.communechat_v2.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Objects;

import my.edu.tarc.communechat_v2.R;
import my.edu.tarc.communechat_v2.model.Student;

public class FriendRequestAdapter extends ArrayAdapter<Student> {
    private Context mContext;
    private int mResource;

    static class ViewHolder {
        ImageView imageViewProfilePic;
        TextView textViewUserID;
        TextView textViewUsername;
        TextView textViewDescription;
        Button buttonAdd;
        Button buttonDetail;
    }

    public FriendRequestAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Student> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        if(convertView == null){
            convertView = inflater.inflate(mResource, parent, false);

            holder = new ViewHolder();
            holder.imageViewProfilePic = convertView.findViewById(R.id.imageView_profilePic);
            holder.textViewUserID = convertView.findViewById(R.id.textView_userID);
            holder.textViewDescription = convertView.findViewById(R.id.textView_description);
            holder.textViewUsername = convertView.findViewById(R.id.textView_username);
            holder.buttonAdd = convertView.findViewById(R.id.button_add);
            holder.buttonDetail = convertView.findViewById(R.id.button_detail);

            convertView.setTag(holder);
        }else {
            holder = (ViewHolder)convertView.getTag();
        }

        Student user = new Student();
        user.setUser_id(Objects.requireNonNull(getItem(position)).getUser_id());
        user.setDisplay_name(Objects.requireNonNull(getItem(position)).getDisplay_name());
        user.setLast_online(Objects.requireNonNull(getItem(position)).getLast_online());
        user.setStatus(Objects.requireNonNull(getItem(position)).getStatus());
        user.setCourse(Objects.requireNonNull(getItem(position)).getCourse());
        user.setAcademic_year(Objects.requireNonNull(getItem(position)).getAcademic_year());
        user.setTutorial_group(Objects.requireNonNull(getItem(position)).getTutorial_group());

        holder.textViewUserID.setText(String.valueOf(user.getUser_id()));
        holder.textViewUsername.setText(user.getDisplay_name());

        StringBuilder sb = new StringBuilder();
        holder.textViewDescription.setText(sb.append(user.getCourse())
                .append(user.getAcademic_year()).append(" ")
                .append("G").append(user.getTutorial_group())
                .append(" - ").append(user.calculateLastOnline()));

        return convertView;
    }
}
