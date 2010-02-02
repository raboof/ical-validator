package net.bzzt.ical.services;

import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.util.validation.CollectingValidationResultHandler;
import net.fortuna.ical4j.util.validation.ValidationResult;
import net.fortuna.ical4j.util.validation.ValidationResultHandler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import junit.framework.TestCase;
import junit.framework.TestSuite;

public class WrongEncodingTest extends TestCase {
	private static final Log LOG = LogFactory.getLog(WrongEncodingTest.class);
	
	private String actualEncoding;
	
	private String specifiedEncoding;
	
	public WrongEncodingTest(String actualEncoding, String specifiedEncoding) {
		super("testEncoding");
		
		this.actualEncoding = actualEncoding;
		this.specifiedEncoding = specifiedEncoding;
	}

	public WrongEncodingTest(String name) {
		super(name);
	}

	public void testEncoding()
	{
		String string = "Pati\u00ebnt";
		byte[] bytes = string.getBytes(Charset.forName(actualEncoding));

		CollectingValidationResultHandler handler = new CollectingValidationResultHandler();
		ValidationResultHandler.set(handler);
		
		LOG.info(IcalendarValidationService.getCalendarString(new ByteArrayInputStream(bytes), specifiedEncoding));

		boolean specifiedCorrectEncoding = actualEncoding.equals(specifiedEncoding);
		
		assertEquals("actual " + actualEncoding + ", specified " + specifiedEncoding, specifiedCorrectEncoding, handler.getResults().isEmpty());
	}
	
	public void testLineNumber()
	{
		String string = "foo\nbar\rba\u00ebz\n";
		
		byte[] bytes = string.getBytes(Charset.forName("ISO-8859-1"));
		
		CollectingValidationResultHandler handler = new CollectingValidationResultHandler();
		ValidationResultHandler.set(handler);
		
		IcalendarValidationService.getCalendarString(new ByteArrayInputStream(bytes), "utf-8");
		
		assertEquals(1, handler.getResults().size());
		Object result = handler.getResults().get(0);
		assertTrue(result instanceof ValidationResult);
		ValidationResult actual = (ValidationResult) result;
		assertEquals(3, actual.args[1]);
	}
	
	
	public static TestSuite suite()
	{
		TestSuite suite = new TestSuite();
		
		suite.addTest(new WrongEncodingTest("ISO-8859-1", "ISO-8859-1"));
		suite.addTest(new WrongEncodingTest("ISO-8859-1", "utf-8"));
		// Pretty much anything in utf-8 is also valid latin-1
		//suite.addTest(new WrongEncodingTest("utf-8", "ISO-8859-1"));
		suite.addTest(new WrongEncodingTest("utf-8", "utf-8"));
		suite.addTest(new WrongEncodingTest("testLineNumber"));
		
		return suite;
	}
}
