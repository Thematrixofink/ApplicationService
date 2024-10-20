package com.hitices.pressure.domain.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class HTTPSamplerProxyVO {
    private int id;

    private String name;

    private String comment;

    private int threadGroupId;

    private String protocol;

    private String server;

    private String path;

    private int port;

    private String method;

    private String body;

    private Map<String,String> arguments;

    private String argumentsString;

    private String contentEncoding;

//    private int connectTimeout;
//
//    private int responseTimeout;

    private boolean useKeepAlive;

    private boolean followRedirects;

    private boolean autoRedirects;
}
