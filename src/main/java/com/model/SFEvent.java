package com.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Setter
@Getter
@Builder
public class SFEvent {

    private String description;

    private ZonedDateTime startTime;

    private ZonedDateTime endTime;

    public SFEvent(String description, ZonedDateTime startTime, ZonedDateTime endTime) {
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public SFEventDTO toSFEventDTO() {
        return SFEventDTO.builder()
                .description(description)
                .startTime(startTime.withZoneSameInstant(ZoneId.of("UTC")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .endTime(endTime.withZoneSameInstant(ZoneId.of("UTC")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build();
    }
}
