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
    // 导入文件
	@Transactional
	public String importFile() {
		BufferedReader br =null;     
		List<String> devicenos = new ArrayList<String>();
		try {
			 br = new BufferedReader(new InputStreamReader(new FileInputStream(myFile)));                   
			 String deviceno;
			 while ((deviceno = br.readLine()) != null) {
				if(deviceno.contains(" ")){
					msg = "导入的数据行中含有非法空格字符!";
					return SUCCESS;
				}
				devicenos.add(deviceno);
			 }
			 br.close();
			 this.subInfoFacade.batchSaveRecords(devicenos);
			 msg = "数据导入成功!";
		} catch (Exception e) {
			 msg = "数据导入失败!"+e.getMessage();
			e.printStackTrace();
		} 
		return SUCCESS;
	}
	
	//导出文件
	@Transactional
	public String exportFile() {
		HttpServletResponse response =  super.getResponse();
		  // 1.文件下载响应头
	    response.setHeader("Content-Disposition", "attachment;filename=underwrite.xls");
	    try{
		    // 2.响应到浏览器
		    WritableWorkbook workbook = Workbook.createWorkbook(response.getOutputStream());
		    // 创建工作簿sheet
		    WritableSheet sheet = workbook.createSheet("underwrite", 0);
		    // 3.设置column名
		    sheet.addCell(new Label(0, 0, "姓名"));
		    sheet.addCell(new Label(1, 0, "安装地址"));
		    sheet.addCell(new Label(2, 0, "智能卡/MAC"));
		    sheet.addCell(new Label(3, 0, "移动电话"));
		    // 4.把核保的数据填充到工作簿中 service调用selectExport()查询数据库
		    List<NormalSubTerminalVo> list = subInfoFacade.selectExport();
		    HttpServletRequest request = super.getRequest();
		    if(StringUtils.isNotBlank(request.getParameter("deviceno"))){
		    	Map<String, Object> params = new HashMap<String, Object>();
		    	params.put("deviceno", request.getParameter("deviceno"));
		    	list.addAll(this.subInfoFacade.queryTernimalInfoListByResourceno(params));
		    }
		    for (int i = 0, j = 1; i < list.size(); i++, j++) {
		    	NormalSubTerminalVo underwrite = list.get(i);
		       //设置列宽
		        sheet.setColumnView(i, 16);
		        //重新设置部分列宽
		        sheet.setColumnView(3, 14);
		        sheet.setColumnView(6, 10);
		        sheet.setColumnView(7, 10);
		        //设置行高
		        sheet.setRowView(i, 350);
		        //设置字体的attribute
		        WritableFont font1=new WritableFont(WritableFont.createFont("楷体 _GB2312"), 12, WritableFont.NO_BOLD);
		        WritableCellFormat format1=new WritableCellFormat(font1);
		        sheet.addCell(new Label(0, j, underwrite.getCustomername(),format1));
		        sheet.addCell(new Label(1, j, underwrite.getAddress(),format1));
		        sheet.addCell(new Label(2, j, underwrite.getDeviceno(),format1));
		        sheet.addCell(new Label(3, j, underwrite.getCustmobile(),format1));
		    }
	        // 5.写入数据
		    workbook.write();
		    // 6.关闭资源
		    workbook.close();
		    this.subInfoFacade.clearOldRecords();
	   }catch (Exception e){
	        e.printStackTrace();
	    }
	    return "singleData";
	}
}
