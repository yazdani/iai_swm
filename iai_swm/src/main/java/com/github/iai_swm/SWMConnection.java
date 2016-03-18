package com.github.iai_swm;

import java.lang.Math.*;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import java.util.*;
import zmq.*;



/**
 * A simple {@link Subscriber} {@link NodeMain}.
 */
public class SWMConnection{

  SocketBase sc;
  Ctx ctx;
    
    //#######################################################################//
    //                                                                       //
    //                        Everything related to the Agent                //
    //                                                                       //
    //#######################################################################//


    /**
     *
     *     Getting the RefID of the Agents
     *     in this case: THE ANIMALS
     *
     **/
    public String getAgentsREFID()
    {
	ctx = ZMQ.init(1);                         
	sc = ZMQ.socket(ctx, ZMQ.ZMQ_REQ);
	String data;
	int result;
	Msg msg;
	JSONObject jsonObject;
	JSONArray array;
	String ref_id;
	boolean rc = ZMQ.connect(sc, "tcp://127.0.0.1:22422");

	data ="{"+
	    "\"@worldmodeltype\": \"RSGQuery\"," +"\"query\": \"GET_NODES\","+
	    "\"attributes\":["+
	    "{\"key\": \"name\", \"value\": \"animals\"}," +"]"+ "}";
 	result = ZMQ.send(sc,data.getBytes(ZMQ.CHARSET),data.length(),0);
 	msg = ZMQ.recv(sc, 0);
 	jsonObject = JSONObject.fromObject(new String(msg.data()));
 	//System.out.println(jsonObject);
	array =  jsonObject.getJSONArray("ids");
       	ZMQ.close(sc);
	ZMQ.term(ctx);
	return array.getString(0);
    }

    /**
     *
     *     Getting the ID of the Agent by 'name'
     *
     *
     **/
   public String getAgentsID(String name)
    {
	//System.out.println("queryTransform function");
    	ctx = ZMQ.init(1);               
	          
  	sc = ZMQ.socket(ctx, ZMQ.ZMQ_REQ);
  	boolean rc = ZMQ.connect(sc, "tcp://127.0.0.1:22422");
  	//System.out.println("TEST3");
	String data ="{"+
	    "\"@worldmodeltype\": \"RSGQuery\"," +"\"query\": \"GET_NODES\","+
	    "\"attributes\":["+
	    "{\"key\": \"sherpa:agent_name\", \"value\": \""+name+"\"}," +"]"+ "}";
	int result = ZMQ.send(sc,data.getBytes(ZMQ.CHARSET),data.length(),0);
	Msg msg = ZMQ.recv(sc, 0);
	
	//System.out.println("received reply");
	JSONObject jsonObject = JSONObject.fromObject(new String(msg.data()));
	//System.out.println(jsonObject);
	
	JSONArray array =  jsonObject.getJSONArray("ids");
	ZMQ.close(sc);
	ZMQ.term(ctx);
	return array.getString(0);
    }
 
    /**
     *
     *  Getting a List back, e.g.
     * ((agents-name, agents-type, agents-transforms))
     *
     **/
    public String queryAgentsData(String name)
    {
	ctx = ZMQ.init(1);                         
	sc = ZMQ.socket(ctx, ZMQ.ZMQ_REQ);
      
	String ref_id, agents_id, agents_trans, result;

	ref_id = getAgentsREFID();
	agents_id = getAgentsID(name);
	agents_trans = getAgentsTransform(agents_id, ref_id);
	
 
	return "(("+name+","+"type"+","+agents_trans+"))";
    }

