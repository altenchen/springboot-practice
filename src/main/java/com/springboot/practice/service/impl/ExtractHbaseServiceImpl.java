//package com.springboot.practice.service.impl;
//
//import cn.hutool.core.date.DateUnit;
//import cn.hutool.core.date.DateUtil;
//import com.springboot.practice.service.ExtractHbaseService;
//import org.apache.hadoop.hbase.client.Result;
//import org.apache.hadoop.hbase.client.Scan;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.hadoop.hbase.HbaseTemplate;
//import org.springframework.data.hadoop.hbase.RowMapper;
//import org.springframework.stereotype.Service;
//
//import java.util.concurrent.ConcurrentHashMap;
//
///**
// * @description:
// * @create: 2020/4/18
// * @author: altenchen
// */
//@Service
//public class ExtractHbaseServiceImpl implements ExtractHbaseService {
//
//    @Autowired
//    private HbaseTemplate hbaseTemplate;
//
//    private static final String packetTable = "realinfo";
//
//    private static final String UNDER_LINE = "_";
//
//
//    @Override
//    public String extractHbaseData(String vid) {
//        //1,根据vin从hbase获取数据
//        long stopTimestamp = System.currentTimeMillis();
//        long startTimestamp = 10000000000000L;
//        String startRow = vid + UNDER_LINE + startTimestamp;
//        String stopRow = vid + UNDER_LINE + stopTimestamp;
//        Scan scan = new Scan();
//        scan.setStartRow(startRow.getBytes());
//        scan.setStartRow(startRow.getBytes());
//
//        hbaseTemplate.find(packetTable, scan, new RowMapper<String>() {
//            @Override
//            public String mapRow(Result result, int rowNum) throws Exception {
//                return null;
//            }
//        });
//
//
//        return null;
//    }
//}
