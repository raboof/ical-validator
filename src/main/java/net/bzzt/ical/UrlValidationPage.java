package net.bzzt.ical;

import net.bzzt.ical.services.IcalendarValidationService;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HeaderElement;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.lang.StringUtils;
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
			String url = getUrl(parameters);
			if (url != null)
			{
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

	private String getUrl(PageParameters parameters) {
		Object url = parameters.get("url");
		if (url == null)
		{
			return null;
		}
		else if (url instanceof String)
		{
			return (String) url;
		}
		else if (url instanceof String[])
		{
			String[] urls = (String[]) url;
			if (urls != null && urls.length > 0)
			{
				return urls[0];
			}
		}
		return null;
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

		method.getResponseCharSet();
		
		try {
			LOG.info("Validating url " + uri);
			Header contentType = method.getResponseHeader("Content-Type");
			String charSet = null;
			if (contentType != null)
			{
				HeaderElement[] headerElements = contentType.getElements();
				if (headerElements.length > 0)
				{
					NameValuePair parameter = headerElements[0].getParameterByName("charset");
					if (parameter != null)
					{
						charSet = parameter.getValue();
					}
				}
			}
			
			if (StringUtils.isBlank(charSet))
			{
				charSet = "utf-8";
			}
			
			add(IcalendarValidationService.getValidationResult("result", method.getResponseBodyAsStream(), charSet));
		} catch (Exception e) {
			add(new WebMarkupContainer("result"));
			error(e);
			LOG.error(e.getMessage(), e);
		}
	}
}
