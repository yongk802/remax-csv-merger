package com.yong.remax;

import java.util.List;

/**
 * Created by yongkim on 7/29/15.
 */
public class RemaxLine {

    private String companyName;
    private String address1;
    private String address2;
    private String address3;
    private String address4;
    private String city;
    private String state;
    private List areaList;
    private int zipCode;
    private String country;
    private String regionId;
    private String officeId;
    private String officeType;
    private List phoneNumberList;
    private List faxNumberList;
    private String email;
    private String uri;
    private int agents;
    private String contactName;
    private String contactType;
    private String nameSuffix;

    public String getCompanyName() {
        return companyName;
    }

    public RemaxLine setCompanyName(String companyName) {
        this.companyName = companyName;
        return this;
    }

    public String getAddress1() {
        return address1;
    }

    public RemaxLine setAddress1(String address1) {
        this.address1 = address1;
        return this;
    }

    public String getAddress2() {
        return address2;
    }

    public RemaxLine setAddress2(String address2) {
        this.address2 = address2;
        return this;
    }

    public String getAddress3() {
        return address3;
    }

    public RemaxLine setAddress3(String address3) {
        this.address3 = address3;
        return this;
    }

    public String getAddress4() {
        return address4;
    }

    public RemaxLine setAddress4(String address4) {
        this.address4 = address4;
        return this;
    }

    public String getCity() {
        return city;
    }

    public RemaxLine setCity(String city) {
        this.city = city;
        return this;
    }

    public String getState() {
        return state;
    }

    public RemaxLine setState(String state) {
        this.state = state;
        return this;
    }

    public List getAreaList() {
        return areaList;
    }

    public RemaxLine setAreaList(List areaList) {
        this.areaList = areaList;
        return this;
    }

    public int getZipCode() {
        return zipCode;
    }

    public RemaxLine setZipCode(int zipCode) {
        this.zipCode = zipCode;
        return this;
    }

    public String getCountry() {
        return country;
    }

    public RemaxLine setCountry(String country) {
        this.country = country;
        return this;
    }

    public String getRegionId() {
        return regionId;
    }

    public RemaxLine setRegionId(String regionId) {
        this.regionId = regionId;
        return this;
    }

    public String getOfficeId() {
        return officeId;
    }

    public RemaxLine setOfficeId(String officeId) {
        this.officeId = officeId;
        return this;
    }

    public String getOfficeType() {
        return officeType;
    }

    public RemaxLine setOfficeType(String officeType) {
        this.officeType = officeType;
        return this;
    }

    public List getPhoneNumberList() {
        return phoneNumberList;
    }

    public RemaxLine setPhoneNumbersList(List phoneNumnberList) {
        this.phoneNumberList = phoneNumnberList;
        return this;
    }

    public List getFaxNumberList() {
        return faxNumberList;
    }

    public RemaxLine setFaxNumberList(List faxNumberList) {
        this.faxNumberList = faxNumberList;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public RemaxLine setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getUri() {
        return uri;
    }

    public RemaxLine setUri(String uri) {
        this.uri = uri;
        return this;
    }

    public int getAgents() {
        return agents;
    }

    public RemaxLine setAgents(int agents) {
        this.agents = agents;
        return this;
    }

    public String getContactName() {
        return contactName;
    }

    public RemaxLine setContactName(String contactName) {
        this.contactName = contactName;
        return this;
    }

    public String getContactType() {
        return contactType;
    }

    public RemaxLine setContactType(String contactType) {
        this.contactType = contactType;
        return this;
    }

    public String getNameSuffix() {
        return nameSuffix;
    }

    public RemaxLine setNameSuffix(String nameSuffix) {
        this.nameSuffix = nameSuffix;
        return this;
    }

    public enum ContactType {
        Owner,
        Manager;
    }

}
