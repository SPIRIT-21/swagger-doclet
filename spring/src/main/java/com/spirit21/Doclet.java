package com.spirit21;

import java.util.logging.Level;

import com.spirit21.parser.Parser;
import com.sun.javadoc.RootDoc;

import lombok.extern.java.Log;

@Log
public class Doclet {
	
	public static boolean start(RootDoc rootDoc) {
		try {
			return new Parser(rootDoc).run();
		} catch (Exception e) {
			log.log(Level.SEVERE, "EXCEPTION", e);
			return false;
		}
	}

}
