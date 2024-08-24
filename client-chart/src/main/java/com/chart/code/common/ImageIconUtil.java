package com.chart.code.common;

import com.google.common.io.BaseEncoding;
import javafx.scene.image.Image;
import org.apache.commons.lang3.StringUtils;

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

    /**
     * base64转换图片
     */
    public static Image base64ToImage(String base64) {
        if (StringUtils.isBlank(base64)) {
            return null;
        }
        String x = ",";
        if (base64.contains(x)) {
            base64 = base64.split(x)[1];
        }
        try {
            byte[] imageBytes = BaseEncoding.base64().decode(base64);
            return new Image(new ByteArrayInputStream(imageBytes));
        } catch (Exception ignored) {
        }
        return null;
    }
}
