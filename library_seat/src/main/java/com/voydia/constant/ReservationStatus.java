package com.voydia.constant;

/**
 * 预约状态
 */
public final class ReservationStatus {

    /** 待签到（预约成功后的默认状态） */
    public static final int PENDING = 0;

    /** 已签到 */
    public static final int CHECKED_IN = 1;

    /** 已取消 */
    public static final int CANCELLED = 2;

    /** 超时未签到，系统自动释放 */
    public static final int RELEASED = 3;

    private ReservationStatus() {
    }
}
