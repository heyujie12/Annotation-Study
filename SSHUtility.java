package com.ericsson.util;

import java.io.InputStream;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

public class SSHUtility {
	public static String executeSSH(String host, String username, String password,
			String command) {
		Session session = null;
		Channel channel = null;

		StringBuilder outputBuffer = new StringBuilder("");

		try {

			JSch jsch = new JSch();
			session = jsch.getSession(username, host, 22);
			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
			session.setPassword(password);
			session.connect();

			channel = session.openChannel("exec");
			((ChannelExec) channel).setCommand(command);
			channel.setInputStream(null);

			((ChannelExec) channel).setErrStream(System.err);

			InputStream in = channel.getInputStream();

			channel.connect();

			byte[] tmp = new byte[1024];
			while (true) {
				while (in.available() > 0) {
					int i = in.read(tmp, 0, 1024);
					if (i < 0)
						break;
					outputBuffer.append(new String(tmp, 0, i));
				}
				if (channel.isClosed()) {
//					System.out.println("exit-status: "
//							+ channel.getExitStatus());
					break;
				}

			}

			channel.disconnect();
			session.disconnect();
		} catch (Exception jsche) {
			System.err.println(jsche.getLocalizedMessage());
		} finally {
			channel.disconnect();
			session.disconnect();
		}
		return outputBuffer.toString();
	}
	
	public static void main(String[] args) {
		String out = executeSSH("159.107.219.241", "root",
				"electricity", "ls /opt");
		System.out.println(out);
	}
}
