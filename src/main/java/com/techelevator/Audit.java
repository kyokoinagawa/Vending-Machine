package com.techelevator;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Audit {
    LocalDateTime currentTime;
    List<String> auditStrings = new ArrayList<>();

    public Audit(List<String> auditStrings) {
        this.auditStrings = auditStrings;
    }

    public String getCurrentTime() {
        currentTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("mm dd yyyy hh:mm:ss a");
        String date = currentTime.format(formatter);
        return date;
    }

    public void setCurrentTime(LocalDateTime currentTime) {
        this.currentTime = currentTime;
    }
}
