package com.hitices.pressure.domain.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ThreadGroupVO {

    private int id;

    private int testPlanId;

    private String threadGroupName;

    private String comment;

    private int threadNum;

    private int rampUp;

    private boolean scheduler;

    private long duration;

    private long delay;

    private boolean stepping;

    private int initialDelay;

    private int startUsersCount;

    private int startUsersCountBurst;

    private int startUsersPeriod;

    private int stopUsersCount;

    private int stopUsersPeriod;

    private int flighttime;

    private LoopControllerVO loopControllerVO;

    private HTTPSamplerProxyVO httpSamplerProxyVO;

    private HeaderManagerVO headerManagerVO;

    private CookieManagerVO cookieManagerVO;

    private List<TimerVO> timers;
}
