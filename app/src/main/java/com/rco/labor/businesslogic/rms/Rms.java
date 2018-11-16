package com.rco.labor.businesslogic.rms;

import android.util.Log;

import com.rco.labor.businesslogic.BusinessRules;
import com.rco.labor.businesslogic.Pair;
import com.rco.labor.businesslogic.PairList;
import com.rco.labor.businesslogic.labor.Labor;
import com.rco.labor.businesslogic.labor.LaborClockManager;
import com.rco.labor.utils.DateUtils;
import com.rco.labor.utils.HttpClient;
import com.rco.labor.utils.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Fernando on 9/16/2018.
 */
public class Rms {
    private static String TAG = "com.rco.mobileoffice.businesslogic.rms";

    private static String url;
    private static String username;
    private static long userId;
    private static String password;
    public static String orgName;
    public static String orgNumber;

    // Construction

    public Rms() {

    }

    // RMS calls

    public static String getRecordsUpdatedFiltered(String recordType, String fullDataLimit, String maxTimestamp, String filterCodingFieldName, String filterCodingFieldValue) throws Exception {
        return HttpClient.get(url);
        //return getFromServer(getRmsUrl("recordservice", "getRecordsUpdatedFiltered", new String[] { recordType, fullDataLimit, maxTimestamp, filterCodingFieldName, filterCodingFieldValue }));
    }

    public static String getRecordsUpdatedXFiltered(String recordDisplayType, int maxNumberFullDataRecords, String filterFields, String strFieldDelim, String filterValues, String strValueDelim, String includeFields) throws IOException {
        return HttpClient.get(Rms.getUrl() + "/Image2000/rest/recordservice/getRecordsUpdatedXFiltered/" +
                Rms.getUsername() + "/" + Rms.getPassword() + "/" +
                recordDisplayType + "/" + maxNumberFullDataRecords + "/+/+/+/+/" + filterFields + "/" +
                strFieldDelim + "/" + filterValues + "/" + strValueDelim + "/+/" + includeFields);
    }

    public static String getUserRights() throws IOException {
        //https://www.rcofox.com/Image2000/rest/securityservice/getUserRights/hg/hg
        return HttpClient.get(Rms.getUrl() + "/Image2000/rest/securityservice/getUserRights/" + Rms.getUsername() + "/" + Rms.getPassword());
    }

