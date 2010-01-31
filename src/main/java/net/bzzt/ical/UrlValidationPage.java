package net.bzzt.ical;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.wicketstuff.annotation.mount.MountPath;
import org.wicketstuff.annotation.strategy.MountMixedParam;
import org.wicketstuff.annotation.strategy.MountQueryString;

@MountPath(path = "urlvalidate")
//MountMixedParams didn't work behind apache proxypass when the url contained a %2F
@MountQueryString
public class UrlValidationPage extends ValidatorLayoutPage {
	private static final Log LOG = LogFactory.getLog(UrlValidationPage.class);

	public UrlValidationPage(PageParameters parameters)
	{
		try {
			String[] urls = (String[]) parameters.get("url");
			if (urls != null && urls.length > 0)
			{
				String url = urls[0];
				init(new URI(url, true));
			}
			else
			{
				init(null);
			}
			
		} catch (URIException e) {
			e.printStackTrace();
		}
	}

	private void init(URI uri) {
		GetMethod method = new GetMethod();
		try {
			method.setURI(uri);
		} catch (Exception e) {
			error(e);
		}

		HttpClient client = new HttpClient();
		try {
			client.executeMethod(method);
		} catch (Exception e) {
			error(e);
		}
		
		add(new UrlFormPanel("urlForm", uri));

		try {
			LOG.info("Validating url " + uri);
			add(HomePage.getValidationResult("result", method.getResponseBodyAsStream(), method.getResponseCharSet()));
		} catch (Exception e) {
			add(new WebMarkupContainer("result"));
			error(e);
		}
	}
}
