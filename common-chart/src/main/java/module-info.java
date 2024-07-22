module chart.common {
    exports com.chart.code.define;
    exports com.chart.code.enums;
    exports com.chart.code.dto;
    exports com.chart.code.vo;

    requires lombok;
    requires com.google.common;
    requires cn.hutool;
    requires com.alibaba.fastjson2;

}