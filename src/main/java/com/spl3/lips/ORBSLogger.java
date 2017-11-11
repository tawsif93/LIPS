package com.spl3.lips;

import org.apache.log4j.Logger;

import java.io.PrintStream;

/**
 * Created by peacefrog on 11/11/17.
 * Time 10:16 PM
 */
public class ORBSLogger {

	private static final Logger logger = Logger.getLogger(ORBSLogger.class);

	public static void tieSystemOutAndErrToLog() {
		System.setOut(createLoggingProxy(System.out));
		System.setErr(createLoggingProxy(System.err));
	}

	public static PrintStream createLoggingProxy(final PrintStream realPrintStream) {
		return new PrintStream(realPrintStream) {
			public void print(final String string) {
				realPrintStream.print(string);
				logger.info(string);
			}
		};
	}
}