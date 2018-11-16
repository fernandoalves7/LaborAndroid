package com.rco.labor.businesslogic.rms;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class BytesForContent {
    public static final int DB_BYTES_LIMIT = 10000000;

    private long id;
    private long parentLobjectId = 0;
    private String parentObjectType;
    private String mimeType;
    private String remoteFullPath;
    private String rmsFileTimestamp;
    private byte[] bytes;
    private String localFileName;
    private boolean isDirty = false;
    private File localFile;

    public BytesForContent() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getParentLobjectId() {
        return parentLobjectId;
    }

    public void setParentLobjectId(long parentLobjectId) {
        this.parentLobjectId = parentLobjectId;
    }

    public String getParentObjectType() {
        return parentObjectType;
    }

    public void setParentObjectType(String parentObjectType) {
        this.parentObjectType = parentObjectType;
    }

    public String getRemoteFullPath() {
        return remoteFullPath;
    }

    public void setRemoteFullPath(String remoteFullPath) {
        this.remoteFullPath = remoteFullPath;
    }

    public String getRmsFileTimestamp() {
        return rmsFileTimestamp;
    }

    public void setRmsFileTimestamp(String rmsFileTimestamp) {
        this.rmsFileTimestamp = rmsFileTimestamp;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public byte[] getBytes() {
        return bytes;
    }
    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public void setBytes(Bitmap image) {
        this.bytes = null;

        if (image != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.PNG, 100, stream);
            this.bytes = stream.toByteArray();
        }
    }

    public void setBytes(File file) {
        this.bytes = null;

        if (file != null) {
            long size = file.length();

            if (size > DB_BYTES_LIMIT) {
                setLocalFileName(file.getAbsolutePath());
                localFile = file;
            } else {
                byte[] data = new byte[(int) file.length()];

                try {
                    new FileInputStream(file).read(data);
                    this.bytes = data;
                } catch (Exception e) {
                    this.bytes = null;
                    e.printStackTrace();
                }
            }
        }
    }

    public Bitmap getBytesAsBitmap() {
        if (bytes == null)
            return null;

        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes , 0, bytes.length);

        return bitmap;
    }

    public File getBytesAsFile() {
        if (bytes == null)
            return null;

        File file = null;

        try {
            file = File.createTempFile("tempfile", ""+getParentLobjectId(), null);
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bytes);
        } catch (IOException e) {
            file = null;
            e.printStackTrace();
        }

        return file;
    }

    public String getLocalFileName() {
        return localFileName;
    }

    public void setLocalFileName(String localFileName) {
        this.localFileName = localFileName;
    }

    public boolean isInLocalFile() {
        return localFile != null;
    }

    public File getLocalFile() {
        return localFile;
    }

    public void setLocalFile(File localFile) {
        this.localFile = localFile;
    }

    public boolean isDirty() {
        return isDirty;
    }

    public void setDirty(boolean dirty) {
        isDirty = dirty;
    }

    @Override
    public String toString() {
        return
            "BytesForContent {" +
                "id=" + id +
                ", parentLobjectId=" + parentLobjectId +
                ", parentObjectType='" + parentObjectType + '\'' +
                ", mimeType='" + mimeType + '\'' +
                ", remoteFullPath='" + remoteFullPath + '\'' +
                ", rmsFileTimestamp='" + rmsFileTimestamp + '\'' +
                ", localFileName='" + localFileName + '\'' +
                ", isDirty='" + isDirty + '\'' +
                ", bytes Size ='" + (bytes != null ? bytes.length : 0) + '\'' +
                ", file Size ='" + (localFile != null ? localFile.length() : 0) + '\'' +
            '}';
    }
}
