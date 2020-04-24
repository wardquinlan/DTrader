package dtrader;

public class Chart {
  private String name;
  private Scope scope;
  
  public Chart(String name, Scope scope) {
    this.name = name;
    this.scope = scope;
  }
  
  public String getName() {
    return name;
  }
  
  public Scope getScope() {
    return scope;
  }
}
