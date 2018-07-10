package com.ruanchao.demo.videorecord;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.ruanchao.demo.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ruanchao on 2018/5/14.
 */

public class VideoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<VideoInfo> mVideoInfos = new ArrayList<>();
    private SimpleDateFormat format = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );

    public VideoAdapter(Context context){
        mContext  = context;
    }

    public void addVideoInfos(List<VideoInfo> VideoInfos){
        mVideoInfos.clear();
        mVideoInfos.addAll(VideoInfos);
    }

    public void addVideoInfoFront(VideoInfo videoInfo){
        mVideoInfos.add(0,videoInfo);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.live_video_item_layout, null, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        itemViewHolder.mVideoTitle.setText(mVideoInfos.get(position).getVideoTitle());
        itemViewHolder.mVideoTime.setText(format.format(mVideoInfos.get(position).getVideoTime()));
        Glide.with( mContext )
                .load( Uri.fromFile( new File( mVideoInfos.get(position).getVideoPath()) ) )
                .placeholder(R.mipmap.video_item_default_image)
                .centerCrop()
                //.override(DensityUtil.dip2px(mContext,50), DensityUtil.dip2px(mContext,50))
                .into(itemViewHolder.mVideoIcon);
        itemViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null){
                    mOnItemClickListener.OnItemClick(mVideoInfos.get(position));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mVideoInfos.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder{

        public TextView mVideoTitle;
        public TextView mVideoTime;
        public ImageView mVideoIcon;
        public View itemView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            mVideoTitle = (TextView) itemView.findViewById(R.id.tv_live_video_title);
            mVideoIcon = (ImageView) itemView.findViewById(R.id.iv_live_video_icon);
            mVideoTime = (TextView) itemView.findViewById(R.id.tv_live_video_time);
            this.itemView = itemView;
        }
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        mOnItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {

        void OnItemClick(VideoInfo VideoInfo);
    }
}
