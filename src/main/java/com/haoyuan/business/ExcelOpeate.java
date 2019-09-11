package com.haoyuan.business;

import com.haoyuan.domain.Nosupport200mupdate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class ExcelOpeate {
    public void delete(){
        String key = "nosupport200mupdate-caid";
        // RedisUtil.deleteRedisData(key);
    }

    //保证数据库数据与redis须一致，要不然出数据
    public void batchSaveRecords(List<Nosupport200mupdate> resultList){
        //存储之前去重过滤
        HashSet<String> tempDatas =  new HashSet<String>();
        //实际存储数据列
        List<Nosupport200mupdate> listToDB = new ArrayList<Nosupport200mupdate>();

        String key = "nosupport200mupdate-caid";
//		RedisUtil.deleteRedisData(key);
        StringBuffer tempCaids = new StringBuffer();
        String redisCaids = "";
        if(resultList.size()>0){
            //存储之前文件去重
            for (Nosupport200mupdate nosupport200mupdate : resultList) {
                if(tempDatas.contains(nosupport200mupdate.getCaid())){
                    continue;
                }
                tempDatas.add(nosupport200mupdate.getCaid());
                listToDB.add(nosupport200mupdate);
            }
           /* boolean existKey = RedisUtil.existsKey(key);
            if(existKey){
                redisCaids = RedisUtil.getRedisData(key);
                //redis追加数据
                tempCaids.append(redisCaids);
                Iterator<Nosupport200mupdate> iter = listToDB.iterator();
                //存储DB
                while(iter.hasNext()){//判断是否还有下一个
                    //二次查询所有DB数据去重
                    if(StringUtils.isNotEmpty(redisCaids)){
                        Nosupport200mupdate nosupport200mupdate = (Nosupport200mupdate)iter.next();
                        if(redisCaids.indexOf(nosupport200mupdate.getCaid())!=-1){
                            System.out.println("redis中存在智能卡caid："+nosupport200mupdate.getCaid()+"的数据行!");
                            iter.remove();
                        }
                    }
                }
            }*/

            for (Nosupport200mupdate nosupport200mupdate : listToDB) {
                // nosupport200mupdate.setId(SequenceUtil.getGlobalSeqID());
                // nosupport200mupdate.setCreatedate(DateUtils.getCurTime());
                nosupport200mupdate.setRemark("导入批量数据");
                tempCaids.append(nosupport200mupdate.getCaid()+';');
            }
            // if(StringUtils.isNotEmpty(redisCaids)){
            //     //清除旧数据
            //     RedisUtil.deleteRedisData(key);
            // }
            // //redis保存新数据
            // RedisUtil.setRedisData(key, tempCaids.toString());
            //
            // this.nosupportDao.bacthSave(AnnotationSqlUtil.getSaveSql(Nosupport200mupdate.class),listToDB);

        }
    }

}
