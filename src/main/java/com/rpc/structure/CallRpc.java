package com.rpc.structure;

import lombok.Data;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

@Data
public class CallRpc implements Serializable {

	private static final long serialVersionUID = 8721263933535062039L;

	Object[] args;
	private String id;
	private String serviceName;
	private String procedureName;

	private void readObject(ObjectInputStream aInputStream) throws ClassNotFoundException, IOException {
		args = (Object[]) aInputStream.readObject();
		id = aInputStream.readUTF();
		serviceName = aInputStream.readUTF();
		procedureName = aInputStream.readUTF();
	}
}
