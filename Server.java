import java.net.*;	
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import java.io.File;
	
public class Server implements DNSlookup {
	
	static String type;
    static String fname;
    static int layer;
    static int port;
    HashMap cache = new HashMap();
    public Server() {}


    public DNSreply lookup(String hostname)
     {
     	//System.out.println("Hostname: "+hostname);
     	//DNSreply reply ;
     	String end=	hostname.substring(hostname.lastIndexOf('.')+1);
     	if(layer==1){
     		//System.out.println("in layer is 1");
     		if(type.equals("R")){
     			
     			int flag=checkCache(end);
     			if(flag==1){
     				DNSreply reply= new DNSreply((String)cache.get(end),hostname.substring(0,hostname.lastIndexOf('.')));
     				try{
     				Registry registry = LocateRegistry.getRegistry((String)cache.get(end), port);
	                DNSlookup stub = (DNSlookup) registry.lookup("DNSlookup1");
	                DNSreply response = stub.lookup(hostname.substring(0,hostname.lastIndexOf('.')));
	                return response;
	            }
	            catch(Exception e){
					System.out.println("in method lookup");
     				
	            }
	            
     			}
     			else{
		     			try{
								Scanner txtscan = new Scanner(new File(fname));
								while(txtscan.hasNextLine()){
						    	String str = txtscan.nextLine();
						    	if(str.indexOf(end) != -1){
							    	String[] ptr=str.split("\\s+");
							    	//System.out.println("ad  "+ptr[1]+" new host: "+hostname.substring(0,hostname.lastIndexOf('.')) );
							    	try{
							    	Registry registry = LocateRegistry.getRegistry(ptr[1], port);
	                                DNSlookup stub = (DNSlookup) registry.lookup("DNSlookup1");
	                                DNSreply response = stub.lookup(hostname.substring(0,hostname.lastIndexOf('.')));
	                                cache.put(end,ptr[1]);
	                                return response;
		                            }
		                            catch(Exception e){
		                            	System.out.println("in else");
		                            }
							        //DNSreply reply= new DNSreply(ptr[1],hostname.substring(0,hostname.lastIndexOf('.')));
							        

							        //System.out.println(reply.address +"  here here   "+reply.hostname );

							        
						    		}
						    		
								}
								//System.out.println("not found layer 1 R ");
						    		DNSreply reply_1= new DNSreply("Not Found","done");
						    		return reply_1;
							}
						catch(FileNotFoundException fnfe){
		         				System.out.println("File not found");
		         			}   
         		}
     			//System.out.println("last part is : "+end );
     		}
      		else if(type.equals("I")){
      			try{
      				Scanner txtscan = new Scanner(new File(fname));
								while(txtscan.hasNextLine()){
						    	String str = txtscan.nextLine();
						    	if(str.indexOf(end) != -1){
						    			String[] ptr=str.split("\\s+");
							    	    //System.out.println("ad  "+ptr[1]+" new host: "+hostname.substring(0,hostname.lastIndexOf('.')) );
							    	    DNSreply reply= new DNSreply(ptr[1],hostname.substring(0,hostname.lastIndexOf('.')));
							    	    return reply;
						    	}
						    	
      			}
      			//System.out.println("not found layer 1 I ");
						    		DNSreply reply_2= new DNSreply("Not Found","done");
						    		return reply_2;
      		}
      			catch(FileNotFoundException fnfe){
		         				System.out.println("File not found");
		         			}
                         
     		}
     	}
     	else if(layer==2){ //for server of secon layer
     		try{
     							//System.out.println("in layer 2 "+ fname+"   "+hostname);
								Scanner txtscan = new Scanner(new File(fname));
								while(txtscan.hasNextLine()){
						    	String str = txtscan.nextLine();
						    	if(str.indexOf(hostname) != -1){
						    		//System.out.println("hit roi ");
							    	String[] ptr=str.split("\\s+");
							    	//System.out.println("ad  "+ptr[1]);
							        DNSreply reply= new DNSreply(ptr[1],"done");
							        //cache.put(end,ptr[1]);
							        return reply;
						    		}
						    		
								}

						    			//System.out.println("not found layer 2 ");
						    			DNSreply reply_3= new DNSreply("Not Found","done");
						    		    return reply_3;
						    	
							}
						catch(FileNotFoundException fnfe){
		         				System.out.println("File not found");
		         			}   
     	}
     	return new DNSreply("","");
    }

    public int checkCache(String end){
    	System.out.println("hello");
    	if(cache.containsKey(end)){
    		System.out.println("Cache Hit");
    		return 1;
    	}
    	else
    	{
    		System.out.println("Cache Miss");
    		return 0;
    	}

    }
	
    public static void main(String args[]) {
   
    int number = args.length;
    if(number<4){
    	System.out.println("Not sufficient arguments");
    	return;
    }

    type=args[0];
    fname=args[1];
    layer=Integer.valueOf(args[2]);
    System.out.println("type is:  "+ type+" filename is: "+fname+" Layer Number is : "+layer);
	port = Integer.valueOf(args[3]);
if(layer==1){
	try {
		Socket s = new Socket("google.com", 80);
		System.setProperty("java.rmi.server.hostname",s.getLocalAddress().getHostAddress());
		s.close();            
	    Server obj = new Server();
	    DNSlookup stub = (DNSlookup) UnicastRemoteObject.exportObject(obj, 0);

	    // Bind the remote object's stub in the registry
	    Registry registry = LocateRegistry.getRegistry(port);
	    registry.bind("DNSlookup", stub);

	    System.err.println("Server ready");
	} catch (Exception e) {
	    System.err.println("Server exception: " + e.toString());
	    e.printStackTrace();
	}
}

if(layer==2){
	try {
		Socket s = new Socket("google.com", 80);
		System.setProperty("java.rmi.server.hostname",s.getLocalAddress().getHostAddress());
		s.close();            
	    Server obj = new Server();
	    DNSlookup stub = (DNSlookup) UnicastRemoteObject.exportObject(obj, 0);

	    // Bind the remote object's stub in the registry
	    Registry registry = LocateRegistry.getRegistry(port);
	    registry.bind("DNSlookup1", stub);

	    System.err.println("Server ready");
	} catch (Exception e) {
	    System.err.println("Server exception: " + e.toString());
	    e.printStackTrace();
	}

}

    }
}


