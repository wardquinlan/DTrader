package dtrader;

import java.sql.Connection;
import java.sql.DriverManager;

public class SeriesDAO {
  public SeriesDAO() throws Exception {
    try
    {
      Class.forName("org.postgresql.Driver");
      Connection c = DriverManager.getConnection("jdbc:postgresql://localhost/dtrader?user=dtrader&amp;password=dtrader00");
      c.close();
    }
    catch(Exception e)
    {
      throw new Exception(e);
    }
  }
  
  public void addSeries(Series series) {
  }
  
  public void addSeriesData(SeriesData seriesData) {
  }
  
  public Series findSeriesById(String id) {
    return null;
  }
}
