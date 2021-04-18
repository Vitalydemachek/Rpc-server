package com.rpc.structure;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

@Data
@Accessors(fluent = true)
public class RpcResult implements Serializable {

	private static final long serialVersionUID = 6529685098267757690L;

	private String id;
	private Object result;


	private void writeObject(ObjectOutputStream aOutputStream) throws IOException {
		aOutputStream.writeUTF(id);
		aOutputStream.writeObject(result);
	}
}
