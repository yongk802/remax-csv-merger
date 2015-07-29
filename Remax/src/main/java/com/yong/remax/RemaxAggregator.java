package com.yong.remax;

import java.io.*;
import java.util.*;

/**
 * Created by yongkim on 7/29/15.
 */
public class RemaxAggregator {

    // the delimiters used by the csv
    public static String csvDelim = ",";
    // the delimiters used in the "Service Areas" portion of the csv
    public static String areasDelim = ";";

    String headerString = "Office Name" + csvDelim + "Address" + csvDelim + "Address2" + csvDelim + "Address3" + csvDelim + "Address4" + csvDelim + "Office Location" + csvDelim + "State" + csvDelim + "Service Areas" + csvDelim + "Zip" + csvDelim + "Country" + csvDelim + "Region ID" + csvDelim + "Office ID" + csvDelim + "Office Type" + csvDelim + "Phone" + csvDelim + "Fax" + csvDelim + "Email" + csvDelim + "Main Web Site" + csvDelim + "Affiliate Count" + csvDelim + "OwnerManager" + csvDelim + "Title\n";

    public static void main(String[] args) throws Exception {
        RemaxAggregator remaxAggregator = new RemaxAggregator();
        remaxAggregator.run();
    }

    private File getWriteFile() throws Exception {
        // absolute file location for our output file
        File file = new File("/Users/yongkim/Downloads/EveryRemaxEver_out_final.csv");
        if (!file.exists()) {
            file.createNewFile();
        }
        return file;
    }

