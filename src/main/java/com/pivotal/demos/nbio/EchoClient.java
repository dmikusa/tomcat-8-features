package com.pivotal.demos.nbio;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.RandomStringUtils;

public class EchoClient {
	public static void main(String[] args) throws Exception {
		String tmp;
		EchoClient echoClient = new EchoClient();
		Map<String, List<String>> resHeaders = new HashMap<>();

		System.out.println("Sending nothing.");
		System.out.println("\tResponse [" +
				echoClient.doPost(false, new EmptyBytesStreamer(), resHeaders, null) + "]");
		pauseRun();

		System.out.println("Sending 'Hello World!'.");
		System.out.println("\tResponse [" +
				echoClient.doPost(false, new HelloBytesStreamer(), resHeaders, null) + "]");
		pauseRun();

		System.out.println("Sending 10 random characters.");
		System.out.println("\tResponse [" +
				echoClient.doPost(false, new RandomBytesStreamer(), resHeaders, null) + "]");
		pauseRun();

		System.out.println("Sending 512 random characters.");
		System.out.println("\tResponse [" +
				echoClient.doPost(false, new RandomBytesStreamer(512, 32, 0), resHeaders, null) + "]");
		pauseRun();

		System.out.println("Sending a million random characters.");
		tmp = echoClient.doPost(false, new RandomBytesStreamer(1_000_000, 131072, 0), resHeaders, null);
		System.out.println("\tGot  [" + tmp.length() + "] characters back.");
		pauseRun();

		System.out.println("Streaming a million random characters.");
		tmp = echoClient.doPost(true, new RandomBytesStreamer(1048576, 131072, 0), resHeaders, null);
		System.out.println("\tGot [" + tmp.length() + "] characters back.");
		pauseRun();

		System.out.println("Streaming 20 million random characters.");
		tmp = echoClient.doPost(true, new RandomBytesStreamer(1048576 * 10, 1048576 * 2, 0), resHeaders, null);
		System.out.println("\tGot [" + tmp.length() + "] characters back.");
	}

	private static void pauseRun() {
		System.out.println("Press enter to continue...");
		try {
			System.in.read();
		} catch (IOException e) {
		}
	}

	private static final String DEFAULT_PATH =
			"http://localhost:8080/tomcat-8-demos/non-blocking-io/AsyncEchoNbioServlet";

	public String doPost(boolean stream, BytesStreamer streamer,
            Map<String, List<String>> reqHead,
            Map<String, List<String>> resHead) throws IOException, Exception
    {
        URL url = new URL(DEFAULT_PATH);
        HttpURLConnection connection =
            (HttpURLConnection) url.openConnection();
        connection.setDoOutput(true);
        connection.setReadTimeout(1000000);
        if (reqHead != null) {
            for (Map.Entry<String, List<String>> entry : reqHead.entrySet()) {
                StringBuilder valueList = new StringBuilder();
                for (String value : entry.getValue()) {
                    if (valueList.length() > 0) {
                        valueList.append(',');
                    }
                    valueList.append(value);
                }
                connection.setRequestProperty(entry.getKey(),
                        valueList.toString());
            }
        }
        if (streamer != null && stream) {
            if (streamer.getLength()>0) {
                connection.setFixedLengthStreamingMode(streamer.getLength());
            } else {
                connection.setChunkedStreamingMode(1024);
            }
        }
        System.out.println("\tConnecting...");
        connection.connect();

        // Write the request body
        OutputStream os = null;
        try {
            os = connection.getOutputStream();
            while (streamer!=null && streamer.available()>0) {
                byte[] next = streamer.next();
                os.write(next);
                os.flush();
            }

        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException ioe) {
                    // Ignore
                }
            }
        }

        System.out.println("\tWaiting for response code...");
        int rc = connection.getResponseCode();
        System.out.println("\tWaiting for headers");
        if (resHead != null) {
            Map<String, List<String>> head = connection.getHeaderFields();
            resHead.putAll(head);
        }
        InputStream is;
        if (rc < 400) {
            is = connection.getInputStream();
        } else {
            is = connection.getErrorStream();
        }

        StringBuilder sb = new StringBuilder();
        BufferedInputStream bis = null;
        try {
            bis = new BufferedInputStream(is);
            byte[] buf = new byte[8196];
            int rd = 0;
            while((rd = bis.read(buf)) > 0) {
//            	System.out.println("Read back [" + rd + "]");
                sb.append(new String(buf, "utf-8"));
            }
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    // Ignore
                }
            }
        }

        if (rc >= 400) {
            System.out.println("Error Occurred.  Response was [" + sb + "]");
        }

        return sb.toString();
	}

	private static interface BytesStreamer {
	    int getLength();
	    int available();
	    byte[] next();
	}

	private static class EmptyBytesStreamer implements BytesStreamer {
		public int getLength() {
			return 0;
		}
		public int available() {
			return 0;
		}
		public byte[] next() {
			return null;
		}
	}

	private static class HelloBytesStreamer implements BytesStreamer {
		private byte[] data = "Hello World!".getBytes();
		private int available = data.length;

		public int getLength() {
			return data.length;
		}
		public int available() {
			return available;
		}
		public byte[] next() {
			available = 0;
			return data;
		}
	}

	private static class RandomBytesStreamer implements BytesStreamer {
		private int available = 10;
		private int total = 10;
		private int chunkSize = 1;
		private int sleep = 0;

		public RandomBytesStreamer() {}

		public RandomBytesStreamer(int total, int chunkSize, int sleep) {
			this.total = total;
			this.available = total;
			this.chunkSize = chunkSize;
			this.sleep = sleep;
		}

		public int getLength() {
			return total;
		}
		public int available() {
			return available;
		}
		public byte[] next() {
			available -= chunkSize;
			byte[] data;
			try {
				String tmp = RandomStringUtils.randomAlphanumeric(chunkSize);
				//System.out.println("Writing [" + tmp + "]");
				data = tmp.getBytes("utf-8");
			} catch (UnsupportedEncodingException e) {
				data = e.getMessage().getBytes();
			}
			if (sleep > 0) {
				try {
//					System.out.println("Sleeping...");
					Thread.sleep(sleep * 1000);
				} catch (InterruptedException ex) {
					// ignore
				}
			}
			return data;
		}
	}
}
