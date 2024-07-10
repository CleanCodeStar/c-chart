package com.chart.code.common;

import com.google.common.io.BaseEncoding;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
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
        String x = ",";
        if (base64.contains(x)) {
            base64 = base64.split(x)[1];
        }
        try {
            byte[] imageBytes = BaseEncoding.base64().decode(base64);
            BufferedImage img = ImageIO.read(new ByteArrayInputStream(imageBytes));
            return new ImageIcon(img);
        } catch (Exception ignored) {
        }
        return null;
    }
}
