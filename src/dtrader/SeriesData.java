package dtrader;

public class SeriesData {
  public static final int IDX_MIN = 0;
  public static final int IDX_MAX = 78;
  private Series series;
  private int data[];
  
  public SeriesData(Series series) {
    this.series = series;
    data = new int[IDX_MAX + 1];
  }

  public Series getSeries() {
    return series;
  }
  
  public int[] getData() {
    return data;
  }
}
