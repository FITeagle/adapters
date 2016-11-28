package org.fiteagle.adapters.OpenBaton.dm;



import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
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
import org.openbaton.catalogue.security.Project;
 

@Path("/")
public class OpenBatonAdapterREST extends AbstractAdapterREST {

	private String fiteagleDirectory = System.getProperty("user.home") +"/.fiteagle/uploads/";


	OpenBatonAdapter adapter;
    @EJB
    private transient OpenBatonAdapterControl controller;

    @Override
    protected Collection<AbstractAdapter> getAdapterInstances() {
	return this.controller.getAdapterInstances();
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
    	UUID uuid = null;
    	String fileName = null;
    	String fileNameWithDirectory = null;
    	String projectId = null;
        
    	try{
    		
    		 Map<String, List<InputPart>> uploadForm = input.getFormDataMap();
    		 
    	        // Get file data to save
    	        List<InputPart> inputParts = uploadForm.get("file");
    	 
    	        for (InputPart inputPart : inputParts) {
    	            try {
    	 
    	                MultivaluedMap<String, String> header = inputPart.getHeaders();
    	                fileName = getFileName(header);
    	   
    	                // convert the uploaded file to inputstream
    	                InputStream inputStream = inputPart.getBody(InputStream.class,
    	                        null);
    	 
    	                byte[] bytes = IOUtils.toByteArray(inputStream);
    	                // constructs upload file path
    	                fileName =  fileName + "--" + new Random().nextInt();
    	                fileNameWithDirectory = fiteagleDirectory + fileName  ;
    	                writeFile(bytes, fileNameWithDirectory);
    	                

    	 
    	            } catch (Exception e) {
    	                e.printStackTrace();
    	            }
    	        }
    	        
    	        
    	        inputParts = uploadForm.get("projectId");
    	        
    	        for (InputPart inputPart : inputParts) {
    	            try {
    	 
    	                MultivaluedMap<String, String> header = inputPart.getHeaders();
    	   
    	                // convert the uploaded file to inputstream
    	                InputStream inputStream = inputPart.getBody(InputStream.class,
    	                        null);

    	                projectId = IOUtils.toString(inputStream,"UTF-8");
    	                // constructs upload file path
    	            	if(adapter == null){
    	            	    adapter = (OpenBatonAdapter) controller.getAdapterInstances().iterator().next();
    	            	}
    	            	
    	            	if(fileName != null  && projectId != null){
    	                	String vnfPackageId = adapter.uploadPackageToDatabase(projectId,fileNameWithDirectory);

    	                	adapter.addUploadedPackageToDatabase(vnfPackageId,fileName,projectId);
    	                	
    	                	return Response.status(200).entity("Uploaded file name : " + fileName + "\n")
    	                            .build();
    	            	}else{
    	            		return Response.status(500).entity("File or project-Id was null" + "\n")
    	                            .build();
    	            	}
    	 
    	                  
    	                
    	 
    	            } catch (Exception e) {
    	                e.printStackTrace();
    	            }
    	        }
    	        
    	        return Response.status(500).entity("Sorry, something went wrong!"+ "\n")
    	                .build();
    		
    	}catch(Exception e){
    		e.printStackTrace();
    		return Response.status(500).entity("Sry, something went wrong"+ "\n")
                    .build();
    	}
       
    }
    
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Path("/upload/v2")
    public Response uploadFileWithExperimenterName(MultipartFormDataInput input,@Context HttpHeaders headers) throws IOException {
    	String fileName = null;
    	String fileNameWithDirectory = null;
    	String username = null;
    	String projectId = null;
        
    	try{
	        List<String> filenameHeader;
	        if(headers.getRequestHeader("filename") != null){
	        	filenameHeader = headers.getRequestHeader("filename");
	        }else{
	        	return Response.status(500).entity("Header \"filename\" was null" + "\n")
                        .build();
	        }
    		
    		 Map<String, List<InputPart>> uploadForm = input.getFormDataMap();
    		 
    	        // Get file data to save
    	        List<InputPart> inputParts = uploadForm.get("file");
    	 
    	        for (InputPart inputPart : inputParts) {
    	            try {
    	 
    	                MultivaluedMap<String, String> header = inputPart.getHeaders();
    	                fileName = getFileName(header);
    	                if(fileName.equals("unknown")){
    	                	fileName = filenameHeader.get(0);
    	                }
    	   
    	                // convert the uploaded file to inputstream
    	                InputStream inputStream = inputPart.getBody(InputStream.class,
    	                        null);
    	 
    	                byte[] bytes = IOUtils.toByteArray(inputStream);
    	                // constructs upload file path
    	                fileName =  fileName + "--" + new Random().nextInt();
    	                fileNameWithDirectory = fiteagleDirectory + fileName  ;
    	                writeFile(bytes, fileNameWithDirectory);
    	                
    	 
    	            } catch (Exception e) {
    	                e.printStackTrace();
    	            }
    	        }
    	        
    	        List<String> usernameHeader;
    	        if(headers.getRequestHeader("username") != null){
    	        	usernameHeader = headers.getRequestHeader("username");
    	        }else{
    	        	return Response.status(500).entity("Header \"username\" was null" + "\n")
                            .build();
    	        }

    	        
	        	
    	        
    	        		username = usernameHeader.get(0);
    	                
    	                // constructs upload file path
    	            	if(adapter == null){
    	            	    adapter = (OpenBatonAdapter) controller.getAdapterInstances().iterator().next();
    	            	}
    	            	
    	            	if(fileName != null  && username != null){
							projectId = getProjectId(username);
							if(projectId == null){
        	            		projectId = adapter.getAdminClient().createNewProjectOnServer(username);
    	            		}
    	                	
    	            		String vnfPackageId = adapter.uploadPackageToDatabase(projectId,fileNameWithDirectory);
    	                	adapter.addUploadedPackageToDatabase(vnfPackageId,fileName,projectId);
    	                	
    	                	return Response.status(200).entity(vnfPackageId)
    	                            .build();
    	            	}else{
    	            		return Response.status(500).entity("File or username was null" + "\n")
    	                            .build();
    	            	}
    		
    	}catch(Exception e){
    		e.printStackTrace();
    		return Response.status(500).entity("Sry, something went wrong"+ "\n")
                    .build();
    	}
       
    }

	private String getProjectId(String username) {
		for(Project p : adapter.getAdminClient().getAllProjectsFromServer()){
            if(p.getName().equals(username)){
				return p.getId();

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
        File directory = new File(fiteagleDirectory);
        if(!directory.exists()){
        	directory.mkdir();
        }
        if (!file.exists()) {
            file.createNewFile();
        }
        FileOutputStream fop = new FileOutputStream(file);
        fop.write(content);
        fop.flush();
        fop.close();
    }

	@DELETE
	@Path("/upload/v2/{id}")
	public Response deletePackage(@PathParam("id") String id, @Context HttpHeaders headers){

		if(adapter == null){
			adapter = (OpenBatonAdapter) controller.getAdapterInstances().iterator().next();
		}
		List<String> usernameHeader;
		String username;
		if(headers.getRequestHeader("username") != null){
			usernameHeader = headers.getRequestHeader("username");
		}else{
			return Response.status(500).entity("Header \"username\" was null" + "\n")
					.build();
		}
		username = usernameHeader.get(0);
		String projectId = getProjectId(username);



		boolean succ = adapter.deleteVNFPackage(id, projectId);
		if(succ){
			return Response.status(200).build();
		}else{
			return Response.notModified().build();
		}

	}
}


