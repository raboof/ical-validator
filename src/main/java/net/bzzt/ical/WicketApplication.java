package net.bzzt.ical;

import java.util.Locale;

import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.IConverterLocator;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.settings.IRequestLoggerSettings;
import org.apache.wicket.util.convert.ConversionException;
import org.apache.wicket.util.convert.ConverterLocator;
import org.apache.wicket.util.convert.IConverter;
import org.wicketstuff.annotation.scan.AnnotatedMountScanner;

/**
 * Application object for your web application. If you want to run this
 * application without deploying, run the Start class.
 * 
 * @see net.bzzt.ical.Start#main(String[])
 */
public class WicketApplication extends WebApplication {
	private static final Log LOG = LogFactory.getLog(WicketApplication.class);

	/**
	 * Constructor
	 */
	public WicketApplication() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.wicket.protocol.http.WebApplication#init()
	 */
	@Override
	protected void init() {
		new AnnotatedMountScanner().scanPackage("net.bzzt.ical").mount(this);

		LOG.info("Mode: " + getConfigurationType());

		// Get the logger
		IRequestLoggerSettings reqLogger = getRequestLoggerSettings();

		// Enable the logger
		reqLogger.setRequestLoggerEnabled(true);

		super.init();
	}

	/**
	 * @see org.apache.wicket.Application#getHomePage()
	 */
	public Class<HomePage> getHomePage() {
		return HomePage.class;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.wicket.Application#newConverterLocator()
	 */
	@Override
	protected IConverterLocator newConverterLocator() {
		ConverterLocator locator = new ConverterLocator();
		locator.set(URI.class, new IConverter() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public Object convertToObject(String value, Locale locale) {
				try {
					return new URI(value, false);
				} catch (Exception e) {
					throw new ConversionException(e);
				}
			}

			public String convertToString(Object value, Locale locale) {
				try {
					return ((URI) value).getURI();
				} catch (URIException e) {
					throw new ConversionException(e);
				}
			}

		});

		return locator;
	}

}
