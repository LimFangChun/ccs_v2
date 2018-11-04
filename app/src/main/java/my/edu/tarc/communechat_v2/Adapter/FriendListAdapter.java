package my.edu.tarc.communechat_v2.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

import java.util.ArrayList;
import java.util.Objects;

import my.edu.tarc.communechat_v2.R;
import my.edu.tarc.communechat_v2.model.Student;

public class FriendListAdapter extends ArrayAdapter<Student> {
    private Context mContext;
    private int mResource;

    static class ViewHolder {
        ImageView imageViewProfilePic;
        TextView textViewUserID;
        TextView textViewUsername;
        TextView textViewDescription;
    }

    public FriendListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Student> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        if (convertView == null) {
            convertView = inflater.inflate(mResource, parent, false);

            holder = new ViewHolder();
            holder.imageViewProfilePic = convertView.findViewById(R.id.imageView_profilePic);
            holder.textViewUserID = convertView.findViewById(R.id.textView_userID);
            holder.textViewDescription = convertView.findViewById(R.id.textView_description);
            holder.textViewUsername = convertView.findViewById(R.id.textView_username);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Student user = new Student();
        user.setUser_id(Objects.requireNonNull(getItem(position)).getUser_id());
        user.setDisplay_name(Objects.requireNonNull(getItem(position)).getDisplay_name());
        user.setLast_online(Objects.requireNonNull(getItem(position)).getLast_online());
        user.setStatus(Objects.requireNonNull(getItem(position)).getStatus());
        user.setCourse(Objects.requireNonNull(getItem(position)).getCourse());
        user.setAcademic_year(Objects.requireNonNull(getItem(position)).getAcademic_year());
        user.setTutorial_group(Objects.requireNonNull(getItem(position)).getTutorial_group());

        String status;
        if (user.getStatus().equals("Offline")) {
            status = "\uD83D\uDD34";
        } else {
            status = "\uD83D\uDD35";
        }

        //github: https://github.com/amulyakhare/TextDrawable/blob/master/README.md
        ColorGenerator colorGenerator = ColorGenerator.MATERIAL;
        int color = colorGenerator.getColor(user.getDisplay_name());
        TextDrawable drawable = TextDrawable.builder().buildRound(user.getDisplay_name().substring(0, 1), color);
        holder.imageViewProfilePic.setImageDrawable(drawable);

        holder.textViewUserID.setText(String.valueOf(user.getUser_id()));
        holder.textViewUsername.setText(Html.fromHtml(status + user.getDisplay_name()));

        StringBuilder temp = new StringBuilder();
        holder.textViewDescription.setText(temp
                .append(user.getCourse()).append(user.getAcademic_year())
                .append(" ").append("G").append(user.getTutorial_group())
                .append(" - ").append(user.calculateLastOnline()));

        return convertView;
    }
}
