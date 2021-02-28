package rmi.data;

import rmi.helper.Helper.MessageType;
import java.io.*;
import java.util.Objects;

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


    @Override
    public String toString() {
        return "Message{" + "data=" + data + ", type=" + type + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Message)) return false;
        Message<?> message = (Message<?>) o;
        return Objects.equals(data, message.data) && type == message.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(data, type);
    }
}