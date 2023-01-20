package day19.workshop.model;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

public class LoveResult {

    private String fname;
    private String sname;
    private Integer percentage;
    private String result;

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getSname() {
        return sname;
    }

    public void setSname(String sname) {
        this.sname = sname;
    }

    public Integer getPercentage() {
        return percentage;
    }

    public void setPercentage(Integer percentage) {
        this.percentage = percentage;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public static LoveResult create(String json) throws IOException {
        LoveResult loveResult = new LoveResult();
        try (
                InputStream is = new ByteArrayInputStream(json.getBytes())) {
            JsonReader r = Json.createReader(is);
            JsonObject o = r.readObject();
            String utfFirst = URLDecoder.decode(o.getString("fname"), "UTF-8");
            String utfSecond = URLDecoder.decode(o.getString("sname"), "UTF-8");
            loveResult.setFname(utfFirst);
            loveResult.setSname(utfSecond);
            loveResult.setPercentage(Integer.parseInt(o.getString("percentage")));
            loveResult.setResult(loveResult.resultString(Integer.parseInt(o.getString("percentage"))));
        }
        return loveResult;
    }

    private String resultString(Integer percentage) {
        if (percentage < 75 && percentage > 0) {
            return "Not compatible";
        }
        if (percentage <= 0) {
            return "wtf";
        }
        return "Compatible";
    }

}
