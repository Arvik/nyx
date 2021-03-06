package nyx.collections.converter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import nyx.collections.Const;

/**
 * Implements Object to byte[] array converter based on standard Java serialization mechanism.
 * 
 * @author varlou@gmail.com	
 */
public class SerialConverter<E> implements Converter<E, byte[]>, Serializable {

	private static final long serialVersionUID = 1100572136596789915L;
	
	// 16Kb byte array stream for object serialization. One instance per thread.
	private static transient ThreadLocal<ByteArrayOutputStream> BAOS = new ThreadLocal<ByteArrayOutputStream>() {
		protected ByteArrayOutputStream initialValue() {
			return new ByteArrayOutputStream(Const._1Kb * 16);
		};
	};
	
	@Override
	public byte[] encode(E from) {
		if (from==null) return null;
		ByteArrayOutputStream baos = BAOS.get();
		try {
			ObjectOutputStream os = new ObjectOutputStream(baos);
			os.writeUnshared(from);
			return baos.toByteArray();
		} catch (IOException e1) {
			BAOS.remove();
			throw new RuntimeException(e1);
		} finally {
			baos.reset();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public E decode(byte[] to) {
		if (to==null) return null;
		try {
			return (E) new ObjectInputStream(new ByteArrayInputStream(to)).readUnshared();
		} catch (ClassNotFoundException | IOException e) {
			throw new RuntimeException(e);
		}
	}

}
