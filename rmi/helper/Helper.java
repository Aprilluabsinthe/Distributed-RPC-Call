
package rmi.helper;

import rmi.RMIException;
import rmi.Skeleton;
import rmi.data.Message;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * The Helper Class for RMI
 * Containing enum MessageType
 * Checking Methods for interfaces <code>c</code> about Throwing RMIExceptions
 * Checking Methods for DataType
 * Checking Methods for isServerInterface
 */
public class Helper {
    public enum MessageType {
        SkeletonRequest, SkeletonResponse,
        MethodRequest, MethodResponse,
        UnexceptedRequest,
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
     * Checking whether the MessageType is the type required
     * @param message
     * @param validtype
     * @return
     */
    public static Boolean checkDataType(Message<?> message, MessageType validtype){
        if(message == null){
            return false;
        }
        return message.getType() == validtype;
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

}