    /*
     *
     *      Getting the Transformation of the Agents
     *      by the ID and the REF-ID
     *
     **/
    public String getAgentsTransform(String id_, String ref_id_)
    {
	//System.out.println("AnimalTrans");
	String id = id_;
	String ref_id = ref_id_;//queryAgentsREFID();
    	ctx = ZMQ.init(1);        
	          
  	sc = ZMQ.socket(ctx, ZMQ.ZMQ_REQ);
  	boolean rc = ZMQ.connect(sc, "tcp://127.0.0.1:22422");
	String data = "{ "+
      			"\"@worldmodeltype\": \"RSGQuery\","+
      			"\"query\": \"GET_TRANSFORM\","+
      			"\"id\": \""+id+"\","+
      			"\"idReferenceNode\": \""+ref_id+"\","+
      			"\"timeStamp\": {"+
      			"\"@stamptype\": \"TimeStampUTCms\","+
	    "\"stamp\": \"0.0\""+
      		"} "+
	    "}";

	int result = ZMQ.send(sc,data.getBytes(ZMQ.CHARSET),data.length(),0);
	Msg msg = ZMQ.recv(sc, 0);
	JSONObject jsonObject = JSONObject.fromObject(new String(msg.data()));
	//System.out.println(jsonObject);
	JSONObject transform =  jsonObject.getJSONObject("transform");
 	//System.out.println(transform);
	JSONArray array = transform.getJSONArray("matrix");
	JSONArray r0 =  array.getJSONArray(0);
	JSONArray r1 =  array.getJSONArray(1);
	JSONArray r2 =  array.getJSONArray(2);
	JSONArray r3 =  array.getJSONArray(3);
	double[][] ar_vec = new double[3][3];
	ar_vec[0][0] = r0.getDouble(0);
	ar_vec[0][1] = r0.getDouble(1);
	ar_vec[0][2] = r0.getDouble(2);
	ar_vec[1][0] = r1.getDouble(0);
	ar_vec[1][1] = r1.getDouble(1);
	ar_vec[1][2] = r1.getDouble(2);
	ar_vec[2][0] = r2.getDouble(0);
	ar_vec[2][1] = r2.getDouble(1);
	ar_vec[2][2] = r2.getDouble(2);
	System.out.println(r0.getDouble(0)+"   "+r0.getDouble(1)+"   "+"    "+r0.getDouble(2)+"    "+"         "+r1.getDouble(0)+"        "+"         "+r1.getDouble(1)+"     "+"      "+r1.getDouble(2)+"      "+"             "+r2.getDouble(0)+"         " +r2.getDouble(1)+"       "+"       "+r2.getDouble(2));



	double[] ant = MatrixToQuatExp(ar_vec);

	System.out.println(ant[0] + " und "+ant[1]+ " und " +ant[2]+ " und "+ant[3]);
	String pose_arr = "[["+r0.getDouble(3)+","+r1.getDouble(3)+","+r2.getDouble(3)+","+r3.getDouble(3)+"]"+
	    "["+ant[0]+","+(ant[1] * -1)+","+(ant[2] * -1)+","+ant[3]+"]]";

	ZMQ.close(sc);
	ZMQ.term(ctx);
	return pose_arr;
    }

    //########################################################//
    //                                                        //
    //          Objects in the Environment and of course      //
    //                        the Mountain                    //
    //                                                        //
    //########################################################//

    public String queryEntitiesData()
    {
	String list ="";
	
	String objref_id = getObjectsREFID();
	//System.out.println("bist UNwwwwwDlll222");
	String[][] array =getSelChildNodes(getChildNodes(objref_id));
	String mount = "";
	String environment= "";
	for(int i =0; i < array.length; i++)
	    {
		  if(containsEnvironment(array[i][1]))
			{
			    String tmp = getTheEnvironmentList(array[i][0]);
			    environment = environment+tmp;
			}
	    }
	return "("+environment+")";
	
    }

    public String getTheEnvironmentList(String array1)
    {
	String[][] array =getTheChildrens(getChildNodes(array1));
	String mount="";
	String river="";
	String house="";
	String wood="";
	
	for(int i = 0; i < array.length; i++)
	    {
		
		System.out.println("test "+array[i][1]);
		if(containsRiver(array[i][1]))
		    {
			String tmp = getTheRiverList(array[i][0],array[i][1]);
			river=river+tmp;
		    }else
		    if(containsMountain(array[i][1]))
		    {
			String tmp = getTheMountainList(array[i][0],array[i][1]);
			mount=mount+tmp;
		    }else
		    if(containsWood(array[i][1]))
		    {
			String tmp = getTheWoodList(array[i][0],array[i][1]);
			wood=wood+tmp;
		    }else
		    if(containsHouse(array[i][1]))
		    {
			String tmp = getTheHouseList(array[i][0],array[i][1]);
			house=house+tmp;
		    }
	    }
	return "("+mount+river+house+wood+")";

    }

    public String getTheHouseList(String array1, String array2)
    { 
	//System.out.println("Get the house list");
	Vector<String> childNds = new Vector<String>(1);
	String name_vector= array2;
	String type_vector = "house"; //@TODO:getTheType(array1);
	childNds = getChildNodes(array1);
	String[][] vec = new String[childNds.size()][2];
	vec = getTheChildrens(childNds);
	String combiner = "";
	String combiner1 = "";
	String txt = "";
	int setFlag = 0;
	String center_vector= "";
	String bbox1_vector= "";
	String bbox2_vector= "";
	for(int i = 0; i < vec.length; i++)
	    {
		if(containsBoundingBox(vec[i][1]))
		    {
			setFlag = 1;
			//		System.out.println("BoundingBox: "+vec[i][0]);
			txt = recursiveBBoxx(name_vector,type_vector, vec[i][0], txt, 1);
			return txt;
		    }

	    }

	if(setFlag == 0)
	    {
		for(int i = 0; i < vec.length; i ++)
		    {
			if(containsCenter(vec[i][1]))
			    {
				center_vector= getTheCenterTransform(array1);
				bbox1_vector=getTheMinBBox(array1);
				bbox2_vector=getTheMaxBBox(array1);
				
				return "("+name_vector+","+type_vector+","+center_vector+","+bbox1_vector+","+bbox2_vector+")";	
			    }
		    }
	    }
	return "Something went wrong inside House";
    }

