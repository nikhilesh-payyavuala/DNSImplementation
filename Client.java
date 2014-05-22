
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Client {

    private Client() {}

    public static void main(String[] args) {

	String host = (args.length < 1) ? null : args[0];
       int port = Integer.valueOf(args[1]);
       String fname = args[2];
      try{ 
       File file = new File("output.txt");
       if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
	try {
	    Registry registry = LocateRegistry.getRegistry(host, port);
	    DNSlookup stub = (DNSlookup) registry.lookup("DNSlookup");
	    BufferedReader reader = new BufferedReader(new FileReader(fname));
        String line = null;
        while ((line = reader.readLine()) != null) {
   		   DNSreply response = stub.lookup(line);
   		   if(!response.hostname.equals("done")){
   		   	//System.out.println("intermediate adrdess");
   		   	try{
   		   		 Registry registry_1 = LocateRegistry.getRegistry(response.address, port);
	             DNSlookup stub_1 = (DNSlookup) registry_1.lookup("DNSlookup1");
	             DNSreply reply = stub_1.lookup(response.hostname);
	             bw.write(line+"\t\t"+"--"+"\t\t"+reply.address+"\n" );
			
	             //System.out.println("I response: " + reply.hostname+"adrdess : "+reply.address);

   		   	}
   		   	catch(Exception e){
   		   		e.printStackTrace();
   		   	}
   		   }
   		   else{
   		   	bw.write(line+"\t\t"+"--"+"\t\t"+response.address+"\n" );
			
   		   	//System.out.println("R response: " + response.hostname+"adrdess : "+response.address);
   		   }
   		   
		}
		bw.close();
	    //DNSreply response = stub.lookup("www.google.com");
	    
	} catch (Exception e) {
	    System.err.println("Client exception: " + e.toString());
	    e.printStackTrace();
	}
}
catch(IOException e){
      e.printStackTrace();
}	
    }
}

