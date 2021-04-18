package com.rpc.structure;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class ClientWorker extends Thread {
	private static final Logger LOG = Logger.getLogger(ClientWorker.class.getName());

	private final static int cores = Runtime.getRuntime().availableProcessors();
	private final static String GET_SERVICE_INSTANCE = "getInstance";
	private Properties properties;
	private Socket socket;
	private ObjectOutputStream out;
	private ObjectInputStream in;

	public ClientWorker(Socket socket, Properties properties) throws IOException {
		this.socket = socket;
		this.properties = properties;
		in = new ObjectInputStream(socket.getInputStream());
		out = new ObjectOutputStream(socket.getOutputStream());
		start();
	}

	@Override
	public void run() {
		handler();
	}

	private void handler() {

		ExecutorService exec = Executors.newFixedThreadPool(cores);
		try {

			while (socket.isConnected()) {
				CallRpc callRpc = (CallRpc) in.readObject();
				Callable<RpcResult> rpcTask = () -> processRpcCall(callRpc, properties);
				Future<RpcResult> future = exec.submit(rpcTask);
				RpcResult rpcResult = future.get(3000, TimeUnit.MILLISECONDS);
				//out.writeObject(rpcResult);
			}

		} catch (ClassNotFoundException
				| IOException
				| InterruptedException
				| ExecutionException
				| TimeoutException
				e) {
			LOG.error("Server response handling finished with error: \n" + e.getMessage());
		}
	}

	private RpcResult processRpcCall(CallRpc callRpc, Properties properties) throws Exception {
		RpcResult wrappedResult = null;
		try {
			String className = properties.getProperty(callRpc.getServiceName());
			Class<?> clazz = Class.forName(className);
			Object instance = clazz.getDeclaredMethod(GET_SERVICE_INSTANCE).invoke(null);
			//Constructor<?> service = clazz.getConstructor();
			//Object object = service.newInstance();

			Object result;
			Method method;
			Object[] methodParam = callRpc.getArgs();
			if (methodParam == null || methodParam.length == 0) {
				method = clazz.getDeclaredMethod(callRpc.getProcedureName());
				result = method.invoke(instance);
			} else {
				Class<?>[] typeParams = new Class[methodParam.length];
				for (int i = 0; i < methodParam.length; i++) {
					typeParams[i] = methodParam[i].getClass();
				}
				method = clazz.getDeclaredMethod(callRpc.getProcedureName(), typeParams);
				result = method.invoke(instance, callRpc.getArgs());
			}

			LOG.info("TEEEEEEEEESSSSSSSSSSSSSSSSSSSSSSSSSTTTTTTTTTTTTTTTTTTTT: " + result);
			wrappedResult = new RpcResult()
					.id(callRpc.getId()).result(result == null
							? "Rpc: " + method.getName() + " successfully done!"
							: result);
			out.writeObject(wrappedResult);
			out.flush();
			return wrappedResult;
		} catch (ClassNotFoundException
				| NoSuchMethodException
				| IllegalArgumentException
				//| InstantiationException
				| IllegalAccessException
				| InvocationTargetException
				| IOException
				ex) {
			wrappedResult = new RpcResult()
					.id(callRpc.getId()).result("Call Rpc: " + callRpc.getProcedureName() + " finished with error:\n" + ex.toString());
			out.writeObject(wrappedResult);
			out.flush();
			return wrappedResult;
		}
	}
}
