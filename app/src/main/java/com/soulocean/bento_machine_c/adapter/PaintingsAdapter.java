package com.soulocean.bento_machine_c.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.soulocean.bento_machine_c.foldablelayoutapi.ContextHelper;
import com.soulocean.bento_machine_c.foldablelayoutapi.GlideHelper;
import com.soulocean.bento_machine_c.foldablelayoutapi.ItemsAdapter;
import com.soulocean.bento_machine_c.foldablelayoutapi.Views;
import com.soulocean.bento_machine_c.R;
import com.soulocean.bento_machine_c.activity.UnfoldableDetailsActivity;
import com.soulocean.bento_machine_c.entity.Painting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * @author soulo
 */
public class PaintingsAdapter extends ItemsAdapter<Painting, PaintingsAdapter.ViewHolder> {


    private OnItemClickLitener mOnItemClickLitener;
    private OnItemlongLitener mOnlongClickListener;
    private String ip, port;
    private List<Integer> lists = new ArrayList<>();


    public PaintingsAdapter(Context context) {
        setItemsList(Arrays.asList(Painting.getAllPaintings(context.getResources())));
    }

    public void setIpandPort(String ip, String port) {
        this.ip = ip;
        this.port = port;
    }

    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener) {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }


    public void setOnItemlongClickListener(OnItemlongLitener mOnlongClickListener) {
        this.mOnlongClickListener = mOnlongClickListener;
    }


    @Override
    protected ViewHolder onCreateHolder(ViewGroup parent, int viewType) {
        final ViewHolder holder = new ViewHolder(parent);
        return holder;
    }

    @Override
    protected void onBindHolder(final ViewHolder holder, final int position) {
        final Painting item = getItem(position);

        holder.image.setTag(R.id.list_item_image, item);
        GlideHelper.loadPaintingImage(holder.image, item);
        holder.title.setText(item.getTitle());


        for (int i = 0;i<4;i++)
        {
            lists.add(i,0);
        }

        holder.image.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickLitener.onItemClick(holder.itemView, position);
                if (holder.checkBox.isChecked()) {
                    holder.checkBox.setChecked(false);
                    lists.set(position,0);
                }else {
                    holder.checkBox.setChecked(true);
                    lists.set(position,1);
                }

            }

        });

        if (mOnlongClickListener != null) {
            holder.image.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mOnlongClickListener.onItemlongClick(holder.itemView, position);
                    final Painting item = (Painting) v.getTag(R.id.list_item_image);
                    final Activity activity = ContextHelper.asActivity(v.getContext());
                    if (activity instanceof UnfoldableDetailsActivity) {
                        ((UnfoldableDetailsActivity) activity).openDetails(v, item);
                    }
                    return true;
                }
            });
        }
    }

    public List<Integer> getLists() {
        return lists;
    }

    //新建点击事件接口
    public interface OnItemClickLitener {
        void onItemClick(View view, int position);
    }

    public interface OnItemlongLitener {
        void onItemlongClick(View view, int position);
    }


    static class ViewHolder extends ItemsAdapter.ViewHolder {
        final ImageView image;
        final TextView title;
        final CheckBox checkBox;


        ViewHolder(ViewGroup parent) {
            super(Views.inflate(parent, R.layout.list_item));
            checkBox = Views.find(itemView, R.id.list_item_checkbox);
            image = Views.find(itemView, R.id.list_item_image);
            title = Views.find(itemView, R.id.list_item_title);
        }
    }


}
