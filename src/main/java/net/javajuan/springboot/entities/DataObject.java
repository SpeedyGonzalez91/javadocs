package net.javajuan.springboot.entities;

import java.util.ArrayList;
import java.util.List;

public class DataObject {
	
	private String data;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
	private String kioskGroupID;
    private String invitationID;
    private String instruction;
    private String location;
    private String reason;
    private String timezone;
    
    
    
    // Constructor
    public DataObject(String kioskGroupID, String invitationID, String instruction, String location, String reason, String timezone) {
        this.kioskGroupID = kioskGroupID;
        this.invitationID = invitationID;
        this.instruction = instruction;
        this.location = location;
        this.reason = reason;
        this.timezone = timezone;
    }

    // Getters and setters
    public String getKioskGroupID() {
        return kioskGroupID;
    }

    public void setKioskGroupID(String kioskGroupID) {
        this.kioskGroupID = kioskGroupID;
    }

    public String getInvitationID() {
        return invitationID;
    }

    public void setInvitationID(String invitationID) {
        this.invitationID = invitationID;
    }

    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    
}

    public List<String> getLabel() {
        List<String> labels = new ArrayList<>();
        labels.add(kioskGroupID);
        labels.add(invitationID);
        labels.add(instruction);
        labels.add(location);
        labels.add(reason);
        labels.add(timezone);
        return labels;
}
}