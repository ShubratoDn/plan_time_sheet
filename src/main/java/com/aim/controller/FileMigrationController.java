package com.aim.controller;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.aim.config.TenantContext;
import com.aim.entity.Company;
import com.aim.entity.HourLogFile;
import com.aim.entity.HourLogFilePath;
import com.aim.model.HourLogFileFromSubDb;
import com.aim.model.HourLogFilePathFromSubDb;
import com.aim.repository.CompanyRepository;
import com.aim.repository.HourLogFilePathRepository;
import com.aim.repository.HourLogFileRepository;
import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;

@RequestMapping("/file")
@Controller
public class FileMigrationController {

	@Value("${file.path}")
	private String FILE_PATH;
	
	@Autowired
	private HourLogFileRepository hourLogFileRepository;
	
	@Autowired
	private HourLogFilePathRepository hourLogFilePathRepository;
	
	@Autowired
	private CompanyRepository companyRepository;
	
	/**
	 * Get file
	 * @param response
	 * @param id
	 * @throws Exception
	 */
	@RequestMapping(value ="/user-time-sheet/{id}", method=RequestMethod.GET)
	public void getFile(HttpServletResponse response, @PathVariable("id") Integer id,
			@RequestParam(name="fileId",required = false) Integer fileId,
			@RequestParam(name="companyUrl") String companyUrl,
			HttpServletRequest request) throws Exception {
		
		Company company = companyRepository.findByUrlSlug(companyUrl);
		String filefolder = company.getFileFolder();
		
		ThreadPoolExecutor executor1 = (ThreadPoolExecutor) Executors.newFixedThreadPool(3);
		HourLogFileFromSubDb hourLogFileFromSubDb = new HourLogFileFromSubDb(hourLogFileRepository,id, company.getDbName());
		Future<HourLogFile> result = executor1.submit(hourLogFileFromSubDb);
		
		HourLogFile hourLogFile = result.get();
		
		response.setContentType(MediaType.ALL_VALUE);
		
		
		ThreadPoolExecutor executor2 = (ThreadPoolExecutor) Executors.newFixedThreadPool(3);
		HourLogFilePathFromSubDb hourLogFilePathFromSubDb = new HourLogFilePathFromSubDb(hourLogFilePathRepository,hourLogFile, company.getDbName());
		Future<List<HourLogFilePath>> result1 = executor2.submit(hourLogFilePathFromSubDb);
		
		List<HourLogFilePath> hourLogFilePaths = result1.get();
		
		HourLogFilePath hFile = new HourLogFilePath();
		if(!CollectionUtils.isEmpty(hourLogFilePaths)) {
			
			String filePath = "";
			TenantContext.clear();
			if(fileId == null) {
				filePath = hourLogFilePaths.get(0).getFilePath();
				hFile = hourLogFilePaths.get(0);
			} else {
				filePath = hourLogFilePaths.stream().filter(f -> f.getId()== fileId).findFirst().get().getFilePath();
				hFile = hourLogFilePaths.stream().filter(f -> f.getId()== fileId).findFirst().get();
			}
			File f = new File(FILE_PATH +filefolder+ filePath);
			String extension = filePath.substring(filePath.lastIndexOf(".") + 1).toLowerCase();
			List <String> allExtension = new ArrayList<String>();
			allExtension.add("png");
			allExtension.add("jpeg");
			allExtension.add("jpg");
			
			if(allExtension.contains(extension)) {
				response.setHeader("Content-Disposition", "attachment;filename="+hFile.getFileOriginalName()+".pdf");
				  
				//https://stackoverflow.com/questions/8361901/how-can-i-convert-a-png-file-to-pdf-using-java
				Image image = Image.getInstance(FILE_PATH+ filefolder + filePath);
				
				Rectangle A4 = PageSize.A4;

		        float scalePortrait = Math.min(A4.getWidth() / image.getWidth(),
		                A4.getHeight() / image.getHeight());

		        float scaleLandscape = Math.min(A4.getHeight() / image.getWidth(),
		                A4.getWidth() / image.getHeight());

		        // We try to occupy as much space as possible
		        // Sportrait = (w*scalePortrait) * (h*scalePortrait)
		        // Slandscape = (w*scaleLandscape) * (h*scaleLandscape)

		        // therefore the bigger area is where we have bigger scale
		        boolean isLandscape = scaleLandscape > scalePortrait;

		        float w;
		        float h;
		        if (isLandscape) {
		            A4 = A4.rotate();
		            w = image.getWidth() * scaleLandscape;
		            h = image.getHeight() * scaleLandscape;
		        } else {
		            w = image.getWidth() * scalePortrait;
		            h = image.getHeight() * scalePortrait;
		        }
		        
		        Document document = new Document(A4, 10, 10, 10, 10);
//				Document document = new Document();
				OutputStream outStream = response.getOutputStream();
			    PdfWriter writer = PdfWriter.getInstance(document, outStream);
			    writer.open();
			    document.open();
			    image.scaleAbsolute(w, h);
		        float posH = (A4.getHeight() - h) / 2;
		        float posW = (A4.getWidth() - w) / 2;
	
		        image.setAbsolutePosition(posW, posH);
		        image.setBorder(Image.NO_BORDER);
		        image.setBorderWidth(0);
			    document.add(image);
			    document.close();
			    writer.close();
			} else {
				try {
					
			        // construct the complete absolute path of the file
			        FileInputStream inputStream = new FileInputStream(FILE_PATH + filefolder + filePath);
			         
			        Path source = Paths.get(FILE_PATH + filefolder + filePath);
			        String mimeType = Files.probeContentType(source);
			        // get MIME type of the file
			        if (mimeType == null) {
			            // set to binary type if MIME mapping not found
			            mimeType = "application/octet-stream";
			        }
			        // set content attributes for the response
			        response.setContentType(mimeType);
			        response.setContentLength((int) f.length());
			 
			        // get output stream of the response
			        OutputStream outStream = response.getOutputStream();
			 
			        byte[] buffer = new byte[100000];
			        int bytesRead = -1;
			 
			        // write bytes read from the input stream into the output stream
			        while ((bytesRead = inputStream.read(buffer)) != -1) {
			            outStream.write(buffer, 0, bytesRead);
			        }
			 
			        inputStream.close();
			        outStream.close();
			 
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
			
		}
	}
	
	@RequestMapping(value ="/getFile", method=RequestMethod.GET)
	public void getFile(HttpServletResponse response, @RequestParam("path") String path) throws Exception {
		
		response.setContentType(MediaType.ALL_VALUE);
		
		File file = new File(FILE_PATH + path);
		
		BufferedImage image = ImageIO.read(file);
		OutputStream out = response.getOutputStream();
		ImageIO.write(image, FilenameUtils.getExtension(file.getName()), out);
		out.close();
	}
}