    public String getTheWoodList(String array1, String array2)
    { 
	//System.out.println("Get the wood list");
	Vector<String> childNds = new Vector<String>(1);
	String name_vector= array2;
	String type_vector = "wood"; //@TODO:getTheType(array1);
	childNds = getChildNodes(array1);
	String[][] vec = new String[childNds.size()][2];
	vec = getTheChildrens(childNds);
	String combiner = "";
	String combiner1 = "";
	String txt = "";
	int setFlag = 0;
	String center_vector= "";
	String bbox1_vector= "";
	String bbox2_vector= "";
	for(int i = 0; i < vec.length; i++)
	    {
		if(containsBoundingBox(vec[i][1]))
		    {
			setFlag = 1;
			//		System.out.println("BoundingBox: "+vec[i][0]);
			txt = recursiveBBoxx(name_vector,type_vector, vec[i][0], txt, 1);
			return txt;
		    }

	    }

	if(setFlag == 0)
	    {
		for(int i = 0; i < vec.length; i ++)
		    {
			if(containsCenter(vec[i][1]))
			    {
				center_vector= getTheCenterTransform(array1);
				bbox1_vector=getTheMinBBox(array1);
				bbox2_vector=getTheMaxBBox(array1);
				
				return "("+name_vector+","+type_vector+","+center_vector+","+bbox1_vector+","+bbox2_vector+")";	
			    }
		    }
	    }
	return "Something went wrong inside Wood";
    }

    public String getTheMountainList(String array1, String array2)
    { 
	//	System.out.println("Get the mountain list");
	Vector<String> childNds = new Vector<String>(1);
	String name_vector= array2;
	String type_vector = "mountain"; //@TODO:getTheType(array1);
	childNds = getChildNodes(array1);
	String[][] vec = new String[childNds.size()][2];
	vec = getTheChildrens(childNds);
	String combiner = "";
	String combiner1 = "";
	String txt = "";
	int setFlag = 0;
	String center_vector= "";
	String bbox1_vector= "";
	String bbox2_vector= "";
	for(int i = 0; i < vec.length; i++)
	    {
		if(containsBoundingBox(vec[i][1]))
		    {
			setFlag = 1;
			txt = recursiveBBoxx(name_vector,type_vector, vec[i][0], txt, 1);
			return txt;
		    }

	    }

	if(setFlag == 0)
	    {
		for(int i = 0; i < vec.length; i ++)
		    {
			if(containsCenter(vec[i][1]))
			    {
				center_vector= getTheCenterTransform(array1);
				bbox1_vector=getTheMinBBox(array1);
				bbox2_vector=getTheMaxBBox(array1);
				
				return "("+name_vector+","+type_vector+","+center_vector+","+bbox1_vector+","+bbox2_vector+")";	
			    }
		    }
	    }
	return "Something went wrong inside mountain";
    }

    public String getTheRiverList(String array1, String array2)
    { 
	//	System.out.println("Get the river list");
	Vector<String> childNds = new Vector<String>(1);
	String name_vector= array2;
	String type_vector = "river"; //@TODO:getTheType(array1);
	childNds = getChildNodes(array1);
	String[][] vec = new String[childNds.size()][2];
	vec = getTheChildrens(childNds);
	String combiner = "";
	String combiner1 = "";
	String txt = "";
	int setFlag = 0;
	String center_vector= "";
	String bbox1_vector= "";
	String bbox2_vector= "";
	for(int i = 0; i < vec.length; i++)
	    {
		//	System.out.println("Go into loop of river list");
		if(containsBoundingBox(vec[i][1]))
		    {
			setFlag = 1;
			//	System.out.println("BoundingBox: "+vec[i][0]);
			txt = recursiveBBoxx(name_vector,type_vector, vec[i][0], txt, 1);
			return txt;
		    }
	    }

	if(setFlag == 0)
	    {
		for(int i = 0; i < vec.length; i ++)
		    {
			if(containsCenter(vec[i][1]))
			    {
				center_vector= getTheCenterTransform(array1);
				bbox1_vector=getTheMinBBox(array1);
				bbox2_vector=getTheMaxBBox(array1);
				return "("+name_vector+","+type_vector+","+center_vector+","+bbox1_vector+","+bbox2_vector+")";	
			    }
		    }
	    }
	return "Something went wrong inside river";
    }

