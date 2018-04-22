package com.example.timotemalherbe.compass;

/**
 * Created by timotemalherbe on 21/04/2018.
 */

public class Obstacle {
    private String type_obstacle;
    private String distance_obstacle;
    private int image_obstacle;//Ressource à produire pour les images d'obstacles

    public Obstacle(int image, String pseudo, String text) {
        this.type_obstacle = pseudo;
        this.distance_obstacle = text;
    }

    public void setType_obstacle(String type_obstacle){
        this.type_obstacle = type_obstacle;
    }

    public void setDistance_obstacle(String distance_obstacle){
        this.distance_obstacle = distance_obstacle;
    }

    public void setImage_obstacle(int image_obstacle) {
        this.image_obstacle = image_obstacle;
    }

    public String getType_obstacle(){
        return this.type_obstacle;
    }

    public String getDistance_obstacle() {
        return distance_obstacle;
    }

    public int getImage_obstacle() {
        return image_obstacle;
    }
}
