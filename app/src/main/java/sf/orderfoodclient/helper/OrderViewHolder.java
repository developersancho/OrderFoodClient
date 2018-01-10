package sf.orderfoodclient.helper;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import sf.orderfoodclient.R;

/**
 * Created by mesutgenc on 10.01.2018.
 */

public class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView order_id, order_status, order_phone, order_address;

    ItemClickListener itemClickListener;

    public OrderViewHolder(View itemView) {
        super(itemView);

        order_id = (TextView) itemView.findViewById(R.id.order_id);
        order_status = (TextView) itemView.findViewById(R.id.order_status);
        order_phone = (TextView) itemView.findViewById(R.id.order_phone);
        order_address = (TextView) itemView.findViewById(R.id.order_address);

        itemView.setOnClickListener(this);

    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v, getAdapterPosition(), false);
    }

}