    private void run() throws Exception {

        File outputFile = getWriteFile();

        // absolute file location for our input file
        String csvFile = "/Users/yongkim/Downloads/SearchResults.csv";

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {

            // read all of the csv into memory
            List<String[]> csvLines = readCsvLines(br);
            // read all of the csv from memory into a list of easily workable objects
            List<RemaxLine> remaxLines = getRemaxLines(csvLines);

            // create our file writers/buffers and write the header line
            FileWriter fw = new FileWriter(outputFile);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(headerString);

            // iterate through each line and do the matching logic
            for (int i = 0; i < remaxLines.size(); i++) {
                // create our iterator
                Iterator<RemaxLine> lineIterator = remaxLines.iterator();

                // check if there's a next line
                if (lineIterator.hasNext()) {
                    // if so, assign it to a variable
                    RemaxLine reline1 = lineIterator.next();
                    // then remove it from the list
                    lineIterator.remove();

                    // if there's another one, we will compare the previously set line it to the next line
                    if (lineIterator.hasNext()) {
                        RemaxLine reline2 = lineIterator.next();

                        RemaxLine lineToWrite;
                        if (doCombine(reline1, reline2)) {
                            // must wait until here to remove the 2nd entry from the iterator
                            // or we will throw away the first match in a case where we don't find a match.
                            // basically, the 2nd iterator will get used as the first iterator in the next pass because it
                            // isn't removed until now.
                            lineIterator.remove();
                            lineToWrite = combine(lineIterator, reline1, reline2);
                        } else {
                            lineToWrite = reline1;
                        }

                        // write each line to the file
                        writeLineToFile(bw, lineToWrite);
                        bw.write("\n");
                    }
                } else {
                    // we are done going through the spreadsheet
                    return;
                }
            }

            // close our streams
            bw.close();
            fw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Done");
    }

    // read an entire csv spreadsheet into a sorted set of lines
    private List<String[]> readCsvLines(BufferedReader br) throws Exception {

        String line;
        List<String[]> csvLines = new ArrayList<>();

        while ((line = br.readLine()) != null) {
            String[] lines = line.split(csvDelim);
            csvLines.add(lines);
        }

        return csvLines;
    }

    // create easily workable objects from the the csv lines
    private List<RemaxLine> getRemaxLines(List<String[]> csvLines) {
        List<RemaxLine> remaxLines = new ArrayList<>();

        csvLines.forEach(line -> remaxLines.add(createRemaxLine(line)));

        // sort by comparing email addresses. null/empty ones will be at the top
        remaxLines.sort((e1, e2) -> {
            String email1 = e1.getEmail();
            String email2 = e2.getEmail();

            if (email1 == null) {
                email1 = "";
            }

            if (email2 == null) {
                email2 = "";
            }

            return email1.trim().compareTo(email2.trim());
        });

        return remaxLines;
    }

    private RemaxLine combine(Iterator<RemaxLine> lineIterator, RemaxLine reline1, RemaxLine reline2) {

        // combine the agents
        int totalAgents = reline1.getAgents() + reline2.getAgents();

        // determine which contact name to use
        String contactName = determineContact(reline1, reline2);

        RemaxLine combinedRemaxLine = new RemaxLine()
                .setAddress1(reline1.getAddress1())
                .setAddress2(reline1.getAddress2())
                .setAddress3(reline1.getAddress3())
                .setAddress4(reline1.getAddress4())
                        // set the agents
                .setAgents(totalAgents)
                .setAreaList(groupAreas(reline1.getAreaList(), reline2.getAreaList()))
                .setOfficeType(reline1.getOfficeType())
                .setCity(reline1.getCity())
                .setCompanyName(reline1.getCompanyName())
                        // set the contact name
                .setContactName(contactName)
                .setEmail(reline1.getEmail())
                .setNameSuffix(reline1.getNameSuffix())
                .setPhoneNumbersList(groupPhoneNumbers(reline1.getPhoneNumberList(), reline2.getPhoneNumberList()))
                .setAddress3(reline1.getAddress3())
                .setState(reline1.getState())
                .setAddress2(reline1.getAddress2())
                .setUri((reline1.getUri() == null ? "" : reline1.getUri()))
                .setZipCode(reline1.getZipCode());

        if (lineIterator.hasNext()) {
            RemaxLine nextLine = lineIterator.next();
            lineIterator.remove();

            if (doCombine(combinedRemaxLine, nextLine)) {
                // recursive call on this method to keep going through each line and adding more agents
                return combine(lineIterator, combinedRemaxLine, nextLine);
            }
        }

        return combinedRemaxLine;
    }

    private String determineContact(RemaxLine reline1, RemaxLine reline2) {
        String contactToReturn = null;

        String email1 = reline1.getEmail();
        String email2 = reline2.getEmail();

        if (!email1.equalsIgnoreCase(email2)) {
            throw new IllegalArgumentException("we should not be using this method unless we have matching emails");
        }

        String contactName1 = reline1.getContactName();
        String contactName2 = reline2.getContactName();

        // Look at the contact names. If one of them is null, then we'll get the non-null one back.
        // That means there's only one choice for the contact name, we will use that one.
        String contactName = chooseNonNullString(contactName1, contactName2);
        if (contactName != null) {
            contactToReturn = contactName;
        } else {
            // otherwise, we need to look at both names
            if (isCloseLastNameEmailMatch(contactName1, email1)) {
                contactToReturn = contactName1;
            } else if (isCloseLastNameEmailMatch(contactName2, email1)) {
                contactToReturn = contactName2;
            }
        }

        return contactToReturn;
    }

    private boolean isCloseLastNameEmailMatch(String contactName, String email) {
        if (contactName == null || contactName.isEmpty()) {
            return false;
        }

        int beginingOflastName = contactName.lastIndexOf(" ");
        String lastName = contactName.substring(beginingOflastName + 1, contactName.length());
        if (email.contains(lastName.toLowerCase())) {
            return true;
        }

        return false;
    }

    // possibly use this as well to get even closer matches
    private boolean isCloseFirstNameEmailMatch(String contactName, String email) {
        if (contactName == null || contactName.isEmpty()) {
            return false;
        }

        int endOfFirstName = contactName.indexOf(" ");
        String firstName = contactName.substring(0, endOfFirstName - 1);
        if (email.contains(firstName.toLowerCase())) {
            return true;
        }

        return false;
    }

    private String chooseNonNullString(String string1, String string2) {
        if (string1 == null || string1.isEmpty()) {
            if (string2 != null && !string2.isEmpty()) {
                return string2;
            }
        }

        if (string2 == null || string2.isEmpty()) {
            if (string1 != null && !string1.isEmpty()) {
                return string1;
            }
        }

        return null;
    }

    // no longer grouping numbers to keep the spreadsheet format the same - maybe in the future if it's asked for.
    // leaving it here, but now this is just to turn a List into a parameterized List of String
    private List<String> groupPhoneNumbers(List phoneNumberList1, List phoneNumberList2) {
        List<String> phoneNumbersList = new ArrayList<>();

        for (int i = 0; i < phoneNumberList1.size(); i++) {
            phoneNumbersList.add((String) phoneNumberList1.get(i));
        }
        // no longer grouping numbers
//        for (int i = 0; i < phoneNumberList2.size(); i++) {
//            phoneNumbersList.add((String) phoneNumberList1.get(i));
//        }

        return phoneNumbersList;
    }

    // group the areas
    private List<String> groupAreas(List list1, List list2) {
        List<String> areaList = new ArrayList<>();

        areaList.addAll(innerGroupAreas(list1));
        areaList.addAll(innerGroupAreas(list2));

        return areaList;
    }

    // add all of the areas together
    private List<String> innerGroupAreas(List list) {
        List<String> areaList = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            String areaString = String.valueOf(list.get(0));
            if (areaString != null) {
                String[] areas = areaString.split(areasDelim);
                if (areas.length > 0) {
                    for (int j = 0; j < areas.length; j++) {
                        areaList.add(areas[j]);
                    }
                }
            }

        }

        return areaList;
    }

