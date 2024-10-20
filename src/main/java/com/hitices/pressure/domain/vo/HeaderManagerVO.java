package com.hitices.pressure.domain.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class HeaderManagerVO {
    private int id;

    private String headerManagerName;

    private String comment;

    private Map<String, String> headerList;

    private String headerListString;

    private int threadGroupId;
}
