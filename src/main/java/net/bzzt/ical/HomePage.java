package net.bzzt.ical;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.ValidationException;
import net.fortuna.ical4j.util.CompatibilityHints;
import net.fortuna.ical4j.util.validation.CollectingValidationResultHandler;
import net.fortuna.ical4j.util.validation.ValidationResult;
import net.fortuna.ical4j.util.validation.ValidationResultHandler;
import net.fortuna.ical4j.util.validation.ValidationRuleInfo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.lf5.util.StreamUtils;
import org.apache.wicket.PageParameters;
import org.apache.wicket.Session;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.wicketstuff.annotation.mount.MountPath;
import org.wicketstuff.annotation.strategy.MountMixedParam;

import sun.misc.Regexp;

/**
 * Homepage
 */
@MountPath(path = "validate")
@MountMixedParam(parameterNames = { "url" })
public class HomePage extends ValidatorLayoutPage {

	private static final long serialVersionUID = 1L;
	private static final ValidationRuleInfo VALID_CRLF_NEWLINES = new ValidationRuleInfo("4.1");
	private static final Log LOG = LogFactory.getLog(HomePage.class);

	public static WebMarkupContainer getValidationResult(String id, InputStream calendarStream,
			String charSet) {

		if (charSet == null)
		{
			charSet = "utf-8";
		}
		
		// We parse in 'relaxed' mode, so we can report as many errors as
		// possible when validating
		CompatibilityHints.setHintEnabled(
				CompatibilityHints.KEY_RELAXED_PARSING, true);

		String calendarString;
		try {
			LOG.info("Getting bytes from calendar stream");
			byte[] bytes = StreamUtils.getBytes(calendarStream);
			LOG.info("Got bytes from calendar stream");
			calendarString = new String(bytes, charSet);
		} catch (UnsupportedEncodingException e2) {
			throw new RuntimeException(e2);
		} catch (IOException e2) {
			throw new RuntimeException(e2);
		}
		
		List<ValidationResult> results = new ArrayList<ValidationResult>();

		calendarString = fixNewlines(calendarString, results);
		
		CalendarBuilder builder = new CalendarBuilder();
		Calendar calendar;
		try {
			calendar = builder.build(new ByteArrayInputStream(calendarString.getBytes(charSet)));
		} catch (ParserException e1) {
			Session.get().error("Error on line " + e1.getLineNo() + ": " + e1.getError());
			return new ParserErrorPanel(id, e1);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		CompatibilityHints.setHintEnabled(
				CompatibilityHints.KEY_RELAXED_PARSING, false);

		CollectingValidationResultHandler validationResultHandler = new CollectingValidationResultHandler();
		ValidationResultHandler.set(validationResultHandler);

		try {
			calendar.validate(true);
		} catch (ValidationException e) {
			throw new IllegalStateException("Iets mis met de handler?"
					+ e.getMessage(), e);
		}

		results.addAll((List<ValidationResult>) validationResultHandler
				.getResults());

		return(new ValidationResultOverviewPanel(id, results));

	}

	private static String fixNewlines(String calendarString,
			List<ValidationResult> extraGeneralFailures) {
		// A calendar may have \n, \r\n or \n\r newlines. All of those should be converted into
		// \r\n.
		String original = calendarString;
			
		calendarString = calendarString.replaceAll("([^\n])\r([^\n])", "$1\n$2");
		calendarString = calendarString.replaceAll("\r", "");
		calendarString = calendarString.replaceAll("\n", "\r\n");
		if (!original.equals(calendarString))
		{
			extraGeneralFailures.add(new ValidationResult(null, "CRLF should be used for newlines", VALID_CRLF_NEWLINES));
		}
		return calendarString;
	}

	// TODO Add any page properties or variables here

	/**
	 * Constructor that is invoked when page is invoked without a session.
	 * 
	 * @param parameters
	 *            Page parameters
	 */
	public HomePage(final PageParameters parameters) {
		add(new UrlFormPanel("urlForm"));
		add(new FileFormPanel("fileForm"));
		add(new SnippetFormPanel("snippetForm", null, null));
		add(new WebMarkupContainer("results"));
	}

}
