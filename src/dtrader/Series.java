package dtrader;

import java.util.ArrayList;
import java.util.List;

public class Series {
  public static final String FREQ_DAILY = "DAILY";
  public static final String FREQ_5MIN = "5MIN";
  String id;
  String title;
  String source;
  String sourceId;
  String notes;
  String freq;
  List<SeriesData> data = new ArrayList<SeriesData>();
  
  public String getId() {
    return id;
  }
  public void setId(String id) {
    this.id = id;
  }
  public String getTitle() {
    return title;
  }
  public void setTitle(String title) {
    this.title = title;
  }
  public String getSource() {
    return source;
  }
  public void setSource(String source) {
    this.source = source;
  }
  public String getSourceId() {
    return sourceId;
  }
  public void setSourceId(String sourceId) {
    this.sourceId = sourceId;
  }
  public String getNotes() {
    return notes;
  }
  public void setNotes(String notes) {
    this.notes = notes;
  }
  public String getFreq() {
    return freq;
  }
  public void setFreq(String freq) {
    this.freq = freq;
  }
  public List<SeriesData> getData() {
    return data;
  }
}
