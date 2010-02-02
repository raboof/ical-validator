package net.bzzt.ical;

import java.io.IOException;

import net.bzzt.ical.services.IcalendarValidationService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.wicketstuff.annotation.mount.MountPath;

@MountPath(path = "filevalidate")
public class FileValidationPage extends ValidatorLayoutPage {
	private static final Log LOG = LogFactory.getLog(FileValidationPage.class);
	
	public FileValidationPage(FileUpload fileUpload)
	{
		try {
			init(fileUpload);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void init(FileUpload fileUpload) throws IOException {
		add(new FileFormPanel("form"));
		
		LOG.info("Validating uploaded file" + fileUpload.getClientFileName());
		
		add(IcalendarValidationService.getValidationResult("result", fileUpload.getInputStream(), null));
	}
}
