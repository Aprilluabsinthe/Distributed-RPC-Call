
package rmi.helper;

import rmi.RMIException;
import rmi.Skeleton;
import rmi.data.Message;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

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

    public static Boolean checkDataType(Message<?> message, MessageType validtype){
        if(message == null){
            return false;
        }
        return message.getType() == validtype;
    }

    public static Boolean isServerInterface(Class<?> c, Skeleton<?> skeleton){
        if(!c.isInterface()){
            return false;
        }
        Class<?>[] interfaces = skeleton.getServer().getClass().getInterfaces();
        return Arrays.asList(interfaces).contains(c);
    }

}