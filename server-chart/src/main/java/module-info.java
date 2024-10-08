module chart.server {
    requires chart.common;
    requires java.sql;
    requires com.google.common;
    requires cn.hutool;
    requires com.alibaba.fastjson2;
    requires org.apache.commons.lang3;

    exports com.chart.code;
    exports com.chart.code.db;
    exports com.chart.code.thread;
}