    public static String setTimecards(final String deviceId, final String username, final User manager, Labor l) throws Exception {
        ArrayList<Labor> labors = new ArrayList();
        labors.add(l);

        final String headerMobileRecordId = getMobileRecordId("TimecardHeader", deviceId, username, Long.toString(Calendar.getInstance().getTimeInMillis()));
        final LaborClockManager clockManager = l.getClockManager();
        final String nowStr = DateUtils.getNowYyyyMmDdHhmmss();

        String postBody = serializeListAsCsvPost(labors,
            new IPostParser<Labor>() { public String parse(final Labor l) {
                String row = serializeItemAsCsvPostLine(new String[] { "O", "H", "", "",
                    headerMobileRecordId,                               // MobileRecordId
                    "", 						                        // Functional Group Name
                    orgName,					                        // Org name
                    orgNumber,					                        // Org number
                    nowStr,                                             // DateTime
                    "",                                                 // Email
                    l.getFirstName(),                                   // First Name
                    l.getLastName(),                                    // Last Name
                    orgName,                                            // Company
                    "",                                                 // Memo
                    "",                                                 // Phone
                    "",                                                 // Reference Number
                    "Pending",                                          // Status
                    clockManager.getCreationDateTimeStr(),              // Time
                    clockManager.getTimecardTotalHoursStr(),            // Total Hours
                    l.getRecordId(),                                    // Worker id
                    "",                                                 // Due Date
                    "",                                                 // Terms
                    "",                                                 // Customer Billed Status
                    "",                                                 // Vendor Billed Status
                    "",                                                 // Employee Bill Status
                    manager.getRecordId(),                              // CreatorRecordId
                    clockManager.getTimecardTotalHoursStr(),            // Total Amount
                    "",                                                 // Processed
                    l.getEmployeeId(),                                  // Employee Id
                    clockManager.getTimecardTotalHoursStr(),            // Straight Time Hours
                    clockManager.getTodayOvertimeStr(),                 // Overtime Hours
                    clockManager.getTodayOvertimeDoubleStr(),           // Double Overtime Hours
                    l.getClockManager().getTodayBreakTotalHoursStr(),   // Break Time Hours
                    "",                                                 // Sick Time Hours
                    ""                                                  // Vacation Time Hours
                });

                String lines = serializeListAsCsvPost(clockManager.getClockItems(), new IPostParser<LaborClockManager.ClockItem>() {
                    public String parse(LaborClockManager.ClockItem i) {
                        return serializeItemAsCsvPostLine(new String[] { "O", "D", "", "",
                            getMobileRecordId("TimecardDetail", deviceId, username),    // MobileRecordId
                            "",                                                                     // FunctionalGroupName
                            orgName,                                                                // organizationName
                            orgNumber,                                                              // organizationNumber
                            orgName,                                                                // Company
                            "",                                                                     // Customer:Job
                            l.getFirstName(),                                                       // First Name
                            l.getLastName(),                                                        // Last Name
                            i.getHoursDifferenceStr(),                                              // Hours
                            i.getStartDateStr(),                                                    // Start Date
                            i.getStartTimeStr(),                                                    // Start time
                            i.getEndDateStr(),                                                      // End Date
                            i.getEndTimeStr(),                                                      // End Time
                            "",                                                                     // Task Detail
                            "Work",                                                                 // Task Name
                            "102",                                                                  // Task number
                            l.getRecordId(),                                                        // Worker Id
                            "no",                                                                   // Billable
                            "",                                                                     // CustomerRecordId
                            "",                                                                     // Customer Billed Status
                            "timecarddetail",                                                       // itemType
                            "",                                                                     // Vendor Billed Status
                            "",                                                                     // Employee Bill Status
                            manager.getRecordId(),                                                  // CreatorRecordId
                            i.getOvertimeStr(),                                                     // Overtime - TODO
                            "",                                                                     // Matter Number
                            "",                                                                     // Matter Description
                            "",                                                                     // Description
                            "",                                                                     // Amount
                            "",                                                                     // Piece Rate
                            "",                                                                     // Piece Quantity
                            "",                                                                     // Location
                            "",                                                                     // Labor Group Name
                            "",                                                                     // Labor Group Number
                            i.getOvertimeStr(),                                                     // Overtime Hours - TODO
                            i.getOvertimeDoubleStr(),                                               // Double Overtime Hours - TODO
                            i.getHoursDifferenceStr(),                                              // Straight Time Hours
                            "",                                                                     // Sick Time Hours
                            "",                                                                     // Holiday Time Hours
                            "",                                                                     // Vacation Time Hours
                            "",                                                                     // Break Time Hours
                            "",                                                                     // Weight
                            l.getEmployeeId(),                                                      // Employee Id
                            "",                                                                     // Billing Code
                            "",                                                                     // Billing Factor
                            "new",                                                                  // Status
                            "",                                                                     // Processed
                        });
                    }
                });

                String lines2 = serializeListAsCsvPost(clockManager.getBreakItems(), new IPostParser<LaborClockManager.BreakItem>() {
                    public String parse(LaborClockManager.BreakItem i) {
                        return serializeItemAsCsvPostLine(new String[] { "O", "D", "", "",
                                getMobileRecordId("TimecardDetail", deviceId, username),    // MobileRecordId
                                "",                                                                     // FunctionalGroupName
                                orgName,                                                                // organizationName
                                orgNumber,                                                              // organizationNumber
                                orgName,                                                                // Company
                                "",                                                                     // Customer:Job
                                l.getFirstName(),                                                       // First Name
                                l.getLastName(),                                                        // Last Name
                                i.getHoursDifferenceStr(),                                              // Hours
                                i.getStartDateStr(),                                                    // Start Date
                                i.getStartTimeStr(),                                                    // Start time
                                i.getEndDateStr(),                                                      // End Date
                                i.getEndTimeStr(),                                                      // End Time
                                "",                                                                     // Task Detail
                                "Break",                                                                // Task Name
                                "101",                                                                  // Task number
                                l.getRecordId(),                                                        // Worker Id
                                "no",                                                                   // Billable
                                "",                                                                     // CustomerRecordId
                                "",                                                                     // Customer Billed Status
                                "timecarddetail",                                                       // itemType
                                "",                                                                     // Vendor Billed Status
                                "",                                                                     // Employee Bill Status
                                manager.getRecordId(),                                                  // CreatorRecordId
                                "0",                                                                    // Overtime
                                "",                                                                     // Matter Number
                                "",                                                                     // Matter Description
                                "",                                                                     // Description
                                "",                                                                     // Amount
                                "",                                                                     // Piece Rate
                                "",                                                                     // Piece Quantity
                                "",                                                                     // Location
                                "",                                                                     // Labor Group Name
                                "",                                                                     // Labor Group Number
                                "0",                                                                    // Overtime Hours
                                "0",                                                                    // Double Overtime Hours
                                "",                                                                     // Straight Time Hours
                                "",                                                                     // Sick Time Hours
                                "",                                                                     // Holiday Time Hours
                                "",                                                                     // Vacation Time Hours
                                i.getHoursDifferenceStr(),                                              // Break Time Hours
                                "",                                                                     // Weight
                                l.getEmployeeId(),                                                      // Employee Id
                                "",                                                                     // Billing Code
                                "",                                                                     // Billing Factor
                                "new",                                                                  // Status
                                "",                                                                     // Processed
                        });
                    }
                });

                return row + (!TextUtils.isNullOrWhitespaces(lines) ? CsvRowNeedle + lines : "") + (!TextUtils.isNullOrWhitespaces(lines2) ? CsvRowNeedle + lines2 : "");
            }
        });

        Log.d(BusinessRules.TAG, ">>>" + postBody);

        return HttpClient.postFile(getUrl() + "/Image2000/rest/timecardservice/setTimecards/" + Rms.getUsername() + "/" + Rms.getPassword(),
            "text/plain", "setTimecards.txt", postBody.getBytes());
    }

