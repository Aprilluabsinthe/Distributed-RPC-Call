package rmi.data;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

/**
 * MethodRequestMessageData abstracts a method request,
 * containing all message data needed for a method request
 * 
 * Ref: https://docs.oracle.com/javase/7/docs/api/java/lang/Class.html
 * https://docs.oracle.com/javase/7/docs/api/java/lang/reflect/Method.html
 * 
 * @author Yuan Gu
 */

public class MethodRequestMessageData implements Serializable{
    private static final long serialVersionUID = 1L;

    private String methodName;
    private Object[] varArgs;
    private Class<?>[] parameterTypes;

    /**
     * Construction Functions
     * @param methodName
     * @param params
     * @param args
     */
    public MethodRequestMessageData(String methodName, Class<?>[] params, Object[] args) {
        this.parameterTypes = params;
        this.methodName = methodName;
        this.varArgs = args;
    }

    public MethodRequestMessageData(Object[] obj){
        this.methodName = (String) obj[0];
        this.varArgs = (Object[]) obj[1];
        this.parameterTypes = (Class<?>[]) obj[2];
    }


    /**
     * Setter for Method Name
     * @param s
     */
    public void setMethodName(String s) {
        this.methodName = s;
    }

    /**
     * Setter for Parameter Types
     * @param s
     */
    public void setParameterTypes(Class<?>[] s) {
        this.parameterTypes = s;
    }

    /**
     * Setter for varArgs
     * @param s
     */
    public void setVarArgs(Object[] s) {
        this.varArgs = s;
    }

    /**
     * Getter for Method Name
     * @return
     */
    public String getMethodName() {
        return methodName;
    }

    /**
     * Getter for Parameter Types
     * @return
     */
    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    /**
     * Getter for varArgs
     * @return
     */
    public Object[] getVarArgs() {
        return varArgs;
    }

    /**
     * Override toString
     * @return
     */
    @Override
    public String toString() {
        return "MethodRequestMessageData{" +
                "methodName='" + methodName + '\'' +
                ", parameterTypes=" + Arrays.toString(parameterTypes) +
                ", varArgs=" + Arrays.toString(varArgs) +
                '}';
    }

    /**
     * Override equals
     */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MethodRequestMessageData)) return false;
        MethodRequestMessageData that = (MethodRequestMessageData) o;
        return methodName.equals(that.methodName) && Arrays.equals(parameterTypes, that.parameterTypes) && Arrays.equals(varArgs, that.varArgs);
    }

    /**
     * Override hashCode()
     * @return
     */
    @Override
    public int hashCode() {
        int result = Objects.hash(methodName);
        result = 31 * result + Arrays.hashCode(parameterTypes);
        result = 31 * result + Arrays.hashCode(varArgs);
        return result;
    }
}