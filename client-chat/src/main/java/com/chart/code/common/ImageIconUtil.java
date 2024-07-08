package com.chart.code.common;

import cn.hutool.core.img.ImgUtil;

import javax.swing.*;
import javax.xml.bind.DatatypeConverter;
import java.awt.*;
import java.io.ByteArrayInputStream;

/**
 * 图片工具类
 *
 * @author CleanCode
 */
public class ImageIconUtil {
    /**
     * base64转换图片
     */
    public static ImageIcon base64ToImageIcon(String base64) {
        byte[] imageBytes = DatatypeConverter.parseBase64Binary(base64);
        try (ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes)) {
            Image image = ImgUtil.read(bis);
            return new ImageIcon(image);
        } catch (Exception ignored) {
        }
        return null;
    }
}
