package com.rco.labor.businesslogic.rms;

import java.io.File;
import java.io.Serializable;
import java.util.List;
import java.util.Observable;
import org.json.JSONObject;
import android.graphics.Bitmap;

import com.rco.labor.businesslogic.Pair;
import com.rco.labor.businesslogic.PairList;
import com.rco.labor.utils.TextUtils;

@SuppressWarnings("serial")
public class ObjectInfo extends Observable implements Serializable, Comparable {
    private long LobjectId = 0;
    private String objectType;
    private String codingTimeStamp;
    private String contentTimeStamp;
    private long rmsCodingTimestamp;
    private boolean isDirty = false;
    protected String mobileRecordId;
    protected String recordId;
    private boolean isBeingSubmitted = false;
    private PairList pairList;
    private boolean markedForDelete = false;

    public boolean hasAccess(User currentUser) {
        return true;
    }

    public enum CONTENT_TYPE { IMAGE, STRINGS, PDF }
    protected CONTENT_TYPE type = null;

    private BytesForContent bytesForContent;

    protected String parentKeyNameForParent = "barcode";
    protected String parentKeyNameForChild = "masterBarcode";
    protected Class childClass = null;

    //private Map<String, Signature> signatures = new Hashtable<String, Signature>();

    // Construction

    public ObjectInfo() {
    }

    public ObjectInfo(PairList codingFields) {
        setFromList(codingFields);
    }

    public PairList getAsPairList() {
        PairList pl = new PairList();

        pl.add("LobjectId", "" + getLobjectId());
        pl.add("objectType", getObjectType());
        pl.add("codingTimeStamp", getCodingTimeStamp());
        pl.add("RMS Efile Timestamp", getContentTimeStamp());
        pl.add("RMS Coding Timestamp", "" + getRmsCodingTimestamp());
        pl.add("MobileRecordId", "" + getMobileRecordId());
        pl.add("RecordId", "" + getRecordId());

        return pl;
    }


