package net.bzzt.ical;

import java.io.ByteArrayInputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wicketstuff.annotation.mount.MountPath;

@MountPath(path = "textvalidate")
public class SnippetValidationPage extends ValidatorLayoutPage {
	private static final Log LOG = LogFactory.getLog(SnippetValidationPage.class);

	public SnippetValidationPage(String snippet)
	{
		init(snippet, "UTF-8");
	}

	private void init(String snippet, String charset) {
		
		snippet = snippet.replaceAll("\r\n", "\n");
		snippet = snippet.replaceAll("\n\n", "\n");
		
		add(new SnippetFormPanel("form", snippet, charset));
		
		try {
			LOG.info("Validating snippet");
			add(HomePage.getValidationResult("result", new ByteArrayInputStream(snippet.getBytes(charset)), charset));
		} catch (Exception e) {
			error(e);
		}
	}
}