    public String recursiveBBoxx(String objname, String type, String arg1, String txt, int count)
    {
	//	System.out.println("Inside RecursiveBBox");
	Vector<String> childNds = new Vector<String>(1);
	childNds = getChildNodes(arg1);
	String[][] vec = new String[childNds.size()][2];
	vec = getTheChildrens(childNds);
	String combiner = "";
	String center_vector= "";
	String bbox1_vector= "";
	String bbox2_vector= "";
	int setFlag = 0;
	for(int i = 0; i < vec.length; i ++)
	    {
		//	System.out.println("Flag is: " +setFlag);
		if(containsBoundingBox(vec[i][1]))
		    {
			setFlag = 1;
			combiner = recursiveBBoxx(objname, type, vec[i][0], txt, count);
			break;		
		    }
	    }
	//	System.out.println("out of for-loop with combiner: "+combiner);

	if(setFlag == 0 && vec.length != 0)
	    {
		for(int i = 0; i < vec.length; i ++)
		    {
			if(containsCenter(vec[i][1]))
			{
			    center_vector= getTheCenterTransform(arg1);
			    bbox1_vector=getTheMinBBox(arg1);
			    bbox2_vector=getTheMaxBBox(arg1);
			    combiner = combiner+ "("+objname+"_part"+count+","+type+","+center_vector+","+bbox1_vector+","+bbox2_vector+")";	
			    count = count + 1;
			    break;
			}
		    }
	    }
	
	txt = txt+combiner;
	String  id_pre = getParentID(getParentID(arg1));
	String id_past = getParentID(arg1);
	String name = objname;
        int counterA = count;
	
   	//System.out.println("arg1: "+arg1);
   	//System.out.println("txt: "+txt);
	Vector<String> childNdsA = new Vector<String>(1);
	childNdsA = getChildNodes(id_past);
	String[][] vecA = new String[childNdsA.size()][2];
	vecA = getTheChildrens(childNdsA);
	int index = 0;
	for(int i = 0; i < vecA.length; i++)
	    {
		if(vecA[i][0].equals(arg1))
		   {
		       index = i+1;
		   }else if(!vecA[i][0].equals(arg1))
		    {
		       if( getChildNodes(vecA[i][0]).size() != 0)
			   {
			       recursiveBBoxx(name, type, vecA[i][0],txt,counterA);
			   }
		    }
	    }
	//System.out.println("TEXTTT: "+txt);
	return txt;

    }


 public String getTheCenterTransform(String id)
    {
	//System.out.println("--->:");
	Vector<String> childNds = new Vector<String>(1);
	childNds = getChildNodes(id);
	String trans = "";
	for(int i = 0; i < childNds.size();i++)
	    {
		ctx = ZMQ.init(1);                         
		sc = ZMQ.socket(ctx, ZMQ.ZMQ_REQ);
		boolean rc = ZMQ.connect(sc, "tcp://127.0.0.1:22422");
	  	
		String data ="{"+
		    "\"@worldmodeltype\": \"RSGQuery\"," +"\"query\": \"GET_NODE_ATTRIBUTES\","+
		    "\"id\": "+childNds.get(i)+ "}";
		int result = ZMQ.send(sc,data.getBytes(ZMQ.CHARSET),data.length(),0);
		Msg msg = ZMQ.recv(sc, 0);
		JSONObject jsonObject = JSONObject.fromObject(new String(msg.data()));
		JSONArray transform = jsonObject.getJSONArray("attributes");
		JSONObject home = transform.getJSONObject(0);
		String checker = home.getString("value");
		ZMQ.close(sc);
		ZMQ.term(ctx);
		//System.out.println("--->:"+checker);
		if(containsCenter(checker))
		    {
			trans=getTheTransform(childNds.get(i));
		    }
	    }

	return trans;

    }



    public String getTheMinBBox(String id)
    {
	//System.out.println("--->:");
	Vector<String> childNds = new Vector<String>(1);
	childNds = getChildNodes(id);
	String trans = "";
	for(int i = 0; i < childNds.size();i++)
	    {
		ctx = ZMQ.init(1);                         
		sc = ZMQ.socket(ctx, ZMQ.ZMQ_REQ);
		boolean rc = ZMQ.connect(sc, "tcp://127.0.0.1:22422");
	  	
		String data ="{"+
		    "\"@worldmodeltype\": \"RSGQuery\"," +"\"query\": \"GET_NODE_ATTRIBUTES\","+
		    "\"id\": "+childNds.get(i)+ "}";
		int result = ZMQ.send(sc,data.getBytes(ZMQ.CHARSET),data.length(),0);
		Msg msg = ZMQ.recv(sc, 0);
		JSONObject jsonObject = JSONObject.fromObject(new String(msg.data()));
		JSONArray transform = jsonObject.getJSONArray("attributes");
		JSONObject home = transform.getJSONObject(0);
		String checker = home.getString("value");
		ZMQ.close(sc);
		ZMQ.term(ctx);
		//System.out.println("--->:"+checker);
		if(containsBBox1(checker))
		    {
			trans=getTheTransform(childNds.get(i));
		    }
	    }

	return trans;

    }

