package rmi.helper;

import rmi.RMIException;
import rmi.Skeleton;
import rmi.data.Message;
import rmi.proxyInvocationHandler;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.List;

public class Helper {

    /**
     * Checking whether the MessageType is the type required
     * @param message
     * @param validtype
     * @return
     */
    public static Boolean checkDataType(Message<?> message, DataStatus validtype){
        if(message == null){
            return false;
        }
        return message.getType() == DataStatus.VALID;
    }

    /**
     * Check whether an Exception can be throw
     * by checking whether it is contained in method
     * @param method
     * @param e
     * @return
     */
    public static boolean methodContainsE(Method method, Exception e) {
        Class<?>[] exceptionsArr = method.getExceptionTypes();
        List<Class<?>> exceptionsList = Arrays.asList(exceptionsArr);
        if (!exceptionsList.contains(e.getClass())){
            return false;
        }
        return true;
    }


    public enum MessageType {
        SkeletonRequest, SkeletonResponse,
        MethodRequest, MethodResponse,
        UnexceptedRequest,
    }

    public enum DataStatus {
        VALID,
        INVALID
    }

    public enum ThreadState {
        NEW,
        RUNNABLE,
        BLOCKED,
        WAITING,
        TIMED_WAITING,
        TERMINATED
    }

    /**
     * Checking whether all intercfaces throw an RMIExceptions
     * <p>
     *     Checking Methods for interfaces <code>c</code> about Throwing RMIExceptions
     *     if any of the interfaces in <code>c</code> do not throw an RMIExceptions, return false
     *     else return True
     *
     * @param c
     * @return
     */
    public static <T> boolean allThrowRMIExceptions(Class<T> c){
        Method[] selfmethods = c.getDeclaredMethods();
        for(int i = 0 ; i < selfmethods.length; i++){
            Class<?>[] exceptionsArr = selfmethods[i].getExceptionTypes();
            List<Class<?>> exceptionsList = Arrays.asList(exceptionsArr);
            if (!exceptionsList.contains(RMIException.class)){
                return false;
            }
        }
        return true;
    }

    /**
     * Checking whether c is a interface in skeleton
     * @param c
     * @param skeleton
     * @return
     */
    public static Boolean isServerInterface(Class<?> c, Skeleton<?> skeleton){
        if(!c.isInterface()){
            return false;
        }
        Class<?>[] interfaces = skeleton.getServer().getClass().getInterfaces();
        return Arrays.asList(interfaces).contains(c);
    }

    /**
     * Override functions for interface <code>Stub</code>
     * should be called in proxyInvocationHandler
     * @param handler
     * @param method
     * @param args
     * @param <T>
     * @return
     * @throws NoSuchMethodException
     */
    public static <T> Object overrideStub(proxyInvocationHandler<T> handler , Method method, Object[] args) throws NoSuchMethodException {
        //Overriding equals function of OBJeCT classes
        if (method.equals(Object.class.getMethod("equals", Object.class))) {
            if (args[0] instanceof Proxy) {
                proxyInvocationHandler other = (proxyInvocationHandler) Proxy.getInvocationHandler(args[0]);
                return (handler.getClazz().equals(other.getClazz()) && handler.getAddress().equals(other.getAddress()));
            }
            return false;
        }

        //Overriding hashCode function of OBJeCT classes
        if (method.equals(Object.class.getMethod("hashCode"))) {
            return handler.getClazz().hashCode() *  31 + handler.getAddress().hashCode() * 31;
        }

        //Overriding toString function of OBJeCT classes
        if(method.equals( Object.class.getMethod("toString") )){
            return "Class Interface=" + handler.getClazz().getCanonicalName()+
                    "remote address hostname=" + handler.getAddress().getHostName() +
                    "remote address port=" + handler.getAddress().getPort();
        }
        return null;
    }
}
