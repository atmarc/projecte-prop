import java.io.*;
import java.util.ArrayList;

public class LZWCompressor extends Compressor {
	private static final String extension = ".zero";
	private static final int BYTE_SIZE = 8;
	private Tree dictionary;
	private int nextIndex = 0;
	private ArrayList<Byte> pattern;
	private int codewordSize;   // la longitud en bits para escribir la codificación

	/**
	 * Crea un objecto compressor con el diccionario básico.
	 */
	public LZWCompressor() {
		inicializar();
	}

	String getExtension() {
		return ".zero";
	}

	/**
	 * @param file El fichero desde cual se tiene que calcular el nombre
	 * @return El nombre con la extension del fichero a comprimir
	 */
	private String getCompressedName(File file) {
		String fileName = file.getPath();
		int pos = fileName.lastIndexOf('.');
		String compressedFileName;
		if (pos != -1) compressedFileName = fileName.substring(0, pos);
		else throw new IllegalArgumentException("Nombre de fichero incorrecto");
		return compressedFileName + extension;
	}

	public void compress() {
		inicializar();
		int nx;
		int codeword;
		while ((nx = readByte()) != -1) {
			byte B = (byte) nx;
			byte[] patternByteArray = toByteArray(pattern);
			byte[] patternPlusNextChar = concatenate(pattern, B);
			if (dictionary.find(patternPlusNextChar) == -1) {
				codeword = dictionary.find(patternByteArray);
				byte[] codewordAsByteArray = toByteArray(codeword);
				writeBytes(codewordAsByteArray);
				dictionary.insert(patternPlusNextChar, nextIndex);
				++nextIndex;
				if (nextIndex >= (1 << codewordSize)) {
					codewordSize += BYTE_SIZE;
				}
				pattern = new ArrayList<>();
				pattern.add(B);
			}
			else pattern.add(B);
		}
		if (pattern.size() > 0) {
		codeword = dictionary.find(toByteArray(pattern));
		byte[] codewordAsByteArray = toByteArray(codeword);
		writeBytes(codewordAsByteArray);
		}
	}

	private byte[] toByteArray(ArrayList<Byte> p) {
		byte[] res = new byte[p.size()];
		for (int i = 0; i < p.size(); ++i) res[i] = p.get(i);
		return res;
	}

	private byte[] concatenate(ArrayList<Byte> p, byte b) {
		byte[] res = new byte[p.size()+1];
		for (int i = 0; i < p.size(); ++i) res[i] = p.get(i);
		res[p.size()] = b;
		return res;
	}

	/**
	 * Convierte un int en un array con elementos de 8 bits
	 * (Es igual a pasar un integer desde la base 10 a base 256)
	 *
	 * @param codeword número a convertir
	 * @return un array con la representación de {@code codeword}
	 */
	private byte[] toByteArray(int codeword) {
		byte[] codewordAsByte = new byte[codewordSize / BYTE_SIZE];
		for (int i = codewordAsByte.length - 1; i >= 0; --i) {
			codewordAsByte[i] = (byte) (codeword & 0xFF);
			codeword >>= 8;
		}
		return codewordAsByte;
	}

	/**
	 * Inicializa el diccionario del compresor al diccionario básico
	 */
	public void inicializar() {
		nextIndex = 0;
		dictionary = new Tree(0);
		byte[] b = new byte[1];
		while (nextIndex < 256) {
			b[0] = (byte) nextIndex;
			dictionary.insert(b, nextIndex);
			++nextIndex;
		}
		codewordSize = 16;
		pattern = new ArrayList<>();
	}

}