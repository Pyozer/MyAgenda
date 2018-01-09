package com.pyozer.myagenda.model;

import java.util.Date;

public class RoomAvailable {

    private String roomName;
    private Date dateEnd;

    public RoomAvailable(String roomName, Date dateEnd) {
        this.roomName = roomName;
        this.dateEnd = dateEnd;
    }

    public String getRoomName() {
        return roomName;
    }

    public Date getDateEnd() {
        return dateEnd;
    }
}