    public static String getUserInfo(String username, String password) throws IOException {
        //https://www.rcofox.com/Image2000/rest/securityservice/getUserInfo/hc7/hc7
        return HttpClient.get(Rms.getUrl() + "/Image2000/rest/securityservice/getUserInfo/" + username + "/" + password);
    }

    // MobileRecordID management

    public static String getMobileRecordId(String methodPrefix, String deviceId, String username) {
        return getMobileRecordId(methodPrefix, deviceId, username, true);
    }

    public static String getMobileRecordId(String methodPrefix, String deviceId, String username, boolean onOff) {
        String uniqueId = Long.toString(new Date().getTime());
        return getMobileRecordId(methodPrefix, deviceId, username, true, uniqueId);
    }

    public static String getMobileRecordId(String methodPrefix, String deviceId, String username, String uniqueId) {
        return getMobileRecordId(methodPrefix, deviceId, username, true, uniqueId);
    }

    public static String getMobileRecordId(String methodPrefix, String deviceId, String username, boolean onOff, String uniqueId) {
        final String needle = "-";
        return methodPrefix + needle + "Android_" + deviceId + needle + username + needle + (onOff ? "ON" : "OFF") + needle + uniqueId;
    }

    // URL

    public static String getUrl() {
        return url;
    }

    public static void setUrl(String value) {
        url = value;
    }

    public static String getUsername() {
        return Rms.username;
    }

    public static void setUsernamePasswordIdentifier(String username, String password, long identifier) {
        Rms.username = username;
        Rms.password = password;

        Rms.userId = identifier;
    }

    public static long getUserId() {
        return userId;
    }

    public static String getPassword() {
        return password;
    }

    public static String getOrgName() {
        return orgName;
    }

    public static String getOrgNumber() {
        return orgNumber;
    }

    // Communication helpers

    private final static String CsvFileName = "fields.txt";
    private final static char CsvColNeedle = ',';
    private final static char CsvRowNeedle = '\n';
    private final static char CsvQuote = '"';

    private static String getRmsUrl(String servicename, String callname, String variable) {
        return getRmsUrl(servicename, callname, new String[]{variable});
    }

    private static String getRmsUrl(String servicename, String callname, String[] variables) {
        String tmpStr = url + "/Image2000/rest/" + servicename + "/" + callname + "/" +
                username + "/" + password;

        return getRmsUrl(servicename, callname, variables, username, password);
    }

    private static String getRmsUrl(String servicename, String callname, String[] variables, String username, String password) {
        String tmpStr = url + "/Image2000/rest/" + servicename + "/" + callname + "/" + username + "/" + password;
        return variables == null || variables.length <= 0 ? tmpStr : tmpStr + "/" + encodeVariables(variables);
    }

    private static String getRmsUrlTest(String servicename, String callname, String[] variables) {
        String tmpStr = url + "/Image2000/rest/" + servicename + "/" + callname + "/" + "ann" + "/" + "ann";
        return variables == null || variables.length <= 0 ? tmpStr : tmpStr + "/" + encodeVariables(variables);
    }

