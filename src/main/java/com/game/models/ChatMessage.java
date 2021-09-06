package com.game.models;


import lombok.Getter;
import lombok.Setter;

import java.text.SimpleDateFormat;
import java.util.Date;

@Getter
@Setter
public class ChatMessage {
   //private Date time = new Date();
   private String time;
   private String name;
   private String description;
   private String category;
   //private long count;
   private String level = "info";

   public ChatMessage(){
      Date date = new Date();
      SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm:ss");//"yyyy-MM-dd HH:mm:ss"
      time=sdf.format(date);
   }

   public ChatMessage(String category, String description) {
      this.category = category;
      this.description = description;
   }
}
