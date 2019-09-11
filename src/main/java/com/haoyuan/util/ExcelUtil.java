package com.haoyuan.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

import com.haoyuan.exception.ExcelException;
import com.haoyuan.domain.Nosupport200mupdate;

/**
 * @author     : YH
 * @Date       : 2019-9-5
 * @Comments   : 导入导出Excel工具类
 * @Version    : 1.0.0
 */
public class ExcelUtil {

    /**
     * @MethodName          : excelToList
     * @Description             : 将Excel转化为List 针对获取文件所有数据行
     * @param in                    ：承载着Excel的输入流
     * @param sheetIndex        ：要导入的工作表序号
     * @param entityClass       ：List中对象的类型（Excel中的每一行都要转化为该类型的对象）
     * @param fieldMap          ：Excel中的中文列头和类的英文属性的对应关系Map
     * @param uniqueFields  ：指定业务主键组合（即复合主键），这些列的组合不能重复
     * @return                      ：List
     * @throws ExcelException
     */
    public static <T>  List<T>  excelToList(
            InputStream in,
            Class<T> entityClass,
            LinkedHashMap<String, String> fieldMap,
            String[] uniqueFields
    ) throws ExcelException{

        //定义要返回的list
        List<T> resultList = new ArrayList<T>();
        try {
            //根据Excel数据源创建WorkBook
            Workbook wb = Workbook.getWorkbook(in);
            //获取工作表
            Sheet[] sheets = wb.getSheets();
            Sheet sheet = null;
            for (int sheetIndex = 0; sheetIndex < sheets.length; sheetIndex++) {
                sheet = sheets[sheetIndex];
                //获取工作表的有效行数
                int realRows = 0;
                for(int i=0;i<sheet.getRows();i++){
                    int nullCols=0;
                    for(int j=0;j<sheet.getColumns();j++){
                        Cell currentCell = sheet.getCell(j,i);
                        if(currentCell==null || "".equals(currentCell.getContents().toString())){
                            nullCols++;
                        }
                    }
                    if(nullCols==sheet.getColumns()){
                        break;
                    }else{
                        realRows++;
                    }
                }
                //如果Excel中没有数据则提示错误
                if(realRows<=1){
//                    throw new ExcelException("Excel文件中没有任何数据");
                    System.out.println("Excel的Sheet"+sheetIndex+"文件中没有任何数据!");
                    continue;
                }

                Cell[] firstRow = sheet.getRow(0);
                String[] excelFieldNames = new String[firstRow.length];
                //获取Excel中的列名
                for(int i=0;i<firstRow.length;i++){
                    excelFieldNames[i] = firstRow[i].getContents().toString().trim();
                }
                //判断需要的字段在Excel中是否都存在
                List<String> excelFieldList = Arrays.asList(excelFieldNames);
                for(String cnName : fieldMap.keySet()){
                    if(!excelFieldList.contains(cnName)){
                        System.out.println("Excel中缺少必要的列名称["+cnName+"]或者该列字段名称有误!");
                    }
                }

                //将列名和列号放入Map中,这样通过列名就可以拿到列号
                LinkedHashMap<String, Integer> colMap = new LinkedHashMap<String, Integer>();
                for(int i=0;i<excelFieldNames.length;i++){
                    colMap.put(excelFieldNames[i], firstRow[i].getColumn());
                }

//              existSameRowData(sheet,uniqueFields, colMap, realRows);

                //将sheet转换为list
                for(int i=1;i<realRows;i++){
                    //新建要转换的对象
                    T entity = entityClass.newInstance();
                    //给对象中的字段赋值
                    for(Entry<String, String> entry : fieldMap.entrySet()){
                        //获取中文字段名
                        String cnNormalName = entry.getKey();
                        //获取英文字段名
                        String enNormalName = entry.getValue();
                        //根据中文字段名获取列号
                        if(colMap.get(cnNormalName)!=null){
                            int col = colMap.get(cnNormalName);
                            //获取当前单元格中的内容
                            String content = sheet.getCell(col, i).getContents().toString().trim();
                            //给对象赋值
                            setFieldValueByName(enNormalName, content, entity);
                        }else{

                        }

                    }
                    resultList.add(entity);
                }
            }

        } catch(Exception e){
            e.printStackTrace();
            //如果是ExcelException，则直接抛出
            if(e instanceof ExcelException){
                throw (ExcelException) e;
                //否则将其它异常包装成ExcelException再抛出
            }else{
                e.printStackTrace();
                throw new ExcelException("导入Excel失败");
            }
        }
        return resultList;
    }

