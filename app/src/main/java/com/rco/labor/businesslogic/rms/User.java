package com.rco.labor.businesslogic.rms;

import com.rco.labor.businesslogic.PairList;
import com.rco.labor.utils.TextUtils;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class User extends ObjectInfo {
    private String FirstName;
    private String LastName;
    private String DeviceId;
    private String EmployeeId;
    private String Company;
    private String RmsUserId;
    private String Email;

    private String itemType;

    private String address1;
    private String address2;
    private String city;
    private String state;
    private String zipcode;

    private String orgName;
    private String orgNumber;

    private String functionalGroupName;
    private String userGroupName;
    private String role;
    private String login;
    private String password;
    private String title;
    private String country;
    private String telephone;
    private String latitude;
    private String longitude;
    private String homeURL;
    private String fax;
    private String directoryName;
    private String clientLogon;
    private String clientPassword;
    private boolean isSyncedUp;
    private String itemDiscount;
    private String serviceDiscount;
    private String jobType;
    private String reference;
    private String salesRep;
    private String driversLicenseNumber;
    private String driversLicenseState;
    private String location;
    private String customerNumber;
    private String dateOfHire;
    private String dateOfBirth;
    private String metricSystem;
    private String truckRule;
    private String status;
    private String setCodingTimestamp;
    private String truckCycleDay;
    private String drivingModeList;
    private String authenticationCode;
    private int cmvOrderNumber;
    private String truckShiftStartDate;
    private String truckShiftStartTime;
    private String truckShiftEndDate;
    private String truckShiftEndTime;
    public PairList extras = new PairList();

    Set<UserRight> userRights = new TreeSet<UserRight>();

    private List<User> timecardSubmitters;
    private Boolean isManager;

    public enum DrivingMode { Personal, YardMoves, None }

    // Construction

    public User() {
        //type = CONTENT_TYPE.IMAGE;

        setLobjectId(0);
        parentKeyNameForParent = "LobjectId";
        parentKeyNameForChild = null;

        //this.mobileRecordId = BusinessRules.getMobileIdentifier("user", null);

        this.functionalGroupName = "";
        this.userGroupName = "";
        this.role = "";
        this.login = "";
        this.password = "";
        this.LastName = "";
        this.FirstName = "";
        this.title = "";
        this.Company = "";
        this.address1 = "";
        this.address2 = "";
        this.city = "";
        this.state = "";
        this.zipcode = "";
        //this.country = Constant.UNITED_STATES;

        this.Email = "";

        this.telephone = "";
        this.orgName = "";
        this.orgNumber = "";
        this.itemType = "";
        this.latitude = "";
        this.longitude = "";
        this.recordId = "";
        this.RmsUserId = "";
        this.homeURL = "";
        this.fax = "";
        this.directoryName = "";
        this.clientLogon = "";
        this.clientPassword = "";
        this.DeviceId = "";
        this.itemDiscount = "0";
        this.serviceDiscount = "0";
        this.isSyncedUp = false;
        this.jobType = "";
        this.reference = "";
        this.salesRep = "";
        this.metricSystem = "false";
        this.truckRule = "";
        this.truckCycleDay = "";
        this.status = "";
        this.setCodingTimestamp = "";
        this.drivingModeList = "";
        this.cmvOrderNumber = 1;
        this.truckShiftStartDate = "";
        this.truckShiftStartTime = "";
        this.truckShiftEndDate = "";
        this.truckShiftEndTime = "";
    }

    public User(PairList codingFields) {
        this();

        setFromList(codingFields);
    }

    public User(User user) {
        this();
        copyAllFields(user);
    }

    @Override
    public void setFromList(PairList pairList) {
        if (pairList == null)
            return;

        super.setFromList(pairList);

        recordId = pairList.getValue("RecordId");
        FirstName = pairList.getValue("First Name");
        LastName = pairList.getValue("Last Name");
        Company = pairList.getValue("Company");
        address1 = pairList.getValue("Address1");
        address2 = pairList.getValue("Address2");
        city = pairList.getValue("City");
        state = pairList.getValue("State");
        zipcode = pairList.getValue("ZipCode");
        orgName = pairList.getValue("Organization Name");
        orgNumber = pairList.getValue("Organization Number");
        latitude = pairList.getValue("Latitude");
        longitude = pairList.getValue("Longitude");
        role = pairList.getValue("roles");
        Email = pairList.getValue("Email");
        country = pairList.getValue("Country");
        title = pairList.getValue("Title");
        password = pairList.getValue("password");
        login = pairList.getValue("loginId");
        functionalGroupName = pairList.getValue("FunctionalGroupName");
        mobileRecordId = pairList.getValue("MobileRecordId");
        fax = pairList.getValue("Fax");
        telephone = pairList.getValue("Telephone");
        itemType = pairList.getValue("ItemType");
        RmsUserId = pairList.getValue("RMS User Id");
        homeURL = pairList.getValue("Home URL");
        itemDiscount = pairList.getValue("Item Discount");
        serviceDiscount = pairList.getValue("Service Discount");
        DeviceId = pairList.getValue("Device ID");
        directoryName = pairList.getValue("Directory Name");
        jobType = pairList.getValue("Job Type");
        reference = pairList.getValue("Reference");
        salesRep = pairList.getValue("SalesRep");
        driversLicenseNumber = pairList.getValue("Driver License Number");
        driversLicenseState = pairList.getValue("Driver License State");
        location = pairList.getValue("Location");
        customerNumber = pairList.getValue("Customer Number");
        dateOfHire = pairList.getValue("Date of Hire");
        dateOfBirth = pairList.getValue("Date of Birth");
        metricSystem = pairList.getValue("Metric System");
        truckRule = pairList.getValue("Truck Rule");
        truckCycleDay = pairList.getValue("Truck Cycle Day");
        status = pairList.getValue("Status");
        setCodingTimestamp = pairList.getValue("SetCodingTimestamp");
        drivingModeList = pairList.getValue("Driver Duty Status");
        truckShiftStartDate = pairList.getValue("Truck Shift Start Date");
        truckShiftStartTime = pairList.getValue("Truck Shift Start Time");
        truckShiftEndDate = pairList.getValue("Truck Shift End Date");
        truckShiftEndTime = pairList.getValue("Truck Shift End Time");
        EmployeeId = pairList.getValue("Employee Id");

        extras.add("MobilePhone", pairList.getValue("MobilePhone"));
        extras.add("Region Name", pairList.getValue("Region Name"));
        extras.add("Region Number", pairList.getValue("Region Number"));
        extras.add("Office Name", pairList.getValue("Office Name"));
        extras.add("Office Number", pairList.getValue("Office Number"));
    }

    public PairList getAsPairList() {
        PairList pl = super.getAsPairList();

        pl.add("RecordId", getRecordId());
        pl.add("First Name", getFirstName());
        pl.add("Last Name", getLastName());
        pl.add("Company", getCompany());
        pl.add("Address1", getAddress1());
        pl.add("Address2", getAddress2());
        pl.add("City", getCity());
        pl.add("State", getState());
        pl.add("ZipCode", getZipCode());
        pl.add("Organization Name", getOrgName());
        pl.add("Organization Number", getOrgNumber());
        pl.add("Latitude", getLatitude());
        pl.add("Longitude", getLongitude());
        pl.add("roles", getRole());
        pl.add("Email", getEmail());
        pl.add("Country", getCountry());
        pl.add("Title", getTitle());
        pl.add("loginId", getLogin());
        pl.add("FunctionalGroupName", getFunctionalGroupName());
        pl.add("MobileRecordId", getMobileRecordId());
        pl.add("Fax", getFax());
        pl.add("Telephone", getTelephone());
        pl.add("ItemType", getItemType());
        pl.add("RMS User Id", getRmsUserId());
        pl.add("Home URL", getHomeURL());
        pl.add("Item Discount", getItemDiscount());
        pl.add("Service Discount", getServiceDiscount());
        pl.add("Device ID", getDeviceId());
        pl.add("Directory Name", getDirectoryName());
        pl.add("Job Type", getJobType());
        pl.add("Reference", getReference());
        pl.add("SalesRep", getSalesRep());
        pl.add("Metric System", getMetricSystem());

        pl.add("Truck Rule", getTruckRule());
        pl.add("Truck Cycle Day", getTruckCycleDay());
        pl.add("Status", getStatus());
        pl.add("SetCodingTimestamp", getSetCodingTimestamp());
        pl.add("Truck Shift Start Date", getTruckShiftStartDate());
        pl.add("Truck Shift Start Time", getTruckShiftStartTime());
        pl.add("Truck Shift End Date", getTruckShiftEndDate());
        pl.add("Truck Shift End Time", getTruckShiftEndTime());

        return pl;
    }

    public void copyAllFields(User user) {
        setLobjectId(user.getLobjectId());

        this.recordId = user.getRecordId();
        this.FirstName = user.getFirstName();
        this.LastName = user.getLastName();
        this.Company = user.getCompany();
        this.RmsUserId = user.getRmsUserId();
        this.itemType = user.getItemType();
        this.address1 = user.getAddress1();
        this.address2 = user.getAddress2();
        this.city = user.getCity();
        this.state = user.getState();
        this.zipcode = user.getZipCode();
        this.homeURL = user.getHomeURL();
        this.telephone = user.getTelephone();
        this.fax = user.getFax();
        this.orgName = user.getOrgName();
        this.mobileRecordId = user.getMobileRecordId();
        this.functionalGroupName = user.getFunctionalGroupName();
        this.userGroupName = user.getUserGroupName();
        this.login = user.getLogin();
        this.password = user.getPassword();
        this.title = user.getTitle();
        this.country = user.getCountry();
        this.Email = user.getEmail();
        this.role = user.getRole();
        this.orgNumber = user.getOrgNumber();
        this.latitude = user.getLatitude();
        this.longitude = user.getLongitude();
        this.DeviceId = user.getDeviceId();
        this.isSyncedUp = user.isSyncedUp();
        this.itemDiscount = user.getItemDiscount();
        this.serviceDiscount = user.getServiceDiscount();
        this.jobType = user.getJobType();
        this.reference = user.getReference();
        this.salesRep = user.getSalesRep();
        this.metricSystem = user.getMetricSystem();

        this.truckCycleDay = user.getTruckCycleDay();
        this.truckRule = user.getTruckRule();
        this.status = user.getStatus();
        this.setCodingTimestamp = user.getSetCodingTimestamp();
        this.drivingModeList = user.getDrivingModeList();
        this.truckShiftStartDate = user.getTruckShiftStartDate();
        this.truckShiftStartTime = user.getTruckShiftStartTime();
        this.truckShiftEndDate = user.getTruckShiftEndDate();
        this.truckShiftEndTime = user.getTruckShiftEndTime();

        //copyBytes(user);
    }

    // Public methods

    public String getDeviceId() {
        return DeviceId;
    }

    public void setDeviceId(String value) {
        DeviceId = value;
    }

    public String getEmployeeId() {
        return EmployeeId;
    }

    public void setEmployeeId(String value) {
        EmployeeId = value;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String value) {
        FirstName = value;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String value) {
        LastName = value;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String value) {
        address1 = value;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String value) {
        address2 = value;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String value) {
        city = value;
    }

    public String getState() {
        return state;
    }

    public void setState(String value) {
        state = value;
    }

    public String getZipCode() {
        return zipcode;
    }

    public void setZipCode(String value) {
        zipcode = value;
    }

    public String getRmsUserId() {
        return RmsUserId;
    }

    public void setRmsUserId(String value) {
        RmsUserId = value;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String value) {
        orgName = value;
    }

    public String getOrgNumber() {
        return orgNumber;
    }

    public void setOrgNumber(String value) {
        orgNumber = value;
    }

    public String getItemType() {
        return this.itemType;
    }

    public void setItemType(String value) {
        this.itemType = value;
    }

    public String getHomeURL() {
        return homeURL;
    }

    public void setHomeURL(String homeURL) {
        this.homeURL = homeURL;
    }

    public String getTelephone() {
        return this.telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getFax() {
        return this.fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getDirectoryName() {
        return this.directoryName;
    }

    public void setDirectoryName(String directoryName) {
        this.directoryName= directoryName;
    }

    public String getClientLogon() {
        return this.clientLogon;
    }

    public void setClientLogon(String clientLogon) {
        this.clientLogon = clientLogon;
    }

    public String getClientPassword() {
        return this.clientPassword;
    }

    public void setClientPassword(String clientPassword) {
        this.clientPassword = clientPassword;
    }

    public String getRole() {
        return "";
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getFunctionalGroupName() {
        return this.functionalGroupName;
    }

    public void setFunctionalGroupName(String value) {
        this.functionalGroupName = value;
    }

    public String getUserGroupName() {
        return this.userGroupName;
    }

    public void setUserGroupName(String value) {
        this.userGroupName = value;
    }

    public String getLogin() {
        return this.login;
    }

    public void setLogin(String value) {
        this.login = value;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String value) {
        this.password = value;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String value) {
        this.title = value;
    }

    public String getCountry() {
        return this.country;
    }

    public void setCountry(String value) {
        this.country = value;
    }

    public String getEmail() {
        return this.Email;
    }

    public void setEmail(String value) {
        this.Email = value;
    }

    public String getLatitude() {
        return this.latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return this.longitude;
    }

    /*public LatLng getLatLng() {
        try {
            double lat = Double.parseDouble(this.getLatitude());
            double lon = Double.parseDouble(this.getLongitude());

            return new LatLng(lat, lon);
        } catch (Exception ex) {

        }

        return null;
    }*/

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getCompany() {
        return Company;
    }

    public void setCompany(String value) {
        this.Company = value;
    }

    // Helpers

    public boolean isItemType(String value) {
        return !TextUtils.isNullOrWhitespaces(itemType) && itemType.compareToIgnoreCase(value) == 0;
    }

    public String getCityStateZip() {
        String cityStateZip = (getCity() + ", " + getState() + " " + getZipCode()).trim();
        return cityStateZip;
    }

    public String getFirstLastName() {
        return (getFirstName() + " " + getLastName()).trim();
    }

    public String getLastFirstName() {
        String needle = ", ";

        if (TextUtils.isNullOrWhitespacesAny(new String[] { getLastName(), getFirstName() }))
            needle = "";

        return (getLastName() + needle + getFirstName()).trim();
    }

    public String getFullAddress() {
        return getAddress1() + ", " + getCity() + ", " + getState() + " " + getZipCode();
    }

    public String getFullQualifiedName() {
        String needle = "";

        if (!com.rco.labor.utils.TextUtils.isNullOrWhitespaces(Company) &&
                !com.rco.labor.utils.TextUtils.isNullOrWhitespaces(getFirstLastName()))
            needle = ",";

        return RmsUserId + " - " + Company + needle + " " + getFirstLastName();
    }

    public String getLabel() {
        return getRecordId() + " - " + getLastFirstName();
    }

    public Collection<UserRight> getUserRights() {
        return userRights;
    }

    public void setUserRights(Collection<UserRight> userRights) {
        if (userRights != null)
        {
            this.userRights.clear();
            this.userRights.addAll(userRights);
        }
    }

    public boolean hasUserRight(String name) {
        UserRight tempRight = new UserRight(name);
        return this.userRights.contains(tempRight);
    }

    public void addUserRights(Collection<String> newRights) {
        if (newRights != null) {
            for (String string : newRights) {
                // BusinessRules.logVerbose(login+ " has right for command = "+string);
                UserRight newRight = new UserRight(string);
                newRight.setUserId(this.getLobjectId());
                this.userRights.add(newRight);
            }
        }
    }

    /*public boolean isTechnician() {
        return itemType != null && itemType.equalsIgnoreCase(Constant.USER_TYPE_TECHNICIAN);
    }*/

    public boolean isSyncedUp() {
        return isSyncedUp;
    }

    public void setSyncedUp(boolean isSyncedUp) {
        this.isSyncedUp = isSyncedUp;
    }

    public String getItemDiscount() {
        return itemDiscount;
    }

    public void setItemDiscount(String itemDiscount) {
        this.itemDiscount = itemDiscount;
    }

    public String getServiceDiscount() {
        return serviceDiscount;
    }

    public void setServiceDiscount(String serviceDiscount) {
        this.serviceDiscount = serviceDiscount;
    }

    public boolean isCustomer()
    {
        return itemType != null && itemType.equalsIgnoreCase("client");
    }

    public boolean isVendor()
    {
        return itemType != null && itemType.equalsIgnoreCase("vendor");
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public String getJobType() {
        return jobType;
    }

    public void setJobType(String jobType) {
        this.jobType = jobType;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getSalesRep() {
        return salesRep;
    }

    public void setSalesRep(String salesRep) {
        this.salesRep = salesRep;
    }

    public boolean isSalesRep()
    {
        return salesRep != null && salesRep.equalsIgnoreCase("true");
    }

    public boolean isLabor() {
        return itemType != null && itemType.trim().toLowerCase().equalsIgnoreCase("labor");
    }

    public String getDriversLicenseNumber() {
        return driversLicenseNumber;
    }

    public void setDriversLicenseNumber(String driversLicenseNumber) {
        this.driversLicenseNumber = driversLicenseNumber;
    }

    public String getDriversLicenseState() {
        return driversLicenseState;
    }

    public void setDriversLicenseState(String driversLicenseState) {
        this.driversLicenseState = driversLicenseState;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCustomerNumber() {
        return customerNumber;
    }

    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
    }

    public String getDateOfHire() {
        return dateOfHire;
    }

    public void setDateOfHire(String dateOfHire) {
        this.dateOfHire = dateOfHire;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getMetricSystem() {
        return metricSystem;
    }

    public void setMetricSystem(String metricSystem) {
        this.metricSystem = metricSystem;
    }

    public boolean usesMetricSystem() {
        return Boolean.parseBoolean(metricSystem);
    }

    public String getTruckRule() {
        return truckRule;
    }

    public void setTruckRule(String truckRule) {
        this.truckRule = truckRule;
    }

    public String getTruckCycleDay() {
        return truckCycleDay;
    }

    public void setTruckCycleDay(String truckCycleDay) {
        this.truckCycleDay = truckCycleDay;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSetCodingTimestamp() {
        return setCodingTimestamp;
    }

    public void setSetCodingTimestamp(String setCodingTimestamp) {
        this.setCodingTimestamp = setCodingTimestamp;
    }

    /*public boolean existsValidLatLng() {
        return getLatLng() != null;
    }*/

    public String getFullAddressTrimed() {
        String fullAddress = (getAddress1() + ", " + getCity() + ", " + getState() + " " + getZipCode()).trim();
        return fullAddress.compareToIgnoreCase(",") == 0 ? "" : TextUtils.trim(fullAddress, ",");
    }

    public String getAvailablePhone() {
        return getTelephone();
    }

    public List<User> getTimecardSubmitters() {
        return timecardSubmitters;
    }

    public void setTimecardSubmitters(List<User> timecardSubmitters) {
        this.timecardSubmitters = timecardSubmitters;
    }

    /*@Override
    public void setParentKeyValue(String value)
    {
        setLobjectId(Utils.getLongValue(value));
    }*/

    @Override
    public String getParentKeyValue() {
        return ""+getLobjectId();
    }

    public String getDrivingModeList() {
        return drivingModeList;
    }

    public void setDrivingModeList(String drivingModeList) {
        this.drivingModeList = drivingModeList;
    }

    public boolean hasDrivingMode(DrivingMode drivingMode) {
        boolean flag = false;
        if (drivingModeList != null && drivingMode != null)
        {
            String temp = drivingModeList.toString().replace(" ", "");
            if (temp.contains(drivingMode.toString())) {
                flag = true;
            }
        }
        return flag;
    }

    public String getAuthenticationCode() {
        return authenticationCode;
    }

    public void setAuthenticationCode(String authenticationCode) {
        this.authenticationCode = authenticationCode;
    }

    public int getCmvOrderNumber() {
        return cmvOrderNumber;
    }

    public void setCmvOrderNumber(int cmvOrderNumber) {
        this.cmvOrderNumber = cmvOrderNumber;
    }

    public String getTruckShiftStartDate() {
        return truckShiftStartDate;
    }

    public void setTruckShiftStartDate(String truckShiftStartDate) {
        this.truckShiftStartDate = truckShiftStartDate;
    }

    public String getTruckShiftStartTime() {
        return truckShiftStartTime;
    }

    public void setTruckShiftStartTime(String truckShiftStartTime) {
        this.truckShiftStartTime = truckShiftStartTime;
    }

    public String getTruckShiftEndDate() {
        return truckShiftEndDate;
    }

    public void setTruckShiftEndDate(String truckShiftEndDate) {
        this.truckShiftEndDate = truckShiftEndDate;
    }

    public String getTruckShiftEndTime() {
        return truckShiftEndTime;
    }

    public void setTruckShiftEndTime(String truckShiftEndTime) {
        this.truckShiftEndTime = truckShiftEndTime;
    }

    @Override
    public String toString() {
        return
            "User {" +
                "FirstName='" + FirstName + '\'' +
                ", LastName='" + LastName + '\'' +
                ", DeviceId='" + DeviceId + '\'' +
                ", Company='" + Company + '\'' +
                ", RmsUserId='" + RmsUserId + '\'' +
                ", itemType='" + itemType + '\'' +
                ", address1='" + address1 + '\'' +
                ", address2='" + address2 + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", zipcode='" + zipcode + '\'' +
                ", orgName='" + orgName + '\'' +
                ", orgNumber='" + orgNumber + '\'' +
                ", functionalGroupName='" + functionalGroupName + '\'' +
                ", userGroupName='" + userGroupName + '\'' +
                ", role='" + role + '\'' +
                ", login='" + login + '\'' +
                ", password='" + password + '\'' +
                ", title='" + title + '\'' +
                ", country='" + country + '\'' +
                ", email='" + Email + '\'' +
                ", telephone='" + telephone + '\'' +
                ", latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                ", homeURL='" + homeURL + '\'' +
                ", fax='" + fax + '\'' +
                ", directoryName='" + directoryName + '\'' +
                ", clientLogon='" + clientLogon + '\'' +
                ", clientPassword='" + clientPassword + '\'' +
                ", isSyncedUp=" + isSyncedUp +
                ", itemDiscount='" + itemDiscount + '\'' +
                ", serviceDiscount='" + serviceDiscount + '\'' +
                ", jobType='" + jobType + '\'' +
                ", reference='" + reference + '\'' +
                ", salesRep='" + salesRep + '\'' +
                ", driversLicenseNumber='" + driversLicenseNumber + '\'' +
                ", driversLicenseState='" + driversLicenseState + '\'' +
                ", location='" + location + '\'' +
                ", customerNumber='" + customerNumber + '\'' +
                ", dateOfHire='" + dateOfHire + '\'' +
                ", dateOfBirth='" + dateOfBirth + '\'' +
                ", metricSystem='" + metricSystem + '\'' +
                ", truckRule='" + truckRule + '\'' +
                ", status='" + status + '\'' +
                ", setCodingTimestamp='" + setCodingTimestamp + '\'' +
                ", truckCycleDay='" + truckCycleDay + '\'' +
                ", drivingModeList=" + drivingModeList +
                ", extras=" + extras +
                ", userRights=" + userRights +
                ", isManager=" + isManager +
                ", timecardSubmitters=" + timecardSubmitters +
            '}';
    }
}