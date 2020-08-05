import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.time.*;
import java.util.Scanner;

import com.google.gson.*;

class Result {

    public static List<Integer> getRecordsByAgeGroup(int ageStart, int ageEnd, int bpDiff) {
        List<Integer> ids = new ArrayList<>();

        try {
            for (int page = 1; page <= 10; page++) {
                String json = getJson(page);
                //System.out.println(json);
                Gson gson = new Gson();
                JsonRes jsonRes = gson.fromJson(json, JsonRes.class);
                //System.out.println(jsonRes);
                for (Data data : jsonRes.data) {
                    Instant instant = Instant.ofEpochMilli(data.timestamp);

                    String dd = data.userDob.substring(0, 2);
                    String mm = data.userDob.substring(3, 5);
                    String yy = data.userDob.substring(6);

                    int year = Integer.parseInt(yy);
                    int month = Integer.parseInt(mm);
                    int day = Integer.parseInt(dd);

                    LocalDate dob = LocalDate.of(year, month, day);
                    LocalDateTime timestamp = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
                    LocalDate timestampDate = timestamp.toLocalDate();
                    //System.out.println(timestampDate + "   :::   " + dob);
                    int ageAtTimestamp = timestampDate.compareTo(dob);
                    int userBpDiff = data.vitals.bloodPressureDiastole - data.vitals.bloodPressureSystole;

                    if (ageStart <= ageAtTimestamp && ageAtTimestamp <= ageEnd) {
                        if (userBpDiff > bpDiff) {
                            ids.add(data.id);
                        }
                    }
                }
            }

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        if (ids.size() == 0) ids.add(-1);

        return ids;
    }

    static String getJson(int page) throws Exception {
        StringBuilder result = new StringBuilder();
        URL url = new URL("https://jsonmock.hackerrank.com/api/medical_records?&page=" + page);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line;
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }
        rd.close();
        return result.toString();
    }
}

public class Solution {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        int ageStart = scanner.nextInt(); // 28
        scanner.skip("(\r\n|[\n\r\u2028\u2029\u0085])?");
        int ageEnd = scanner.nextInt(); // 30
        scanner.skip("(\r\n|[\n\r\u2028\u2029\u0085])?");
        int bpDiff = scanner.nextInt(); // 63
        scanner.skip("(\r\n|[\n\r\u2028\u2029\u0085])?");

        scanner.close();

        System.out.println(Result.getRecordsByAgeGroup(ageStart, ageEnd, bpDiff)); // 31
    }
}

class JsonRes {
    int page;
    int per_page;
    int total;
    int total_pages;
    List<Data> data = new ArrayList<>();

    @Override
    public String toString() {
        return "page: " + page + "\n" +
                "per_page: " + per_page + "\n" +
                "total: " + total + "\n" +
                "total_pages: " + total_pages + "\n" +
                "data: " + data;
    }
}

class Data {
    int id;
    Long timestamp;
    String userDob;
    Vitals vitals ;

    @Override
    public String toString() {
        return "\n{\n id: " + id + "\n" +
                "timeStamp: " + timestamp + "\n" +
                "userDob: " + userDob + "\n" +
                "vitals: " + vitals + "\n}\n";
    }
}

class Vitals {
    int bloodPressureDiastole;
    int bloodPressureSystole;
    @Override
    public String toString() {
        return "\n{\n bloodPressureDiastole: " + bloodPressureDiastole + "\n" +
                "bloodPressureSystole: " + bloodPressureSystole + "\n}\n";
    }
}
