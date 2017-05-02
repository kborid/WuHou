/*
 * cron4j - A pure Java cron-like scheduler
 * 
 * Copyright (C) 2007-2010 Carlo Pelliccia (www.sauronsoftware.it)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version
 * 2.1, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License 2.1 for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License version 2.1 along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 */
package com.prj.sdk.util;

import java.util.UUID;

/**
 * A GUID generator.
 * 
 * @author Carlo Pelliccia
 * @since 2.0
 */
public class GUIDGenerator {

	/**
	 * Generates a GUID (48 chars).
	 * 
	 * @return The generated GUID.
	 */
	public static String generate() {		
		return UUID.randomUUID().toString().replaceAll("-", "");
	}

}
