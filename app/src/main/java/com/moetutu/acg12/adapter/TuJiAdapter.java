package com.moetutu.acg12.adapter;

import android.net.Uri;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.moetutu.acg12.R;
import com.moetutu.acg12.entity.TestMode;
import com.moetutu.acg12.view.widget.BaseArrayRecyclerAdapter;

/**
 * Created by chengwanying on 16/5/27.
 */
public class TuJiAdapter extends BaseArrayRecyclerAdapter<TestMode.PostsBean> {


    @Override
    public void onBindHoder(ViewHolder holder, TestMode.PostsBean acg12Obj, int position) {
        if (acg12Obj == null) return;
            TextView xiaotouming = holder.obtainView(R.id.xiaotouming);
            xiaotouming.getBackground().setAlpha(60);
            xiaotouming.setText(acg12Obj.getPost_title());
            holder.setText(R.id.liulantext, acg12Obj.getPost_author().getName());
            SimpleDraweeView phone = holder.obtainView(R.id.phone);
            Uri uri = Uri.parse(acg12Obj.getThumbnail().getMedium());
            phone.setImageURI(uri);

    }

//    @Override
//    public void onViewRecycled(ViewHolder holder) {
//        super.onViewRecycled(holder);
//        if(holder!=null){
//            SimpleDraweeView phone = holder.obtainView(R.id.phone);
//            phone.shutDown();
//        }
//    }

    @Override
    public int bindView(int viewtype) {
        return R.layout.item_main;
    }
}
