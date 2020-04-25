package dtrader;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class FunctionCaller {
  private static Log log = LogFactory.getFactory().getInstance(FunctionCaller.class);
  public boolean isFunction(String funcName) {
    return funcName.equals("println")      ||
           funcName.equals("set_property") ||
           funcName.equals("get_property");
  }
  
  public Object invokeFunction(String funcName, List<Object> params, Scope scope) throws Exception {
    switch(funcName) {
      case "println":
        return println(params);
      case "set_property":
        return setProperty(params, scope);
      default:
        throw new Exception("unknown function: " + funcName);
    }
  }
  
  private Object println(List<Object> params) throws Exception {
    if (params.size() > 1) {
      throw new Exception("println: too many arguments");
    }
    if (params.size() == 0) {
      System.out.println();
      return 0;
    } else {
      System.out.println(params.get(0).toString());
      return params.get(0);
    }
  }
  
  private Object setProperty(List<Object> params, Scope scope) throws Exception {
    if (params.size() != 2) {
      throw new Exception("set_property: requires 2 arguments");
    }
    if (!(params.get(0) instanceof String)) {
      throw new Exception("set_property: param 1 must be a string");
    }
    scope.setProperty((String) params.get(0), params.get(1));
    return params.get(1);
  }
}
