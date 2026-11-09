package com.gamil.api.data;
import com.gamil.api.core.Json;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.InputStream;
public final class Cases {
 public static Object[][] load(String file) throws Exception {
  try(InputStream in=Cases.class.getResourceAsStream("/data/"+file)) {
   if(in==null) throw new IllegalArgumentException("Missing dataset "+file);
   JsonNode rows=Json.MAPPER.readTree(in); Object[][] data=new Object[rows.size()][1];
   for(int i=0;i<rows.size();i++) data[i][0]=rows.get(i); return data;
  }
 }
}