    private RemaxLine createRemaxLine(String[] content) {

        RemaxLine remaxLine = new RemaxLine();

        try {
            int i = 0;
            String companyName = content[i++];
            remaxLine.setCompanyName(companyName);
            String address1 = content[i++];
            remaxLine.setAddress1(address1);
            String address2 = content[i++];
            remaxLine.setAddress2(address2);
            String address3 = content[i++];
            remaxLine.setAddress3(address3);
            String address4 = content[i++];
            remaxLine.setAddress4(address4);
            String city = content[i++];
            remaxLine.setCity(city);
            String state = content[i++];
            remaxLine.setState(state);
            List areaList = Arrays.asList(content[i++]);
            remaxLine.setAreaList(areaList);
            int zipCode = Integer.parseInt(content[i++]);
            remaxLine.setZipCode(zipCode);
            String country = content[i++];
            remaxLine.setCountry(country);
            String regionId = content[i++];
            remaxLine.setRegionId(regionId);
            String officeId = content[i++];
            remaxLine.setOfficeId(officeId);
            String officeType = content[i++];
            remaxLine.setOfficeType(officeType);
            List phoneNumberList = Arrays.asList(content[i++]);
            remaxLine.setPhoneNumbersList(phoneNumberList);
            List faxNumberList = Arrays.asList(content[i++]);
            remaxLine.setFaxNumberList(faxNumberList);
            String email = content[i++];
            remaxLine.setEmail(email);
            String uri = content[i++];
            remaxLine.setUri(uri);
            int agents = Integer.parseInt(content[i++]);
            remaxLine.setAgents(agents);
            String contactName = content[i++];
            remaxLine.setContactName(contactName);
            String contactTypeOrNameSuffix = content[i++];
            String contactType = "";
            String nameSuffix = "";
            // sometimes the contact type is an owner/manager, but sometimes a name prefix gets used,
            // which moves everything over one column, so switch these around depending on this logic.
            if (contactTypeOrNameSuffix.equalsIgnoreCase(RemaxLine.ContactType.Manager.name()) ||
                    contactTypeOrNameSuffix.equalsIgnoreCase(RemaxLine.ContactType.Owner.name())) {
                contactType = contactTypeOrNameSuffix;
                remaxLine.setContactType(contactType);
            } else {
                nameSuffix = contactTypeOrNameSuffix;
                contactType = content[i];
                remaxLine.setNameSuffix(nameSuffix);
                remaxLine.setContactType(contactType);
            }

            return remaxLine;

        } catch (Exception e) {
            if (e instanceof ArrayIndexOutOfBoundsException) {
                // it's fine - just a blank or null field. return what we have.
                return remaxLine;
            }
        }

        return remaxLine;
    }

    public boolean doCombine(RemaxLine remaxLine1, RemaxLine remaxLine2) {
        if (remaxLine1.getEmail() == null || remaxLine2.getEmail() == null) {
            return false;
        }

        return remaxLine1.getEmail().equalsIgnoreCase(remaxLine2.getEmail());
    }

    // temporary hack
    private String stripBrackets(List list) {
        if (list == null) {
            return null;
        }
        return list.toString().replaceAll("\\[", "").replaceAll("\\]", "").replaceAll(",", "|");
    }

    public String serializeRemaxLine(RemaxLine reline) {

        List areasList = reline.getAreaList();
        StringBuilder sb = new StringBuilder();
        if (areasList != null) {
            sb.append("[");
            areasList.forEach(area -> {
                sb.append(area.toString());
                sb.append(";");
            });
            sb.append("]");
        }

        String relineString = replaceNull(reline.getCompanyName()) + csvDelim +
                replaceNull(reline.getAddress1()) + csvDelim +
                replaceNull(reline.getAddress2()) + csvDelim +
                replaceNull(reline.getAddress3()) + csvDelim +
                replaceNull(reline.getAddress4()) + csvDelim +
                replaceNull(reline.getCity()) + csvDelim +
                replaceNull(reline.getState()) + csvDelim +
                replaceNull(sb.toString()).replaceAll(";;", "").replaceAll("\\[;\\]", "").replaceAll(";\\]", "\\]") + csvDelim +
                reline.getZipCode() + csvDelim +
                replaceNull(reline.getCountry()) + csvDelim +
                replaceNull(reline.getRegionId()) + csvDelim +
                replaceNull(reline.getOfficeId()) + csvDelim +
                replaceNull(reline.getOfficeType()) + csvDelim +
                replaceNull(stripBrackets(reline.getPhoneNumberList())) + csvDelim +
                replaceNull(stripBrackets(reline.getFaxNumberList())) + csvDelim +
                replaceNull(reline.getEmail()) + csvDelim +
                replaceNull(reline.getUri()) + csvDelim +
                reline.getAgents() + csvDelim +
                replaceNull(reline.getContactName()) + csvDelim +
                replaceNull(reline.getContactType()) + csvDelim +
                replaceNull(reline.getNameSuffix());

        return relineString;
    }

    private String replaceNull(String string) {
        if (string == null || string.isEmpty()) {
            return "";
        }

        return string;
    }

    public void writeLineToFile(BufferedWriter bw, RemaxLine remaxLine) throws Exception {
        bw.write(serializeRemaxLine(remaxLine));
    }

}
