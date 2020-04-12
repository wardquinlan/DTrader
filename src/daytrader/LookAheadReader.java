package daytrader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class LookAheadReader {
  private InputStreamReader inputStreamReader;
  private Integer next;
  
  public LookAheadReader(InputStream inputStream) {
    this.inputStreamReader = new InputStreamReader(inputStream);
  }
  
  public int read() throws IOException {
    if (this.inputStreamReader == null) {
      throw new IOException("reader already closed");
    }
    if (this.next == null) {
      this.next = inputStreamReader.read();
    }
    int tmp = this.next; 
    this.next = inputStreamReader.read();
    return tmp;
  }
  
  public int peek() {
    return next;
  }
  
  public void close() throws IOException {
    this.inputStreamReader.close();
    this.inputStreamReader = null;
  }
}