    public JSONObject getAsJSON() {
        PairList pl = getAsPairList();

        pl.remove("LobjectId");
        pl.remove("objectType");

        List<Pair> list = pl.toList();
        JSONObject obj = new JSONObject();

        try {
            obj.put("LobjectId", getLobjectId());
            obj.put("objectType", getObjectType());
            JSONObject so = new JSONObject();
            obj.put("mapCodingInfo", so);

            for (int i = 0; i < list.size(); i++) {
                Pair pair = list.get(i);
                so.put(pair.Key, pair.Value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return obj;
    }

    public void resetObject() {
        LobjectId = 0;
        objectType = null;
        codingTimeStamp = null;
        contentTimeStamp = null;
        rmsCodingTimestamp = 0;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof ObjectInfo))
            return false;

        return ((ObjectInfo)o).getLobjectId() == getLobjectId();
    }

    @Override
    public int hashCode() {
        return Long.valueOf(LobjectId).hashCode();
    }

    // Getters / setters

    public String getObjectType() {
        return objectType;
    }

    public void setObjectType(String value) {
        objectType = value;
    }

    public String getCodingTimeStamp() {
        return codingTimeStamp;
    }

    public void setCodingTimeStamp(String value) {
        codingTimeStamp = value;
    }

    public String getContentTimeStamp() {
        return contentTimeStamp;
    }

    public void setContentTimeStamp(String value) {
        contentTimeStamp = value;
    }

    public long getLobjectId() {
        return LobjectId;
    }

    public void setLobjectId(long lobjectId) {
        this.LobjectId = lobjectId;
    }

    // Helpers

    public boolean existsObjectInfo() {
        return getLobjectId() > 0 && !TextUtils.isNullOrWhitespaces(getObjectType());
    }

    public void setObjectInfo(long lobjectId, String objectType) {
        this.LobjectId = lobjectId;
        this.objectType = objectType;
    }

    public void setObjectInfo(ObjectInfo o) {
        this.objectType = o.objectType;
        this.codingTimeStamp = o.codingTimeStamp;
        this.contentTimeStamp = o.contentTimeStamp;
        this.LobjectId = o.LobjectId;
        this.rmsCodingTimestamp = o.rmsCodingTimestamp;
    }

    public void setFromList(PairList codingFields) {
        if (codingFields == null)
            return;

        if (codingFields.exists("LobjectId"))
            this.LobjectId = Long.parseLong(codingFields.getValue("LobjectId"));

        if (this.LobjectId <= 0 && codingFields.exists("objectId"))
            this.LobjectId = Long.parseLong(codingFields.getValue("objectId"));

        this.objectType = codingFields.getValue("objectType");

        codingTimeStamp = codingFields.getValue("codingTimeStamp");
        contentTimeStamp = codingFields.getValue("RMS Efile Timestamp");

        if (codingTimeStamp != null)
            rmsCodingTimestamp = Long.parseLong(codingTimeStamp);

        setRmsCodingTimestamp(codingFields);

        this.pairList = codingFields;
        this.mobileRecordId = codingFields.getValue("MobileRecordId");
    }

    public long getRmsCodingTimestamp() {
        return rmsCodingTimestamp;
    }

    private void setRmsCodingTimestamp(long rmsCodingTimestamp) {
        this.rmsCodingTimestamp = rmsCodingTimestamp;
    }

    private void setRmsCodingTimestamp(PairList list) {
        if (list != null && list.getValue("RMS Coding Timestamp") != null)
            setRmsCodingTimestamp(Long.parseLong(list.getValue("RMS Coding Timestamp")));
    }

    public boolean isValid() {
        return existsObjectInfo();
    }

    public void setParentKeyValue(String value) {
        throw new RuntimeException("setParentKeyValue needs to be implemented for "+this.getClass().getCanonicalName());
    }

    public String getParentKeyValue() {
        throw new RuntimeException("getParentKeyValue needs to be implemented for "+this.getClass().getCanonicalName());
    }

    public void addDetail(ObjectInfo detail) {
    }

    public void clearDetails() {
    }

    public String getParentKeyNameForChild() {
        return parentKeyNameForChild;
    }

    public String getParentKeyNameForParent() {
        return parentKeyNameForParent;
    }

    @Override
    public int compareTo(Object another) {
        if (another == null || !(another instanceof ObjectInfo) )
            return -1;

        return (int) (getLobjectId() - ((ObjectInfo) another).getLobjectId());
    }

    public boolean isBeingSubmitted() {
        return isBeingSubmitted;
    }

    public synchronized void setBeingSubmitted(boolean isBeingSubmitted) {
        boolean notifyOthers = false;

        if (this.isBeingSubmitted != isBeingSubmitted) {
            notifyOthers = true;
        }

        this.isBeingSubmitted = isBeingSubmitted;

        if (notifyOthers) {
            setChanged();
            notifyObservers();
        }

        this.notifyAll();
    }

    public PairList getPairList() {
        return pairList;
    }

    public boolean isMarkedForDelete() {
        return markedForDelete;
    }

    public void setMarkedForDelete(boolean markedForDelete) {
        this.markedForDelete = markedForDelete;
    }

    public boolean hasBytes() {
        return getBytesForContent() != null && getBytesForContent().getBytes() != null;
    }

    public BytesForContent getBytesForContent() {
        return this.bytesForContent;
    }

    public void setBytesForContent(BytesForContent bytesForContent) {
        this.bytesForContent = bytesForContent;
        setParentValues();
    }

    public void setBytes(Bitmap bitmap) {
        this.bytesForContent = new BytesForContent();
        this.bytesForContent.setBytes(bitmap);

        setParentValues();
    }

    public void setBytes(File file) {
        this.bytesForContent = new BytesForContent();
        this.bytesForContent.setBytes(file);

        setParentValues();
    }

    public void setBytes(byte[] bytes) {
        this.bytesForContent = new BytesForContent();
        this.bytesForContent.setBytes(bytes);

        setParentValues();
    }

    private void setParentValues() {
        if (this.bytesForContent != null) {
            this.bytesForContent.setParentLobjectId(getLobjectId());
            this.bytesForContent.setParentObjectType(getObjectType());
        }
    }

    public void copyBytes(ObjectInfo other) {
        this.bytesForContent = null;

        if (other != null && other.bytesForContent != null) {
            this.bytesForContent = new BytesForContent();
            this.bytesForContent.setBytes(other.getBytesForContent().getBytes());

            setParentValues();
        }
    }

    public CONTENT_TYPE getContentType() {
        return type;
    }

    // Should be overridden in child type if it is being used.

    public void setContent(List<String> content) {

    }

    public void setContent(File file, String type) {
        // Type example "application/pdf"

        bytesForContent = new BytesForContent();
        bytesForContent.setMimeType(type);
        bytesForContent.setBytes(file);
    }

    public File getBytesAsFile() {
        if (bytesForContent != null)
            return bytesForContent.getBytesAsFile();

        return null;
    }

    public Bitmap getBytesAsBitmap() {
        if (bytesForContent != null)
            return bytesForContent.getBytesAsBitmap();

        return null;
    }

    public List<? extends ObjectInfo> getDetails() {
        return null;
    }

    /*public void setSignature(String key, Signature signature) {
        if (signature != null && key != null) {
            signature.setParent(this);
            signature.setDocumentTitle(key);
            signatures.put(key, signature);
        }
    }

    public Signature getSignature(String key) {
        if (key != null)
            return signatures.get(key);

        return null;
    }

    public List<Signature> getSignatures() {
        List<Signature> temp = new Vector<>();
        temp.addAll(signatures.values());
        return temp;
    }*/

    public Class getChildClass() {
        return childClass;
    }

    public boolean isDirty() {
        return isDirty;
    }

    public void setDirty(boolean dirty) {
        isDirty = dirty;
        List<? extends ObjectInfo> details = this.getDetails();

        if (details == null)
            return;

        for (ObjectInfo detail: details )
            detail.setDirty(dirty);
    }

    public String getMobileRecordId() {
        return mobileRecordId;
    }

    public void setMobileRecordId(String mobileRecordId) {
        this.mobileRecordId = mobileRecordId;
    }

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    @Override
    public String toString() {
        return
            "ObjectInfo {" +
                "LobjectId=" + LobjectId +
                ", objectType='" + objectType + '\'' +
                ", recordId='" + recordId + '\'' +
                ", mobileRecordId='" + mobileRecordId + '\'' +
                ", isDirty=" + isDirty +
            '}';
    }
}