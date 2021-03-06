package rmi.data;

import rmi.helper.Helper.MessageType;
import java.io.*;
import java.util.Objects;

/**
 * Message abstracts a message between stub and skeleton
 * implements Serializable
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

    /**
     * Setter for Data
     * @param data
     */
    public void setData(T data) {
		this.data = data;
    }

    /**
     * Getter for Data
     */
    public Object getData() {
		return data;
    }

    /**
     * Setter for type
     * @param type
     */
    public void setType(MessageType type) {
		this.type = type;
	}

    /**
     * Getter for Type
     * @return type
     */
	public MessageType getType() {
		return type;
	}

    /**
     * Overide toString
     * @return String containing data and Type
     */
    @Override
    public String toString() {
        return "Message{" + "data=" + data + ", type=" + type + '}';
    }

    /**
     * Overide equals
     * @return true or false
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Message)) return false;
        Message<?> message = (Message<?>) o;
        return Objects.equals(data, message.data) && type == message.type;
    }

    /**
     * Overide hashCode
     * @return hashcode for data and type
     */
    @Override
    public int hashCode() {
        return Objects.hash(data, type);
    }
}