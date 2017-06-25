package hbasedemo;

import java.io.IOException;
import java.util.NavigableMap;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;

public class HbaseUtil {
	
	private Configuration conf = HBaseConfiguration.create();

	public HbaseUtil() {
		conf.set("hbase.zookeeper.quorum", "master:2181,slave1:2181,slave2:2181");
	}
	public void createTable(String talName) throws IOException{
		TableName tableName = TableName.valueOf(talName);
		Connection conn = ConnectionFactory.createConnection(conf);
		//create table ÓÉregionserver ¸ºÔð
		Admin admin = conn.getAdmin();
		HTableDescriptor desc = new HTableDescriptor(tableName);
		desc.addFamily(new HColumnDescriptor("cf1"));
		desc.addFamily(new HColumnDescriptor("cf2"));
		admin.createTable(desc);
	    
	}
	
	public void scanTable(String talName) throws IOException{
		TableName tableName = TableName.valueOf(talName);
		Connection conn = ConnectionFactory.createConnection(conf);
		ResultScanner rs = conn.getTable(tableName).getScanner(new Scan());
		rs.forEach(r->{
			String rowkey = Bytes.toString(r.getRow());
		    NavigableMap<byte[], NavigableMap<byte[], NavigableMap<Long, byte[]>>> map = r.getMap();	
		    for(byte[] family: map.keySet()){
		    	String s_family = Bytes.toString(family);
		    	 NavigableMap<byte[], NavigableMap<Long, byte[]>> qtvMap = map.get(family);
		    	 for(byte[] qualifier: qtvMap.keySet()){
		    		 String s_qualifier = Bytes.toString(qualifier);
		    		 NavigableMap<Long, byte[]> t2vMap = qtvMap.get(qualifier);
		    		 for(Long timeStamp: t2vMap.keySet()){
		    			 String str = String.format("rowkey: %s, column: %s:%s, value: %s, timestamp:%s",
		    					 rowkey, s_family, s_qualifier, Bytes.toString(t2vMap.get(timeStamp)), timeStamp);
		    			 System.out.println(str);
		    		 }
		    	 }
		    }
		
		});
	}
	public static void main(String[] args) throws IOException {
		HbaseUtil hbaseUtil = new HbaseUtil();
		//hbaseUtil.createTable("myTable");
         hbaseUtil.scanTable("emp");
	}

}
