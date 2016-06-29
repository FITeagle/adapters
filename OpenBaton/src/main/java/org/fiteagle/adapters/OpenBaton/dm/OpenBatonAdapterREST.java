package org.fiteagle.adapters.OpenBaton.dm;

//import java.util.Collection;
//import java.util.List;
//import java.util.Map;
//
//import javax.ejb.EJB;
//import javax.ws.rs.Consumes;
//import javax.ws.rs.FormParam;
//import javax.ws.rs.GET;
//import javax.ws.rs.POST;
//import javax.ws.rs.Path;
//import javax.ws.rs.core.MediaType;
//import javax.ws.rs.core.MultivaluedMap;
//import javax.ws.rs.core.Response;
//
//import org.fiteagle.abstractAdapter.AbstractAdapter;
//import org.fiteagle.abstractAdapter.dm.AbstractAdapterREST;
//import org.fiteagle.adapters.OpenBaton.OpenBatonAdapter;
//import org.fiteagle.adapters.OpenBaton.OpenBatonAdapterControl;
//import org.glassfish.jersey.media.multipart.FormDataParam;
//import org.jboss.resteasy.spi.HttpRequest;
//
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//
//import org.apache.commons.io.IOUtils;
//import org.apache.http.HttpResponse;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.entity.mime.MultipartEntity;
//import org.apache.http.entity.mime.content.FileBody;
//import org.apache.http.impl.client.DefaultHttpClient;
//import org.apache.http.impl.*;

//import org.jboss.resteasy.plugins.providers.multipart.InputPart;
//import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.ejb.EJB;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import org.apache.commons.io.IOUtils;
import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.dm.AbstractAdapterREST;
import org.fiteagle.adapters.OpenBaton.OpenBatonAdapter;
import org.fiteagle.adapters.OpenBaton.OpenBatonAdapterControl;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
 

@Path("/")
public class OpenBatonAdapterREST extends AbstractAdapterREST {

	private String fiteagleDirectory = "/home/home/.fiteagle/uploads/";


	OpenBatonAdapter adapter;
    @EJB
    private transient OpenBatonAdapterControl controller;

    @Override
    protected Collection<AbstractAdapter> getAdapterInstances() {
	return this.controller.getAdapterInstances();
    }

    @GET
    @Path("/create")
    public Response hallo() {
    	if(adapter == null){
    	    adapter = (OpenBatonAdapter) controller.getAdapterInstances().iterator().next();
    	}
    	adapter.createNewVnfPackage();
	return Response.ok("HalloWelt2").build();
    }
    
    @GET
    @Path("/update")
    public Response update() {
    	if(adapter == null){
    	    adapter = (OpenBatonAdapter) controller.getAdapterInstances().iterator().next();
    	}
    	adapter.updateOldVnfPackage();
	return Response.ok("HalloWelt2").build();
    }
    
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Path("/upload")
    public Response uploadFile(MultipartFormDataInput input) throws IOException {
        
        Map<String, List<InputPart>> uploadForm = input.getFormDataMap();
 
        // Get file data to save
        List<InputPart> inputParts = uploadForm.get("data");
 
        for (InputPart inputPart : inputParts) {
            try {
 
                MultivaluedMap<String, String> header = inputPart.getHeaders();
                String fileName = getFileName(header);
   
                // convert the uploaded file to inputstream
                InputStream inputStream = inputPart.getBody(InputStream.class,
                        null);
 
                byte[] bytes = IOUtils.toByteArray(inputStream);
                // constructs upload file path
                UUID uuid = UUID.randomUUID();
                fileName =  uuid +  "--" +fileName;
                String fileNameWithDirectory = fiteagleDirectory + fileName  ;
                writeFile(bytes, fileNameWithDirectory);
                
            	if(adapter == null){
            	    adapter = (OpenBatonAdapter) controller.getAdapterInstances().iterator().next();
            	}
            	adapter.addUploadedPackage(uuid,fileName);
 
                  
                return Response.status(200).entity("Uploaded file name : " + fileName + "\n")
                        .build();
 
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        return null;
    }
 
    private String getFileName(MultivaluedMap<String, String> header) {
 
        String[] contentDisposition = header.getFirst("Content-Disposition").split(";");
 
        for (String filename : contentDisposition) {
            if ((filename.trim().startsWith("filename"))) {
 
                String[] name = filename.split("=");
 
                String finalFileName = name[1].trim().replaceAll("\"", "");
                return finalFileName;
            }
        }
        return "unknown";
    }
 
    // Utility method
    private void writeFile(byte[] content, String filename) throws IOException {
        File file = new File(filename);
        if (!file.exists()) {
            file.createNewFile();
        }
        FileOutputStream fop = new FileOutputStream(file);
        fop.write(content);
        fop.flush();
        fop.close();
    }
}   


