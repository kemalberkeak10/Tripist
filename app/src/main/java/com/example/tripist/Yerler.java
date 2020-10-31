package com.example.tripist;

import java.io.Serializable;

public class Yerler implements Serializable {

    public String name ;

    public Double latitude;
    public Double longitude;

       public Yerler(String name,Double latitude, Double longitude){
           this.name = name;
           this.latitude = latitude;
           this.longitude = longitude;
       }
}
