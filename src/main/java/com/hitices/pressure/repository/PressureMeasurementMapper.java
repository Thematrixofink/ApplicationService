package com.hitices.pressure.repository;

import com.hitices.pressure.entity.*;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PressureMeasurementMapper {

    List<TestPlanVO> getAllTestPlans();

    TestPlanVO getTestPlanById(@Param("id") int id);

    HTTPSamplerProxyVO getHTTPSamplerProxyByThreadGroupId(@Param("threadGroupId") int threadGroupId);

    HeaderManagerVO getHeaderManagerByThreadGroupId(@Param("threadGroupId") int threadGroupId);

    CookieManagerVO getCookieManagerByThreadGroupId(@Param("threadGroupId") int threadGroupId);

    LoopControllerVO getLoopControllerByThreadGroupId(@Param("threadGroupId") int threadGroupId);

    List<TimerVO> getTimersByThreadGroupId(@Param("threadGroupId") int threadGroupId);

    ConstantTimerVO getConstantTimerById(@Param("id") int id);

    UniformRandomTimerVO getUniformRandomTimerById(@Param("id") int id);

    GaussianRandomTimerVO getGaussianRandomTimerById(@Param("id") int idList);

    PoissonRandomTimerVO getPoissonRandomTimerById(@Param("id") int idList);

    List<ThreadGroupVO> getThreadGroupsByTestPlanId(@Param("testPlanId") int testPlanId);

    int insertTestPlan(TestPlanVO testPlan);

    int insertThreadGroups(@Param("threadGroups") List<ThreadGroupVO> threadGroups);

    int insertHTTPSamplerProxyList(@Param("httpSamplerProxyList") List<HTTPSamplerProxyVO> httpSamplerProxyList);

    int insertHeaderManagerList(@Param("headerManagerList") List<HeaderManagerVO> headerManagerList);

    int insertCookieManagerList(@Param("cookieManagerList") List<CookieManagerVO> cookieManagerList);

    int insertLoopController(LoopControllerVO loopControllerVO);

    int insertConstantTimerList(@Param("constantTimerList") List<ConstantTimerVO> constantTimerList);

    int insertGaussianRandomTimerList(@Param("gaussianRandomTimerList") List<GaussianRandomTimerVO> gaussianRandomTimerList);

    int insertPoissonRandomTimerList(@Param("poissonRandomTimerList") List<PoissonRandomTimerVO> poissonRandomTimerList);

    int insertUniformRandomTimerList(@Param("uniformRandomTimerList") List<UniformRandomTimerVO> uniformRandomTimerList);

    int insertTimers(@Param("timers") List<TimerVO> timers);

    int deleteTestPlan(@Param("testPlanId") int testPlanId);

    int deleteThreadGroup(@Param("threadGroupId") int threadGroupId);

    int deleteThreadGroupByTestPlanId(@Param("testPlanId") int testPlanId);

    void updateTestPlan(TestPlanVO testPlan);
}
