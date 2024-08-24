module chart.client {
    requires javafx.controls;
    requires static lombok;
    requires chart.common;
    requires TableLayout;
    requires org.apache.commons.io;
    requires swingx.all;
    requires javafx.swing;
    requires cn.hutool;
    requires com.google.common;
    requires com.alibaba.fastjson2;
    requires org.apache.commons.lang3;

    exports com.chart.code;
    exports com.chart.code.common;
    exports com.chart.code.component;
    exports com.chart.code.thread;
}