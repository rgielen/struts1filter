/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package de.rgielen.struts1.filter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

/**
 * ParamWrapperFilter.
 *
 * @author Rene Gielen
 */
public class ParamWrapperFilter implements Filter {

	private Pattern pattern;

	public void init( FilterConfig filterConfig ) throws ServletException {
		pattern = Pattern.compile(filterConfig.getInitParameter("excludeParams"), Pattern.DOTALL);
	}

	public void doFilter( ServletRequest request, ServletResponse response, FilterChain chain )
			throws IOException, ServletException {
		chain.doFilter(new ParamFilteredRequest(request, pattern), response);
	}

	public void destroy() {
	}

	static class ParamFilteredRequest extends HttpServletRequestWrapper {

		private final String body;
		private final Pattern pattern;

		public ParamFilteredRequest( ServletRequest request, Pattern pattern ) {
			super((HttpServletRequest) request);
			this.pattern = pattern;
			StringBuilder stringBuilder = new StringBuilder();
			BufferedReader bufferedReader = null;

			try {
				InputStream inputStream = request.getInputStream();

				if (inputStream != null) {
					bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

					char[] charBuffer = new char[128];
					int bytesRead = -1;

					while ( (bytesRead = bufferedReader.read(charBuffer)) > 0 ) {
						stringBuilder.append(charBuffer, 0, bytesRead);
					}
				} else {
					stringBuilder.append("");
				}
			} catch ( IOException ex ) {
				// Add logger here
			} finally {
				if (bufferedReader != null) {
					try {
						bufferedReader.close();
					} catch ( IOException ex ) {
						// Add logger here
					}
				}
			}
			body = stringBuilder.toString();

		}

		public Enumeration getParameterNames() {
			List requestParameterNames = Collections.list((Enumeration) super.getParameterNames());
			List finalParameterNames = new ArrayList();

			final Iterator iterator = requestParameterNames.iterator();
			while ( iterator.hasNext() ) {
				String parameterName = (String) iterator.next();
				if (!pattern.matcher(parameterName).matches()) {
					finalParameterNames.add(parameterName);
				}
			}
			return Collections.enumeration(finalParameterNames);
		}

		public ServletInputStream getInputStream() throws IOException {

			System.out.println(body);
			final ByteArrayInputStream byteArrayInputStream;
			if (pattern.matcher(body).matches()) {
				System.out.println("matches");
				byteArrayInputStream = new ByteArrayInputStream("".getBytes());
			} else {
				System.out.println("do not match");
				byteArrayInputStream = new ByteArrayInputStream(body.getBytes());
			}
			return new ServletInputStream() {
				public int read() throws IOException {
					return byteArrayInputStream.read();
				}
			};
		}


	}
}
