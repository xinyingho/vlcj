package uk.co.caprica.vlcj.player.base;

import uk.co.caprica.vlcj.player.MediaPlayerEventListener;
import uk.co.caprica.vlcj.player.condition.BeforeConditionAbortedException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public final class SnapshotService extends BaseService {

    /**
     * Optional name of the directory to save video snapshots to.
     * <p>
     * If this is not set then snapshots will be saved to the user home directory.
     */
    private String snapshotDirectoryName;

    SnapshotService(DefaultMediaPlayer mediaPlayer) {
        super(mediaPlayer);
    }

    /**
     * Set the directory into which snapshots of the video are saved.
     * <p>
     * If the specified directory path does not yet exist, it will be created.
     *
     * @param snapshotDirectoryName name of the directory to save snapshots to
     */
    public void setSnapshotDirectory(String snapshotDirectoryName) {
        this.snapshotDirectoryName = snapshotDirectoryName;
    }

    /**
     * Save a snapshot of the currently playing video.
     * <p>
     * The size of the image will be that produced by the libvlc native snapshot function, i.e. the
     * size of the media itself.
     * <p>
     * The snapshot will be created in the directory set via {@link #setSnapshotDirectory(String)},
     * unless that directory has not been set in which case the snapshot will be created in the
     * user's home directory, obtained via the "user.home" system property.
     * <p>
     * The snapshot will be assigned a filename based on the current time.
     * <p>
     * The size of the image will be that produced by the libvlc native snapshot function.
     * <p>
     * Taking a snapshot is an asynchronous function, the snapshot is not available until
     * after the {@link MediaPlayerEventListener#snapshotTaken(MediaPlayer, String)} event
     * is received.
     *
     * @return <code>true</code> if the snapshot was saved, otherwise <code>false</code>
     */
    public boolean saveSnapshot() {
        return saveSnapshot(0, 0);
    }

    /**
     * Save a snapshot of the currently playing video.
     * <p>
     * The snapshot will be created in the directory set via {@link #setSnapshotDirectory(String)},
     * unless that directory has not been set in which case the snapshot will be created in the
     * user's home directory, obtained via the "user.home" system property.
     * <p>
     * The snapshot will be assigned a filename based on the current time.
     * <p>
     * If one of width or height is zero the original image aspect ratio will be preserved.
     * <p>
     * If both width and height are zero, the original image size will be used, see
     * {@link #saveSnapshot()}.
     * <p>
     * Taking a snapshot is an asynchronous function, the snapshot is not available until
     * after the {@link MediaPlayerEventListener#snapshotTaken(MediaPlayer, String)} event
     * is received.
     *
     * @param width desired image width
     * @param height desired image height
     * @return <code>true</code> if the snapshot was saved, otherwise <code>false</code>
     */
    public boolean saveSnapshot(int width, int height) {
        File snapshotDirectory = new File(snapshotDirectoryName == null ? System.getProperty("user.home") : snapshotDirectoryName);
        File snapshotFile = new File(snapshotDirectory, "vlcj-snapshot-" + System.currentTimeMillis() + ".png");
        return saveSnapshot(snapshotFile, width, height);
    }

    /**
     * Save a snapshot of the currently playing video.
     * <p>
     * The size of the image will be that produced by the libvlc native snapshot function, i.e. the
     * size of the media itself.
     * <p>
     * Any missing directory path will be created if it does not exist.
     * <p>
     * Taking a snapshot is an asynchronous function, the snapshot is not available until
     * after the {@link MediaPlayerEventListener#snapshotTaken(MediaPlayer, String)} event
     * is received.
     *
     * @param file file to contain the snapshot
     * @return <code>true</code> if the snapshot was saved, otherwise <code>false</code>
     */
    public boolean saveSnapshot(File file) {
        return saveSnapshot(file, 0, 0);
    }

    /**
     * Save a snapshot of the currently playing video.
     * <p>
     * Any missing directory path will be created if it does not exist.
     * <p>
     * If one of width or height is zero the original image aspect ratio will be preserved.
     * <p>
     * If both width and height are zero, the original image size will be used, see
     * {@link #saveSnapshot(File)}.
     * <p>
     * Taking a snapshot is an asynchronous function, the snapshot is not available until
     * after the {@link MediaPlayerEventListener#snapshotTaken(MediaPlayer, String)} event
     * is received.
     *
     * @param file file to contain the snapshot
     * @param width desired image width
     * @param height desired image height
     * @return <code>true</code> if the snapshot was saved, otherwise <code>false</code>
     */
    public boolean saveSnapshot(File file, int width, int height) {
        File snapshotDirectory = file.getParentFile();
        if (snapshotDirectory == null) {
            snapshotDirectory = new File(".");
        }
        if (!snapshotDirectory.exists()) {
            snapshotDirectory.mkdirs();
        }
        if (snapshotDirectory.exists()) {
            boolean snapshotTaken = libvlc.libvlc_video_take_snapshot(mediaPlayerInstance, 0, file.getAbsolutePath(), width, height) == 0;
            return snapshotTaken;
        } else {
            throw new RuntimeException("Directory does not exist and could not be created for '" + file.getAbsolutePath() + "'");
        }
    }

    /**
     * Get a snapshot of the currently playing video.
     * <p>
     * The size of the image will be that produced by the libvlc native snapshot function, i.e. the
     * size of the media itself.
     * <p>
     * This implementation uses the native libvlc method to save a snapshot of the currently playing
     * video. This snapshot is saved to a temporary file and then the resultant image is loaded from
     * the file.
     * <p>
     * Taking a snapshot is an asynchronous function, the snapshot is not available until
     * after the {@link MediaPlayerEventListener#snapshotTaken(MediaPlayer, String)} event
     * is received.
     *
     * @return snapshot image, or <code>null</code> if a snapshot could not be taken
     */
    public BufferedImage getSnapshot() {
        return getSnapshot(0, 0);
    }

    /**
     * Get a snapshot of the currently playing video.
     * <p>
     * This implementation uses the native libvlc method to save a snapshot of the currently playing
     * video. This snapshot is saved to a temporary file and then the resultant image is loaded from
     * the file.
     * <p>
     * If one of width or height is zero the original image aspect ratio will be preserved.
     * <p>
     * If both width and height are zero, the original image size will be used, see
     * {@link #getSnapshot()}
     * <p>
     * Taking a snapshot is an asynchronous function, the snapshot is not available until
     * after the {@link MediaPlayerEventListener#snapshotTaken(MediaPlayer, String)} event
     * is received.
     *
     * @param width desired image width
     * @param height desired image height
     * @return snapshot image, or <code>null</code> if a snapshot could not be taken
     */
    public BufferedImage getSnapshot(int width, int height) {
        File file = null;
        try {
            file = File.createTempFile("vlcj-snapshot-", ".png");
            return ImageIO.read(new File(new WaitForSnapshot(mediaPlayer, file, width, height).await()));
        }
        catch(IOException e) {
            throw new RuntimeException("Failed to get snapshot image", e);
        }
        catch(InterruptedException e) {
            throw new RuntimeException("Failed to get snapshot image", e);
        }
        catch(BeforeConditionAbortedException e) {
            return null;
        }
        finally {
            if(file != null) {
                file.delete();
            }
        }
    }

}
