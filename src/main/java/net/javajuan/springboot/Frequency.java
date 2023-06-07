package net.javajuan.springboot;

public class Frequency {
    public static final String KIOSK_GROUP_ID_LABEL = "KioskGroupId";
    public static String getKioskGroupIdLabel() {
		return KIOSK_GROUP_ID_LABEL;
	}

	public static String getInvitationIdLabel() {
		return INVITATION_ID_LABEL;
	}

	public static String getInstructionLabel() {
		return INSTRUCTION_LABEL;
	}

	public static String getLocationLabel() {
		return LOCATION_LABEL;
	}

	public static String getReasonLabel() {
		return REASON_LABEL;
	}

	public static String getTimeZoneLabel() {
		return TIME_ZONE_LABEL;
	}

	public static final String INVITATION_ID_LABEL = "Invitation ID";
    public static final String INSTRUCTION_LABEL = "Instruction";
    public static final String LOCATION_LABEL = "Location";
    public static final String REASON_LABEL = "Reason";
    public static final String TIME_ZONE_LABEL = "TimeZone";

    private String category;
    private int frequency;

    public Frequency(String category, int frequency) {
        this.category = category;
        this.frequency = frequency;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }
}
