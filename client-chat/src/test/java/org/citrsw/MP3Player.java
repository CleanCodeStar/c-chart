package org.citrsw;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

/**
 * Mp3播放
 *
 * @author CleanCode
 */
public class MP3Player {

    private final Clip clip;
    private int currentFramePosition;

    public MP3Player(String mp3Path) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        this(new File(mp3Path));
    }

    public MP3Player(File mp3File) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
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

    }

    /**
     * 从头播放
     */
    public void start() {
        new Thread(() -> {
            clip.start();
            while ((clip.getFrameLength() > 0 && clip.getFramePosition() == 0) || clip.isRunning()) {
            }
        }).start();

    }

    /**
     * 从指定位置开始播放
     *
     * @param startMicroseconds 开始播放的位置(毫秒)
     */
    public void start(long startMicroseconds) {
        new Thread(() -> {
            clip.setMicrosecondPosition(startMicroseconds);
            clip.start();
            while ((clip.getFrameLength() > 0 && clip.getFramePosition() == 0) || clip.isRunning()) {
            }
        }).start();
    }

    /**
     * 从指定位置开始播放
     *
     * @param startFramePosition 开始播放的位置(帧)
     */
    public void start(int startFramePosition) {
        new Thread(() -> {
            clip.setFramePosition(startFramePosition);
            clip.start();

            while ((clip.getFrameLength() > 0 && clip.getFramePosition() == 0) || clip.isRunning()) {
            }
        }).start();
    }

    /**
     * 暂停
     */
    public void pause() {
        currentFramePosition = clip.getFramePosition();
        clip.stop();
    }

    /**
     * 继续播放
     */
    public void resume() {
        start(currentFramePosition);
    }

    /**
     * 停止
     */
    public void stop() {
        clip.close();
    }

    public static void main(String[] args) throws UnsupportedAudioFileException, LineUnavailableException, IOException, InterruptedException {
        MP3Player mp3Player = new MP3Player("E:\\设计资料\\合成源文件+素材\\NO020\\宇宙航行\\视频\\Two Steps From Hell-Victory.mp3");
        mp3Player.start();
        Thread.sleep(5000);
        mp3Player.pause();
        Thread.sleep(3000);
        mp3Player.resume();
        Thread.sleep(3000);
        mp3Player.stop();


    }
}
