//package com.lizhi.threadUtil;
//
//import java.io.*;
//import java.net.MalformedURLException;
//import java.net.URL;
//import java.net.URLConnection;
//import java.util.UUID;
//
//public class FileUtil {
//
//    private static FileUtil fileUtil = new FileUtil();
//
//    public void save(String link) {
//        InputStream inStream = null;
//        ByteArrayOutputStream outStream = null;
//        FileOutputStream op = null;
//        try {
//            URL url = new URL(link);
//            URLConnection con = url.openConnection();
//            inStream = con.getInputStream();
//            outStream = new ByteArrayOutputStream();
//            byte[] buf = new byte[1024];
//            int len = 0;
//            while ((len = inStream.read(buf)) != -1) {
//                outStream.write(buf, 0, len);
//            }
//            File file = new File("C:\\Users\\lx\\Desktop\\test\\" + UUID.randomUUID() + ".jpg"); // ͼƬ���ص�ַ
//            op = new FileOutputStream(file);
//            op.write(outStream.toByteArray());
//        } catch (MalformedURLException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } finally {
//            close(op, outStream, inStream);
//        }
//    }
//
//    public static void saveImage(String link) {
//        System.out.println("保存图片地址：" + link);
//        fileUtil.save(link);
//    }
//
////    public static void main(String[] args) {
////        FileUtil.saveImage("http://chinesepornmovie.net/upload/photos/2018/09/DctFnKi4AHsw7rcv7fyX_06_139cf270008c7009bba8343c023bb4a0_image.jpg");
////    }
//
//    public void close(Closeable... streams) {
//        for (Closeable stream : streams) {
//            if (stream != null) {
//                try {
//                    stream.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
//}
