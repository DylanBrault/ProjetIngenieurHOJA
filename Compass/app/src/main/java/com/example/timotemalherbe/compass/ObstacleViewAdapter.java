package com.example.timotemalherbe.compass;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by timotemalherbe on 21/04/2018.
 */

public class ObstacleViewAdapter extends ArrayAdapter<Obstacle> {

    public ObstacleViewAdapter(Context context, List<Obstacle> obstacles) {
        super(context, 0, obstacles);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_obstacle,parent, false);
        }

        ObstacleViewHolder viewHolder = (ObstacleViewHolder) convertView.getTag();
        if(viewHolder == null){
            viewHolder = new ObstacleViewHolder();
            viewHolder.type_obstacle = (TextView) convertView.findViewById(R.id.type_obstacle);
            viewHolder.distance_obstacle = (TextView) convertView.findViewById(R.id.distance_inter_obstacle);
            viewHolder.image_obstacle = (ImageView) convertView.findViewById(R.id.image_obstacle);
            convertView.setTag(viewHolder);
        }

        Obstacle obstacle = getItem(position);

        viewHolder.type_obstacle.setText(obstacle.getType_obstacle());
        viewHolder.distance_obstacle.setText(obstacle.getDistance_obstacle());
        viewHolder.image_obstacle.setImageResource(obstacle.getImage_obstacle());

        return convertView;
    }

    private class ObstacleViewHolder{
        public TextView type_obstacle;
        public TextView distance_obstacle;
        public ImageView image_obstacle;
    }
}