    public static <T> String serializeListAsCsvPost(List<T> list, IPostParser<T> p) {
        StringBuilder result = new StringBuilder();

        for (T item : list)
            result.append(p.parse(item)).append(CsvRowNeedle);

        return TextUtils.trimEnd(result.toString(), String.valueOf(CsvRowNeedle));
    }

    public static String serializeItemAsCsvPostLine(String[] values) {
        StringBuilder result = new StringBuilder();

        for (String v : values) {
            String field = parseSpecialChars(v);
            result.append(CsvQuote).append(field != null ? field : "").append(CsvQuote).append(CsvColNeedle);
        }

        return TextUtils.trimEnd(result.toString(), String.valueOf(CsvColNeedle));
    }

    private static String parseSpecialChars(String content) {
        if (content == null)
            return null;

        return TextUtils.replace(TextUtils.replace(TextUtils.replace(content, "%", "\\x11"),
            "\"", "\\x22"), "\r\n", "\\x0A0D");
    }

    public interface IPostParser<T> {
        public String parse(T values);
    }

    // Generic helpers

    private static byte[] toByteArray(int[] values) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        for (int i = 0; i < values.length; ++i)
            dos.writeInt(values[i]);

        return baos.toByteArray();
    }

    private static PairList toCodingInfo(JSONObject item) throws JSONException {
        PairList result = new PairList();
        JSONArray fields = item.getJSONArray("arCodingInfo");

        if (fields != null)
            for (int i = 0; i < fields.length(); i++) {
                JSONObject pair = fields.getJSONObject(i);

                String displayName = pair.getString("displayName");
                String value = pair.getString("value");

                result.add(new Pair(displayName, value));
            }

        return result;
    }

    private static String encodeVariables(String[] variables) {
        String variablesStr = "";

        if (variables != null && variables.length > 0)
            for (int i = 0; i < variables.length; i++)
                try {
                    String temp = variables[i];
                    if (temp == null || temp.trim().length() == 0) {
                        variablesStr += "%20/";
                    } else if (temp.trim().equals("+")) {
                        variablesStr += "+/";
                    } else {
                        variablesStr += URLEncoder.encode(temp.trim()) + "/";
                    }

//					BusinessRules.logVerbose("encoded"+variables[i]);
                } catch (Exception e) {
                    //Log.d(TAG,"cannot encode " + variables[i]);
                }

//		if (variablesStr.charAt(variablesStr.length()-1) == '/')
//		{
//			variablesStr = variablesStr.substring(0, variablesStr.length()-1);
//		}
        return variablesStr;
    }

    public static byte[] toByteArray(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int reads = is.read();

        while (reads != -1) {
            baos.write(reads);
            reads = is.read();
        }

        return baos.toByteArray();
    }

    // JSON helpers

    public static PairList parseJsonCodingFields(JSONObject item) throws JSONException {
        if (item == null)
            return null;

        if (item.has("mapCodingInfo")) {
            return parseMap(item);
        } else if (item.has("arCodingInfo")) {
            return parseAr(item);
        } else if (item.has("LobjectId") || item.has("objectId")) {
            PairList result = new PairList();
            parseCommon(item, result);

            return result;
        }

        throw new JSONException("Invalid JSON element! " + (item != null ? item.toString() : "null item"));
    }

    private static PairList parseAr(JSONObject item) throws JSONException {
        PairList result = new PairList();
        parseCommon(item, result);
        JSONArray fields = item.getJSONArray("arCodingInfo");

        if (fields == null)
            return result;

        for (int i=0; i<fields.length(); i++) {
            JSONObject pair = fields.getJSONObject(i);

            String displayName = pair.getString("displayName");
            String value = pair.getString("value");

            result.add(new Pair(displayName, value));
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    private static void parseCommon(JSONObject item, PairList result) throws JSONException {
        if (item == null || result == null)
            return;

        Iterator<String> keys = item.keys();

        while (keys.hasNext()) {
            String key = keys.next();
            result.add(key, item.getString(key));
        }
    }

    @SuppressWarnings("unchecked")
    private static PairList parseMap(JSONObject item) throws JSONException {
        PairList result = new PairList();

        parseCommon(item, result);
        JSONObject fields = item.getJSONObject("mapCodingInfo");
        Map<String, String> map = new HashMap<String, String>();
        Iterator<String> keys = fields.keys();

        while (keys.hasNext()) {
            String key = keys.next();
            map.put(key, fields.getString(key));
        }

        result.setMap(map, false);
        return result;
    }

    /*public static PairList getRecordCoding(ObjectInfo o) throws Exception {
        String response = getFromServer(getRmsUrl("recordservice", "getRecordCoding",
            new String[]{ String.valueOf(o.getLobjectId()), o.getObjectType() }));

        if (response == null || com.rco.mobileoffice.utils.TextUtils.isNullOrWhitespaces(response))
            return null;

        JSONArray jsonArray = new JSONArray(response);

        if (jsonArray == null || jsonArray.length() == 0)
            return null;

        return new PairList(jsonArray);
    }*/

    /*public static ObjectInfo setRecordCodingFields(ObjectInfo object) throws Exception {
        if (object != null) {
            List<ObjectInfo> list = new Vector<ObjectInfo>();
            list.add(object);
            List<ObjectInfo> temp = setRecordCodingFields(list);
            if (temp != null && temp.size() > 0)
                return temp.get(0);

        }
        return null;
    }*/

    /*public static List<ObjectInfo> setRecordCodingFields(List<? extends ObjectInfo> objects) throws Exception {
        if (objects == null) return null;
        StringBuilder wholeFile = new StringBuilder();
        for (ObjectInfo object : objects) {
            StringBuilder oneLine = new StringBuilder();
            PairList pl = object.getAsPairList();
            pl.remove("LobjectId");
            pl.remove("objectType");
            oneLine.append("\"LobjectId\",\"" + object.getLobjectId() + "\",");
            oneLine.append("\"objectType\",\"" + object.getObjectType() + "\",");
//			if (FORCE_ORG_TO_MATCH)
//			{
//				pl.remove("Organization Number");
//				pl.remove("Organization Name");
//				oneLine.append("\"Organization Number\",\"" + orgNumber + "\",");
//				oneLine.append("\"Organization Name\",\"" + orgName + "\",");
//			}
            List<Pair> pairs = pl.toList();
            for (Pair p : pairs)
                oneLine.append("\"" + p.Key + "\",\"" + p.Value + "\",");

            oneLine.append("\n");
            wholeFile.append(oneLine.toString());
        }

        String csvContentStr = wholeFile.toString();
        Logger.logDebug("FILECONTENTS = " + csvContentStr);

        String response = postToServer(getRmsUrl("recordservice", "setRecordCodingFields",
                new String[]{}), "data.csv", csvContentStr.getBytes(), null);

        if (com.rco.mobileoffice.utils.TextUtils.isNullOrWhitespaces(response))
            return null;

        JSONArray jsonArray = new JSONArray(response);
        List<ObjectInfo> result = new Vector<ObjectInfo>();

        for (int i = 0; i < jsonArray.length(); i++)
            result.add(new ObjectInfo(parseJsonCodingFields(jsonArray.getJSONObject(i))));

        return result;
    }*/

    /*public static String uploadeEfileContent(String serviceName, String callName, String[] parameters, String mimetype, String filename, byte[] data) throws Exception {
        String response = postToServer(getRmsUrl(serviceName, callName, parameters), filename, data, null);

        if (com.rco.mobileoffice.utils.TextUtils.isNullOrWhitespaces(response))
            return null;

        return response;
    }*/

    /*private static String uploadCsvContent(String serviceName, String callName, String content) throws Exception {
        return uploadCsvContent(serviceName, callName, new String[]{}, content);
    }*/

    /*public static String uploadCsvContent(String serviceName, String callName, String[] parameters, String content) throws Exception {
        String response;

        try {
            response = postToServer(getRmsUrl(serviceName, callName, parameters), "data.csv", content.getBytes(), null);

            if (com.rco.mobileoffice.utils.TextUtils.isNullOrWhitespaces(response))
                return null;

            return response;
        } catch (Exception ex) {
            throw ex;
        } catch (Exception ex) {
            Log.w(TAG,"ERROR WITH " + content);
            throw new Exception(ex.getMessage());
        }
    }*/

    /*public static String setUserPassword(long objectIdUser, String objectTypeUser, String newPassword) throws Exception {
        //{webserver}/userservice/setUserPassword/{login}/{password}/{objectIdUser}/{objectTypeUser}/{newPassword}/

        String response = getFromServer(getRmsUrl("userservice", "setUserPassword",
                new String[]{String.valueOf(objectIdUser), objectTypeUser, newPassword}));

        if (com.rco.mobileoffice.utils.TextUtils.contains(response, "login failed", false))
            throw new Exception();

        return response;
    }*/
}
