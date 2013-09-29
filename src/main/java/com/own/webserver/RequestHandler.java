package com.own.webserver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author MacYser
 */
public class RequestHandler implements Runnable {

	private final static String CRLF = "\r\n";
	protected Socket socket;
	protected BufferedReader in;
	protected BufferedWriter out;

	public RequestHandler(Socket socket) throws IOException {
		this.socket = socket;

	}

	@Override
	public void run() {
		try {
			processRequest();
			socket.close();
		} catch (IOException ex) {
			Logger.getLogger(RequestHandler.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	protected void processRequest() throws IOException {
		try {
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			String s;
			while ((s = in.readLine()) != null) {
				System.out.println(s);
				if (s.isEmpty()) {
					break;
				}
			}

			long time = System.currentTimeMillis();
			out("HTTP/1.1 200 OK");
			out("Date: " + new Date());
			out("Server: SomeServer/0.0.1");
			out("Content-Type: text/html");
			//			out("Content-Length: 258");
			out("Expires: Sat, 01 Jan 2100 00:59:59 GMT");
			out("Last-modified: Fri, 09 Aug 1996 14:21:40 GMT");
			out("");
			out("<TITLE>Hello Time</TITLE>");
			out("<P>" + time + "</P>");
		} catch (IOException ex) {
			Logger.getLogger(RequestHandler.class.getName()).log(Level.SEVERE, null, ex);
		} finally {
			out.close();
			in.close();
		}
	}

	private void out(String output) throws IOException {
		out.write(output + CRLF);
	}
}
