package com.meiren.utils;

import com.meiren.common.utils.DateUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.NumberToTextConverter;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author caoguoshun   （这里替换为自己的名字）
 * @ClassName: ExcelUtil
 * @Description: ${todo}
 * @date 2017/7/17 9:55
 */

	public abstract class ExcelUtil {

		/**

		 * 读取Excel,支持任何不规则的Excel文件,

		 * 外层List表示所有的数据行，内层List表示每行中的cell单元数据位置

		 * 假设获取一个Excel第三行第二个单元格的数据，例子代码：

		 * FileInputStream excelStream = new FileInputStream(path);

		 * List<List<Object>> list = ExcelUtil.readExcel(excelStream);

		 * System.out.println(list.get(2).get(1));//第三行第二列,索引行位置是2,列的索引位置是1

		 * @param excelStream Excel文件流

		 * @param sheetIndex Excel-Sheet 的索引

		 * @return List<List<Object>>

		 * @throws Exception

		 */
		public static List<List<Object>> readExcel(InputStream excelStream,int sheetIndex)throws Exception {
			List<List<Object>> datas = new ArrayList<List<Object>>();
			Workbook workbook = WorkbookFactory.create(excelStream);
			//只读取第一个sheet

			Sheet sheet = workbook.getSheetAt(sheetIndex);
			int rows = sheet.getPhysicalNumberOfRows();
			for (int i = 0; i < rows; i++) {
				Row row = sheet.getRow(i);
				short cellNum = row.getLastCellNum();
				List<Object> item = new ArrayList<Object>(cellNum);
				for(int j=0;j<cellNum;j++){
					Cell cell = row.getCell(j);
					Object value = ExcelUtil.getCellValue(cell);
					item.add(value);
				}
				datas.add(item);
			}
			excelStream.close();
			return datas;
		}

		/**

		 * 读取Excel,支持任何不规则的Excel文件,默认读取第一个sheet页

		 * 外层List表示所有的数据行，内层List表示每行中的cell单元数据位置

		 * 假设获取一个Excel第三行第二个单元格的数据，例子代码：

		 * FileInputStream excelStream = new FileInputStream(path);

		 * List<List<Object>> list = ExcelUtil.readExcel(excelStream);

		 * System.out.println(list.get(2).get(1));//第三行第二列,索引行位置是2,列的索引位置是1

		 * @param excelStream Excel文件流

		 * @return List<List<Object>>

		 * @throws Exception

		 */
		public static List<List<Object>> readExcel(InputStream excelStream)throws Exception {
			return readExcel(excelStream,0);
		}

		/**

		 * 设置Cell单元的值

		 *

		 * @param cell

		 * @param value

		 */
		public static void setCellValue(Cell cell, Object value) {
			if (value != null) {
				if (value instanceof String) {
					cell.setCellValue((String) value);
				} else if (value instanceof Number) {
					cell.setCellValue(Double.parseDouble(String.valueOf(value)));
				} else if (value instanceof Boolean) {
					cell.setCellValue((Boolean) value);
				} else if (value instanceof Date) {
					cell.setCellValue((Date) value);
				} else {
					cell.setCellValue(value.toString());
				}
			}
		}

		/**

		 * 获取cell值

		 *

		 * @param cell

		 * @return

		 */
		public static Object getCellValue(Cell cell) {
			Object value = null;
			if (null != cell) {
				switch (cell.getCellType()) {
				// 空白

				case Cell.CELL_TYPE_BLANK:
					break;
				// Boolean

				case Cell.CELL_TYPE_BOOLEAN:
					value = cell.getBooleanCellValue();
					break;
				// 错误格式

				case Cell.CELL_TYPE_ERROR:
					break;
				// 公式

				case Cell.CELL_TYPE_FORMULA:
					Workbook wb = cell.getSheet().getWorkbook();
					CreationHelper crateHelper = wb.getCreationHelper();
					FormulaEvaluator evaluator = crateHelper.createFormulaEvaluator();
					value = getCellValue(evaluator.evaluateInCell(cell));
					break;
				// 数值

				case Cell.CELL_TYPE_NUMERIC:
					// 处理日期格式

					if (org.apache.poi.ss.usermodel.DateUtil.isCellDateFormatted(cell)) {
						value = cell.getDateCellValue();
					} else {
						value = NumberToTextConverter.toText(cell.getNumericCellValue());
					}
					break;
				case Cell.CELL_TYPE_STRING:
					value = cell.getRichStringCellValue().getString();
					break;
				default:
					value = null;
				}
			}
			return value;
		}


	/**
	 * 用法：
	 *
	 * ArrayList<ArrayList<Object>> listContent = new ArrayList<>();
	 * List<String> listFields = Lists.newArrayList("口令ID", "状态", "邀请时间", "邀请过期时间", "被邀请人手机号", "被邀请人角色", "邀请人", "邀请人角色");
	 * ArrayList<FamilyMemberInviteEntity> list = (ArrayList<FamilyMemberInviteEntity>) result.getData();
	 * ArrayList<Object> listInner = new ArrayList<>();
	 * listInner.add('2');
	 * listInner.add('美人信息');
	 * listInner.add('2017-11-20 13-53-12');
	 * listContent.add(listInner);
	 * HSSFWorkbook workbook = new HSSFWorkbook();
	 * String returnFileName = ExcelUtil.exportExcel("统计", listFields, listContent, workbook);
	 *
	 * 导出excel文件
	 * @param name 文件名 例："口令统计"
	 * @param listFields 列名	例：{"ID", "名称", "时间"...}
	 * @param listContent 具体内容 n*m list 例：{{'a','b','c'...},{'d','d','f'...}...}
	 * @param workbook HSSFWorkbook 对象 例：new HSSFWorkbook()
	 *
	 * @author	jijc
	 *
	 * @return 例："口令统计.xls"
	 */
	public static String exportExcel(String name, List<String> listFields, ArrayList<ArrayList<Object>> listContent, HSSFWorkbook workbook)
			throws UnsupportedEncodingException {
			//创建当前工作表
			HSSFSheet sheet = workbook.createSheet(name);
			//设置标题
			//创建第一行
			HSSFRow headerrow = sheet.createRow(0);
			HSSFCell headercell = null;
			// 设置表格默认列宽度为20个字节
			sheet.setDefaultColumnWidth((short) 20);
			// 生成一个样式
			HSSFCellStyle style = workbook.createCellStyle();
			// 设置这些样式--背景色
			style.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
			style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			//边框
			style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
			style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
			style.setBorderRight(HSSFCellStyle.BORDER_THIN);
			style.setBorderTop(HSSFCellStyle.BORDER_THIN);
			style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			// 自动换行
			style.setWrapText(true);
			// 生成一个字体
			HSSFFont font = workbook.createFont();
			font.setFontHeightInPoints((short) 11);
			font.setColor(HSSFColor.ORCHID.index);
			font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
			font.setFontName("宋体");
			// 把字体 应用到当前样式
			style.setFont(font);

			String returnFileName = "";
			if (listContent.size()>0) {
					// 产生表格标题行
					for (short i = 0; i < listFields.size(); i++) {
						headercell = headerrow.createCell(i);
						headercell.setCellStyle(style);
						HSSFRichTextString text = new HSSFRichTextString(listFields.get(i));
						headercell.setCellValue(text);
					}
					//产生数据行
					for (int i = 0; i < listContent.size(); i++) {
						HSSFRow row = sheet.createRow(i + 1);
						HSSFCell cell = null;
						for (int c = 0; c < listContent.get(i).size(); c++){
							cell = row.createCell(c);
							if(listContent.get(i).get(c) instanceof RichTextString){
								cell.setCellValue(
										(RichTextString) listContent.get(i).get(c));
							}else if(listContent.get(i).get(c) instanceof Double){
								cell.setCellValue(
										(Double) listContent.get(i).get(c));
							}else if(listContent.get(i).get(c) instanceof Date){
								cell.setCellValue(DateUtils.format(
										(Date) listContent.get(i).get(c),"yyyy-MM-dd hh-mm-ss"));
							}else {
								cell.setCellValue(
										listContent.get(i).get(c).toString());
							}
						}
					}
					returnFileName = URLEncoder.encode(name+".xls", "UTF-8");
				}
				return returnFileName;
		}
}
