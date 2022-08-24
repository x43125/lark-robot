package com.wx.robot.entity.title;

import java.util.List;

/**
 * @author wangxiang
 * @date 2022/8/24 10:25
 * @description
 */
public class TodayRecord {
    private List<TodayRecordChild> titleBodies;

    public List<TodayRecordChild> getTitleBodies() {
        return titleBodies;
    }

    public void setTitleBodies(List<TodayRecordChild> titleBodies) {
        this.titleBodies = titleBodies;
    }
}
