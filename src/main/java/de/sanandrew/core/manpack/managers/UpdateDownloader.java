/*
 * Decompiled with CFR 0.152.
 */
package de.sanandrew.core.manpack.managers;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Observable;

public class UpdateDownloader
extends Observable
implements Runnable {
    private static final int MAX_BUFFER_SIZE = 1024;
    private final URL url;
    private int size;
    private int downloaded;
    private final File modJar;
    private volatile EnumDlState status;
    private Runnable execWhenSucceed;

    public UpdateDownloader(URL url, File jar) {
        this.url = url;
        this.size = -1;
        this.downloaded = 0;
        this.status = EnumDlState.PENDING;
        this.modJar = jar;
        this.download();
    }

    public void setSucceedRunnable(Runnable runnable) {
        this.execWhenSucceed = runnable;
    }

    public String getUrl() {
        return this.url.toString();
    }

    public int getSize() {
        return this.size;
    }

    public float getProgress() {
        return (float)this.downloaded / (float)this.size * 100.0f;
    }

    public EnumDlState getStatus() {
        return this.status;
    }

    public void pause() {
        this.status = EnumDlState.PAUSED;
        this.stateChanged();
    }

    public void resume() {
        this.status = EnumDlState.DOWNLOADING;
        this.stateChanged();
        this.download();
    }

    public void cancel() {
        this.status = EnumDlState.CANCELLED;
        this.stateChanged();
    }

    private void error() {
        this.status = EnumDlState.ERROR;
        this.stateChanged();
    }

    private void download() {
        Thread thread = new Thread(this);
        thread.start();
    }

    private static String getFileName(URL url) {
        String fileName = url.getFile();
        return fileName.substring(fileName.lastIndexOf(47) + 1);
    }

    @Override
    public void run() {
        try {
            int contentLength;
            HttpURLConnection connection = (HttpURLConnection)this.url.openConnection();
            connection.setRequestProperty("Range", "bytes=" + this.downloaded + '-');
            connection.connect();
            if (connection.getResponseCode() / 100 != 2) {
                this.error();
            }
            if ((contentLength = connection.getContentLength()) < 1) {
                this.error();
            }
            if (this.size == -1) {
                this.size = contentLength;
                this.stateChanged();
            }
            try (RandomAccessFile file = new RandomAccessFile(new File(this.modJar.getParent(), UpdateDownloader.getFileName(this.url)), "rw");
                 InputStream stream = connection.getInputStream();){
                file.seek(this.downloaded);
                this.status = EnumDlState.DOWNLOADING;
                this.stateChanged();
                while (this.status == EnumDlState.DOWNLOADING) {
                    byte[] buffer = this.size - this.downloaded > 1024 ? new byte[1024] : new byte[this.size - this.downloaded];
                    int read = stream.read(buffer);
                    if (read == -1) {
                        break;
                    }
                    file.write(buffer, 0, read);
                    this.downloaded += read;
                    this.stateChanged();
                }
            }
            if (this.status == EnumDlState.DOWNLOADING) {
                this.status = EnumDlState.COMPLETE;
                this.stateChanged();
                if (!this.modJar.delete()) {
                    this.error();
                }
                if (this.execWhenSucceed != null) {
                    this.execWhenSucceed.run();
                }
            }
        }
        catch (IOException | SecurityException e) {
            this.error();
        }
    }

    private void stateChanged() {
        this.setChanged();
        this.notifyObservers();
    }

    public static enum EnumDlState {
        DOWNLOADING,
        PAUSED,
        COMPLETE,
        CANCELLED,
        ERROR,
        PENDING;

    }
}