 public String getTheMaxBBox(String id)
    {
	//System.out.println("--->:");
	Vector<String> childNds = new Vector<String>(1);
	childNds = getChildNodes(id);
	String trans = "";
	for(int i = 0; i < childNds.size();i++)
	    {
		ctx = ZMQ.init(1);                         
		sc = ZMQ.socket(ctx, ZMQ.ZMQ_REQ);
		boolean rc = ZMQ.connect(sc, "tcp://127.0.0.1:22422");
	  	
		String data ="{"+
		    "\"@worldmodeltype\": \"RSGQuery\"," +"\"query\": \"GET_NODE_ATTRIBUTES\","+
		    "\"id\": "+childNds.get(i)+ "}";
		int result = ZMQ.send(sc,data.getBytes(ZMQ.CHARSET),data.length(),0);
		Msg msg = ZMQ.recv(sc, 0);
		JSONObject jsonObject = JSONObject.fromObject(new String(msg.data()));
		JSONArray transform = jsonObject.getJSONArray("attributes");
		JSONObject home = transform.getJSONObject(0);
		String checker = home.getString("value");
		ZMQ.close(sc);
		ZMQ.term(ctx);
		//System.out.println("--->:"+checker);
		if(containsBBox2(checker))
		    {
			trans=getTheTransform(childNds.get(i));
		    }
	    }

	return trans;

    }

    //###################################################################
    //#                  These are the containFunctions                 #
    //###################################################################


     public static boolean containsMountain( String text )
    {
	return text.contains("mount");// || text.contains("center") 
	// || text.contains("bbox01") || text.contains("bbox02");
    }

   public static boolean containsRiver( String text )
    {
	return text.contains("river");// || text.contains("center") 
	// || text.contains("bbox01") || text.contains("bbox02");
    }

   public static boolean containsWood( String text )
    {
	return text.contains("wood");// || text.contains("center") 
	// || text.contains("bbox01") || text.contains("bbox02");
    }

   public static boolean containsHouse( String text )
    {
	return text.contains("house");// || text.contains("center") 
	// || text.contains("bbox01") || text.contains("bbox02");
    }

    public static boolean containsEnvironment( String text )
    {
	return text.contains("environment");// || text.contains("center") 
	// || text.contains("bbox01") || text.contains("bbox02");
    }

    public boolean containsBBox1(String word)
    {

	return(word.contains("bbox01") || word.contains("bbox1") || world.contains("bbx_river_point_min"));
    }

    public boolean containsBoundingBox(String word)
    {

	return(word.contains("bounding_box"));
    }

    public boolean containsBBox2(String word)
    {

	return(word.contains("bbox02") || word.contains("bbox2") || word.contains("bbx_river_point_max"));
    }

    public boolean containsCenter(String word)
    {

	return(word.contains("center") || word.contains("centre"));
    }
    
    public String getTheTransform(String id)
    {
	String ref_id = getParentID(id);
    	ctx = ZMQ.init(1);                  
  	sc = ZMQ.socket(ctx, ZMQ.ZMQ_REQ);
  	boolean rc = ZMQ.connect(sc, "tcp://127.0.0.1:22422");
	String data = "{ "+
      			"\"@worldmodeltype\": \"RSGQuery\","+
      			"\"query\": \"GET_TRANSFORM\","+
      			"\"id\": \""+id+"\","+
      			"\"idReferenceNode\": \""+ref_id+"\","+
      			"\"timeStamp\": {"+
      			"\"@stamptype\": \"TimeStampUTCms\","+
	    "\"stamp\": \"0.0\""+
      		"} "+
	    "}";

	int result = ZMQ.send(sc,data.getBytes(ZMQ.CHARSET),data.length(),0);
	Msg msg = ZMQ.recv(sc, 0);
	JSONObject jsonObject = JSONObject.fromObject(new String(msg.data()));
	JSONObject transform =  jsonObject.getJSONObject("transform");
	JSONArray array = transform.getJSONArray("matrix");
	JSONArray r0 =  array.getJSONArray(0);
	JSONArray r1 =  array.getJSONArray(1);
	JSONArray r2 =  array.getJSONArray(2);
	JSONArray r3 =  array.getJSONArray(3);
	double[][] ar_vec = new double[3][3];
	ar_vec[0][0] = r0.getDouble(0);
	ar_vec[0][1] = r0.getDouble(1);
	ar_vec[0][2] = r0.getDouble(2);
	ar_vec[1][0] = r1.getDouble(0);
	ar_vec[1][1] = r1.getDouble(1);
	ar_vec[1][2] = r1.getDouble(2);
	ar_vec[2][0] = r2.getDouble(0);
	ar_vec[2][1] = r2.getDouble(1);
	ar_vec[2][2] = r2.getDouble(2);

	double[] ant = MatrixToQuatExp(ar_vec);
	String pose_arr = "[["+r0.getDouble(3)+","+r1.getDouble(3)+","+r2.getDouble(3)+","+r3.getDouble(3)+"]"+
	    "["+ant[0]+","+(ant[1] * -1)+","+(ant[2] * -1)+","+ant[3]+"]]";     


	ZMQ.close(sc);
	ZMQ.term(ctx);
	return pose_arr;
    }

