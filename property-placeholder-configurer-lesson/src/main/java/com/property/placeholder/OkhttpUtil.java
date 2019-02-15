package com.property.placeholder;

import okhttp3.*;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by xiaowu.zhou@tongdun.cn on 2019/2/12.
 */
public class OkhttpUtil {

    private static final String server = "http://www.iocoder.cn/";


    private static final String dir = "images/Dubbo/";



    public static void main(String[] args) {
        List<String> dates = collectLocalDates("2019-01-01","2019-02-12");
        System.out.println(dates);


        for (String date : dates){


            callDate(date);

        }
    }

    private static void callDate(String date) {

        List<String> picUrls = getPicUrls(date);

        for (String picUrl : picUrls){

            OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                    .connectionPool(new ConnectionPool(5,1, TimeUnit.SECONDS))
                    .build();

            Request request = new Request.Builder().url(picUrl).build();
            Call call = okHttpClient.newCall(request);

            InputStream is = null;
            FileOutputStream fos = null;

            Response response = null;

            try {
                response = call.execute();

                if (response.code() != 200){
                    continue;
                }

                byte[] buf = new byte[2048];
                int len = 0;
                is = response.body().byteStream();


                String filePrefix = "/Users/zhouxiaowu/IdeaProject-gupao/content目录_分布式/dubbo-blog/source/";

                String fileStr = filePrefix + picUrl.replace(server ,"");


                File file = new File(fileStr);

                if (!file.exists()){

                    File parentFile = file.getParentFile();

                    if (!parentFile.exists()){
                        parentFile.mkdirs();
                    }

                    file.createNewFile();
                }

                fos = new FileOutputStream(file);
                while ((len = is.read(buf)) != -1) {
                    fos.write(buf, 0, len);
                }
                fos.flush();


            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (is != null) {
                        is.close();
                    }
                    if (fos != null) {
                        fos.close();
                    }

                    if (response !=null){
                        response.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


    }

    private static List<String> getPicUrls(String date) {
        List<String> picUrls = new ArrayList<>();

        for (int i=1 ; i<=25;i++){
            String str = String.format("%02d", i);
            picUrls.add(server + dir + date + "/" + str+".png");
        }

        return picUrls;
    }

//    public static boolean downloadPic(OkHttpClient okHttpClient, String picUrl, String localFileName) {
//        final File file = new File(localFileName);
//        if (file.exists()) {
//            file.delete();
//        } else {
//            try {
//                FileUtils.forceMkdir(file.getParentFile());
//                file.createNewFile();
//            } catch (IOException e) {
//                log.error("create file failed, file = " + file.getAbsolutePath(), e);
//            }
//        }
//
//        InputStream is = null;
//        FileOutputStream fos = null;
//
//        try {
//            picUrl = FormatUtils.getItemPicUrl(picUrl);
//            Request request = new Request.Builder().url(picUrl).build();
//            Call call = okHttpClient.newCall(request);
//            Response response = call.execute();
//            byte[] buf = new byte[2048];
//            int len = 0;
//            is = response.body().byteStream();
//            fos = new FileOutputStream(file);
//            while ((len = is.read(buf)) != -1) {
//                fos.write(buf, 0, len);
//            }
//            fos.flush();
//            return true;
//        } catch (IOException e) {
//            log.error("download failed", e);
//            return false;
//        } finally {
//            try {
//                if (is != null) {
//                    is.close();
//                }
//                if (fos != null) {
//                    fos.close();
//                }
//            } catch (IOException e) {
//                log.error("down loaded failed", e);
//            }
//        }
//
//    }





//    private static List<String> getUrlList(){
//
//        List<String> arr = new ArrayList<String>();
//
//        for (int i=1)
//
//
//    }


    /**
     * 收集起始时间到结束时间之间所有的时间并以字符串集合方式返回
     * @param timeStart
     * @param timeEnd
     * @return
     */
    public static List<String> collectLocalDates(String timeStart, String timeEnd){
        return collectLocalDates(LocalDate.parse(timeStart), LocalDate.parse(timeEnd));
    }

    /**
     * 收集起始时间到结束时间之间所有的时间并以字符串集合方式返回
     * @param start
     * @param end
     * @return
     */
    public static List<String> collectLocalDates(LocalDate start, LocalDate end){

//        start.format(DateTimeFormatter.ofPattern("yyyy_MM_dd"));


        // 用起始时间作为流的源头，按照每次加一天的方式创建一个无限流
        return Stream.iterate(start, localDate -> localDate.plusDays(1))
                // 截断无限流，长度为起始时间和结束时间的差+1个
                .limit(ChronoUnit.DAYS.between(start, end) + 1)
                // 由于最后要的是字符串，所以map转换一下
                .map(t -> t.format(DateTimeFormatter.ofPattern("yyyy_MM_dd")))
                // 把流收集为List
                .collect(Collectors.toList());
    }


}
