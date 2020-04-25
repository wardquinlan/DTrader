package dtrader;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class FunctionCaller {
  private static Log log = LogFactory.getFactory().getInstance(FunctionCaller.class);
  public boolean isFunction(String funcName) {
    return funcName.equals("println")      ||
           funcName.equals("set_property") ||
           funcName.equals("get_property") ||
           funcName.equals("print_scope");
  }
  
  public Object invokeFunction(String funcName, List<Object> params, Scope scope) throws Exception {
    switch(funcName) {
      case "println":
        return println(params);
      case "set_property":
        return setProperty(params, scope);
      case "get_property":
        return getProperty(params, scope);
      case "print_scope":
        return print_scope(params, scope);
      default:
        throw new Exception("unknown function: " + funcName);
    }
  }
  
  private Object print_scope(List<Object> params, Scope scope) throws Exception {
    if (params.size() > 0) {
      throw new Exception("print_scope: too many arguments");
    }
    params.add(scope);
    return println(params);
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
  
  private Object getProperty(List<Object> params, Scope scope) throws Exception {
    if (params.size() != 1) {
      throw new Exception("get_property: requires 1 argument");
    }
    if (!(params.get(0) instanceof String)) {
      throw new Exception("get_property: param 1 must be a string");
    }
    Object value = scope.getProperty((String) params.get(0));
    if (value == null) {
      throw new Exception("get_property: property not found: " + params.get(0));
    }
    return scope.getProperty((String) params.get(0));
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