    public String getTheType(String id)
    {
	String id_;
	ctx = ZMQ.init(1);                         
	sc = ZMQ.socket(ctx, ZMQ.ZMQ_REQ);
	String data;
	int result;
	Msg msg;
	JSONObject jsonObject;
	JSONArray array;
	String ref_id;
	boolean rc = ZMQ.connect(sc, "tcp://127.0.0.1:22422");
	
	data ="{"+
	    "\"@worldmodeltype\": \"RSGQuery\"," +"\"query\": \"GET_NODE_ATTRIBUTES\","+"\"id\" : "+id+"}";
	result = ZMQ.send(sc,data.getBytes(ZMQ.CHARSET),data.length(),0);
	msg = ZMQ.recv(sc, 0);
	jsonObject = JSONObject.fromObject(new String(msg.data()));
	JSONArray transform =  jsonObject.getJSONArray("attributes");
	JSONObject home = transform.getJSONObject(1);
	id_ = home.getString("value");
	ZMQ.close(sc);
	ZMQ.term(ctx);
	return id_;
    }

 /**
     *
     *     Getting the RefID of the Objects
     *     in this case: THE OBJECTS
     *
     **/
    public String getObjectsREFID()
    {
	ctx = ZMQ.init(1);                         
	sc = ZMQ.socket(ctx, ZMQ.ZMQ_REQ);
	String data;
	int result;
	Msg msg;
	JSONObject jsonObject;
	JSONArray array;
	String ref_id;
	boolean rc = ZMQ.connect(sc, "tcp://127.0.0.1:22422");

	data ="{"+
	    "\"@worldmodeltype\": \"RSGQuery\"," +"\"query\": \"GET_NODES\","+
	    "\"attributes\":["+
	    "{\"key\": \"name\", \"value\": \"objects\"}," +"]"+ "}";
 	result = ZMQ.send(sc,data.getBytes(ZMQ.CHARSET),data.length(),0);
 	msg = ZMQ.recv(sc, 0);
 	jsonObject = JSONObject.fromObject(new String(msg.data()));
	array =  jsonObject.getJSONArray("ids");
       	ZMQ.close(sc);
	ZMQ.term(ctx);
	return array.getString(0);
      }

    /**
     *        Get The Child Nodes by the 'id'
     *
     **/
    public Vector<String> getChildNodes(String id)
    {
	ctx = ZMQ.init(1);                         
	sc = ZMQ.socket(ctx, ZMQ.ZMQ_REQ);
	String data;
	int result;
	Msg msg;
	JSONObject jsonObject;
	JSONArray array;
	String ref_id;
	boolean rc = ZMQ.connect(sc, "tcp://127.0.0.1:22422");
	data ="{"+
	    "\"@worldmodeltype\": \"RSGQuery\"," +"\"query\": \"GET_GROUP_CHILDREN\","+
	    "\"id\": "+id+ "}";
 	result = ZMQ.send(sc,data.getBytes(ZMQ.CHARSET),data.length(),0);
 	msg = ZMQ.recv(sc, 0);
 	jsonObject = JSONObject.fromObject(new String(msg.data()));
	array =  jsonObject.getJSONArray("ids");
       	ZMQ.close(sc);
	ZMQ.term(ctx);
	Vector<String> vec = new Vector<String>(1);
	for(int i = 0; i < array.size() ; i++)
	    {
		vec.addElement(array.getString(i));
	    }
	return vec;
    }

    public String [][] getTheChildrens(Vector<String> vector)
    {
	int index=0;
	String[][] vector2 = new String[vector.size()][2];
	vector2 = getIDANDName(vector);
	Vector<String> vec1 = new Vector<String>(2);
	Vector<String> vec2 = new Vector<String>(2);
	String[][] arrays2;
	for(int i = 0;i < vector2.length;i++)
	    {
		vec1.addElement(vector2[i][0]);
		vec2.addElement(vector2[i][1]);
		
	    }
	arrays2 = new String[vec1.size()][2];
	while(vec1.size() > index)
	    {
		arrays2[index][0] = vec1.get(index);
		arrays2[index][1] = vec2.get(index);
		index++;
	    }
	return arrays2;
    }


    /**
     *
     *      Get The Selected ChildNodes without 'observations'
     *      and 'animals'
     *
     **/
    public String[][] getSelChildNodes(Vector<String> vector)
    {
	String[][] vector2 = new String[vector.size()][2];
	vector2 = getIDANDName(vector);
	Vector<String> vec1 = new Vector<String>(2);
	Vector<String> vec2 = new Vector<String>(2);
	String[][] arrays2;

	int index = 0;
	for(int i = 0;i < vector2.length;i++)
	    {
		if(vector2[i][1].equals("observations") ||
		   vector2[i][1].equals("animals"))
		    {}
		else{
			vec1.addElement(vector2[i][0]);
			vec2.addElement(vector2[i][1]);
		}
	    }
	arrays2 = new String[vec1.size()][2];
	while(vec1.size() > index)
	    {
		arrays2[index][0] = vec1.get(index);
		arrays2[index][1] = vec2.get(index);
		index++;
	    }
	return arrays2;
    }

