package com.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SFEventDTO {

    private String description;

    private String startTime;

    private String endTime;
}
