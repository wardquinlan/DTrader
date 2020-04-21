package dtrader;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SeriesDAO {
  private static Log log = LogFactory.getFactory().getInstance(SeriesDAO.class);
  private String url;
  
  public SeriesDAO() throws Exception {
    try
    {
      Class.forName("org.postgresql.Driver");
      log.info("attempting to connect to database with host=" + System.getProperty("dtrader.db.host") + 
               ", name=" + System.getProperty("dtrader.db.name") +
               ", username=" + System.getProperty("dtrader.db.username"));
      url = "jdbc:postgresql://" + System.getProperty("dtrader.db.host") +  
            "/" + System.getProperty("dtrader.db.name") + 
            "?user=" + System.getProperty("dtrader.db.username") +
            "&password=" + System.getProperty("dtrader.db.password");
      Connection c = DriverManager.getConnection(url);  
      c.close();
    }
    catch(Exception e)
    {
      throw new Exception(e);
    }
  }

  private Connection open() throws SQLException {
    return DriverManager.getConnection(url);
  }
  
  private void close(Connection c) {
    try {
      c.close();
    } catch(SQLException e) {
      log.error("close failed", e);
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
