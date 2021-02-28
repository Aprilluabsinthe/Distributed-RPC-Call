package rmi.data;

import java.io.Serializable;

/**
 * MethodRequestMessageData abstracts a method request,
 * containing all message data needed for a method request
 * 
 * Ref: https://docs.oracle.com/javase/7/docs/api/java/lang/Class.html
 * https://docs.oracle.com/javase/7/docs/api/java/lang/reflect/Method.html
 * 
 * @author Yuan Gu
 */

public class MethodRequestMessageData<T> implements Serializable{
    private static final long serialVersionUID = 1L;

    private String methodName;
    private Class<T>[] parameterTypes;
    private Object[] varArgs;

    public MethodRequestMessageData(String methodName, Class<T>[] params, Object[] args) {
        this.parameterTypes = params;
        this.methodName = methodName;
        this.varArgs = args;
    }

    public void setMethodName(String s) {
        this.methodName = s;
    }

    public void setParameterTypes(Class<T>[] s) {
        this.parameterTypes = s;
    }

    public void setVarArgs(Object[] s) {
        this.varArgs = s;
    }

    public String getMethodName() {
        return methodName;
    }

    public Class<T>[] getParameterTypes() {
        return parameterTypes;
    }

    public Object[] getVarArgs() {
        return varArgs;
    }
}