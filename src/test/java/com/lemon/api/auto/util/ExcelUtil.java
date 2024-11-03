package com.lemon.api.auto.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.lemon.api.auto.pojo.WriteBackData;

public class ExcelUtil {
	
	//该方法取出的map集合格式为   列名：索引（ApiId：0）
	public static Map<String, Integer> loadCellNameAndIndexMapping(Sheet sheet) {
		Map<String, Integer> cellNameAndIndexMapping = new HashMap<String, Integer>();
		//获取第一行也就是标题行
		Row titleRow = sheet.getRow(0);
		//获取Excel的实际列数 ，titleRow.getLastCellNum()会在最大的索引值上面加1
		int lastCellnum = titleRow.getLastCellNum(); 
		//所以只能i < lastCellnum
		for (int i = 0; i < lastCellnum; i++) {
			//获取标题行的每一列
			Cell cell = titleRow.getCell(i);
			//将每一列数据当做字符串处理
			cell.setCellType(CellType.STRING);
			//依次取出标题行的每一列的值
			String title = cell.getStringCellValue();
			//进行字符串截取
			title = title.substring(0, title.indexOf("("));
			cellNameAndIndexMapping.put(title, i);
		}
		return cellNameAndIndexMapping;
	}
	
	/**读取表单标题行的映射数据（列索引对应标题的映射）
	 * @param sheet
	 * @return
	 */
	//该方法取出的map集合格式为   索引：列名（0：ApiId）
	public static Map<Integer,String> loadIndexAndCellNameMapping(Sheet sheet) {
		Map<Integer,String> indexAndcellNameMapping = new HashMap<Integer,String>();
		//获取第一行也就是标题行
		Row titleRow = sheet.getRow(0);
		//获取Excel的实际列数 ，titleRow.getLastCellNum()会在最大的索引值上面加1
		int lastCellnum = titleRow.getLastCellNum(); 
		//所以只能i < lastCellnum
		for (int i = 0; i < lastCellnum; i++) {
			//获取标题行的每一列
			Cell cell = titleRow.getCell(i);
			//将每一列数据当做字符串处理
			cell.setCellType(CellType.STRING);
			//依次取出标题行的每一列的值
			String title = cell.getStringCellValue();
			//进行字符串截取，只要 "(" 之前的标题
			//然后将索引和标题存到map，构成映射
			title = title.substring(0, title.indexOf("("));
			indexAndcellNameMapping.put(i, title);
			//将标题和索引存到map，形成映射；便于根据列明取到索引
			cellNameAndIndexMapping.put(title, i);
		}
		return indexAndcellNameMapping;
	}
	
	//该集合存放的数据格式是：标题-索引；便于根据标题列名取到标题索引
	public static Map<String, Integer> cellNameAndIndexMapping = new HashMap<String, Integer>();
	
	//先准备一个list集合，用于储存需要回写的数据
	//需要回写的数据要依靠 caseId（用例信息表的用例编号）+列名+content（回写的内容）
	public static List<WriteBackData> writeBackDatas = new ArrayList<WriteBackData>();
	
	
	//该集合存放的是表单第一列的值和值所对应的索引，数据回写时，根据用例编号来获取行索引
	public static Map<String, Integer> rowIdentifierAndIndexMapping = new HashMap<String, Integer>();
	
