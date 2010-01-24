package net.bzzt.ical;

import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.wicketstuff.annotation.mount.MountPath;

@MountPath(path = "filevalidate")
public class FileValidationPage extends ValidatorLayoutPage {
	public FileValidationPage(FileUpload fileUpload)
	{
		init(fileUpload);
	}

	private void init(FileUpload fileUpload) {
		add(new FileFormPanel("form"));
		
		try {
			add(HomePage.getValidationResult("result", fileUpload.getInputStream(), fileUpload.getContentType()));
		} catch (Exception e) {
			error(e);
		}
	}
}
