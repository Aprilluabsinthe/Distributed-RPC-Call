package rmi.data;

import rmi.helper.Helper.MessageType;
import java.io.*;
/**
 * Message abstracts a message between stub and skeleton
 * @author Yuan Gu
 */

public class Message<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private T data;
    private MessageType type;

    public Message(T data, MessageType type) {
        this.data = data;
        this.type = type;
    }

    public void setData(T data) {
		this.data = data;
    }

    public Object getData() {
		return data;
    }
    
    public void setType(MessageType type) {
		this.type = type;
	}

	public MessageType getType() {
		return type;
	}
}