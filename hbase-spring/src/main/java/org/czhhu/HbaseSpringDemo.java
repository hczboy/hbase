package org.czhhu;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.hadoop.hbase.HbaseTemplate;
import org.springframework.data.hadoop.hbase.RowMapper;

public class HbaseSpringDemo {
	private static ApplicationContext ctx;
	
	public static void main(String[] args) {
		ctx = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
		HbaseTemplate template = (HbaseTemplate)ctx.getBean("hbaseTemplate");
		hbaseScan(template);
	}
	
	private static void hbaseScan(HbaseTemplate template){
		Scan scan = new Scan();
		//scan.addFamily(Bytes.toBytes("info"));
		List<Map<String, String>> list= template.find("emp", scan, new RowMapper<Map<String, String>>(){

			public Map<String, String> mapRow(Result result, int rowNum) throws Exception {
			    List<Cell> list = result.listCells();
			    Map<String, String> map = new HashMap<>();
			    if(list != null && list.size()>0){
			    	for(Cell cell: list){
			    		String rowkey = Bytes.toString(cell.getRowArray(), cell.getRowOffset(), cell.getRowLength());
			    		String value = Bytes.toString(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength());
			    		String family = Bytes.toString(cell.getFamilyArray(), cell.getFamilyOffset(),cell.getFamilyLength());
			    		
			    		String qualifier = Bytes.toString(cell.getQualifierArray(),cell.getQualifierOffset(), cell.getQualifierLength());
			    		map.put(family + ":" + qualifier, value);
			    	}
			    }
			    return map;
			}
			
		});
		for(Map<String, String> m: list){
			System.out.println(m);
		}
	}
}
