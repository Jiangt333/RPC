package client;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;


public class RPCClient {
    private String host;
    private int port;
    public RPCClient(String host, int port) throws Exception {
        this.host = host;
        this.port = port;
    }

    public Object invoke(String serviceName, String methodName, Object args) throws IOException,RuntimeException {
    	Socket socket = new Socket();
    	socket.connect(new InetSocketAddress(InetAddress.getByName(host),port),10000);//
    	socket.setSoTimeout(5000);
        OutputStream out = socket.getOutputStream();
        InputStream in = socket.getInputStream();
        Map<String, Object> request = new HashMap<String, Object>();
        request.put("FuncName", serviceName);
        request.put("methodName", methodName);
        request.put("Args", args);
        String requestJson = JSON.toJSONString(request);
        out.write(requestJson.getBytes());
        out.flush();
        byte[] buffer = new byte[1024];
        int len = in.read(buffer);
        String responseJson = new String(buffer, 0, len);      
        JSONObject response = JSON.parseObject(responseJson);
        if (response.containsKey("exception")) {
            throw new RuntimeException(response.getString("exception"));
        }
		socket.close();
        return response.get("result");
    }
 
    public static void main(String[] args) throws Exception {
    	String[] help = {"-i Server IP address", "-p Server port", "-s Server service", "-m Server JAVA:MethodName"};
    	String ip = "192.168.43.50";
    	int port = 8002;
		String serviceName = null;
        String methodName = null;
		Object[] params = null;
    	if(args.length>0)
    	{
    		if(args[0].equals("-h"))
    		{
    			for(int i=0;i<help.length;i++)
        			System.out.println(help[i]);
    			return;
    		}
    		ip = args[1];
        	String portString = args[3];
        	port = Integer.parseInt(portString);
			if(args[0].equals("-i")&& args[2].equals("-p")&&args[4].equals("-s"))
        	{
				ip = args[1];
				String portstr = args[3];
				port = Integer.parseInt(portstr);
        		serviceName = args[5];
				if(args.length>6)
				{
					if(args[6].equals("-m"))
					{
						methodName  = args[7];
						params = Arrays.copyOfRange(args, 8, args.length);
					}
					else
					{
						params = Arrays.copyOfRange(args, 6, args.length);
					}
					
				}	
				
        	}
        	else {
        		System.out.println("启动参数输入错误");
        		return;
    		}	  
    		
    	}
 	    RPCClient client = new RPCClient(ip, port);   
 	    Object servicelist = client.invoke(serviceName, methodName, params);
 	    System.out.println(servicelist);
    	
}
}
    