    /**
     * @MethodName  : setFieldValueByName
     * @Description : 根据字段名给对象的字段赋值
     * @param fieldName  字段名
     * @param fieldValue    字段值
     * @param o 对象
     */
    private static void setFieldValueByName(String fieldName,Object fieldValue,Object o) throws Exception{

        Field field = getFieldByName(fieldName, o.getClass());
        if(field!=null){
            field.setAccessible(true);
            //获取字段类型
            Class<?> fieldType = field.getType();

            //根据字段类型给字段赋值
            if (String.class == fieldType) {
                field.set(o, String.valueOf(fieldValue));
            } else if ((Integer.TYPE == fieldType)
                    || (Integer.class == fieldType)) {
                field.set(o, Integer.parseInt(fieldValue.toString()));
            } else if ((Long.TYPE == fieldType)
                    || (Long.class == fieldType)) {
                field.set(o, Long.valueOf(fieldValue.toString()));
            } else if ((Float.TYPE == fieldType)
                    || (Float.class == fieldType)) {
                field.set(o, Float.valueOf(fieldValue.toString()));
            } else if ((Short.TYPE == fieldType)
                    || (Short.class == fieldType)) {
                field.set(o, Short.valueOf(fieldValue.toString()));
            } else if ((Double.TYPE == fieldType)
                    || (Double.class == fieldType)) {
                field.set(o, Double.valueOf(fieldValue.toString()));
            } else if (Character.TYPE == fieldType) {
                if ((fieldValue!= null) && (fieldValue.toString().length() > 0)) {
                    field.set(o, Character
                            .valueOf(fieldValue.toString().charAt(0)));
                }
            }else if(Date.class==fieldType){
                field.set(o, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(fieldValue.toString()));
            }else{
                field.set(o, fieldValue);
            }
        }else{
            throw new ExcelException(o.getClass().getSimpleName() + "类不存在字段名 "+fieldName);
        }
    }

    /**
     * @MethodName  : getFieldByName
     * @Description : 根据字段名获取字段
     * @param fieldName 字段名
     * @param clazz 包含该字段的类
     * @return 字段
     */
    private static Field getFieldByName(String fieldName, Class<?>  clazz){
        //拿到本类的所有字段
        Field[] selfFields=clazz.getDeclaredFields();

        //如果本类中存在该字段，则返回
        for(Field field : selfFields){
            if(field.getName().equals(fieldName)){
                return field;
            }
        }

        //否则，查看父类中是否存在此字段，如果有则返回
        Class<?> superClazz=clazz.getSuperclass();
        if(superClazz!=null  &&  superClazz !=Object.class){
            return getFieldByName(fieldName, superClazz);
        }

        //如果本类和父类都没有，则返回空
        return null;
    }


    private static void existSameRowData(Sheet sheet,String[] uniqueFields, LinkedHashMap<String, Integer> colMap,int realRows)
            throws ExcelException{
        HashSet<String> set =  new HashSet<String>();
        //判断是否有重复行
        //1.获取uniqueFields指定的列
        Cell[][] uniqueCells = new Cell[uniqueFields.length][];
        for(int i=0;i<uniqueFields.length;i++){
            int col = colMap.get(uniqueFields[i]);
            uniqueCells[i] = sheet.getColumn(col);
        }
        //2.从指定列中寻找重复行
        for(int i=1;i<realRows;i++){
            for(int j=0;j<uniqueFields.length;j++){
                String currentContent = uniqueCells[j][i].getContents();
                if(set.contains(currentContent)){
                    throw new ExcelException("Excel中有重复行，请检查");
                }
                set.add(currentContent);
            }
        }
    }

    public static LinkedHashMap<String, String> getFieldMap(){
        LinkedHashMap<String, String> fieldMap = new LinkedHashMap<String, String>();
        fieldMap.put("包装箱号", "boxno");
//    	fieldMap.put("箱号", "boxno2");
        fieldMap.put("机器序列号", "boxsn");
        fieldMap.put("mac", "mac");
        fieldMap.put("ca_id", "caid");
        fieldMap.put("cmmac", "cmmac");
        fieldMap.put("wifimac", "wifimac");
        return fieldMap;
    }


    public static void main(String[] args) throws Exception{
        String directory = "F:/V1_BOXUPDATE";
        File file = new File(directory);
        if (!file.exists()) {
            file.mkdir();
        }
        File[] files = file.listFiles();
        LinkedHashMap<String, String> fieldMap = getFieldMap();
        String[] uniqueFields = new String[]{"cmmac"};
        List<Nosupport200mupdate> resultList = new ArrayList<Nosupport200mupdate>();
        for (int i = 0; i < files.length; i++) {
            // 如果为文件，记下当前的路径
            if (files[i].isFile()){
                String filetype = files[i].getPath().substring(files[i].getPath().lastIndexOf(".") + 1);
                if ("xls".equalsIgnoreCase(filetype)) {
                    FileInputStream in = new FileInputStream(files[i]);
                    resultList.addAll(ExcelUtil.excelToList(in,Nosupport200mupdate.class, fieldMap, uniqueFields));
                }
            }
        }

    }


}

