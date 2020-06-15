package com.atlassian.migration.datacenter.core.util;

import org.apache.commons.lang3.SystemUtils;

public class AnalyticsHelper
{
    // Should be mapped in analytics_whitelist.json
    public enum  OsType {
        Linux,
        Mac,
        Windows,
        UnixOther,
        Other
    }

    static public OsType getOsType() {
        OsType os;
        if (SystemUtils.IS_OS_LINUX) {
            os = OsType.Linux;
        } else if (SystemUtils.IS_OS_MAC) {
            os = OsType.Mac;
        } else if (SystemUtils.IS_OS_WINDOWS) {
            os = OsType.Windows;
        } else if (SystemUtils.IS_OS_UNIX) {
            os = OsType.UnixOther;
        } else {
            os = OsType.Other;
        }
        return os;
    }

}
