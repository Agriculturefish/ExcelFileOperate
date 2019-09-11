import com.haoyuan.business.ExcelOpeate;
import com.haoyuan.domain.Nosupport200mupdate;
import com.haoyuan.exception.ExcelException;
import com.haoyuan.util.ExcelUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class ExcelOperateTest {
    private ExcelOpeate excelOpeate = new ExcelOpeate();

    public void testdeleRedisDB() throws FileNotFoundException, ExcelException {
        // cs.delete();
    }

    //jxl只能支持xls格式的文件，对于xlsx格式就不再支持
    public void testImportExcelToDB() throws FileNotFoundException, ExcelException {
        //String directory = "F:/V1_BOXUPDATE/tianwei9/112572订单（深圳天威HCMY-20T0D09）1500台2015.12.10(柏英特）H.xls";
        String directory = "F:/V1_BOXUPDATE/tianwei";
        File file = new File(directory);
        if (!file.exists()) {
            file.mkdir();
        }
        long startTime=System.currentTimeMillis();   //获取开始时间
        List<String> noFileNames = batchSaveFromFiles(file);
        if(noFileNames!=null && noFileNames.size()>0){
            for (String filename : noFileNames) {
                System.out.println(filename);
            }
        }
        long endTime=System.currentTimeMillis(); //获取结束时间
        System.out.println("程序运行时间： "+(endTime-startTime)+"ms");
    }
    //仅限目录
    private List<String> batchSaveFromFiles(File file){
        List<String> noFileNames = new ArrayList<String>();
        LinkedHashMap<String, String> fieldMap = ExcelUtil.getFieldMap();
        String[] uniqueFields = new String[]{"cmmac"};
        List<Nosupport200mupdate> resultList = null;
        File[] files = file.listFiles();
        for(File file2:files){
            // 如果为文件，记下当前的路径
            if (file2.isFile()){
                resultList = new ArrayList<Nosupport200mupdate>();
                String filetype = file2.getPath().substring(file2.getPath().lastIndexOf(".") + 1);
                if ("xls".equalsIgnoreCase(filetype)) {
                    FileInputStream in;
                    try {
                        in = new FileInputStream(file2);
                        resultList.addAll(ExcelUtil.excelToList(in,Nosupport200mupdate.class, fieldMap, uniqueFields));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (ExcelException e) {
                        noFileNames.add(file2.getName());
                        e.printStackTrace();
                    }
                }
                excelOpeate.batchSaveRecords(resultList);
            }/*else{
				func(file2);
			}*/
        }
        return noFileNames;
    }


    private  List<String> func(File file){
        List<String> noFileNames = new ArrayList<String>();
        LinkedHashMap<String, String> fieldMap = ExcelUtil.getFieldMap();
        String[] uniqueFields = new String[]{"cmmac"};
        List<Nosupport200mupdate> resultList = new ArrayList<Nosupport200mupdate>();
        if (file.isFile()){
            resultList = new ArrayList<Nosupport200mupdate>();
            String filetype = file.getPath().substring(file.getPath().lastIndexOf(".") + 1);
            if ("xls".equalsIgnoreCase(filetype)) {
                FileInputStream in;
                try {
                    in = new FileInputStream(file);
                    resultList.addAll(ExcelUtil.excelToList(in,Nosupport200mupdate.class, fieldMap, uniqueFields));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (ExcelException e) {
                    noFileNames.add(file.getName());
                    e.printStackTrace();
                }
            }
            excelOpeate.batchSaveRecords(resultList);
        }else{
            File[] files = file.listFiles();
            if(file.isDirectory()){
                for(File file2:files){
                    // 如果为文件，记下当前的路径
                    if (file2.isFile()){
                        resultList = new ArrayList<Nosupport200mupdate>();
                        String filetype = file2.getPath().substring(file2.getPath().lastIndexOf(".") + 1);
                        if ("xls".equalsIgnoreCase(filetype)) {
                            FileInputStream in;
                            try {
                                in = new FileInputStream(file2);
                                resultList.addAll(ExcelUtil.excelToList(in,Nosupport200mupdate.class, fieldMap, uniqueFields));
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (ExcelException e) {
                                noFileNames.add(file2.getName());
                                e.printStackTrace();
                            }
                        }
                        excelOpeate.batchSaveRecords(resultList);
                    }else{
                        func(file2);
                    }
                }
            }
        }
        return noFileNames;
    }
}
