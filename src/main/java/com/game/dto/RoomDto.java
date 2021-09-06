package com.game.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomDto {
    int roomId;
    int gid; //namely gameId
    String hostName;
    Set<String> clientNames;
}
