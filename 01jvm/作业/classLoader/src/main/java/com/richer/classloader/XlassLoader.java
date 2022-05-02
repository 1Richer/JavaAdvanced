package com.richer.classloader;


import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class XlassLoader extends ClassLoader {
    private final static String SUFFIX = ".xlass";

    public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        //目标类名
        String className = "Hello";
        //目标方法名
        String methorName = "hello";
        //1.创建类加载器
        ClassLoader classLoader = new XlassLoader();
        //2.加载指定类
        Class<?> clazz = classLoader.loadClass(className);
        //3.创建类的实例
        Object object = clazz.getDeclaredConstructor().newInstance();
        //4.调用实例的方法
        Method method = clazz.getMethod(methorName);
        method.invoke(object);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        String fileName = name + SUFFIX;
        InputStream fis = null;
        try {
            //2.1输入流读取resources下的.xlass文件
            fis = getClass().getClassLoader().getResourceAsStream(fileName);
            //2.2获取文件大小
            int length = fis.available();
            //2.3读取输入流字节进字节数组
            byte[] byteArray = new byte[length];
            fis.read(byteArray);
            byte[] decodeArray = decode(byteArray);
            //2.4字节数组生成字节码文件
            return defineClass(name, decodeArray, 0, decodeArray.length);
        } catch (IOException e) {
            throw new ClassNotFoundException(name);
        } finally {
            close(fis);
        }
    }

    //解码
    private static byte[] decode(byte[] fromArray) {
        byte[] toArray = new byte[fromArray.length];
        for (int i = 0; i < fromArray.length; i++) {
            toArray[i] = (byte) (255 - fromArray[i]);
        }
        return toArray;
    }

    //关闭流
    private static void close(Closeable closeable) {
        if (null != closeable) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