	/**int sheetIndex：表单的索引
	 * @param <T>
	 * @return
	 */
	public static <T> List<T> readPojos(Class<T> clazz,int sheetIndex){	
		//准备list集合,不写集合类型，传入什么类型的对象就是什么类型
		List list = new ArrayList();
		InputStream inputStream = null;	
		try {
			//创建workbook工作簿对象
			File file = new File("src/test/resources/cases_v7.xlsx");
			//准备输入流
			inputStream = new FileInputStream(file);
			//创建工作簿
			Workbook workbook = WorkbookFactory.create(inputStream);
			//创建第二张表单对象===接口信息表
			Sheet sheet = workbook.getSheetAt(sheetIndex);
			//获取表单标题的的索引和列名的集合     其格式为 索引：列名（0：ApiId）    加载映射关系
			Map<Integer, String> cellIndexAndcellNameMapping = loadIndexAndCellNameMapping(sheet);
			//拿到Excel表的实际行数
			int lastRownum = sheet.getLastRowNum();
			//拿到实际的列数,列数会在实际列数的基础上+1
			int lastCellnum = sheet.getRow(0).getLastCellNum();
			//第一行是标题，从第二行开始，循环取出每一行
			for (int i = 1; i <= lastRownum; i++) {
				//通过字节码创建对应类型的对象   （调用newInstance方法得到一个对象）
				//每一行对应一个数据对象,再把每一个对象存入obj中
				Object obj = clazz.newInstance();
				//获取每一行
				Row dataRow = sheet.getRow(i);
				//循环每一列
				for (int j = 0; j < lastCellnum; j++) {
					//防止获取的行为空，导致空指针异常
					//判断行对象是否为空，若是为空的话就跳过这次执行
					if (dataRow==null) {
						//跳过这次对象执行
						continue;
					}
					//获取每一列    
					//设置空列的处理策略
					//MissingCellPolicy.CREATE_NULL_AS_BLANK:防止获取的列值没有数据，导致空指针异常
					//如果遇到无内容的列，默认为空字符串，而不是null
					Cell cell = dataRow.getCell(j,MissingCellPolicy.CREATE_NULL_AS_BLANK);
					cell.setCellType(CellType.STRING);
					//取到列上面的值
					String cellValue = cell.getStringCellValue();
					//基于反射去封装数据
					//Class clazz = Case.class;
					//根据字节码到推出类里面的细节（定义的属性、方法，他都能拿到）
				    //获取要反射调用的set方法名（列标题前面拼接一个set字符串就得到了此列要反射的方法名）
					String methodName = "set"+cellIndexAndcellNameMapping.get(j);//获取value值
					//拿到set方法对象    （两个参数：第一个是方法对象，第二个是方法的类型）
					//当一个方法中有多个类型，比如int、string类型，那么就要写成  int.class,string.class
					Method method = clazz.getMethod(methodName, String.class);
					//反射调用方法    (两个参数：第一个是要操作的对象，第二个是设置进去的值)
					method.invoke(obj, cellValue);
					//j==0意味着正在处理第一列（用例编号caseId列）
					//该集合用于提供根据用例编号取出行索引
					if (j==0) {
						//存入的数据格式为cellValue：i  存入rowIdentifierAndIndexMapping集合中
						rowIdentifierAndIndexMapping.put(cellValue, i);
					}
				}
				//把每一个api对象存入apis集合中
				list.add(obj);
			}
	
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if (inputStream!=null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return list;		
	}
	
	/**数据回写
	 * @param sheetnum：表单的索引
	 * @param rownum：根据用例编号取出行索引,
	 * @param cellnum：根据列名得到列索引
	 * @param content：需要回写的内容
	 */
	public static void write(int sheetnum,int rownum,int cellnum,String content) {
		//回写断言结果到excel用例
		InputStream inputStream = null;
		OutputStream outputStream = null;
		try {
			//1.创建Workbook工作薄对象
			File file = new File("src/test/resources/cases_v7.xlsx");
			//准备输入流
			inputStream = new FileInputStream(file);
			//1.创建工作薄
			Workbook workbook = WorkbookFactory.create(inputStream);
			//2.创建sheet表单对象
			Sheet sheet = workbook.getSheetAt(sheetnum);

			//根据行索引取出相应的行
			Row row = sheet.getRow(rownum);
			
			//通过列索引取出要写入的列 ，设置空列的处理策略：防止获取的列值没有数据，导致空指针异常
			Cell cell = row.getCell(cellnum,MissingCellPolicy.CREATE_NULL_AS_BLANK);
			cell.setCellType(CellType.STRING);
			//将值放到此列
			cell.setCellValue(content);
			//准备输出流对象
			outputStream = new FileOutputStream(file);
			//将数据写入文件
			workbook.write(outputStream);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try {
				if(inputStream!=null){
					inputStream.close();
				}
				if(outputStream!=null){
					outputStream.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	
	/**
	 * 封装批量回写数据的方法
	 */
	public static void batchWriteBackDatas() {
		//获取集合中的数据
		List<WriteBackData> writeBackDatas = ExcelUtil.writeBackDatas;
		//回写断言和响应报文到Excel表单中
		InputStream inputStream = null;   //读取的io流
		OutputStream outputStream = null;  //写入的io流
		try {
			//1.创建Workbook工作薄对象
			File file = new File("src/test/resources/cases_v7.xlsx");
			//准备输入流
			inputStream = new FileInputStream(file);
			//1.创建工作薄
			Workbook workbook = WorkbookFactory.create(inputStream);
			//2.创建sheet表单对象
			Sheet sheet = workbook.getSheetAt(1);
			//循环每一个writeBackData对象，获取
			for (WriteBackData writeBackData : writeBackDatas) {
				//根据用例编号获取到相应的行
				//根据循环获取每个对象的用例编号
				String caseId = writeBackData.getCaseId();
				//根据用例编号获取用例的行索引
				int rownum = ExcelUtil.rowIdentifierAndIndexMapping.get(caseId);
				//根据行索引取出相应的行
				Row row = sheet.getRow(rownum);
				
				//根据循环获取获取每个对象的用例列名（列标题）
				String cellName = writeBackData.getCellName();
				//通过列名获取列的索引
				int cellnum = ExcelUtil.cellNameAndIndexMapping.get(cellName);
				//通过列索引取出要写入的列 ，设置空列的处理策略：防止获取的列值没有数据，导致空指针异常
				Cell cell = row.getCell(cellnum,MissingCellPolicy.CREATE_NULL_AS_BLANK);
				cell.setCellType(CellType.STRING);
				//获取需要写入的值，将值放到此列
				String content = writeBackData.getContent();
				cell.setCellValue(content);
			}
			//准备输出流对象
			outputStream = new FileOutputStream(file);
			//将数据写入文件
			workbook.write(outputStream);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try {
				if(inputStream!=null){
					inputStream.close();
				}
				if(outputStream!=null){
					outputStream.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	
	/**因为获取回写数据的方法类似，所以将获取回写数据的方法封装起来（断言回写和响应报文回写都能调用该方法）
	 * 将数据存入writeBackDatas集合中
	 * @param caseId：用例编号
	 * @param cellName：列名（列标题）
	 * @param content：需要写入的数据
	 */
	public static void saveWriteBackData(String caseId,String cellName,String content) {		
		//包装WriteBackData对象
		WriteBackData writeBackData = new WriteBackData(caseId, cellName, content);
		//将数据对象存入writeBackDatas集合中
		ExcelUtil.writeBackDatas.add(writeBackData);
	}
}
