package sf.orderfoodclient.helper;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import sf.orderfoodclient.R;

/**
 * Created by mesutgenc on 26.01.2018.
 */

public class CommentViewHolder extends RecyclerView.ViewHolder {
    public TextView txtUserPhone, txtComment;
    public RatingBar ratingBar;


    public CommentViewHolder(View itemView) {
        super(itemView);

        txtUserPhone = (TextView) itemView.findViewById(R.id.txtUserPhone);
        txtComment = (TextView) itemView.findViewById(R.id.txtComment);
        ratingBar = (RatingBar) itemView.findViewById(R.id.ratingBar);
    }

}
