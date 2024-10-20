package com.hitices.pressure.domain.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CookieManagerVO {
    private int id;

    private int threadGroupId;

    private String cookieManagerName;

    private String comment;

    private List<String> cookies;

    private String cookiesString;

    private boolean clearEachIteration;
}
