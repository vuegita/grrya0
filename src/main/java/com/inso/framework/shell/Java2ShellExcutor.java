package com.inso.framework.shell;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;

public class Java2ShellExcutor {
	
	private static final Log LOG = LogFactory.getLog(Java2ShellExcutor.class);

	public static String lineseparator = System.getProperty("line.separator");
	public static String COMMAND_SH = "sh";
	public static String COMMAND_EXIT = "exit\n";
	public static String COMMAND_LINE_END = "\n";
	
	private ExecutorService mThreadPool;
	
	public Java2ShellExcutor(int threadCount)
	{
		this.mThreadPool = Executors.newFixedThreadPool(threadCount);
	}
	
	public CommandResult execCommand(String command) {
		return execCommand(new String[] { command }, true);
	}

	public CommandResult execCommand(String command, boolean isNeedResultMsg) {
		return execCommand(new String[] { command }, isNeedResultMsg);
	}

	public CommandResult execCommand(List<String> commands, boolean isNeedResultMsg) {
		return execCommand(commands == null ? null : commands.toArray(new String[] {}), isNeedResultMsg);
	}
	
	public CommandResult execScript(String script, String args, String[] envp, String dir)
	{
		int result = -1;
		StringBuilder successMsg = new StringBuilder();
		StringBuilder errorMsg = new StringBuilder();
		
		Process process = null;
		try {
			String cmd = COMMAND_SH + " " + script + args;
			process = Runtime.getRuntime().exec(cmd, envp, new File(dir));
			
			BufferedReader successResult = new BufferedReader(new InputStreamReader(process.getInputStream()));
			BufferedReader errorResult = new BufferedReader(new InputStreamReader(process.getErrorStream()));

			// http://249wangmang.blog.163.com/blog/static/52630765201261334351635/
			mThreadPool.execute(new Runnable() {
				public void run() {
					try {
						String s;
						while ((s = successResult.readLine()) != null) {
							successMsg.append(s);
							successMsg.append(lineseparator);
						}
					} catch (Exception e) {}
				}
			});
			
			// 启动两个线程,解决process.waitFor()阻塞问题
			mThreadPool.execute(new Runnable() {
				public void run() {
					try {
						String s;
						while ((s = errorResult.readLine()) != null) {
							errorMsg.append(s);
							errorMsg.append(lineseparator);
						}
					} catch (Exception e) {}
				}
			});
			
			result = process.waitFor();
			if (errorResult != null) {
				errorResult.close();
			}
			if (successResult != null) {
				successResult.close();
			}
			
		} catch (Exception e) {
			LOG.error("exec shell error:", e);
		} finally
		{
			if (process != null) process.destroy();
		}
		return new CommandResult(result, successMsg == null ? null : successMsg.toString(),
				errorMsg == null ? null : errorMsg.toString());
		
	}
	
	public CommandResult execCommand(String[] commands, boolean needResponse) {
		int result = -1;
		if (commands == null || commands.length == 0) {
			return new CommandResult(result, null, "空命令");
		}

		Process process = null;

		StringBuilder successMsg = new StringBuilder();
		StringBuilder errorMsg = new StringBuilder();

//		DataOutputStream os = null;
		try {
			process = Runtime.getRuntime().exec(commands);
//			os = new DataOutputStream(process.getOutputStream());
//			for (String command : commands) {
//				if (command == null) continue;
//				// donnot use os.writeBytes(commmand), avoid chinese charset
//				// error
//				os.write(command.getBytes());
//				os.writeBytes(COMMAND_LINE_END);
//				os.flush();
//			}
//			os.writeBytes(COMMAND_EXIT);
//			os.flush();

			BufferedReader successResult = new BufferedReader(new InputStreamReader(process.getInputStream()));
			BufferedReader errorResult = new BufferedReader(new InputStreamReader(process.getErrorStream()));

			// http://249wangmang.blog.163.com/blog/static/52630765201261334351635/
			if(needResponse)
			{
				mThreadPool.execute(new Runnable() {
					public void run() {
						try {
							String s;
							while ((s = successResult.readLine()) != null) {
								successMsg.append(s);
								successMsg.append(lineseparator);
							}
						} catch (Exception e) {}
					}
				});
			}
			
			// 启动两个线程,解决process.waitFor()阻塞问题
			if (needResponse) 
			{
				mThreadPool.execute(new Runnable() {
					public void run() {
						try {
							String s;
							while ((s = errorResult.readLine()) != null) {
								errorMsg.append(s);
								errorMsg.append(lineseparator);
							}
						} catch (Exception e) {}
					}
				});
			}
			
			result = process.waitFor();
			if (errorResult != null) {
				errorResult.close();
			}
			if (successResult != null) {
				successResult.close();
			}

		} catch (Exception e) {
			LOG.error("exec shell error:", e);
		} finally {
			if (process != null) process.destroy();
		}
		return new CommandResult(result, successMsg == null ? null : successMsg.toString(),
				errorMsg == null ? null : errorMsg.toString());
	}

	public static class CommandResult {

		public int result;
		public String responseMsg;
		public String errorMsg;

		public CommandResult(int result) {
			this.result = result;
		}

		public CommandResult(int result, String responseMsg, String errorMsg) {
			this.result = result;
			this.responseMsg = responseMsg;
			this.errorMsg = errorMsg;
		}

		@Override
		public String toString() {
			return "[result=" + result + "]" + " - [success=" + responseMsg + "]" + " - [error=" + errorMsg + "]";
		}
	}
	
}
