package net.bzzt.ical;

import java.io.ByteArrayInputStream;

import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.wicketstuff.annotation.mount.MountPath;

@MountPath(path = "textvalidate")
public class SnippetValidationPage extends ValidatorLayoutPage {
	public SnippetValidationPage(String snippet)
	{
		init(snippet, "UTF-8");
	}

	private void init(String snippet, String charset) {
		
		snippet = snippet.replaceAll("\r\n", "\n");
		snippet = snippet.replaceAll("\n\n", "\n");
		
		add(new SnippetFormPanel("form", snippet, charset));
		
		try {
			add(HomePage.getValidationResult("result", new ByteArrayInputStream(snippet.getBytes(charset)), charset));
		} catch (Exception e) {
			error(e);
		}
	}
}
