package net.bzzt.ical.services;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.MalformedInputException;
import java.util.List;

import net.bzzt.ical.ParserErrorPanel;
import net.bzzt.ical.ValidationResultOverviewPanel;
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
import org.apache.wicket.markup.html.WebMarkupContainer;

public class IcalendarValidationService {
	private static final ValidationRuleInfo VALID_CHARSET_UTF8 = new ValidationRuleInfo(
			"3.1.4");

	private static final ValidationRuleInfo VALID_CHARSET_PRESENT = new ValidationRuleInfo(
			"3.1.4");

	private static final Log LOG = LogFactory
			.getLog(IcalendarValidationService.class);

	private static final ValidationRuleInfo VALID_CRLF_NEWLINES = new ValidationRuleInfo(
			"3.1");

	private static final ValidationRuleInfo VALID_EMPTY_LINES = new ValidationRuleInfo("");

	public static WebMarkupContainer getValidationResult(String id,
			InputStream calendarStream, String charSet) {
		CollectingValidationResultHandler validationResultHandler = new CollectingValidationResultHandler();
		ValidationResultHandler.set(validationResultHandler);

		if (charSet == null) {
			validationResultHandler.onValidationResult(new ValidationResult(
					null, "Specifying the charset in the MIME Content-Type is mandatory", 
					VALID_CHARSET_PRESENT));
			charSet = "utf-8";
		}

		// We parse in 'relaxed' mode, so we can report as many errors as
		// possible when validating
		CompatibilityHints.setHintEnabled(
				CompatibilityHints.KEY_RELAXED_PARSING, true);

		String calendarString = getCalendarString(calendarStream, charSet);

		try {
			calendarString = fixNewlines(calendarString);
		} catch (ValidationException e) {
			throw new RuntimeException(e);
		}

		if (!"utf-8".equalsIgnoreCase(charSet)) {
			validationResultHandler.onValidationResult(new ValidationResult(
					null, "Charset was [{0}] instead of utf-8 as recommended",
					new Object[] { charSet }, VALID_CHARSET_UTF8));
		}

		CalendarBuilder builder = new CalendarBuilder();
		Calendar calendar;
		try {
			calendar = builder.build(new ByteArrayInputStream(calendarString
					.getBytes(charSet)));
		} catch (ParserException e1) {
			return new ParserErrorPanel(id, calendarString, e1);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		CompatibilityHints.setHintEnabled(
				CompatibilityHints.KEY_RELAXED_PARSING, false);

		try {
			calendar.validate(true);
		} catch (ValidationException e) {
			throw new IllegalStateException("Iets mis met de handler?"
					+ e.getMessage(), e);
		}

		return (new ValidationResultOverviewPanel(id, charSet,
				(List<ValidationResult>) validationResultHandler.getResults()));

	}

	static String getCalendarString(InputStream calendarStream, String charSet) {
		try {
			LOG.info("Getting bytes from calendar stream");
			byte[] bytes = StreamUtils.getBytes(calendarStream);

			// new CharsetProvider().charsetForName(charSet);

			LOG.info("Got bytes from calendar stream");
			checkCharset(bytes, charSet);

			return new String(bytes, charSet);
		} catch (UnsupportedEncodingException e2) {
			throw new RuntimeException(e2);
		} catch (IOException e2) {
			throw new RuntimeException(e2);
		}

	}

	private static void checkCharset(byte[] bytes, String charSet) {
		int offset = 0;
		int length = 0;

		for (int lineno = 1;; lineno++) {
			offset = nextLineStart(bytes, offset + length);
			if (offset == -1) {
				return;
			}
			length = currentLineLength(bytes, offset);

			checkCharset(bytes, charSet, offset, length, lineno);
		}
	}

	private static void checkCharset(byte[] bytes, String charSet, int offset,
			int length, int lineno) {
		CharsetDecoder decoder = Charset.forName(charSet).newDecoder();
		decoder.onMalformedInput(CodingErrorAction.REPORT);
		decoder.onUnmappableCharacter(CodingErrorAction.REPORT);
		try {
			decoder.decode(ByteBuffer.wrap(bytes, offset, length));
		} catch (MalformedInputException e) {
			try {
				ValidationResultHandler
						.get()
						.onValidationResult(
								new ValidationResult(
										null,
										"Input contains non-[{0}] characters on line {1}: [{2}]",
										new Object[] { charSet, lineno, new String(bytes, offset, length) },
										VALID_CHARSET_UTF8));
			} catch (ValidationException e1) {
				throw new RuntimeException(e1);
			}
		} catch (CharacterCodingException e) {
			try {
				ValidationResultHandler
						.get()
						.onValidationResult(
								new ValidationResult(
										null,
										"Input contains non-[{0}] characters on line {1}: [{2}]",
										new Object[] { charSet, lineno, new String(bytes, offset, length) },
										VALID_CHARSET_UTF8));
			} catch (ValidationException e1) {
				throw new RuntimeException(e1);
			}
		}
	}

	private static int nextLineStart(byte[] bytes, int offset) {
		while (offset < bytes.length) {
			if (bytes[offset] != '\n' && bytes[offset] != '\r') {
				return offset;
			}
			offset++;
		}
		return -1;
	}

	private static int currentLineLength(byte[] bytes, int offset) {
		int result = 0;
		while (offset < bytes.length && bytes[offset] != '\n'
				&& bytes[offset] != '\r') {
			offset++;
			result++;
		}
		return result;
	}

	private static String fixNewlines(String calendarString)
			throws ValidationException {
		// A calendar may have \n, \r\n or \n\r newlines. All of those should be
		// converted into
		// \r\n.
		String original = calendarString;

		calendarString = calendarString
				.replaceAll("([^\n])\r([^\n])", "$1\n$2");
		calendarString = calendarString.replaceAll("\r", "");
		calendarString = calendarString.replaceAll("\n", "\r\n");
		if (!original.equals(calendarString)) {
			ValidationResultHandler.get().onValidationResult(
					new ValidationResult(null,
							"CRLF should be used for newlines",
							VALID_CRLF_NEWLINES));
		}
		if (calendarString.contains("\r\n\r\n"))
		{
			ValidationResultHandler.get().onValidationResult(
					new ValidationResult(null,
							"Feeds should not contain empty lines",
							VALID_EMPTY_LINES));			
		}
		return calendarString;
	}

}
