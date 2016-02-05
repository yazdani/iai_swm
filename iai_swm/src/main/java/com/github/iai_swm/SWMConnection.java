package com.github.iai_swm;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import zmq.*;



/**
 * A simple {@link Subscriber} {@link NodeMain}.
 */
public class SWMConnection{

  SocketBase sc;
  Ctx ctx;
    
    public String testString(String name){
	System.out.println("Sendinging Request: \n"+name);
	return "mAMAMA";
    }

    public String queryTransform(String name)
    {
	String id, ref_id;
	id = "";
	ref_id = "";
    	ctx = ZMQ.init(1);
	//	if(name.equals("genius") || name.equals("human"))
	
	id = "e8015bd0-2eca-4764-8298-acf81eb987b5";
	ref_id = "853cb0f0-e587-4880-affe-90001da1262d";
	    
  	sc = ZMQ.socket(ctx, ZMQ.ZMQ_REQ);
  	boolean rc = ZMQ.connect(sc, "tcp://127.0.0.1:22422");
  	

	String data = "{ "+
      			"\"@worldmodeltype\": \"RSGQuery\","+
      			"\"query\": \"GET_TRANSFORM\","+
      			"\"id\": \""+id+"\","+
      			"\"idReferenceNode\": \""+ref_id+"\","+
      			"\"timeStamp\": {"+
      			"	\"@stamptype\": \"TimeStampUTCms\","+
      		"		\"stamp\": \"0.0\""+
      		"		} "+
	    "}";
	System.out.println("Sendinging Request: \n"+data);
	int result = ZMQ.send(sc,data.getBytes(ZMQ.CHARSET),data.length(),0);
	System.out.println("Sended \n"+result + " bytes");
	
	System.out.println("Waiting for Reply");
	Msg msg = ZMQ.recv(sc, 0);
	
	System.out.println("received replyt");
	JSONObject jsonObject = JSONObject.fromObject(new String(msg.data()));
	
	JSONObject transform =  jsonObject.getJSONObject("transform");
	//List transform_list =  JSONArray 
	JSONArray array = transform.getJSONArray("matrix");
	JSONArray r0 =  array.getJSONArray(0);
	JSONArray r1 =  array.getJSONArray(1);
	JSONArray r2 =  array.getJSONArray(2);
	JSONArray r3 =  array.getJSONArray(3);
	
	System.out.println("Translation");
	System.out.println("("+      r0.getDouble(3)+","+      r1.getDouble(3)+","+      r2.getDouble(3)+")");
	//log.info(new String(msg.data()));
	//ZMQ.close(sc);
	//ZMQ.term(ctx);
	
	//Matrix4d poseMat = new Matrix4d(pose_arr);
	
        String pose_arr = "[["+r0.getDouble(3)+","+r1.getDouble(3)+","+r2.getDouble(3)+","+r3.getDouble(3)+"]"+
	    "["+r3.getDouble(0)+","+r2.getDouble(0)+","+r1.getDouble(0)+","+r0.getDouble(0)+"]]";

	//FIXME remove shutown
	
	ZMQ.close(sc);
	ZMQ.term(ctx);
	
	//
	return pose_arr;
	
    }
    
    
    
    
    
    
    
    public SWMConnection() {
	//	ctx = ZMQ.init(1);
	System.out.println("moiiiweweewensen\n");
	
	//    	sc = ZMQ.socket(ctx, ZMQ.ZMQ_REQ);
	// 	boolean rc = ZMQ.connect(sc, "tcp://127.0.0.1:22422");
	System.out.println("moiiinsen\n");
	//	testString("hello");
	//queryTransform("92876bfd-3b6d-44a3-a9a1-a7b36c53acd1" ,"e379121f-06c6-4e21-ae9d-ae78ec1986a1" , null);
    }
}
