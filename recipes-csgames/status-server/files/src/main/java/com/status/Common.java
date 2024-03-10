package com.status;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

public class Common {
    private static enum Type {
        STRING,
        ARRAY
    }

    public static void sendPacket(DataOutputStream outputStream, List<Object> elements) throws IOException {
        outputStream.writeShort(calculateArraySize(elements));
        for (Object element : elements) {
            if (element instanceof String) {
                writeString(outputStream, (String) element);
            } else if (element instanceof List) {
                writeArray(outputStream, (List<Object>) element);
            }
        }
        outputStream.flush();
    }

    private static void writeString(DataOutputStream outputStream, String str) throws IOException {
        outputStream.writeShort(str.length());
        outputStream.writeByte(Type.STRING.ordinal());
        outputStream.writeBytes(str);
    }

    private static void writeArray(DataOutputStream outputStream, List<Object> array) throws IOException {
        outputStream.writeShort(calculateArraySize(array));
        outputStream.writeByte(Type.ARRAY.ordinal());
        for (Object element : array) {
            if (element instanceof String) {
                writeString(outputStream, (String) element);
            } else if (element instanceof List) {
                writeArray(outputStream, (List<Object>) element);
            }
        }
    }

    private static int calculateArraySize(List<Object> elements) {
        int size = 0;
        for (Object element : elements) {
            if (element instanceof String) {
                size += 2 + 1 + ((String) element).length();
            } else if (element instanceof List) {
                size += 2 + 1 + calculateArraySize((List)element);
            }
        }
        return size;
    }

    public static List<Object> receivePacket(DataInputStream inputStream) throws IOException {
        int size = inputStream.readShort();
        byte[] bytes = new byte[size];
        inputStream.readFully(bytes);
        return readElements(new DataInputStream(new ByteArrayInputStream(bytes)));
    }

    private static List<Object> readElements(DataInputStream inputStream) throws IOException {
        List<Object> array = new ArrayList<>();
        while (inputStream.available() > 0) {
            int size = inputStream.readShort();
            byte type = inputStream.readByte();
            byte[] bytes = new byte[size];
            inputStream.readFully(bytes);

            if (type == Type.STRING.ordinal()) {
                array.add(new String(bytes));
            } else if (type == Type.ARRAY.ordinal()) {
                array.add(readElements(new DataInputStream(new ByteArrayInputStream(bytes))));
            } else {
                throw new IOException("Invalid data type: " + type);
            }
        }
        return array;
    }
}
