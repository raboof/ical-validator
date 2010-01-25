package net.bzzt.ical;

import java.io.IOException;

import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.wicketstuff.annotation.mount.MountPath;

@MountPath(path = "filevalidate")
public class FileValidationPage extends ValidatorLayoutPage {
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
		
		add(HomePage.getValidationResult("result", fileUpload.getInputStream(), null));
	}
}