    /**
     *             
     *           Get all the ID and Names of the IDs
     *           inside the vector
     *
     **/
    public String[][] getIDANDName(Vector<String> vector)
    {     
	String data;
	String[][] id_name = new String[vector.size()][2];
	int result;
	Msg msg;
	JSONObject jsonObject;
	JSONArray array;
	String ref_id;
	for(int i = 0; i < vector.size(); i++)
	    {	
		ctx = ZMQ.init(1);                         
		sc = ZMQ.socket(ctx, ZMQ.ZMQ_REQ);
		boolean rc = ZMQ.connect(sc, "tcp://127.0.0.1:22422");
	  	
		data ="{"+
		    "\"@worldmodeltype\": \"RSGQuery\"," +"\"query\": \"GET_NODE_ATTRIBUTES\","+
		    "\"id\": "+vector.get(i)+ "}";
		result = ZMQ.send(sc,data.getBytes(ZMQ.CHARSET),data.length(),0);
		msg = ZMQ.recv(sc, 0);
		jsonObject = JSONObject.fromObject(new String(msg.data()));
		JSONArray transform = jsonObject.getJSONArray("attributes");
		JSONObject home = transform.getJSONObject(0);
		id_name[i][0] = vector.get(i);
		id_name[i][1] = home.getString("value");
		ZMQ.close(sc);
		ZMQ.term(ctx);
       	    }
	//System.out.println("HA");
	return id_name;	
    }

    public String getParentID(String id)
    {
	ctx = ZMQ.init(1);                         
	sc = ZMQ.socket(ctx, ZMQ.ZMQ_REQ);
	boolean rc = ZMQ.connect(sc, "tcp://127.0.0.1:22422");
	
	String data ="{"+
	    "\"@worldmodeltype\": \"RSGQuery\"," +"\"query\": \"GET_NODE_PARENTS\","+"\"id\": "+id+ "}";
	int result = ZMQ.send(sc,data.getBytes(ZMQ.CHARSET),data.length(),0);
	Msg msg = ZMQ.recv(sc, 0);
	JSONObject jsonObject = JSONObject.fromObject(new String(msg.data()));
	JSONArray transform =  jsonObject.getJSONArray("ids");
        JSONArray array =  jsonObject.getJSONArray("ids");
	
	ZMQ.close(sc);
	ZMQ.term(ctx);
	
	return array.getString(0);

    }

    public double[] MatrixToQuaternion(double[][] Rot)
    {
	double[] Quat = new double[4];

	double tr = Rot[0][0]+ Rot[1][1]+ Rot[2][2];
	int ii;
	ii=0;
	if (Rot[1][1] > Rot[0][0]) ii=1;
	if (Rot[2][2] > Rot[ii][ii]) ii=2;
		double s;
		if (tr >= 0){
			s = java.lang.Math.sqrt(tr + 1);
			Quat[0] = s * 0.5;
			s = 0.5 / s;
			Quat[1] = (Rot[2][1] - Rot[1][2]) * s;
			Quat[2] = (Rot[0][2] - Rot[2][0]) * s;
			Quat[3] = (Rot[1][0] - Rot[0][1]) * s;
		}
		else {
			switch(ii) {
				case 0:
					s = java.lang.Math.sqrt(Rot[0][0]-Rot[1][1]-Rot[2][2]+1);
					Quat[1] = s * 0.5;
					s = 0.5 / s;
					Quat[2] = (Rot[1][0] + Rot[0][1]) * s;//Update pose estimation
					Quat[3] = (Rot[2][0] + Rot[0][2]) * s;
					Quat[0] = (Rot[2][1] - Rot[1][2]) * s;
				break;
				case 1:
					s = java.lang.Math.sqrt(Rot[1][1]-Rot[2][2]-Rot[0][0]+1);
					Quat[2] = s * 0.5;
					s = 0.5 / s;

					Quat[3] = (Rot[2][1] + Rot[1][2]) * s;
					Quat[1] = (Rot[0][1] + Rot[1][0]) * s;
					Quat[0] = (Rot[0][2] - Rot[2][0]) * s;
				break;
				case 2:
					s = java.lang.Math.sqrt(Rot[2][2]-Rot[0][0]-Rot[1][1]+1);
					Quat[3] = s * 0.5;
					s = 0.5 / s;
					Quat[1] = (Rot[0][2] + Rot[2][0]) * s;
					Quat[2] = (Rot[1][2] + Rot[2][1]) * s;
					Quat[0] = (Rot[1][0] - Rot[0][1]) * s;
				break;
			}
		}
		return Quat;

    }


    // public double[] MatrixToQuat(double[][] Rot)
    // {

