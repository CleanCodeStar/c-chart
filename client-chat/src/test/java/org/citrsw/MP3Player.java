package org.citrsw;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class MP3Player {

    private static final String MP3_FILE = "E:\\设计资料\\合成源文件+素材\\NO020\\宇宙航行\\视频\\Two Steps From Hell-Victory.mp3";
    private static Clip clip;

    public static void main(String[] args) {
        File mp3File = new File(MP3_FILE);

        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(mp3File);
            AudioFormat baseFormat = audioInputStream.getFormat();
            AudioFormat decodedFormat = new AudioFormat(
                    AudioFormat.Encoding.PCM_SIGNED,
                    baseFormat.getSampleRate(),
                    16,
                    baseFormat.getChannels(),
                    baseFormat.getChannels() * 2,
                    baseFormat.getSampleRate(),
                    false);

            // 使用 Tritonus 插件来解码 MP3
            AudioInputStream decodedAudioInputStream = AudioSystem.getAudioInputStream(decodedFormat, audioInputStream);

            // 获取 Clip
            DataLine.Info info = new DataLine.Info(Clip.class, decodedAudioInputStream.getFormat());
            clip = (Clip) AudioSystem.getLine(info);
            clip.open(decodedAudioInputStream);

            long startMicroseconds = 1_000_000*120;
            clip.setMicrosecondPosition(startMicroseconds);

            // 播放音频
            clip.start();

            // 播放一段时间后停止
            Thread.sleep(1000000);
            clip.stop();

        } catch (UnsupportedAudioFileException | LineUnavailableException | IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (clip != null) {
                clip.close();
            }
        }
    }
}
