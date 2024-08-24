module chart.common {
    requires lombok;
    requires com.google.common;
    requires cn.hutool;
    requires com.alibaba.fastjson2;

    exports com.chart.code.define;
    exports com.chart.code.enums;
    exports com.chart.code.dto;
    exports com.chart.code.vo;
    exports com.chart.code.annotation;
    opens com.chart.code.define to chart.server;
}