    // 	double epsilon = 0.000001;
    // 	double[] Quat = new double[4];

    // 	double tr = Rot[0][0]+ Rot[1][1]+ Rot[2][2] + 1.0;

    // 	if (tr > epsilon)
    // 	    {
    // 	    double s = java.lang.Math.sqrt(tr) / 0.5;
    // 	    Quat[0] = (Rot[2][1] - Rot[1][2]) * s;
    // 	    Quat[1] = (Rot[0][2] - Rot[2][0]) * s;
    // 	    Quat[2] = (Rot[1][0] - Rot[0][1]) * s;
    // 	    Quat[3] = 0.25 / s;
    // 	    }else

    // 	if(Rot[0][0] > Rot[1][1] && Rot[0][0] > Rot[2][2])
    // 	    {
    // 		double s = 2.0 * java.lang.Math.sqrt((Rot[1][1] * -1) + (Rot[2][2] * -1) + 1.0 + Rot[0][0]);
    // 		Quat[0] = 0.25 * s;
    // 		Quat[1] = (Rot[0][1] + Rot[1][0]) / s;
    // 		Quat[2] = (Rot[0][2] + Rot[2][0]) / s;
    // 		Quat[3] = (Rot[2][1] - Rot[1][2]) / s;
    // 	    }else
    // 	    if(Rot[1][1] > Rot[2][2])
    // 		{
		    
    // 		double s = 2.0 * java.lang.Math.sqrt(Rot[1][1]  + (Rot[0][0] * -1) + (Rot[2][2] * -1));
    // 		Quat[1] = 0.25 * s;
    // 		Quat[0] = (Rot[0][1] + Rot[1][0]) / s;
    // 		Quat[2] = (Rot[1][2] + Rot[2][1]) / s;
    // 		Quat[3] = (Rot[0][2] - Rot[2][0]) / s;
    // 		}else
    // 		{
    // 		double Quat1[] = new double[4];
	
    // 		double s = 2.0 * java.lang.Math.sqrt(1.0 + Rot[2][2]  + (Rot[0][0] * -1) + (Rot[1][1] * -1));
    // 		Quat1[2] = 0.25 * s;
    // 		Quat1[0] = (Rot[0][2] + Rot[2][0]) / s;
    // 		Quat1[1] = (Rot[1][2] + Rot[2][1]) / s;
    // 		Quat1[3] = (Rot[1][0] - Rot[0][1]) / s;
		
    // 		Quat = internal_normalize(Quat1[0], Quat1[1], Quat1[2], Quat1[3]);
    // 		}

	   

    // 	return Quat;

    // }


public double[] MatrixToQuatExp(double[][] Rot)
    {

	double epsilon = 0.000001;
	double[] Quat = new double[4];

	double tr = Rot[0][0]+ Rot[1][1]+ Rot[2][2];

	if (tr > epsilon)
	    {
	    double s = java.lang.Math.sqrt(tr+1.0) * 2;
	    Quat[0] = (Rot[2][1] - Rot[1][2]) / s;
	    Quat[1] = (Rot[0][2] - Rot[2][0]) / s;
	    Quat[2] = (Rot[1][0] - Rot[0][1]) / s;
	    Quat[3] = 0.25 * s;
	    }else

	if(Rot[0][0] > Rot[1][1] && Rot[0][0] > Rot[2][2])
	    {
		double s = 2.0 * java.lang.Math.sqrt((Rot[1][1] * -1) + (Rot[2][2] * -1) + 1.0 + Rot[0][0]);
		Quat[0] = 0.25 * s;
		Quat[1] = (Rot[0][1] + Rot[1][0]) / s;
		Quat[2] = (Rot[0][2] + Rot[2][0]) / s;
		Quat[3] = (Rot[2][1] - Rot[1][2]) / s;
	    }else
	    if(Rot[1][1] > Rot[2][2])
		{
		    
		double s = 2.0 * java.lang.Math.sqrt(1.0 + Rot[1][1]  + (Rot[0][0] * -1) + (Rot[2][2] * -1));
		Quat[1] = 0.25 * s;
		Quat[0] = (Rot[0][1] + Rot[1][0]) / s;
		Quat[2] = (Rot[1][2] + Rot[2][1]) / s;
		Quat[3] = (Rot[0][2] - Rot[2][0]) / s;
		}else
		{
		double s = 2.0 * java.lang.Math.sqrt(1.0 + Rot[2][2]  + (Rot[0][0] * -1) + (Rot[1][1] * -1));
		Quat[2] = 0.25 * s;
		Quat[0] = (Rot[0][2] + Rot[2][0]) / s;
		Quat[1] = (Rot[1][2] + Rot[2][1]) / s;
		Quat[3] = (Rot[1][0] - Rot[0][1]) / s;
		}  

	return Quat;

    }

  
	
    public SWMConnection() {
	System.out.println("SWM-Connection is starting");
    }
}
