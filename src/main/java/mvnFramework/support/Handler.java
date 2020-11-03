package mvnFramework.support;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class Handler {

    //对象属性
    private Object object;

    //方法属性
    private Method method;

    //url属性
    private Pattern pattern;

    //参数列表
    private Map<String,Integer> paramList;

    public Handler(Object object, Method method, Pattern pattern) {
        this.object = object;
        this.method = method;
        this.pattern = pattern;
        this.paramList = new HashMap<>();
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public void setPattern(Pattern pattern) {
        this.pattern = pattern;
    }

    public Map<String, Integer> getParamList() {
        return paramList;
    }

    public void setParamList(Map<String, Integer> paramList) {
        this.paramList = paramList;
    }
}
